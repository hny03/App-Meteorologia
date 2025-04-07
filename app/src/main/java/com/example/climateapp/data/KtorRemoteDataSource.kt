package com.example.climateapp.data

import com.example.climateapp.data.remote.RemoteDataSource
import com.example.climateapp.data.remote.response.HourlyData
import com.example.climateapp.data.remote.response.WeatherDataResponse
import com.example.climateapp.data.remote.response.WeatherbitHourlyResponse
import com.example.climateapp.data.remote.response.WeatherbitDailyResponse
import com.example.climateapp.data.remote.response.DailyData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Inject

class KtorRemoteDataSource @Inject constructor() : RemoteDataSource {

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    companion object {
        private const val CURRENT_URL = "https://api.weatherbit.io/v2.0/current"
        private const val HOURLY_URL = "https://api.weatherbit.io/v2.0/forecast/hourly"
        private const val DAILY_URL = "https://api.weatherbit.io/v2.0/forecast/daily" // ✅ novo
        private const val API_KEY = "98c13e90a04d48bbba455170923ce167"
    }

    override suspend fun getWeatherDataResponse(lat: Float, lng: Float): WeatherDataResponse {
        return httpClient
            .get(CURRENT_URL) {
                parameter("lat", lat)
                parameter("lon", lng)
                parameter("key", API_KEY)
                parameter("lang", "pt")
            }
            .body()
    }

    override suspend fun getHourlyForecastFromWeatherbit(lat: Float, lon: Float): List<HourlyData> {
        val response: WeatherbitHourlyResponse = httpClient.get(HOURLY_URL) {
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("key", API_KEY)
            parameter("hours", 24)
        }.body()

        return response.data
    }

    override suspend fun getDailyForecastFromWeatherbit(lat: Float, lon: Float): List<DailyData> {
        val response: WeatherbitDailyResponse = httpClient.get(DAILY_URL) {
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("key", API_KEY)
            parameter("days", 7)
        }.body()

        return response.data
    }
}
