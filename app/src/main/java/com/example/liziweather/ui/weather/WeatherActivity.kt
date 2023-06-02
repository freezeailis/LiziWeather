package com.example.liziweather.ui.weather

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.liziweather.R
import com.example.liziweather.databinding.ActivityWeatherBinding
import com.example.liziweather.databinding.SingleDayInfoLineBinding
import com.example.liziweather.logic.model.Location
import com.example.liziweather.logic.model.Place
import com.example.liziweather.makeToast
import kotlin.math.log

class WeatherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeatherBinding
    private val viewModel by lazy {
        ViewModelProvider(this).get(WeatherViewModel::class.java)
    }

    companion object {
        fun startActivity(context: Context, place: Place){
            val intent = Intent(context, WeatherActivity::class.java)
            intent.putExtra("lng", place.location.lng)
            intent.putExtra("lat", place.location.lat)
            intent.putExtra("placeName", place.name)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loc = Location(intent.getDoubleExtra("lng", 0.0),
            intent.getDoubleExtra("lat", 0.0))
        // 请求天气数据
        viewModel.getWeatherInfo(loc)
        // 注册对天气信息变化的回调
        viewModel.weatherInfo.observe(this, Observer{ weatherInfo->
            val realtimeRes = weatherInfo.getOrNull()?.realtimeWeatherResponse
            val dailyRes = weatherInfo.getOrNull()?.dailyTimeWeatherResponse
            if (realtimeRes != null && dailyRes != null) {
                WeatherDescriptionUtils.skycon2desInfo[realtimeRes.realtime.skycon]?.let {
                    binding.sv.setBackgroundResource(it.bg)
                }
                // 更新 currentTemperaturePart
                val currentTemperaturePart = binding.currentTemperaturePart
                val curTemperatureText = "  " + realtimeRes.realtime.temperature.toString() + getString(R.string.temperature_simple_sign)
                val maxMinTemperatureText = WeatherDescriptionUtils.skycon2desInfo[realtimeRes.realtime.skycon]?.des + " " +
                        dailyRes.daily.temperature[0].min.toInt().toString() + getString(R.string.temperature_simple_sign) +
                        dailyRes.daily.temperature[0].max.toInt().toString() + getString(R.string.temperature_simple_sign)
                currentTemperaturePart.currentTemperatureTv.text = curTemperatureText
                currentTemperaturePart.maxMinTemperatureTv.text = maxMinTemperatureText
                // 更新 futureDaysTemperaturePart
                val futureDaysTemperaturePart = binding.futureDaysTemperaturePart
                futureDaysTemperaturePart.futureLl.removeAllViews()

                for (idx in 0 until 3){
                    val viewBinding = SingleDayInfoLineBinding.inflate(layoutInflater, futureDaysTemperaturePart.root, false)
                    WeatherDescriptionUtils.skycon2desInfo[dailyRes.daily.skycon[idx].value]?.let {
                        viewBinding.todayIconIv.setImageResource(it.ic)
                        val des = WeatherDescriptionUtils.idx2dayDes[idx] + it.des
                        viewBinding.todayInfoTv.text = des
                        viewBinding.todayWeatherQualityTv.text = WeatherDescriptionUtils.aqi2des(dailyRes.daily.air_quality.aqi[idx])
                        val temperatureText = dailyRes.daily.temperature[idx].min.toInt().toString() + getString(R.string.temperature_simple_sign) + "/" +
                                dailyRes.daily.temperature[idx].max.toInt().toString() + getString(R.string.temperature_simple_sign)
                        viewBinding.todayMaxMinTemperatureTv.text = temperatureText
                    }
                    futureDaysTemperaturePart.futureLl.addView(viewBinding.root)
                }
                // 更新 lifeIndexPart
                val lifeIndexPart = binding.lifeIndexPart
                lifeIndexPart.caiWashingTv.text = dailyRes.daily.life_index.carWashing[0].desc
                lifeIndexPart.coldRiskTv.text = dailyRes.daily.life_index.coldRisk[0].desc
                lifeIndexPart.dressTv.text = dailyRes.daily.life_index.dressing[0].desc
                lifeIndexPart.ultravioletTv.text = dailyRes.daily.life_index.ultraviolet[0].desc


            } else {
                "No Info got".makeToast()
                Log.d("noInfo", weatherInfo.toString())
            }
        })

    }
}