package com.example.climateapp.data.remote.response

import android.content.Context
import com.example.climateapp.data.Cidade
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class CityRepository(private val context: Context) {
    private var cities: List<Cidade> = emptyList()

    init {
        loadCities()
    }

    private fun loadCities() {
        try {
            val jsonString = context.assets.open("municipios.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<Cidade>>() {}.type
            cities = Gson().fromJson(jsonString, type)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun searchCities(query: String): List<Cidade> {
        if (query.isBlank()) return emptyList()
        return cities.filter { city ->
            city.city_name.contains(query, ignoreCase = true)
        }.take(10) // Limit to 10 results for better performance
    }
} 