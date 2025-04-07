package com.example.climateapp.data.repository

import com.example.climateapp.data.local.SavedCity
import kotlinx.coroutines.flow.Flow

interface SavedCityRepository {
    fun getAllCities(): Flow<List<SavedCity>>
    suspend fun saveCity(city: SavedCity)
    suspend fun deleteCity(city: SavedCity)
    suspend fun getCity(cityName: String, state: String): SavedCity?
} 