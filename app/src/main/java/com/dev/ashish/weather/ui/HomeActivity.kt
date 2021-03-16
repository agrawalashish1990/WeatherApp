package com.dev.ashish.weather.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.dev.ashish.weather.R
import com.dev.ashish.weather.fragment.MyFavFragment
import com.dev.ashish.weather.fragment.MyHomeFragment
import com.google.android.gms.location.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*


//
// Created by Ashish on 15/03/21.
//

class HomeActivity : BaseActivity(),  EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

    private val TAG = HomeActivity::class.java.name
    private val RC_LOCATION_PERM = 101
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var startLocationUpdate = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showFragment(MyHomeFragment.getInstance())
    }

    private fun menuDarkModeSelected() {
        val isNightTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (isNightTheme) {
            Configuration.UI_MODE_NIGHT_YES ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Configuration.UI_MODE_NIGHT_NO ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private fun menuCurrentLocationSelected() {
        if (!hasLocationPermissions()) {
            checkPermission()
        }else{
           getDeviceLocation()
        }
    }

    private fun menuExitClicked() {
        finish()
    }

    private fun menuMyFavClicked() {
        showFragment(MyFavFragment.getInstance())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_dark_mode -> menuDarkModeSelected()
            R.id.action_current_location -> menuCurrentLocationSelected()
            R.id.action_exit -> menuExitClicked()
            R.id.action_my_fav -> menuMyFavClicked()
        }
        return true
    }


    fun showFragment(newFragment: Fragment) {
        var tag = newFragment::class.java.simpleName
        supportFragmentManager.beginTransaction().replace(R.id.container, newFragment, tag)
            .addToBackStack(tag).commitAllowingStateLoss()
    }

    @SuppressLint("MissingPermission")
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        getDeviceLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size)
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    fun hasLocationPermissions(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    fun checkPermission() {
        if (!hasLocationPermissions()) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.rationale_location),
                RC_LOCATION_PERM,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    @SuppressLint("StringFormatMatches")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            checkPermission()
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {
        Log.d(TAG, "onRationaleAccepted:" + requestCode)
    }

    override fun onRationaleDenied(requestCode: Int) {
        Log.d(TAG, "onRationaleDenied:" + requestCode)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    fun setUpLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                startLocationUpdate = false
                stopLocationUpdatesListener()
                if (!locationResult.locations.isEmpty()) {
                    var location = locationResult.locations[0]
                    var postalCode = getPostalCode(location)
                    getWeatherInfo(postalCode)
                }
            }

            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getDeviceLocation() {
        if (hasLocationPermissions()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location == null) {
                    startLocationUpdate = true
                    setUpLocationCallback()
                    startLocationUpdateListner()
                } else {
                    startLocationUpdate = false
                    var postalCode = getPostalCode(location)
                    getWeatherInfo(postalCode)
                }
            }
        }
    }

    fun getWeatherInfo(postalCode : String){
        var fragment = getFragment(MyHomeFragment::class.java.simpleName)
        fragment?.let {
            if(!fragment.isVisible){
                supportFragmentManager.popBackStack()
            }
            (fragment as MyHomeFragment).fetchData(postalCode)
        }
    }

    fun getFragment(tag : String) : Fragment?{
        return supportFragmentManager.findFragmentByTag(tag)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdateListner() {
        fusedLocationClient?.requestLocationUpdates(
            LocationRequest.create(),
            locationCallback, Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdatesListener() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    fun getPostalCode(location: Location?): String {
        location?:return ""
        location?.let {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses: List<Address> =
                geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addresses?.isEmpty()) {
                val postalCode: String = addresses[0].postalCode
                return postalCode;
            }
        }
        return ""
    }
}