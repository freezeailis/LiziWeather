package com.example.liziweather.logic.network

import com.example.liziweather.LiziWeatherApplication
import com.example.liziweather.logic.model.DailyTimeWeatherResponse
import com.example.liziweather.logic.model.RealtimeWeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherService {
    /**
     * 根据经纬度获取所在地的天气信息
     *@author aris
     *@time 2023/5/6 17:35
    */
    @GET("v2.6/${LiziWeatherApplication.token}/{longitude},{latitude}/realtime.json")
    fun getRealTimeWeather(@Path("longitude") longitude: Double,
                           @Path("latitude") latitude: Double): Call<RealtimeWeatherResponse>

    @GET("v2.6/${LiziWeatherApplication.token}/{longitude},{latitude}/daily")
    fun getDailyWeather(@Path("longitude") longitude: Double, @Path("latitude") latitude: Double,
                        @Query("dailysteps") dailySteps: Int): Call<DailyTimeWeatherResponse>
}