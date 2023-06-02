package com.example.liziweather.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.liziweather.logic.Repository
import com.example.liziweather.logic.model.Location
import java.security.KeyStore.LoadStoreParameter
import kotlin.math.ln

class WeatherViewModel: ViewModel() {
    var location: Location = Location(0.0, 0.0)
    var placeName: String = ""

    // 用于观察location的变化
    private val locationLiveData = MutableLiveData<Location>()

    val weatherInfo = Transformations.switchMap(locationLiveData){
        Repository.getWeatherInfo(it.lng, it.lat)
    }

    fun getWeatherInfo(location: Location){
        locationLiveData.value = Location(location.lng, location.lat)
    }

}