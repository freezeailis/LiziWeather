package com.example.liziweather.ui.place

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ProcessLifecycleInitializer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.liziweather.logic.Repository
import com.example.liziweather.logic.dao.PlaceDao
import com.example.liziweather.logic.model.Place
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlacesViewModel: ViewModel(){
    // 用于观察query的变化
    private val requireLiveData = MutableLiveData<String>()
    // 缓存
    val placesList = ArrayList<Place>()
    // placesLiveDat里面的value更新时执行block里面的内容
    // 同时将返回的liveData转换成可被观察的liveData
    // 返回的liveData每次都在变化所以不可被观察
    // 转化后的places是一个val, 可以被外界观察
    val places: LiveData<Result<List<Place>>> = Transformations.switchMap(requireLiveData){
        Repository.getPlacesInfo(it)
    }
    // 更新placesLiveData里面的value
    fun getPlacesInfo(query: String){
        requireLiveData.value = query
    }


    private val placeQueryLiveData = MutableLiveData<Any?>()

    val placeCacheLiveData = Transformations.switchMap(placeQueryLiveData){
        Repository.getPlaceCache()
    }
    fun hasPlaceCache(): Boolean = Repository.hasPlaceCache()

    fun getPlaceCache(){
        placeQueryLiveData.value = placeQueryLiveData.value
    }

    fun savePlaceCache(place: Place){
        // 不需要真正地改变内部的值就能触发switchMap内部的block
        // 只需要调用了set或者post就可
        Repository.savePlaceCache(place)
    }

}