package com.example.liziweather.ui.weather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.liziweather.logic.Repository
import com.example.liziweather.logic.model.Location

class FifteenDayViewModel: ViewModel() {
    private val queryLiveData = MutableLiveData<Location>()


    /**
     *  根据 getFifteenDaysInfo 传入的 location 其中的经纬度坐标请求该坐标下的 15 日天气变化情况
     *@author aris
     *@time 2023/6/20 15:11
    */
    val fifteenDaysLiveData = Transformations.switchMap(queryLiveData){
        Repository.getWeatherInfo(it.lng, it.lat, 15)
    }

    fun getFifteenDaysInfo(location: Location){
        queryLiveData.value = location
    }
}