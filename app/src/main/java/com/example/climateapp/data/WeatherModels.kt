package com.example.climateapp.data

data class CurrentWeather(
    val temperature: Double,
    val humidity: Int,
    val windSpeed: Double,
    val rain: Double,
    val description: String,
    val icon: String
)

data class HourlyForecast(
    val time: String,
    val temperature: Double,
    val icon: String
)

data class DailyForecast(
    val date: String,
    val maxTemperature: Double,
    val minTemperature: Double,
    val icon: String
)

data class WeatherData(
    val current: CurrentWeather,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>
) 