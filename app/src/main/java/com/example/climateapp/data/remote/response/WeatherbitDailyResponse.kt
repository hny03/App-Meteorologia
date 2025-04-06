package com.example.climateapp.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherbitDailyResponse(
    val data: List<DailyData>,
    val city_name: String
)

@Serializable
data class DailyData(
    @SerialName("valid_date") val valid_date: String,
    @SerialName("max_temp") val max_temp: Double,
    @SerialName("min_temp") val min_temp: Double,
    val weather: WeatherDescription
)
