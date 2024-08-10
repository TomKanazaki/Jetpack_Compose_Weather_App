package com.example.jetpack_compose_weather_app
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WeatherViewModel: ViewModel() {
    private val weatherDataFl = MutableStateFlow<WeatherResponse?>(null)
    val weatherData: StateFlow<WeatherResponse?> = weatherDataFl
    private val weatherApi = WeatherApi.create()

    fun fetchWeather(city: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(city, apiKey)
                Log.d("API Response", "$response") // Log the response
                weatherDataFl.value = response
            } catch (e: Exception) {
                Log.e("API Error", "Error fetching weather", e) // Log any errors
                e.printStackTrace() 
            }
        }
    }
}