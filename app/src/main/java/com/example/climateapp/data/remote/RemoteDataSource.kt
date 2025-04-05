package com.example.climateapp.data.remote

import com.example.climateapp.data.remote.response.WeatherDataResponse

interface RemoteDataSource {

    suspend fun getWeatherDataResponse(lat: Float, lng: Float ): WeatherDataResponse
}