package com.example.climateapp.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherbitHourlyResponse(
    val data: List<HourlyData>,
    @SerialName("city_name") val cityName: String
)

@Serializable
data class HourlyData(
    @SerialName("timestamp_utc") val timestampUtc: String,
    val temp: Double,
    val weather: WeatherDescription
)

@Serializable
data class WeatherDescription(
    val description: String,
    val icon: String
)
