package com.example.liziweather.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.liziweather.logic.Repository
import com.example.liziweather.logic.model.Location

class WeatherViewModel: ViewModel() {
    // 用于观察location的变化
    private val locationLiveData = MutableLiveData<Location>()
    var placeName = ""
    var location: Location = Location(0.0, 0.0)

    /**
     *  根据 getWeatherInfo 传入的 location 所对应的经纬度坐标请求当日天气信息
     *@author aris
     *@time 2023/6/20 15:10
    */
    val weatherInfo = Transformations.switchMap(locationLiveData){
        Repository.getWeatherInfo(it.lng, it.lat)
    }

    fun getWeatherInfo(location: Location){
        locationLiveData.value = Location(location.lng, location.lat)
    }

}