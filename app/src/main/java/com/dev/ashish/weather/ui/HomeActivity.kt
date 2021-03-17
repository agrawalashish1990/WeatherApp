package com.dev.ashish.weather.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.dev.ashish.weather.R
import com.dev.ashish.weather.fragment.HomeFragment
import com.dev.ashish.weather.fragment.MyFavFragment
import com.dev.ashish.weather.storage.prefs.SessionPref
import com.dev.ashish.weather.utils.WeatherAppUtils
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

//
// Created by Ashish on 15/03/21.
//

class HomeActivity : BaseActivity(),  EasyPermissions.PermissionCallbacks {

    private val TAG = HomeActivity::class.java.name
    private val RC_LOCATION_PERM = 101
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var startLocationUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        showFragment(HomeFragment.getInstance())
    }

    /**
     * Change Theme - Menu Click Event
     */
    private fun menuChangeThemeSelected() {
        val sessionPref = SessionPref.getSharedPref(applicationContext)
        sessionPref.setDarkTheme(!sessionPref.getDarkTheme())
        WeatherAppUtils.setAppTheme(applicationContext)
    }

    /**
     * Get weather info for device location
     */
    private fun menuCurrentLocationSelected() {
        if (!hasLocationPermissions()) {
            checkPermission()
        }else{
           getDeviceLocation()
        }
    }

    /**
     * exit menu click event
     */
    private fun menuExitClicked() {
        finish()
    }

    /**
     * My Fav menu click event
     */
    private fun menuMyFavClicked() {
        showFragment(MyFavFragment.getInstance())
    }

    /**
     * Menu Options Setup
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /**
     * Change Theme Name As per current theme
     */
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            val item = menu.findItem(R.id.action_change_theme)
            if (SessionPref.getSharedPref(this).getDarkTheme()) {
                item.title = getString(R.string.light_mode)
            } else {
                item.title = getString(R.string.dark_mode)
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    /**
     * Menu Item Click Event
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_change_theme -> menuChangeThemeSelected()
            R.id.action_current_location -> menuCurrentLocationSelected()
            R.id.action_exit -> menuExitClicked()
            R.id.action_my_fav -> menuMyFavClicked()
        }
        return true
    }

    /**
     * Load Fragment
     */
    private fun showFragment(newFragment: Fragment) {
        val tag = newFragment::class.java.simpleName
        supportFragmentManager.beginTransaction().replace(R.id.container, newFragment, tag)
            .addToBackStack(tag).commitAllowingStateLoss()
    }

    /**
     * This function will be called when required permissions will be granted
     */
    @SuppressLint("MissingPermission")
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        getDeviceLocation()
    }

    /**
     * Callback - when user accepts/deny permission
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    /**
     * This function will be called when required permissions will be denied
     */
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size)
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    /**
     * Returns whether app has LOCATION permissions or not
     */
    private fun hasLocationPermissions(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    /**
     * This method asks for permissions if not granted
     */

    private fun checkPermission() {
        if (!hasLocationPermissions()) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.rationale_location),
                RC_LOCATION_PERM,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            checkPermission()
        }
    }


    /**
     *  Fetch device last saved location if exists
     *  if location == null then start location update
     */
    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        if (hasLocationPermissions()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                //If location not available then
                if (location == null) {
                    startLocationUpdate = true
                    startLocationUpdateListner()
                } else {
                    startLocationUpdate = false
                    val postalCode = WeatherAppUtils.getPostalCode(applicationContext,location)
                    getWeatherInfo(postalCode)
                }
            }
        }
    }

    /**
     * This method will fetch weather info using HomeFragment
     *
     */
    private fun getWeatherInfo(postalCode: String){
        val fragment = getFragment(HomeFragment::class.java.simpleName)
        fragment?.let {
            //If fragment is not visible then show that screen
            if(!fragment.isVisible){
                supportFragmentManager.popBackStack()
            }
            //Fetch data
            (fragment as HomeFragment).fetchData(postalCode)
        }
    }



    /**
     * This method used if we don't get last location from fused Location provider
     * In this we will setup LOCATION listener to update the device location
     */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdateListner() {
        locationCallback = object : LocationCallback() {
            /**
             * This method is called when device receive location changes
             */
            override fun onLocationResult(locationResult: LocationResult) {
                startLocationUpdate = false
                //Stop location update further because we dont need it
                stopLocationUpdatesListener()

                //Process location
                if (locationResult.locations.isNotEmpty()) {
                    //Get Postal code from current location
                    val postalCode = WeatherAppUtils.getPostalCode(applicationContext,locationResult.locations[0])
                    //get weather info for location
                    getWeatherInfo(postalCode)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            LocationRequest.create(),
            locationCallback, Looper.getMainLooper()
        )
    }

    /**
     * Stop location updates
     */

    private fun stopLocationUpdatesListener() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    override fun onBackPressed() {
       if(supportFragmentManager.backStackEntryCount <=1){
           finish()
       }else{
           supportFragmentManager.popBackStack()
       }
    }


}