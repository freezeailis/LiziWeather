package com.example.liziweather.logic

import androidx.lifecycle.liveData
import com.example.liziweather.logic.dao.PrePlaceDao
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

    // 下述三个函数用于 上次显示城市 的缓存的读取和保存
    fun getPlaceCache() = liveDataWithExceptionHandle<Place>(Dispatchers.IO){
        val place = PrePlaceDao.getCache()
        if (place == null){
            Result.failure(Exception("return value is null"))
        } else {
            Result.success(place)
        }
    }

    fun savePlaceCache(place: Place){
        PrePlaceDao.saveCache(place)
    }

    fun hasPlaceCache(): Boolean = PrePlaceDao.hasCache()

    // 下述三个函数用于 关注城市(不止一个) 的缓存的读取和保存


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