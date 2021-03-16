package com.dev.ashish.weather.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dev.ashish.weather.R
import com.dev.ashish.weather.model.WeatherResponseModel
import com.dev.ashish.weather.storage.prefs.SessionPref
import com.dev.ashish.weather.utils.WeatherAppUtils
import com.dev.ashish.weather.utils.WeatherAppUtils.loadImage
import com.dev.ashish.weather.viewmodel.WeatherViewModel
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.layout_weather_detail.*

//
// Created by Ashish on 16/03/21.
//

class MyHomeFragment : Fragment(), View.OnClickListener{

    private val TAG = MyHomeFragment::class.java.name
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var progressDialog: ProgressDialog

    companion object{
        fun getInstance() : MyHomeFragment{
            return MyHomeFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.content_main, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        resetUI()
        setUpListners()
        initViewModel()
    }

    private fun initUI() {
        progressDialog = ProgressDialog(activity)
        progressDialog.setMessage(getString(R.string.loading));
        WeatherAppUtils.setupUI(requireActivity(),rootLayout)
    }

    private fun resetUI() {
        detailLayout.visibility = View.GONE
        etInput.setText("")
    }

    private fun setUpListners() {
        btnSearch.setOnClickListener(this)
        btnAddFav.setOnClickListener(this)
    }

    private fun initViewModel() {
        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        weatherViewModel.weatherData.observe(this, androidx.lifecycle.Observer {
            setUpDataOnUI(it)
        })
        weatherViewModel.dataLoading.observe(this, androidx.lifecycle.Observer {
            if (it) {
                detailLayout.visibility = View.GONE
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }
        })
        weatherViewModel.dataError.observe(this, androidx.lifecycle.Observer {
            if (it) {
                Toast.makeText(context, R.string.no_data_found, Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun setUpDataOnUI(response: WeatherResponseModel) {
        detailLayout.visibility = View.VISIBLE
        (!response.weather.isEmpty()).let {
            ivWeather.loadImage(response.weather[0].icon)
            tvWeatherType.text = response.weather[0].main
        }
        tvCityName.text = response.name
        tvCurrentTemp.text =
            getString(R.string.temp, WeatherAppUtils.convertToCelcius(response.main.temp))
        tvMinMaxTemp.text =
            getString(
                R.string.min_max_temp, WeatherAppUtils.convertToCelcius(response.main.temp_min),
                WeatherAppUtils.convertToCelcius(response.main.temp_max)
            )
        if(SessionPref.getSharedPref(requireContext() )?.isAlreadyExist(response.name)){
            btnAddFav.text = getString(R.string.remove_to_fav)
        }else{
            btnAddFav.text = getString(R.string.add_to_fav)
        }
    }

    private fun searchClicked() {
        var text = etInput.text.toString()
        if (text.trim().isEmpty()) {
            Toast.makeText(requireContext(), R.string.plz_enter_input, Toast.LENGTH_SHORT)
                .show()
        } else {
            weatherViewModel.fetchWeather(text)
        }
    }

    fun fetchData(input : String){
        weatherViewModel.fetchWeather(input)
    }

    private fun addToFavClicked() {
        weatherViewModel.weatherData.value?.let { data ->
            if(btnAddFav.text.toString().equals(getString(R.string.add_to_fav))){
                SessionPref.getSharedPref(requireContext()).addToFav(data)
                btnAddFav.text = getString(R.string.remove_to_fav)
            }else{
                SessionPref.getSharedPref(requireContext()).removeFavourite(data.name)
                btnAddFav.text = getString(R.string.add_to_fav)
            }
        }

    }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSearch -> searchClicked()
            R.id.btnAddFav -> addToFavClicked()
        }
    }
}