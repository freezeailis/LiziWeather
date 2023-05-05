package com.example.liziweather.logic

import androidx.lifecycle.liveData
import com.example.liziweather.logic.model.Place
import com.example.liziweather.logic.network.LiziWeatherNetwork
import kotlinx.coroutines.Dispatchers

/**
 * 仓库决定本地缓存和网络请求的选择
 * 同时在这里也做了一次线程转换[Dispatchers.IO]
 *@author aris
 *@time 2023/4/27 20:48
 */
object Repository {
   /**
    * 返回一个LiveData, 同时用emit通知这个LiveData的变化
    *@author aris
    *@time 2023/4/27 21:14
   */
    fun getPlacesInfo(query: String) = liveData<Result<List<Place>>>(Dispatchers.IO){
        val res = try {
            val response = LiziWeatherNetwork.getPlacesInfo(query)
            if (response.status == "ok") {
                val place = response.places
                Result.success(place)
            } else {
                Result.failure(RuntimeException("Response status is ${response.status}"))
            }
        } catch (e: java.lang.Exception){
            Result.failure(e)
        }
        emit(res)
    }

}