package com.example.jetpack_compose_weather_app.main_event
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpack_compose_weather_app.api.Constant
import com.example.jetpack_compose_weather_app.api.NetworkResponse
import com.example.jetpack_compose_weather_app.api.RetrofitInstance
import com.example.jetpack_compose_weather_app.api.WeatherModel
import kotlinx.coroutines.launch

class WeatherViewModel: ViewModel() {
    private val weatherApi = RetrofitInstance.weatherApi
    private val weather_Result = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = weather_Result

    fun fetchWeather(city: String) {
        weather_Result.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(Constant.apiKey, city)
                if (response.isSuccessful) {
//                val weatherResponse = response.body()
//                Log.d("WeatherViewModel", "Weather response: $weatherResponse")
                    response.body()?.let {
                        weather_Result.value = NetworkResponse.Success(it)
                    }
                }else{
//                Log.e("WeatherViewModel", "Error fetching weather: ${response.code()}")
                    weather_Result.value = NetworkResponse.Error("Failed to fetch weather")
                }
            }catch (e: Exception) {
                //Log.e("WeatherViewModel", "Error fetching weather", e)
                weather_Result.value = NetworkResponse.Error("Failed to fetch weather")
            }
        }
    }
}