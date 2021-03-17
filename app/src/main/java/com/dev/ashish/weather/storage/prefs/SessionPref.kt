package com.dev.ashish.weather.storage.prefs

import android.content.Context
import android.content.SharedPreferences
import android.util.LruCache
import androidx.core.content.edit
import com.dev.ashish.weather.model.WeatherResponseModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

//
// Created by Ashish on 15/03/21.
//

class SessionPref private constructor(context: Context) {

    private val keyFavSearch = "fav_search"
    private val keyDarkTheme = "dark_theme"
    private val cacheSize = 1 * 1024 * 1024 // 1 MB


    private var sharedPref: SharedPreferences? = null
    private var cache : LruCache<String, WeatherResponseModel>  = LruCache(cacheSize)

    companion object {
        private var sessionPref: SessionPref? = null

        @JvmStatic
        fun getSharedPref(context: Context): SessionPref {
            if(sessionPref == null){
                sessionPref = SessionPref(context)
            }
            return sessionPref!!
        }
    }

    init {

        sharedPref = context.getSharedPreferences("PrefsWeatherApp", Context.MODE_PRIVATE)
    }

    /**
     * This method will set the theme mode
     */
    fun setDarkTheme(darkTheme: Boolean) {
        sharedPref?.edit {
            putBoolean(keyDarkTheme, darkTheme)
            apply()
        }
    }

    /**
     * This method will return
     *
     * TRUE -> If Dark theme
     * FALSE -> If Light Theme
     */
    fun getDarkTheme() : Boolean {
      return sharedPref?.getBoolean(keyDarkTheme, false)!!
    }

    /**
     * This method will add data to favourite list
     */

    fun addToMyFavourite(data: WeatherResponseModel) {
        sharedPref?.edit {
            val listFav = getMyFavouriteWeathersList()
            listFav.add(0, data)
            val jsonVal = Gson().toJson(listFav)
            putString(keyFavSearch, jsonVal)
            apply()
        }
    }

    /**
     * This method will return all favourite place details
     */

    fun getMyFavouriteWeathersList(): MutableList<WeatherResponseModel> {
        val listFav = mutableListOf<WeatherResponseModel>()
        val type = object : TypeToken<ArrayList<WeatherResponseModel?>?>() {}.type
        val valueJson: String? = sharedPref?.getString(keyFavSearch, null)
        valueJson?.let {
            listFav.addAll(Gson().fromJson(valueJson, type))
        }
        return listFav
    }

    /**
     * This method will return true if record exists in shared preferences
     *
     * TRUE -> If exists
     * FALSE -> If doesn't exists
     *
     */

    fun hasWeatherInfo(name: String): Boolean {
        val listFav = getMyFavouriteWeathersList()
        listFav.forEach {
            if (it.name.toLowerCase(Locale.getDefault()) == (name.toLowerCase(Locale.getDefault()))) {
                return true
            }
        }
        return false
    }

    /**
     * This method will return data from SharedPreference if exists
     */
    fun getWeatherInfoIfExists(name: String): WeatherResponseModel? {
        val listFav = getMyFavouriteWeathersList()
        listFav.forEach { data->
            if (data.name.toLowerCase(Locale.getDefault())==(name.toLowerCase(Locale.getDefault()))) {
                return data
            }
        }
        return null
    }



    /**
     * This method will remove favourite data from preference
     */
    fun removeWeatherInfo(name: String) {
        val listFav = getMyFavouriteWeathersList()
        val iterator = listFav.iterator()

        var isRemoved  = false

        while (iterator.hasNext()) {
            val data = iterator.next()
            if (data.name.toLowerCase(Locale.getDefault())==(name.toLowerCase(Locale.getDefault()))) {
                listFav.remove(data)
                isRemoved = true
                break
            }
        }

        if(isRemoved) {
            sharedPref?.edit {
                val jsonVal = Gson().toJson(listFav)
                putString(keyFavSearch, jsonVal)
                apply()
            }
        }
    }

    fun addInCache(data: WeatherResponseModel?){
        synchronized(cache){
            data?.let {
                cache.put(data.name.trim().toLowerCase(Locale.getDefault()), data)
            }
        }
    }

    fun getFromCache(name: String?) : WeatherResponseModel?{
        name?.let {
            synchronized(cache){
                return cache.get(it.trim().toLowerCase(Locale.getDefault()))
            }
        }?:return null

    }
}