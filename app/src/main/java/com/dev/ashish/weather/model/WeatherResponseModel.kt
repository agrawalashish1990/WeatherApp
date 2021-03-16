package com.dev.ashish.weather.model

//
// Created by Ashish on 15/03/21.
//

data class WeatherResponseModel(
    val coord: CordsDetail,
    val weather: List<WeatherInfo>,
    val base: String,
    val main: MainDetail,
    val visibility: Long,
    val wind: WindDetail,
    val clouds: CloudDetail,
    val dt: Long,
    val sys: SysDetail,
    val timezone: Long,
    val id: Long,
    val name: String,
    val cod: Int
) {
}

data class CordsDetail(val lon: Double, val lat: Double) {}

data class WeatherInfo(val id: Long, val main: String, val description: String, val icon: String)

data class MainDetail(
    val temp: Double, val feels_like: Double, val temp_min: Double, val temp_max: Double,
    val pressure: Int, val humidity: Int
)

data class WindDetail(val speed: Double, val deg: Double)
data class CloudDetail(val all: Int)

data class SysDetail(
    val type: Int,
    val id: Long,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)



