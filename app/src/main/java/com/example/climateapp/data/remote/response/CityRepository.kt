package com.example.climateapp.data.remote.response

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class CityRepository(private val context: Context) {
    private var cities: List<CityResponse> = emptyList()

    init {
        loadCities()
    }

    private fun loadCities() {
        try {
            val jsonString = context.assets.open("cidades_brasil.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<CityResponse>>() {}.type
            cities = Gson().fromJson(jsonString, type)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun searchCities(query: String): List<CityResponse> {
        if (query.isBlank()) return emptyList()
        return cities.filter { city ->
            city.city_name.contains(query, ignoreCase = true) ||
            city.state.contains(query, ignoreCase = true)
        }.take(10) // Limit to 10 results for better performance
    }
} 