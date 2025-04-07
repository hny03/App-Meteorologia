package com.example.climateapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedCityDao {
    @Query("SELECT * FROM saved_cities ORDER BY cityName ASC")
    fun getAllCities(): Flow<List<SavedCity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: SavedCity)

    @Delete
    suspend fun deleteCity(city: SavedCity)

    @Query("SELECT * FROM saved_cities WHERE cityName = :cityName AND state = :state")
    suspend fun getCity(cityName: String, state: String): SavedCity?
} 