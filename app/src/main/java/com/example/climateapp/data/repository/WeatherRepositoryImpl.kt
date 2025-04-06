package com.example.climateapp.data.repository

import android.util.Log
import com.example.climateapp.data.HourlyForecast
import com.example.climateapp.data.di.module.WeatherInfo
import com.example.climateapp.data.remote.RemoteDataSource
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : WeatherRepository {

    override suspend fun getWeatherData(lat: Float, lng: Float): WeatherInfo {
        val response = remoteDataSource.getWeatherDataResponse(lat, lng)

        Log.d("DEBUG", "API response: $response")
        Log.d("DEBUG", "Response data size: ${response.data.size}")

        val weather = response.data.firstOrNull() ?: run {
            Log.e("ERROR", "Lista de dados vazia! Nenhuma informação de clima retornada.")
            throw Exception("Sem dados de clima retornados da API.")
        }

        val hourlyForecast = remoteDataSource.getHourlyForecastFromWeatherbit(lat, lng)

        // Apenas para log e teste por enquanto
        hourlyForecast.take(5).forEach {
            Log.d("WeatherbitHourly", "${it.timestampUtc} - ${it.temp}°C - ${it.weather.description}")
        }

        return WeatherInfo(
            locationName = weather.cityName,
            Icon = weather.weather.icon,
            codeIcon = weather.weather.code,
            condition = weather.weather.description,
            temperature = weather.temperature,
            apparentTemperature = weather.apparentTemperature,
            //dayOfWeek = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
            humidity = weather.relativeHumidity,
            windSpeed = weather.windSpeed,
            rain = weather.precipitation
            //isDay = true // pode ser usado se quiser identificar dia/noite
        )
    }

    override suspend fun getHourlyForecast(lat: Float, lng: Float): List<HourlyForecast> {
        val data = remoteDataSource.getHourlyForecastFromWeatherbit(lat, lng)

        return data.take(6).map {
            val time = try {
                val utcTime = java.time.LocalDateTime.parse(it.timestampUtc)
                utcTime
                    .atOffset(java.time.ZoneOffset.UTC)
                    .atZoneSameInstant(java.time.ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("HH:mm"))
            } catch (e: Exception) {
                Log.e("ParseTime", "Erro ao converter hora: ${it.timestampUtc}", e)
                "--:--"
            }


            HourlyForecast(
                time = time,
                temperature = it.temp,
                icon = it.weather.icon
            )
        }
    }
}
