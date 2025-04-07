package com.example.climateapp.data.repository

import android.util.Log
import com.example.climateapp.data.HourlyForecast
import com.example.climateapp.data.di.module.WeatherInfo
import com.example.climateapp.data.remote.RemoteDataSource
import com.example.climateapp.data.DailyForecast
import java.text.SimpleDateFormat
import java.util.*
import java.time.LocalDate
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

        hourlyForecast.take(5).forEach {
            Log.d("WeatherbitHourly", "${it.timestampUtc} - ${it.temp}°C - ${it.weather.description}")
        }

        return WeatherInfo(
            locationName = weather.cityName,
            icon = weather.weather.icon,
            codeIcon = weather.weather.code,
            condition = weather.weather.description,
            temperature = weather.temperature,
            apparentTemperature = weather.apparentTemperature,
            humidity = weather.relativeHumidity,
            windSpeed = weather.windSpeed,
            rain = weather.precipitation,
            isDay = weather.weather.icon.last() == 'd'
        )
    }

    override suspend fun getHourlyForecast(lat: Float, lng: Float): List<HourlyForecast> {
        val data = remoteDataSource.getHourlyForecastFromWeatherbit(lat, lng)

        return data.take(24).map {
            val time = try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val date = inputFormat.parse(it.timestampUtc)
                outputFormat.format(date)
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

    override suspend fun getDailyForecast(lat: Float, lng: Float): List<DailyForecast> {
        val data = remoteDataSource.getDailyForecastFromWeatherbit(lat, lng)

        return data.take(7).map {
            val day = try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = inputFormat.parse(it.valid_date)
                val calendar = Calendar.getInstance()
                calendar.time = date
                
                val dayNames = arrayOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")
                dayNames[calendar.get(Calendar.DAY_OF_WEEK) - 1]
            } catch (e: Exception) {
                Log.e("ParseDay", "Erro ao converter data: ${it.valid_date}", e)
                "Dia"
            }

            DailyForecast(
                date = day,
                maxTemperature = it.max_temp,
                minTemperature = it.min_temp,
                icon = it.weather.icon
            )
        }
    }
}
