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

    val SPLASH_TIME_OUT: Long = 3000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_splash)
        setupSplashTimer()
    }

    fun setupSplashTimer() {
        var timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                navigateToNextScreen()
                timer.cancel()
            }
        }, SPLASH_TIME_OUT, SPLASH_TIME_OUT)
    }

    fun navigateToNextScreen() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}