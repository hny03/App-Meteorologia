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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.climateapp.data.*
import com.example.climateapp.data.local.SavedCity
import com.example.climateapp.ui.WeatherViewModel
import com.example.climateapp.ui.components.*
import com.example.climateapp.ui.theme.ClimateAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.climateapp.ui.theme.BlueSky
import com.example.climateapp.ui.theme.DarkBlueSky
import com.example.climateapp.ui.theme.DarkNightBlue
import com.example.climateapp.ui.theme.LightBlueSky
import com.example.climateapp.ui.theme.LightNightBlue
import com.example.climateapp.ui.theme.NightBlue
import java.text.Normalizer


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var locationService: LocationService
    private var currentLocation by mutableStateOf<Location?>(null)

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                getLocation()
            }
            else -> {
                Toast.makeText(this, "A permissão de localização é obrigatória", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        locationService = LocationService(this)

        setContent {
            ClimateAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> getLocation()
            else -> locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun getLocation() {
        if (!locationService.isLocationEnabled()) {
            Toast.makeText(this, "Ative os serviços de localização", Toast.LENGTH_LONG).show()
            return
        }

        locationService.getLastLocation { location ->
            currentLocation = location
            if (location == null) {
                Toast.makeText(this, "Não foi possível obter a localização. Verifique o GPS.", Toast.LENGTH_LONG).show()
            }
        }
    }
}

@Composable
fun MainScreen(

    modifier: Modifier = Modifier,
    currentLocation: Location?,
    onGetLocation: () -> Unit
) {
    val viewModel: WeatherViewModel = viewModel()
    val state = viewModel.weatherInfoState.collectAsState().value
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val cidades = remember { carregarCidadesDoJson(context) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearchResults by remember { mutableStateOf(false) }

    val searchResults = remember(searchQuery) {
        if (searchQuery.length >= 2) {
            val normalizedQuery = normalizeText(searchQuery)
            cidades.filter {
                normalizeText(it.city_name).contains(normalizedQuery, ignoreCase = true) ||
                normalizeText(it.state).contains(normalizedQuery, ignoreCase = true)
            }
        } else emptyList()
    }

    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            viewModel.updateWeatherInfo(it.latitude.toFloat(), it.longitude.toFloat())
        }
    }

    LaunchedEffect(state.dailyForecast) {
        Log.d("DEBUG", "DailyForecast size: ${state.dailyForecast.size}")
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(280.dp),
                drawerContainerColor = if (state.weatherInfo?.isDay ?: true) LightBlueSky else LightNightBlue,
                drawerContentColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Menu",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Divider(color = Color.White.copy(alpha = 0.5f))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            showSearchResults = true
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        placeholder = { Text("Pesquisar localização...", color = Color.White.copy(alpha = 0.7f)) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        searchQuery = ""
                                        showSearchResults = false
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Limpar pesquisa",
                                        tint = Color.White
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                        )
                    )

                    if (showSearchResults && searchResults.isNotEmpty()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(),
                            color = if (state.weatherInfo?.isDay ?: true) LightBlueSky else LightNightBlue,
                            tonalElevation = 8.dp
                        ) {
                            LazyColumn {
                                items(searchResults) { city ->
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                city.city_name,
                                                color = Color.White
                                            )
                                        },
                                        supportingContent = {
                                            Text(
                                                city.state,
                                                color = Color.White.copy(alpha = 0.7f)
                                            )
                                        },
                                        modifier = Modifier.clickable {
                                            searchQuery = ""
                                            showSearchResults = false
                                            viewModel.updateWeatherInfo(city.lat.toFloat(), city.lon.toFloat())
                                            viewModel.saveCurrentCity(city.city_name, city.state, city.lat, city.lon)
                                            keyboardController?.hide()
                                            scope.launch { drawerState.close() }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "Cidades Salvas",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    val savedCities by viewModel.savedCities.collectAsState()
                    SavedCitiesList(
                        savedCities = savedCities,
                        onCitySelected = { city ->
                            viewModel.updateWeatherInfo(city.latitude.toFloat(), city.longitude.toFloat())
                            scope.launch { drawerState.close() }
                        },
                        onDeleteCity = { city ->
                            viewModel.deleteSavedCity(city)
                        }
                    )


                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Localização",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                state.weatherInfo?.locationName ?: "Carregando...",
                                color = Color.White
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = if (state.weatherInfo?.isDay ?: true) {
                                listOf(BlueSky, LightBlueSky)
                            } else listOf(NightBlue, LightNightBlue)
                        )
                    )
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    state.weatherInfo?.let {
                        val currentWeather = CurrentWeather(
                            temperature = it.temperature ?: 0.0,
                            humidity = it.humidity ?: 0,
                            windSpeed = it.windSpeed ?: 0.0,
                            rain = it.rain ?: 0.0,
                            description = it.condition ?: "N/A",
                            icon = it.icon ?: "01d",
                            city = ""
                        )
                        val cor = if (state.weatherInfo?.isDay ?: true) {
                            DarkBlueSky
                        } else DarkNightBlue

                        CurrentWeatherCard(currentWeather, context, cor)
                        Spacer(modifier = Modifier.height(13.dp))
                        HourlyForecastRow(state.hourlyForecast, cor)
                        Spacer(modifier = Modifier.height(13.dp))
                        DailyForecastList(state.dailyForecast, cor)
                    }
                }
            }
        }
    }
}

@Composable
fun SavedCitiesList(
    savedCities: List<SavedCity>,
    onCitySelected: (SavedCity) -> Unit,
    onDeleteCity: (SavedCity) -> Unit
) {
    LazyColumn {
        items(savedCities) { city ->
            ListItem(

                headlineContent = { 
                    Text(
                        text = city.cityName,
                        color = Color.White
                    ) 
                },
                supportingContent = { 
                    Text(
                        text = city.state,
                        color = Color.White.copy(alpha = 0.7f)
                    ) 
                },
                trailingContent = {
                    IconButton(onClick = { onDeleteCity(city) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Deletar cidade",
                            tint = Color.White
                        )
                    }
                },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.clickable { onCitySelected(city) }

            )
        }
    }
}

fun normalizeText(text: String): String {
    return Normalizer.normalize(text, Normalizer.Form.NFD)
        .replace("[^\\p{ASCII}]".toRegex(), "")
        .lowercase()
}
