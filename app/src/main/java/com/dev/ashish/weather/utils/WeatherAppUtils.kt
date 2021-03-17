package com.dev.ashish.weather.utils

import android.app.Activity
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.dev.ashish.weather.R
import com.dev.ashish.weather.storage.prefs.SessionPref
import java.util.*


//
// Created by Ashish on 16/03/21.
//

object WeatherAppUtils {

    /**
     * This method will return about connectivity status of device
     */
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                ) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * This method will convert fahrenheit to celcius
     */

    fun convertFahrenheitToCelsius(fahrenheitVal: Double): Double{
       return (0.5556*(fahrenheitVal-32))
    }

    /**
     * This method is used to hide the keyboard
     */
    private fun hideKeyboard(activity: Activity) {
        activity?.let {
            activity.currentFocus?.let { currentFocus->
                try {
                    val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * This method will set touch listener for all views except EditText
     * It is used to hide keyboard when user touch screen out side keyboard/editext
     */

    fun setupUI(activity: Activity, view: View) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                hideKeyboard(activity)
                false
            }
        }
        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(activity, innerView)
            }
        }
    }

    /**
     * This method will fetch the mode from local cache and will set theme DARK/LIGHT
     */

     fun setAppTheme(context: Context){
        var isDarkMode : Boolean = SessionPref.getSharedPref(context).getDarkTheme()
        if(isDarkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    /**
     * Get postal code from location
     *
     */
     fun getPostalCode(context: Context,location: Location?): String {
        if(location != null){
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: List<Address> =
                geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses.isNotEmpty()) {
                return addresses[0].postalCode
            }
        }
        return ""
    }
}