package com.dev.ashish.weather

import android.app.Application
import com.dev.ashish.weather.utils.WeatherAppUtils

/**
 *
 * Ashish Agrawal
 * Created on 15/03/21
 *
 */
class WeatherApp : Application(){

    override fun onCreate() {
        super.onCreate()
        //Set app theme - DARK / LIGHT
        WeatherAppUtils.setAppTheme(this)
    }
}