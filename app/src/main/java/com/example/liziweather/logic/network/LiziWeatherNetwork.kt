package com.example.liziweather.logic.network

import com.example.liziweather.logic.model.DailyTimeWeatherResponse
import com.example.liziweather.logic.model.PlaceResponse
import com.example.liziweather.logic.model.RealtimeWeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object LiziWeatherNetwork {
    private val placeService = ServiceCreator.create<PlacesService>()

    private val weatherService = ServiceCreator.create<WeatherService>()

    suspend fun getPlacesInfo(query: String): PlaceResponse {
        // 先返回Call<T>, 随后调用await内的suspendCoroutine, 将响应返回到这里
        return placeService.getPlacesInfo(query).await()
    }

    suspend fun getRealTimeWeather(longitude: Double, latitude: Double): RealtimeWeatherResponse{
        return weatherService.getRealTimeWeather(longitude, latitude).await()
    }

    suspend fun getDailyTimeWeather(longitude: Double, latitude: Double, step: Int): DailyTimeWeatherResponse{
        return weatherService.getDailyWeather(longitude, latitude, step).await()
    }
    
    
    /**
     * await()函数借助内部的suspendCoroutine方法
     * 可以将回调函数形式的异步代码风格转化为同步代码风格
     *@author aris
     *@time 2023/4/26 21:28
    */
    private suspend fun<T> Call<T>.await(): T {
        return suspendCoroutine {
            enqueue(object: Callback<T>{
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) {
                        it.resume(body)
                    } else {
                        it.resumeWithException(RuntimeException("response body is empty"))
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    it.resumeWithException(t)
                }
            })
        }
    }
}