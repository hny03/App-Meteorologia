@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.climateapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.climateapp.data.*
import com.example.climateapp.data.remote.response.CityRepository
import com.example.climateapp.ui.WeatherViewModel
import com.example.climateapp.ui.components.*
import com.example.climateapp.ui.theme.ClimateAppTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var locationService: LocationService
    private var currentLocation by mutableStateOf<Location?>(null)

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        Log.d("ClimaApp", "Resultado da permissão: $permissions")
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                Log.d("ClimaApp", "Permissão de localização concedida")
                getLocation()
            }
            else -> {
                Log.d("ClimaApp", "Permissão de localização negada")
                Toast.makeText(this, "A permissão de localização é obrigatória", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ClimaApp", "MainActivity criada")
        enableEdgeToEdge()

        locationService = LocationService(this)

        setContent {
            ClimateAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Log.d("ClimaApp", "Renderizando MainScreen...")
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        currentLocation = currentLocation,
                        onGetLocation = { checkLocationPermission() }
                    )
                }
            }
        }

        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        Log.d("ClimaApp", "Verificando permissão de localização")
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("ClimaApp", "Permissão já concedida")
                getLocation()
            }
            else -> {
                Log.d("ClimaApp", "Solicitando permissão de localização")
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun getLocation() {
        Log.d("ClimaApp", "Obtendo localização atual")
        if (!locationService.isLocationEnabled()) {
            Log.d("ClimaApp", "Serviços de localização estão desativados")
            Toast.makeText(this, "Ative os serviços de localização", Toast.LENGTH_LONG).show()
            return
        }

        locationService.getLastLocation { location ->
            Log.d("ClimaApp", "Localização recebida: $location")
            currentLocation = location
            if (location == null) {
                Toast.makeText(this, "Não foi possível obter a localização. Verifique o GPS.", Toast.LENGTH_LONG).show()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    currentLocation: Location?,
    onGetLocation: () -> Unit
) {
    val viewModel: WeatherViewModel = viewModel()
    val state = viewModel.weatherInfoState.collectAsState().value
    var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var searchQuery by remember { mutableStateOf("") }
    var showSearchResults by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val cityRepository = remember { CityRepository(context) }
    val searchResults = remember(searchQuery) {
        if (searchQuery.length >= 2) {
            cityRepository.searchCities(searchQuery)
        } else {
            emptyList()
        }
    }

    LaunchedEffect(currentLocation) {
        Log.d("ClimaApp", "LaunchedEffect acionado. Localização = $currentLocation")
        currentLocation?.let {
            Log.d("ClimaApp", "Chamando ViewModel com lat=${it.latitude}, lon=${it.longitude}")
            viewModel.updateWeatherInfo(it.latitude.toFloat(), it.longitude.toFloat())
        }
    }

    val weatherData = state.weatherInfo?.let {
        // Data atual para calcular os dias da semana
        val today = LocalDate.now()

        // Função para obter o nome do dia da semana começando por sexta-feira
        fun getDayOfWeekString(dayOffset: Int): String {
            val date = today.plusDays(dayOffset.toLong())
            return date.dayOfWeek.name.capitalize() // Ex: Sexta, Sábado, etc.
        }

        WeatherData(
            current = CurrentWeather(
                temperature = it.temperature ?: 0.0,
                humidity = it.humidity ?: 0,
                windSpeed = it.windSpeed ?: 0.0,
                rain = it.rain ?: 0.0,
                description = it.condition ?: "N/A",
                icon = it.Icon ?: "01d"
            ),
            hourly = List(6) { hour ->
                // Começando das 21:00
                val hourTime = 21 + hour
                HourlyForecast(
                    time = "${if (hourTime > 12) hourTime - 12 else hourTime}:00", // Ajuste para 12h format
                    temperature = 25.0 + hour,
                    icon = "01d"
                )
            },
            daily = List(7) { day ->
                // Calculando os dias começando por sexta-feira
                val dayOfWeek = getDayOfWeekString(day + 2) // "2" porque hoje é considerado 0 e sexta-feira é 2
                DailyForecast(
                    date = dayOfWeek,
                    maxTemperature = 28.0 + day,
                    minTemperature = 20.0 + day,
                    icon = "01d"
                )
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "Menu",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                Divider()
                Box {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { 
                            searchQuery = it
                            showSearchResults = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        placeholder = { Text("Pesquisar localização...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        singleLine = true
                    )
                    
                    if (showSearchResults && searchResults.isNotEmpty()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 80.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        ) {
                            LazyColumn {
                                items(searchResults) { city ->
                                    ListItem(
                                        headlineContent = { Text(city.city_name) },
                                        supportingContent = { Text(city.state) },
                                        modifier = Modifier.clickable {
                                            searchQuery = "${city.city_name}, ${city.state}"
                                            showSearchResults = false
                                            viewModel.updateWeatherInfo(city.lat.toFloat(), city.lon.toFloat())
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Clima App") },

                )
            }
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Location Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Localização Atual", style = MaterialTheme.typography.titleMedium)
                            IconButton(onClick = onGetLocation) { Text("") }
                        }

                        currentLocation?.let { location ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Latitude", style = MaterialTheme.typography.bodyMedium)
                                    Text(String.format("%.6f°", location.latitude), style = MaterialTheme.typography.titleMedium)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Longitude", style = MaterialTheme.typography.bodyMedium)
                                    Text(String.format("%.6f°", location.longitude), style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        } ?: Text(
                            text = "Localização não disponível",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                weatherData?.let {
                    Log.d("ClimaApp", "Renderizando dados do clima com API")
                    CurrentWeatherCard(it.current)
                    Spacer(modifier = Modifier.height(16.dp))
                    HourlyForecastRow(it.hourly)
                    Spacer(modifier = Modifier.height(16.dp))
                    DailyForecastList(it.daily)
                }
            }
        }
    }
}
