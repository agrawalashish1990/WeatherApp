package com.dev.ashish.weather.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.dev.ashish.weather.R


//
// Created by Ashish on 16/03/21.
//

object WeatherAppUtils {

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


    fun ImageView.loadImage(icon: String){
        Glide
            .with(this)
            .load(Constants.IMAGE_BASE_URL.plus(icon).plus("@2x.png"))
            .centerCrop()
            .placeholder(R.mipmap.ic_launcher)
            .into(this);
    }

    fun convertToCelcius(far: Double): Double{
       return ((far-32)*0.55)
    }

    fun hideKeyboard(activity: Activity) {
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
}