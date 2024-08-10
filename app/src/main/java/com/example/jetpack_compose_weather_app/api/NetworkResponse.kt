package com.example.jetpack_compose_weather_app.api

//T=WeatherModel
sealed class NetworkResponse<out T>{
    data class Success<out T>(val data: T): NetworkResponse<T>()
    data class Error(val message: String): NetworkResponse<Nothing>()
    object Loading: NetworkResponse<Nothing>()
}