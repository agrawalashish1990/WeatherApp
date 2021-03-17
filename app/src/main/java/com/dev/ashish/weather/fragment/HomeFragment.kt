package com.dev.ashish.weather.fragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dev.ashish.weather.R
import com.dev.ashish.weather.extensions.WeatherAppExtensions.loadImage
import com.dev.ashish.weather.model.WeatherResponseModel
import com.dev.ashish.weather.storage.prefs.SessionPref
import com.dev.ashish.weather.ui.HomeActivity
import com.dev.ashish.weather.utils.WeatherAppUtils
import com.dev.ashish.weather.viewmodel.WeatherViewModel
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.layout_weather_detail.*


//
// Created by Ashish on 16/03/21.
//

class HomeFragment : Fragment(), View.OnClickListener{

    private lateinit var weatherViewModel: WeatherViewModel

    companion object{
        fun getInstance() : HomeFragment{
            return HomeFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setUpListeners()
        initViewModel()
    }

    //set UI setting for startup
    private fun setupUI() {
        //Hide Result view
        detailLayout.visibility = View.GONE
        etInput.setText("")
        //Hide keyboard - touch anywhere on screen except edittext
        WeatherAppUtils.setupUI(requireActivity(), rootLayout)
        (activity as HomeActivity).setupToolbar(getString(R.string.app_name),false)
    }

    //set callback, click event
    private fun setUpListeners() {
        btnSearch.setOnClickListener(this)
        btnAddFav.setOnClickListener(this)
        etInput.setOnEditorActionListener(object : TextView.OnEditorActionListener{
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    searchClicked()
                    return true
                }
                return false
            }
        })
    }

    //setup view model
    private fun initViewModel() {
        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        weatherViewModel.weatherData.observe(this,  {
            setUpDataOnUI(it)
        })
        weatherViewModel.dataLoading.observe(this,  {
            if (it) {
                detailLayout.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        })
        weatherViewModel.dataError.observe(this,  {
            if (it) {
                Toast.makeText(context, R.string.no_data_found, Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    //show data on UI
    private fun setUpDataOnUI(response: WeatherResponseModel) {
        detailLayout.visibility = View.VISIBLE
        (response.weather.isNotEmpty()).let {
            ivWeather.loadImage(response.weather[0].icon)
            tvWeatherType.text = response.weather[0].main
        }
        tvCityName.text = response.name
        tvCurrentTemp.text =
            getString(R.string.temp, WeatherAppUtils.convertFahrenheitToCelsius(response.main.temp))
        tvMinMaxTemp.text =
            getString(
                R.string.min_max_temp, WeatherAppUtils.convertFahrenheitToCelsius(response.main.temp_min),
                WeatherAppUtils.convertFahrenheitToCelsius(response.main.temp_max)
            )
        if(SessionPref.getSharedPref(requireContext()).hasWeatherInfo(response.name)){
            btnAddFav.text = getString(R.string.remove_to_fav)
        }else{
            btnAddFav.text = getString(R.string.add_to_fav)
        }
    }

    /**
     * This method will be called when user click on search button / press keyboard search icon
     */
    private fun searchClicked() {
        val text = etInput.text.toString()
        if (text.trim().isEmpty()) {
            Toast.makeText(requireContext(), R.string.plz_enter_input, Toast.LENGTH_SHORT)
                .show()
        } else {
            fetchData(text)
        }
    }

    /**
     * Fetch data from view model for given input parameter
     */
    fun fetchData(input: String){
        weatherViewModel.fetchWeather(requireContext(), input)
    }

    /**
     * This method will take action when user clicks on Add/Remove Fav button
     */
    private fun addRemoveFavClicked() {
        weatherViewModel.weatherData.value?.let { data ->
            if(btnAddFav.text.toString() == getString(R.string.add_to_fav)){
                SessionPref.getSharedPref(requireContext()).addToMyFavourite(data)
                btnAddFav.text = getString(R.string.remove_to_fav)
            }else{
                SessionPref.getSharedPref(requireContext()).removeWeatherInfo(data.name)
                btnAddFav.text = getString(R.string.add_to_fav)
            }
        }

    }

    /**
     * This method will handle click event of views
     */
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSearch -> searchClicked()
            R.id.btnAddFav -> addRemoveFavClicked()
        }
    }
}