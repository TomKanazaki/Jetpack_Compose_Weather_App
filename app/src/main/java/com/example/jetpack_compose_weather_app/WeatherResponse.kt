package com.example.jetpack_compose_weather_app

data class WeatherResponse (
    val name: String,
    val main: Main,
    val weather: List<Weather>
    )

data class Main(
    val temp: Float,
    val humidity: Int
)

data class Weather(
    val description: String
)