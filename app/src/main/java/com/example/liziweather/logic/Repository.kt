package com.example.liziweather.logic

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.liziweather.logic.dao.PlaceDao
import com.example.liziweather.logic.model.Place
import com.example.liziweather.logic.model.WeatherInfo
import com.example.liziweather.logic.network.LiziWeatherNetwork
import kotlinx.coroutines.*

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
    fun getPlacesInfo(query: String) = liveDataWithExceptionHandle<List<Place>>(Dispatchers.IO){
       val res = LiziWeatherNetwork.getPlacesInfo(query)
       if (res.status == "ok"){
           Result.success(res.places)
       } else {
           Result.failure(RuntimeException("response status is ${res.status}"))
       }
   }


    fun getWeatherInfo(longitude: Double, latitude: Double, dailySteps: Int = 3) =
        liveDataWithExceptionHandle<WeatherInfo>(Dispatchers.IO){
            coroutineScope {
                val realTimeDeferred = async { LiziWeatherNetwork.getRealTimeWeather(longitude, latitude) }
                val dailyTimeDeferred = async { LiziWeatherNetwork.getDailyTimeWeather(longitude, latitude, dailySteps) }
                val realTimeWeatherResponse = realTimeDeferred.await()
                val dailyTimeWeatherResponse = dailyTimeDeferred.await()

                if (realTimeWeatherResponse.status == "ok" && dailyTimeWeatherResponse.status == "ok"){
                    Result.success(WeatherInfo(realTimeWeatherResponse.result, dailyTimeWeatherResponse.result))
                } else {
                    Result.failure(Exception("realTime response status is ${realTimeWeatherResponse.status} and " +
                            "dailyTime response status is ${dailyTimeWeatherResponse.status}"))
                }
            }
    }

    fun getPlaceCache() = liveDataWithExceptionHandle<Place>(Dispatchers.IO){
        val place = PlaceDao.getPlace()
        if (place == null){
            Result.failure(Exception("return value is null"))
        } else {
            Result.success(place)
        }
    }

    fun savePlaceCache(place: Place){
        PlaceDao.savePlace(place)
    }

    fun hasPlaceCache(): Boolean = PlaceDao.hasPlaceCache()

    private fun <T> liveDataWithExceptionHandle(dispatcher: CoroutineDispatcher, block: suspend () -> Result<T>) =
        liveData<Result<T>>(dispatcher){
            val res = try {
                block()
            } catch (e: Exception) {
                Result.failure(e)
            }
            emit(res)
        }

}