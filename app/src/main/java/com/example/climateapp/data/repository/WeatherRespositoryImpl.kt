package com.example.climateapp.data.repository

import com.example.climateapp.data.di.module.WeatherInfo
import com.example.climateapp.data.remote.RemoteDataSource
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

class WeatherRespositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
): WeatherRepository {
    override suspend fun getWeatherData(lat: Float, lng: Float): WeatherInfo {
        val response = remoteDataSource.getWeatherDataResponse(lat, lng)
        val weather = response.data[0]

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
        )
    }

}

//val locationName: String,
//val Icon: String,
//val codeIcon: Int,
//val condition: String,
//val temperature: Double,
//val apparentTemperature: Double,
//val dayOfWeek: String,
//val isDay: Boolean,
//val humidity: Int,
//val windSpeed: Int,
//val rain: Double