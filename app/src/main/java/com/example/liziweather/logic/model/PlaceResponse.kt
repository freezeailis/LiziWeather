package com.example.liziweather.logic.model

import com.google.gson.annotations.SerializedName

data class PlaceResponse(val status: String, val query: String, val places: List<Place>)

/**
 *位置信息的Gson解析类
 *@author aris
 *@time 2023/4/26 19:12
*/
data class Place(val id: String, val name: String,
                 @SerializedName("formatted_address") val address: String,
                 val location: Location, @SerializedName("place_id") val placeId: String)

data class Location(val lat: Double, val lng: Double)