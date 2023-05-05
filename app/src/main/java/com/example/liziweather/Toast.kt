package com.example.liziweather

import android.widget.Toast

fun String.makeToast(duration: Int =Toast.LENGTH_SHORT){
    Toast.makeText(LiziWeatherApplication.context, this, duration).show()
}

fun Int.makeToast(duration: Int =Toast.LENGTH_SHORT){
    Toast.makeText(LiziWeatherApplication.context, this, duration).show()
}