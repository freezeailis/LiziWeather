package com.example.liziweather.logic.network

import com.example.liziweather.LiziWeatherApplication
import com.example.liziweather.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface PlacesService {
    /**
     * 根据地区名称获取包含经纬度的地区消息
     *@author aris
     *@time 2023/4/25 21:16
    */
    @GET("v2/place?token=${LiziWeatherApplication.token}&lang=${LiziWeatherApplication.language}")
    fun getPlacesInfo(@Query("query") query: String): Call<PlaceResponse>

}
