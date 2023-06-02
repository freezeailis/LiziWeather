package com.example.liziweather.logic.model

data class WeatherInfo(val realtimeWeatherResponse: RealtimeWeatherResponse.Result,
                       val dailyTimeWeatherResponse: DailyTimeWeatherResponse.Result)