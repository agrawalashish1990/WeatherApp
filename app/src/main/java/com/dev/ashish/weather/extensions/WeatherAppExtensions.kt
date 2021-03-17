package com.dev.ashish.weather.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.dev.ashish.weather.R
import com.dev.ashish.weather.utils.Constants

/**
 *
 * Ashish Agrawal
 * Created on 17/03/21
 *
 */
object WeatherAppExtensions {

    /**
     * This extension method will load icon from "Weather API"
     */
    fun ImageView.loadImage(icon: String){
        Glide
            .with(this)
            .load(Constants.IMAGE_BASE_URL.plus(icon).plus("@2x.png"))
            .centerCrop()
            .placeholder(R.mipmap.ic_launcher)
            .into(this);
    }
}