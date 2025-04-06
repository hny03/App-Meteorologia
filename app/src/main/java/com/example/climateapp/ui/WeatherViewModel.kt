package com.example.climateapp.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.climateapp.data.HourlyForecast
import com.example.climateapp.data.repository.WeatherRepository
import com.example.climateapp.data.DailyForecast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _weatherInfoState = MutableStateFlow(WeatherInfoState())
    val weatherInfoState: StateFlow<WeatherInfoState> = _weatherInfoState.asStateFlow()

    fun updateWeatherInfo(latitude: Float, longitude: Float) {
        viewModelScope.launch {
            Log.d("ClimaApp", "Iniciando busca do clima: lat=$latitude, lon=$longitude")
            try {
                _weatherInfoState.update { it.copy(isLoading = true, error = null) }

                val weatherInfo = repository.getWeatherData(latitude, longitude)
                val hourlyForecast = repository.getHourlyForecast(latitude, longitude)
                val dailyForecast = repository.getDailyForecast(latitude, longitude)

                Log.d("ClimaApp", "Dados recebidos da API: $weatherInfo")
                Log.d("ClimaApp", "Previsão horária: $hourlyForecast")
                Log.d("ClimaApp", "Previsão diária: $dailyForecast")

                _weatherInfoState.update {
                    it.copy(
                        weatherInfo = weatherInfo,
                        hourlyForecast = hourlyForecast,
                        dailyForecast = dailyForecast,
                        isLoading = false,
                        error = null
                    )
                }

            } catch (e: Exception) {
                Log.e("ClimaApp", "Erro ao buscar clima: ${e.message}", e)
                _weatherInfoState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Falha ao buscar dados do clima"
                    )
                }
            }
        }
    }

    fun clearError() {
        _weatherInfoState.update { it.copy(error = null) }
    }
}
