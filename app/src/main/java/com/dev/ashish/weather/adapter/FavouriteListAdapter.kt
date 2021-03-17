package com.dev.ashish.weather.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.ashish.weather.R
import com.dev.ashish.weather.extensions.WeatherAppExtensions.loadImage
import com.dev.ashish.weather.model.WeatherResponseModel
import com.dev.ashish.weather.utils.WeatherAppUtils
import kotlinx.android.synthetic.main.layout_weather_detail.view.*

//
// Created by Ashish on 16/03/21.
//

class FavouriteListAdapter(var context : Context, var list : MutableList<WeatherResponseModel>) : RecyclerView.Adapter<FavouriteListAdapter.FavViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        return FavViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.layout_fav_weather_detail,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    override fun getItemCount(): Int {
      return list.size
    }

    class FavViewHolder(val view : View) : RecyclerView.ViewHolder(view){
        fun bind(response: WeatherResponseModel){
            (!response.weather.isEmpty()).let {
                view.ivWeather.loadImage(response.weather[0].icon)
                view.tvWeatherType.text = response.weather[0].main
            }
            view.tvCityName.text = response.name
            view.tvCurrentTemp.text =
                view.context.getString(R.string.temp, WeatherAppUtils.convertFahrenheitToCelsius(response.main.temp))
            view.tvMinMaxTemp.text =
                view.context.getString(
                    R.string.min_max_temp, WeatherAppUtils.convertFahrenheitToCelsius(response.main.temp_min),
                    WeatherAppUtils.convertFahrenheitToCelsius(response.main.temp_max)
                )
        }
    }
}