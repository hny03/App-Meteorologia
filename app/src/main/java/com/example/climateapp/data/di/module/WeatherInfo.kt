package com.example.climateapp.data.di.module

import com.example.climateapp.data.HourlyForecast
import com.example.climateapp.data.DailyForecast

data class WeatherInfo(
    val locationName: String,
    val icon: String,
    val codeIcon: Int,
    val condition: String,
    val temperature: Double,
    val apparentTemperature: Double,
    val humidity: Int,
    val windSpeed: Double,
    val rain: Double,
    val hourlyForecast: List<HourlyForecast> = emptyList(),
    val dailyForecast: List<DailyForecast> = emptyList(),
    val isDay: Boolean
)
