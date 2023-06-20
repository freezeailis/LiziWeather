package com.example.liziweather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.example.liziweather.LiziWeatherApplication
import com.example.liziweather.logic.model.Place
import com.google.gson.Gson

object FollowedPlacesListDao {
    private val sharedPreferences = LiziWeatherApplication.context.
                                    getSharedPreferences("LiziWeather", Context.MODE_PRIVATE)

    fun hasCache(): Boolean{
        return sharedPreferences.contains("followedPlacesList")
    }

    fun getCache(): ArrayList<Place>{
        val cache = Gson().fromJson(sharedPreferences.getString("followedPlacesList", ""), ArrayList::class.java)
        val res = ArrayList<Place>()
        for (c in cache){
            res.add(c as Place)
        }
        return res
    }

    fun saveCache(placesList: ArrayList<Place>) {
        sharedPreferences.edit {
            putString("followedPlacesList", Gson().toJson(placesList))
            apply()
        }
    }
}