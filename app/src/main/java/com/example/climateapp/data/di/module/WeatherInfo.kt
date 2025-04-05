package com.example.climateapp.data.di.module

data class WeatherInfo(
    val locationName: String,
    val Icon: String,
    val codeIcon: Int,
    val condition: String,
    val temperature: Double,
    val apparentTemperature: Double,
    //al dayOfWeek: String,
    val humidity: Int,
    val windSpeed: Double,
    val rain: Double
)