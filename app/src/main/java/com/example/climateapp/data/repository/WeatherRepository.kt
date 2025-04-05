package com.example.climateapp.data.repository

import com.example.climateapp.data.di.module.WeatherInfo

interface WeatherRepository {

    suspend fun getWeatherData(lat: Float, lng: Float): WeatherInfo
}