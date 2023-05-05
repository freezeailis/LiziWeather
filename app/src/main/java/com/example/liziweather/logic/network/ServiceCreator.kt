package com.example.liziweather.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {
    private const val baseUrl = "https://api.caiyunapp.com/"
    private val retrofit: Retrofit = Retrofit
        .Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceCls: Class<T>): T{
        return retrofit.create(serviceCls)
    }

    inline fun<reified T> create(): T{
        return create(T::class.java)
    }
}