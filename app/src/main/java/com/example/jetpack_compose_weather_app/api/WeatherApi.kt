package com.example.jetpack_compose_weather_app.api

import com.example.jetpack_compose_weather_app.data.WeatherModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("/v1/current.json")
    suspend fun getWeather(
        @Query("key") apiKey: String,
        @Query("q") city: String): Response<WeatherModel>

}