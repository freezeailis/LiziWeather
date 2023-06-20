package com.example.liziweather.ui.place

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.liziweather.logic.Repository
import com.example.liziweather.logic.model.Place

class PlacesViewModel: ViewModel(){

    // 缓存
    val responsePlacesList = ArrayList<Place>()
    // 根据 query 的变化请求该关键字对应的城市信息
    private val keywordQueryLiveData = MutableLiveData<String>()
    // placesLiveDat里面的value更新时执行block里面的内容
    // 同时将返回的liveData转换成可被观察的liveData
    // 返回的liveData每次都在变化所以不可被观察
    // 转化后的places是一个val, 可以被外界观察
    /**
     *  根据 getPlacesInfo 传入的关键字信息, 请求该关键字可能对应的城市 名称 / 经纬度坐标
     *@author aris
     *@time 2023/6/20 15:15
    */
    val responsePlacesLiveData: LiveData<Result<List<Place>>> = Transformations.switchMap(keywordQueryLiveData){
        Repository.getPlacesInfo(it)
    }
    // 更新placesLiveData里面的value
    fun getPlacesInfo(query: String){
        keywordQueryLiveData.value = query
    }


    // 请求上次显示城市的缓存
    private val placeQueryLiveData = MutableLiveData<Any?>()
    /**
     *  请求单独一个城市(上次显示城市)的信息
     *@author aris
     *@time 2023/6/20 15:18
    */
    val placeCacheLiveData = Transformations.switchMap(placeQueryLiveData){
        Repository.getPlaceCache()
    }
    fun hasPlaceCache(): Boolean = Repository.hasPlaceCache()

    fun getPlaceCache(){
        // 不需要真正地改变内部的值就能触发switchMap内部的block
        // 只需要调用了set或者post就可
        placeQueryLiveData.value = placeQueryLiveData.value
    }

    fun savePlaceCache(place: Place){
        Repository.savePlaceCache(place)
    }


    // 缓存
    val followedPlacesList = ArrayList<Place>()
    // 请求 关注城市列表
    private val placesListQueryLiveData = MutableLiveData<Any?>()

    val followedPlacesLiveData: LiveData<Result<List<Place>>> = Transformations.switchMap(placesListQueryLiveData){

    }
    fun getPlacesListCache(){
        placesListQueryLiveData.value = placesListQueryLiveData.value
    }

    fun savePlaceCacheList(){

    }

}