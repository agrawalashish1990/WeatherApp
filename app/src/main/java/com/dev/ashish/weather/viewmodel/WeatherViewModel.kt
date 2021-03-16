package com.dev.ashish.weather.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dev.ashish.weather.model.WeatherResponseModel
import com.dev.ashish.weather.network.ApiClient
import com.dev.ashish.weather.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//
// Created by Ashish on 16/03/21.
//

class WeatherViewModel : ViewModel() {
    val weatherData = MutableLiveData<WeatherResponseModel>()
    val dataLoading = MutableLiveData<Boolean>().apply { value = false }
    val dataError = MutableLiveData<Boolean>().apply { value = false }

    fun fetchWeather(input : String) {
        dataLoading.value = true
        dataError.value = false

        ApiClient.getClient.getWeatherInfo(input, Constants.KEY_WEATHER_API).enqueue(object :
            Callback<WeatherResponseModel?> {
            override fun onResponse(
                call: Call<WeatherResponseModel?>,
                response: Response<WeatherResponseModel?>) {
                dataLoading.value = false
                if (response.isSuccessful() && response.code() == Constants.STATUS_CODE_SUCCESS) {
                    weatherData.postValue(response.body())
                } else {
                    dataError.value = true
                }
            }

            override fun onFailure(call: Call<WeatherResponseModel?>, t: Throwable) {
                Log.e("TAG", "onFailure: $t")
                dataLoading.value = false
                dataError.value = true
            }
        })
    }
}