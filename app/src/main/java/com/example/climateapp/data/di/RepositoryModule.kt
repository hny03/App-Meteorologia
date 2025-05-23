package com.example.climateapp.data.di

import com.example.climateapp.data.repository.SavedCityRepository
import com.example.climateapp.data.repository.SavedCityRepositoryImpl
import com.example.climateapp.data.repository.WeatherRepository
import com.example.climateapp.data.repository.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryModule {

    @Binds
    fun bindWeatherRepository(repository: WeatherRepositoryImpl): WeatherRepository

    @Binds
    fun bindSavedCityRepository(repository: SavedCityRepositoryImpl): SavedCityRepository
}