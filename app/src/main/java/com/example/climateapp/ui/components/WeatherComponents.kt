package com.example.climateapp.ui.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import android.location.Geocoder
import android.os.Build
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import com.example.climateapp.ui.theme.BlueSky
import com.example.climateapp.ui.theme.DarkBlueSky
import com.example.climateapp.ui.theme.DarkNightBlue
import com.example.climateapp.ui.theme.LightBlueSky
import com.example.climateapp.ui.theme.LightNightBlue
import com.example.climateapp.ui.theme.NightBlue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun CurrentWeatherCard(weather: CurrentWeather, context: Context, cor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),

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
fun HourlyForecastRow(forecasts: List<HourlyForecast>,
                      cor: Color,
                      context: Context,
                      latitude: Double,
                      longitude: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
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
                    HourlyForecastItem(forecast, context, latitude, longitude)
                }
            }
        }
    }
}

/**
 * Ajusta um horário UTC para o fuso horário local baseado em coordenadas
 * @param time String no formato "HH:mm"
 * @param latitude Latitude da localização
 * @param longitude Longitude da localização
 * @return String no formato "HH:mm" ajustado para o fuso horário local
 */
fun adjustTimeForLocation(
    context: Context,
    time: String,
    latitude: Double,
    longitude: Double
): String {
    // Obter o fuso horário aproximado baseado na longitude
    val hourOffset = (longitude / 15.0).toInt()
    val timezoneId = when {
        // Brasil (simplificado)
        latitude >= -33.0 && latitude <= 5.0 && longitude >= -74.0 && longitude <= -34.0 -> {
            when {
                // Acre/Amazonas (UTC-4)
                longitude <= -56.0 -> "GMT-4"
                // Horário de Brasília (UTC-3)
                else -> "GMT-3"
            }
        }
        // Caso não seja Brasil, usa cálculo aproximado pela longitude
        else -> if (hourOffset >= 0) "GMT+$hourOffset" else "GMT$hourOffset"
    }

    try {
        // Converte o horário
        val today = SimpleDateFormat("yyyy-MM-dd").format(Date())
        val dateTimeStr = "$today $time"

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm").apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val date = inputFormat.parse(dateTimeStr) ?: return time

        val outputFormat = SimpleDateFormat("HH:mm").apply {
            timeZone = TimeZone.getTimeZone(timezoneId)
        }

        return outputFormat.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
        return time
    }
}

@Composable
fun HourlyForecastItem(forecast: HourlyForecast,
                       context: Context,
                       latitude: Double,
                       longitude: Double) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = adjustTimeForLocation(
                context = context,
                time = forecast.time,
                latitude = latitude,
                longitude = longitude,
                ),
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
