package com.example.climateapp.data.remote

import com.example.climateapp.data.remote.response.HourlyData
import com.example.climateapp.data.remote.response.DailyData
import com.example.climateapp.data.remote.response.WeatherDataResponse

interface RemoteDataSource {

    suspend fun getWeatherDataResponse(lat: Float, lng: Float): WeatherDataResponse

    suspend fun getHourlyForecastFromWeatherbit(lat: Float, lon: Float): List<HourlyData>

    suspend fun getDailyForecastFromWeatherbit(lat: Float, lon: Float): List<DailyData> // ✅ novo
}
