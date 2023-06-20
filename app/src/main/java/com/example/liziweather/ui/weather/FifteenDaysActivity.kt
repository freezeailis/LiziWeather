package com.example.liziweather.ui.weather

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.liziweather.R
import com.example.liziweather.databinding.ActivityFifteenDaysBinding
import com.example.liziweather.databinding.SingleDayInfoColumnBinding
import com.example.liziweather.logic.model.DailyTimeWeatherResponse
import com.example.liziweather.logic.model.Location
import com.example.liziweather.logic.model.Place
import com.example.liziweather.makeToast

class FifteenDaysActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFifteenDaysBinding
    val viewModel by lazy { ViewModelProvider(this).get(FifteenDayViewModel::class.java) }
    val DayLimit = 15

    companion object {
        fun startActivity(context: Context, location: Location){
            val intent = Intent(context, FifteenDaysActivity::class.java)
            intent.putExtra("lng", location.lng)
            intent.putExtra("lat", location.lat)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFifteenDaysBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 设置沉浸式
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val windowInsetsControllerCompat = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsControllerCompat.isAppearanceLightStatusBars = true

        // extra info from intent
        val lng = intent.getDoubleExtra("lng", 0.0)
        val lat = intent.getDoubleExtra("lat", 0.0)
        val location = Location(lng, lat)
        // 请求15日天气
        viewModel.getFifteenDaysInfo(location)

        // 注册对请求结果的回调
        viewModel.fifteenDaysLiveData.observe(this, Observer { result ->
            val fifteenDaysRes = result.getOrNull()
            if (fifteenDaysRes != null){
                val temperatures = fifteenDaysRes.dailyTimeWeatherResponse.daily.temperature.toTypedArray()
                val skycons = fifteenDaysRes.dailyTimeWeatherResponse.daily.skycon.toTypedArray()
                val maxTemperatures = IntArray(DayLimit){ temperatures[it].max.toInt() }
                val minTemperatures = IntArray(DayLimit){ temperatures[it].min.toInt() }
                binding.temperaturesPlotView.minTemperatures = minTemperatures
                binding.temperaturesPlotView.maxTemperatures = maxTemperatures
                binding.temperaturesPlotView.invalidate()
                for(idx in 0 until DayLimit){
                    val subBinding = SingleDayInfoColumnBinding.inflate(layoutInflater, binding.root, false)
                    val dateString = temperatures[idx].date

                    // 前三天用今天, 明天, 后天描述, 之后的天数显示星期
                    val weekDes = if (idx <= 2) DateDescriptionUtils.idx2dayDes[idx] else DateDescriptionUtils.dateString2weekDes(dateString)
                    // 日期的中文描述
                    val dateDes = DateDescriptionUtils.dateString2dateDes(dateString)
                    // 天气的简洁描述
                    val skycon = WeatherDescriptionUtils.skycon2desInfo[skycons[idx].value]

                    subBinding.chineseDesTv.text = dateDes
                    subBinding.weekDesTv.text = weekDes
                    skycon?.run {
                        subBinding.weatherIconIv.setImageResource(this.ic)
                        subBinding.weatherDesTv.text = this.des
                    }
                    if (idx == 0){
                        subBinding.dayInfoColumnMcv.setCardBackgroundColor(getColor(R.color.emphasize))
                    }
                    // 添加 view
                    binding.weatherDesColumnLl.addView(subBinding.root)
                }
            } else {
                "no info got".makeToast()
            }
         })

//        val maxTemperatures = intArrayOf(25, 26, 31, 32, 30, 26, 24, 26, 30, 33, 33, 31, 31, 31, 23, 22)
//        val minTemperatures = IntArray(maxTemperatures.size){maxTemperatures[it] - it}
//
//        binding.temperaturesPlotView.minTemperatures = minTemperatures
//        binding.temperaturesPlotView.maxTemperatures = maxTemperatures

    }
}