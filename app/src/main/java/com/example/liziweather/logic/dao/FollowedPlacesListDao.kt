package com.example.liziweather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.example.liziweather.LiziWeatherApplication
import com.example.liziweather.logic.model.Place
import com.example.liziweather.logic.model.PlacesList
import com.google.gson.Gson

object FollowedPlacesListDao {
    private val sharedPreferences = LiziWeatherApplication.context.
                                    getSharedPreferences("LiziWeather", Context.MODE_PRIVATE)

    fun hasCache(): Boolean{
        return sharedPreferences.contains("followedPlacesList")
    }

    fun getCache(): ArrayList<Place> {
        val json = sharedPreferences.getString("followedPlacesList", "")
        val cache = Gson().fromJson(json, PlacesList::class.java)
        val placesList = cache.placesList
        val res = ArrayList<Place>()
        for (c in placesList){
            res.add(c)
        }
        return res
    }

    fun saveCache(placesList: ArrayList<Place>) {
        sharedPreferences.edit {
            val saveList = PlacesList(placesList)
            putString("followedPlacesList", Gson().toJson(saveList))
            apply()
        }
    }
}