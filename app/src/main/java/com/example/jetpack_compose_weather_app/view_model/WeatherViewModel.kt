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
    private val weather_Result = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = weather_Result

    fun fetchWeather(city: String) {
        if (city.isBlank()) {
            weather_Result.value = NetworkResponse.Error("Please enter a valid city name.")
            return
        }
        weather_Result.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(Constant.apiKey, city)
                if (response.isSuccessful) {
                val weatherResponse = response.body()
                Log.d("WeatherViewModel", "Weather response: $weatherResponse")
                    response.body()?.let {
                        weather_Result.value = NetworkResponse.Success(it)
                    }
                }else{
                Log.e("WeatherViewModel", "Error fetching weather: ${response.code()}")
                    weather_Result.value = NetworkResponse.Error("API request failed. Please check your city name.")
                }
            }catch (e: Exception) {
                Log.e("WeatherViewModel", "Error fetching weather", e)
                if (e is IOException) {
                    weather_Result.value = NetworkResponse.Error("Network error. Please check your internet connection.")
                } else {
                    weather_Result.value = NetworkResponse.Error("An unexpected error occurred. Please try again later.")
                }
            }
        }
    }
}