package com.example.climateapp.ui

import com.example.climateapp.data.HourlyForecast
import com.example.climateapp.data.di.module.WeatherInfo

data class WeatherInfoState(
    val weatherInfo: WeatherInfo? = null,
    val hourlyForecast: List<HourlyForecast> = emptyList(), // ✅ corrigido
    val isLoading: Boolean = false,
    val error: String? = null
)
