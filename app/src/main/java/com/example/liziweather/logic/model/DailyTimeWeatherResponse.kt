package com.example.liziweather.logic.model

data class DailyTimeWeatherResponse(
    val api_status: String,
    val api_version: String,
    val lang: String,
    val location: List<Double>,
    val result: Result,
    val server_time: Int,
    val status: String,
    val timezone: String,
    val tzshift: Int,
    val unit: String
) {
    data class Result(
        val daily: Daily,
        val primary: Int
    )

    data class Daily(
        val air_quality: AirQuality,
        val astro: List<Astro>,
        val cloudrate: List<Cloudrate>,
        val dswrf: List<Dswrf>,
        val humidity: List<Humidity>,
        val life_index: LifeIndex,
        val precipitation: List<Precipitation>,
        val precipitation_08h_20h: List<Precipitation>,
        val precipitation_20h_32h: List<Precipitation>,
        val pressure: List<Pressure>,
        val skycon: List<Skycon>,
        val skycon_08h_20h: List<Skycon>,
        val skycon_20h_32h: List<Skycon>,
        val status: String,
        val temperature: List<Temperature>,
        val temperature_08h_20h: List<Temperature>,
        val temperature_20h_32h: List<Temperature>,
        val visibility: List<Visibility>,
        val wind: List<Wind>,
        val wind_08h_20h: List<Wind>,
        val wind_20h_32h: List<Wind>
    )

    data class AirQuality(
        val aqi: List<Aqi>,
        val pm25: List<Pm25>
    )

    data class Astro(
        val date: String,
        val sunrise: Time,
        val sunset: Time
    )

    data class Cloudrate(
        val avg: Double,
        val date: String,
        val max: Double,
        val min: Double
    )

    data class Dswrf(
        val avg: Double,
        val date: String,
        val max: Double,
        val min: Double
    )

    data class Humidity(
        val avg: Double,
        val date: String,
        val max: Double,
        val min: Double
    )

    data class LifeIndex(
        val carWashing: List<LifeIndexInfo>,
        val coldRisk: List<LifeIndexInfo>,
        val comfort: List<LifeIndexInfo>,
        val dressing: List<LifeIndexInfo>,
        val ultraviolet: List<LifeIndexInfo>
    )

    data class Precipitation(
        val avg: Double,
        val date: String,
        val max: Double,
        val min: Double,
        val probability: Int
    )

    data class Pressure(
        val avg: Double,
        val date: String,
        val max: Double,
        val min: Double
    )

    data class Skycon(
        val date: String,
        val value: String
    )


    data class Temperature(
        val avg: Double,
        val date: String,
        val max: Double,
        val min: Double
    )


    data class Visibility(
        val avg: Double,
        val date: String,
        val max: Double,
        val min: Double
    )

    data class Wind(
        val avg: WindInfo,
        val date: String,
        val max: WindInfo,
        val min: WindInfo
    )

    data class Aqi(
        val avg: AqiDes,
        val date: String,
        val max: AqiDes,
        val min: AqiDes
    )

    data class Pm25(
        val avg: Int,
        val date: String,
        val max: Int,
        val min: Int
    )

    data class AqiDes(
        val chn: Int,
        val usa: Int
    )

    data class Time(
        val time: String
    )

    data class LifeIndexInfo(
        val date: String,
        val desc: String,
        val index: String
    )

    data class WindInfo(
        val direction: Double,
        val speed: Double
    )
}

