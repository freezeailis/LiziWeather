package com.example.liziweather.logic.network

import com.example.liziweather.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object LiziWeatherNetwork {
    private val placeService = ServiceCreator.create<PlacesService>()

    suspend fun getPlacesInfo(query: String): PlaceResponse {
        return placeService.getPlacesInfo(query).await()
    }
    
    
    /**
     * 该函数借助内部的suspendCoroutine方法
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