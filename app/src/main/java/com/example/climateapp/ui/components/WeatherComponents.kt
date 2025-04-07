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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn

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
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                ) {
                    Text(
                        text = "${weather.temperature}°C",
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = weather.description,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                val iconDrawableResId: Int = context.resources.getIdentifier(
                    "img_${weather.icon}",
                    "drawable",
                    context.packageName
                )

                Image(
                    painter = painterResource(id = iconDrawableResId),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 8.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WeatherInfoItem("Umidade", "${weather.humidity}%")
                Spacer(modifier = Modifier.width(32.dp))
                WeatherInfoItem("Vento", "${weather.windSpeed} km/h")
                Spacer(modifier = Modifier.width(32.dp))
                WeatherInfoItem("Chuva", "${weather.rain} mm")
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
                text = "Previsão para as Próximas 24 Horas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(forecasts) { forecast ->
                    HourlyForecastItem(forecast)
                }
            }
        }
    }
}

/**
 * Subtrai 3 horas de um horário no formato HH:mm
 * @param time String no formato "HH:mm" (formato 24h)
 * @return String no formato "HH:mm" após subtrair 3 horas
 */
fun subtractThreeHours(time: String): String {
    try {
        // Divide a string em horas e minutos
        val parts = time.split(":")
        if (parts.size != 2) {
            return time // Retorna o valor original em caso de formato inválido
        }

        var hours = parts[0].toInt()
        val minutes = parts[1].toInt()

        // Subtrai 3 horas
        hours -= 3

        // Ajusta horas negativas (caso seja entre 00:00 e 02:59)
        if (hours < 0) {
            hours += 24
        }

        // Formata o resultado para garantir que tenha dois dígitos
        return String.format("%02d:%02d", hours, minutes)
    } catch (e: Exception) {
        // Em caso de erro, retorna o valor original
        return time
    }
}

@Composable
fun HourlyForecastItem(forecast: HourlyForecast) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = subtractThreeHours(forecast.time),
            style = MaterialTheme.typography.bodyMedium
        )
        val iconDrawableResId: Int = LocalContext.current.resources.getIdentifier(
            "img_${forecast.icon}",
            "drawable",
            LocalContext.current.packageName
        )
        Image(
            painter = painterResource(id = iconDrawableResId),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(32.dp)
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
        val iconDrawableResId: Int = LocalContext.current.resources.getIdentifier(
            "img_${forecast.icon}",
            "drawable",
            LocalContext.current.packageName
        )
        Image(
            painter = painterResource(id = iconDrawableResId),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = "${forecast.minTemperature}°C / ${forecast.maxTemperature}°C",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 8.dp)
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
