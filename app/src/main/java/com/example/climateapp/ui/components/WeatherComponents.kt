package com.example.climateapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.climateapp.data.CurrentWeather
import com.example.climateapp.data.DailyForecast
import com.example.climateapp.data.HourlyForecast

@Composable
fun CurrentWeatherCard(weather: CurrentWeather) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${weather.temperature}°C",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = weather.description,
                style = MaterialTheme.typography.titleMedium
            )
            
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
fun HourlyForecastRow(forecasts: List<HourlyForecast>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Hourly Forecast",
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
fun DailyForecastList(forecasts: List<DailyForecast>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "7-Day Forecast",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            forecasts.forEach { forecast ->
                DailyForecastItem(forecast)
                if (forecast != forecasts.last()) {
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
            text = "${forecast.maxTemperature}°C / ${forecast.minTemperature}°C",
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