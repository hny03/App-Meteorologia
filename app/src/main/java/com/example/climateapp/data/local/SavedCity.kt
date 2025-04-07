package com.example.climateapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "saved_cities",
    indices = [androidx.room.Index(value = ["cityName", "state"], unique = true)]
)
data class SavedCity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cityName: String,
    val state: String,
    val latitude: Double,
    val longitude: Double
) 