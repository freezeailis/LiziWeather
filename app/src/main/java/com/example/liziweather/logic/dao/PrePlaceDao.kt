package com.example.liziweather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.example.liziweather.LiziWeatherApplication
import com.example.liziweather.logic.model.Place
import com.google.gson.Gson

object PrePlaceDao {
    private val sharedPreferences = LiziWeatherApplication.context.
                                    getSharedPreferences("LiziWeather", Context.MODE_PRIVATE)

    fun hasCache(): Boolean{
        return sharedPreferences.contains("place") && sharedPreferences.getString("place", " ") != ""
    }


    // 借助 Gson 完成 对象 和 JSON之间的转换, 实际存取只需要存取 Json 格式的字符串就可以了
    fun getCache(): Place? {
        return Gson().fromJson(sharedPreferences.getString("place", ""), Place::class.java)
    }


    fun saveCache(place: Place) {
        sharedPreferences.edit {
            putString("place", Gson().toJson(place))
            apply()
        }
    }
}