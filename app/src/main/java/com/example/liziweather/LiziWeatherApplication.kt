package com.example.liziweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class LiziWeatherApplication: Application() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        const val token = "DniWT99VcKAqffYX"
        const val language = "zh_CN"
    }


    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}