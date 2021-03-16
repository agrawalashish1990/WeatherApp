package com.dev.ashish.weather.storage.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.dev.ashish.weather.model.WeatherResponseModel
import com.dev.ashish.weather.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList

//
// Created by Ashish on 15/03/21.
//

class SessionPref private constructor(context: Context) {
    private val KEY_FAV_SEARCH = "fav_search"
    private var sharedPref: SharedPreferences? = null

    companion object {
        var sessionPref: SessionPref? = null

        @JvmStatic
        fun getSharedPref(context: Context): SessionPref {
            if(sessionPref == null){
                sessionPref = SessionPref(context)
            }
            return sessionPref!!;
        }
    }

    init {
        sharedPref = context.getSharedPreferences("PrefsWeatherApp", Context.MODE_PRIVATE)
    }



    fun addToFav(data: WeatherResponseModel) {
        sharedPref?.edit {
            var listFav = getFavList()?: mutableListOf()
            listFav.add(data)
            val jsonVal = Gson().toJson(listFav)
            putString(KEY_FAV_SEARCH, jsonVal)
        }.apply { }
    }

    fun getFavList(): MutableList<WeatherResponseModel>? {
        var listFav = mutableListOf<WeatherResponseModel>()
        val type = object : TypeToken<ArrayList<WeatherResponseModel?>?>() {}.type
        val valueJson: String? = sharedPref?.getString(KEY_FAV_SEARCH, null)
        valueJson?.let {
            listFav.addAll(Gson().fromJson(valueJson, type))
        }
        return listFav
    }

    fun isAlreadyExist(name: String): Boolean {
        var listFav = getFavList()
        listFav?.forEach {
            if (it.name.equals(name)) {
                return true
            }
        }
        return false
    }

    fun getFavData(name: String): WeatherResponseModel? {
        var listFav = getFavList()
        listFav?.forEach {
            if (it.name.equals(name)) {
                return it
            }
        }
        return null
    }

    fun updateFavData(newData: WeatherResponseModel) {
        removeFavourite(newData.name)
        addToFav(newData)
    }

    fun removeFavourite(name: String) {
        var listFav = getFavList()
        var iterator = listFav?.iterator()!!
        while (iterator?.hasNext()) {
            var data = iterator.next()
            if (data.name.equals(name)) {
                listFav.remove(data)
            }
        }
        sharedPref?.edit {
            val jsonVal = Gson().toJson(listFav)
            putString(KEY_FAV_SEARCH, jsonVal)
        }.apply { }
    }

}