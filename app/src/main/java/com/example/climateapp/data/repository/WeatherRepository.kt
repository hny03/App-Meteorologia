package com.example.climateapp.data.repository

import com.example.climateapp.data.HourlyForecast
import com.example.climateapp.data.di.module.WeatherInfo
import com.example.climateapp.data.DailyForecast

interface WeatherRepository {

    suspend fun getWeatherData(lat: Float, lng: Float): WeatherInfo

    suspend fun getHourlyForecast(lat: Float, lng: Float): List<HourlyForecast>

    suspend fun getDailyForecast(lat: Float, lng: Float): List<DailyForecast> // ✅ novo
}
