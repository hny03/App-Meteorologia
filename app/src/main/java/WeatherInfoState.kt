package com.example.climateapp.ui

import com.example.climateapp.data.HourlyForecast
import com.example.climateapp.data.di.module.WeatherInfo
import com.example.climateapp.data.DailyForecast


data class WeatherInfoState(
    val weatherInfo: WeatherInfo? = null,
    val hourlyForecast: List<HourlyForecast> = emptyList(),
    val dailyForecast: List<DailyForecast> = emptyList(), // ✅ novo
    val isLoading: Boolean = false,
    val error: String? = null
)
