package com.dev.ashish.weather.ui

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.dev.ashish.weather.R
import java.util.*

//
// Created by Ashish on 15/03/21.
//

class SplashActivity : BaseActivity() {

    private val SPLASH_TIME_OUT: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_splash)
        setupSplashTimer()
    }

    /**
     * This method will set timer and will launch next screen
     */
   private fun setupSplashTimer() {
        var timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                navigateToNextScreen()
            }
        }, SPLASH_TIME_OUT)
    }

    /**
     * This method will navigate to Home Screen
     */
    private fun navigateToNextScreen() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}