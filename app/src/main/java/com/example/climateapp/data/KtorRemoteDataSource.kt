package com.example.climateapp.data

import com.example.climateapp.data.remote.RemoteDataSource
import com.example.climateapp.data.remote.response.WeatherDataResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
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
        private const val BASE_URL = "https://api.weatherbit.io/v2.0/current"
        private const val API_KEY = "f4e94ecc2f0042f6b727f985999d97ff"
    }

    override suspend fun getWeatherDataResponse(lat: Float, lng: Float): WeatherDataResponse {
        return httpClient
            .get("${BASE_URL}?lat=$lat&lon=$lng&key=${API_KEY}&lang=pt")
            .body()
    }
}
