package com.dev.ashish.weather.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*

//
// Created by Ashish on 15/03/21.
//

open class BaseActivity : AppCompatActivity() {

    /**
     * This method will search fragment in stack by tagName and return
     *
     */
    fun getFragment(tag: String) : Fragment?{
        return supportFragmentManager.findFragmentByTag(tag)
    }

    /**
     * This method will setup toolbar text & back button
     */
    fun setupToolbar( title : String, backButton: Boolean){
        toolbar.title = title
        if(backButton){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }else{
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}