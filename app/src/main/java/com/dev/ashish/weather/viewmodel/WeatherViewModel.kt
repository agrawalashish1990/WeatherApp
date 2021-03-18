package com.dev.ashish.weather.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dev.ashish.weather.R
import com.dev.ashish.weather.model.WeatherResponseModel
import com.dev.ashish.weather.network.ApiClient
import com.dev.ashish.weather.storage.prefs.SessionPref
import com.dev.ashish.weather.utils.Constants
import com.dev.ashish.weather.utils.WeatherAppUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//
// Created by Ashish on 16/03/21.
//

class WeatherViewModel() : ViewModel() {

    val weatherData = MutableLiveData<WeatherResponseModel>()
    val dataLoading = MutableLiveData<Boolean>().apply { value = false }
    val dataError = MutableLiveData<Boolean>().apply { value = false }


    /**
     * This method will fetch the weather info for given input (city/state code/ country code
     */
    fun fetchWeather(context : Context,input : String) {
        dataLoading.value = true
        dataError.value = false

        //Id network not exists then return response from Shared preference if exists
        if(!WeatherAppUtils.isOnline(context)){
            Toast.makeText(context, R.string.no_internet_connection, Toast.LENGTH_SHORT).show()

            var weatherResponseModel =  SessionPref.getSharedPref(context).getFromCache(input)

            weatherResponseModel?.let {
                Toast.makeText(context, R.string.msg_show_data_from_cache, Toast.LENGTH_SHORT).show()
                weatherData.postValue(it)
            }
            dataLoading.value = false
            return
        }

        ApiClient.getClient.getWeatherInfo(input).enqueue(object :
            Callback<WeatherResponseModel?> {
            override fun onResponse(
                call: Call<WeatherResponseModel?>,
                response: Response<WeatherResponseModel?>) {

                dataLoading.value = false

                if (response.isSuccessful() && response.code() == Constants.STATUS_CODE_SUCCESS) {
                    SessionPref.getSharedPref(context).addInCache(response.body())
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