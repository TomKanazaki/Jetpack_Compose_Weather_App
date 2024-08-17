package com.example.jetpack_compose_weather_app.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpack_compose_weather_app.api.Constant
import com.example.jetpack_compose_weather_app.api.NetworkResponse
import com.example.jetpack_compose_weather_app.api.RetrofitInstance
import com.example.jetpack_compose_weather_app.data.WeatherModel
import kotlinx.coroutines.launch
import java.io.IOException

class WeatherViewModel: ViewModel() {
    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult

    fun fetchWeather(city: String) {
        if (city.isBlank()) {
            _weatherResult.value = NetworkResponse.Error("Please enter a valid city name.")
            return
        }
        _weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(Constant.apiKey, city)
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    Log.d("WeatherViewModel", "Weather response: $weatherResponse")
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    }
                }else{
                    Log.e("WeatherViewModel", "Error fetching weather: ${response.code()}")
                    _weatherResult.value = NetworkResponse.Error("API request failed. Please check your city name.")
                }
            }catch (e: Exception) {
                Log.e("WeatherViewModel", "Error fetching weather", e)
                if (e is IOException) {
                    _weatherResult.value = NetworkResponse.Error("Network error. Please check your internet connection.")
                } else {
                    _weatherResult.value = NetworkResponse.Error("An unexpected error occurred. Please try again later.")
                }
            }
        }
    }

    fun fetchTopCitiesForCountry(country: String): List<String> {
        return try {
            when (country) {
                "United States of America" -> listOf("New York", "Los Angeles", "Chicago")
                "United Kingdom" -> listOf("London", "Manchester", "Birmingham")
                "France" -> listOf("Paris", "Lyon", "Marseille")
                "Japan" -> listOf("Tokyo", "Osaka", "Kyoto")
                "Italy" -> listOf("Rome", "Milan", "Naples")
                "Australia" -> listOf("Sydney", "Melbourne", "Brisbane")
                "China" -> listOf("Beijing", "Shanghai", "Shenzhen")
                "India" -> listOf("Mumbai", "Delhi", "Bangalore")
                "Canada" -> listOf("Toronto", "Montreal", "Vancouver")
                else -> listOf("New York", "London", "Tokyo")
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}