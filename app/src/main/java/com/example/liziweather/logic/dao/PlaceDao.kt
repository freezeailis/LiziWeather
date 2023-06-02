package com.example.liziweather.logic.dao

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.example.liziweather.LiziWeatherApplication
import com.example.liziweather.logic.model.Place
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

object PlaceDao {
    private val sharedPreferences = LiziWeatherApplication.context.
                                    getSharedPreferences("LiziWeather", Context.MODE_PRIVATE)

    fun hasPlaceCache(): Boolean{
        return sharedPreferences.contains("place") && sharedPreferences.getString("place", " ") != ""
    }

    fun getPlace(): Place? {
        return Gson().fromJson(sharedPreferences.getString("place", ""), Place::class.java)
    }


    fun savePlace(place: Place) {
        sharedPreferences.edit {
            putString("place", Gson().toJson(place))
            apply()
        }
    }
}