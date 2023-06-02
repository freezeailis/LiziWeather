package com.example.liziweather.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {
    private const val baseUrl = "https://api.caiyunapp.com/"
    // Retrofit对象可以全局共用
    private val retrofit: Retrofit = Retrofit
        .Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceCls: Class<T>): T{
        return retrofit.create(serviceCls)
    }
    
    /**
     * 基于公用的Retrofit
     * 以泛型的方式创建一个我们需要的Service
     *@author aris
     *@time 2023/5/6 17:26
    */
    inline fun<reified T> create(): T{
        return create(T::class.java)
    }


}