package com.example.climateapp.ui.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.climateapp.data.CurrentWeather
import com.example.climateapp.data.DailyForecast
import com.example.climateapp.data.HourlyForecast
import com.example.climateapp.ui.theme.BlueSky
import com.example.climateapp.ui.theme.DarkBlueSky
import com.example.climateapp.ui.theme.DarkNightBlue
import com.example.climateapp.ui.theme.LightBlueSky
import com.example.climateapp.ui.theme.LightNightBlue
import com.example.climateapp.ui.theme.NightBlue

@Composable
fun CurrentWeatherCard(weather: CurrentWeather, context: Context, cor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardColors(
            containerColor = cor,
            contentColor = Color.White,
            disabledContainerColor = cor,
            disabledContentColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val iconDrawableResId: Int = context.resources.getIdentifier(
                    "img_${weather.icon}",
                    "drawable",
                    context.packageName
                )

                Image(
                    painter = painterResource(id = iconDrawableResId),
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )

                Column(
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = weather.city,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "${weather.temperature}°C",
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = weather.description,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherInfoItem("Humidity", "${weather.humidity}%")
                WeatherInfoItem("Wind", "${weather.windSpeed} km/h")
                WeatherInfoItem("Rain", "${weather.rain} mm")
            }
        }
    }
}

@Composable
fun HourlyForecastRow(forecasts: List<HourlyForecast>, cor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardColors(
            containerColor = cor,
            contentColor = Color.White,
            disabledContainerColor = cor,
            disabledContentColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Previsão por Hora",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(forecasts) { forecast ->
                    HourlyForecastItem(forecast)
                }
            }
        }
    }
}

@Composable
fun HourlyForecastItem(forecast: HourlyForecast) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = forecast.time,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "${forecast.temperature}°C",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun DailyForecastList(forecasts: List<DailyForecast>, cor : Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardColors(
            containerColor = cor,
            contentColor = Color.White,
            disabledContainerColor = cor,
            disabledContentColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Previsão dos Próximos 7 Dias",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            forecasts.forEachIndexed { index, forecast ->
                DailyForecastItem(forecast)
                if (index < forecasts.lastIndex) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
fun DailyForecastItem(forecast: DailyForecast) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = forecast.date,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${forecast.minTemperature}°C / ${forecast.maxTemperature}°C",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun WeatherInfoItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
