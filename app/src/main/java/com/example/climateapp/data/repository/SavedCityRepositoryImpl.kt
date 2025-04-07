package com.example.climateapp.data.repository

import com.example.climateapp.data.local.SavedCity
import com.example.climateapp.data.local.SavedCityDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SavedCityRepositoryImpl @Inject constructor(
    private val savedCityDao: SavedCityDao
) : SavedCityRepository {
    override fun getAllCities(): Flow<List<SavedCity>> = savedCityDao.getAllCities()

    override suspend fun saveCity(city: SavedCity) {
        savedCityDao.insertCity(city)
    }

    override suspend fun deleteCity(city: SavedCity) {
        savedCityDao.deleteCity(city)
    }

    override suspend fun getCity(cityName: String, state: String): SavedCity? {
        return savedCityDao.getCity(cityName, state)
    }
} 