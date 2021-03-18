package com.dev.ashish.weather.network

import com.dev.ashish.weather.model.WeatherResponseModel
import com.dev.ashish.weather.utils.Constants
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//
// Created by Ashish on 15/03/21.
//

interface ApiInterface {

    @GET("weather")
    fun getWeatherInfo(
        @Query("q") input: String,
        @Query("appid") key: String = Constants.KEY_WEATHER_API,
        @Query("units") units : String = "imperial"
    ): Call<WeatherResponseModel>

}