package com.example.liziweather.ui.weather

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.inputmethod.InputMethodManager
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.liziweather.R
import com.example.liziweather.databinding.ActivityWeatherBinding
import com.example.liziweather.databinding.SingleDayInfoRowBinding
import com.example.liziweather.logic.model.Location
import com.example.liziweather.logic.model.Place
import com.example.liziweather.logic.model.WeatherInfo
import com.example.liziweather.makeToast
import java.util.*

class WeatherActivity : AppCompatActivity() {
    lateinit var binding: ActivityWeatherBinding
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
        LayoutInflater.from(this).inflate(R.layout.fragment_places, this.findViewById(R.id.drawerLayout), false)
        setContentView(binding.root)

        Log.d("WeatherActivity", "OnCreate")

        // 设置沉浸式
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val windowInsetsControllerCompat = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsControllerCompat.isAppearanceLightNavigationBars = false

        // extract info from intent
        viewModel.location = Location(intent.getDoubleExtra("lng", 0.0),
            intent.getDoubleExtra("lat", 0.0))
        viewModel.placeName = intent.getStringExtra("placeName") ?: ""
        // 注册 drawerLayout 的事件
//        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.drawerLayout.addDrawerListener(object :DrawerLayout.DrawerListener{
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
            }

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })
        binding.navigationPart.navBtn.setOnClickListener { binding.drawerLayout.openDrawer(GravityCompat.START) }

        binding.futureDaysTemperaturePart.fifteenDaysBtn.setOnClickListener {
            FifteenDaysActivity.startActivity(this, viewModel.location)
        }

        // 请求天气数据
        viewModel.getWeatherInfo(viewModel.location)

        // 注册对天气信息变化的回调
        // 在回调中更新天气数据
        viewModel.weatherInfo.observe(this, Observer{ weatherInfoRes->
            val weatherInfo = weatherInfoRes.getOrNull()
            if (weatherInfo != null){
                showWeatherInfo(weatherInfo, viewModel.placeName)
                if(binding.smartRefreshLayout.isRefreshing){
                    binding.smartRefreshLayout.finishRefresh(1000)
                }
            } else {
                "no info got".makeToast()
                Log.d("net", weatherInfoRes.exceptionOrNull()?.toString() ?: "")
                if (binding.smartRefreshLayout.isRefreshing){
                    binding.smartRefreshLayout.finishRefresh(false)
                }
            }
        })

        binding.smartRefreshLayout.setOnRefreshListener {
            refreshPage()
        }

        supportFragmentManager.findFragmentById(R.id.placeFragment)


    }

    private fun showWeatherInfo(weatherInfo: WeatherInfo, placeName: String){
        val realtimeRes = weatherInfo.realtimeWeatherResponse
        val dailyRes = weatherInfo.dailyTimeWeatherResponse
        WeatherDescriptionUtils.skycon2desInfo[realtimeRes.realtime.skycon]?.let {
            binding.backgroundLl.setBackgroundResource(it.bg)
        }
        // 更新 navigationPart
        binding.navigationPart.navTv.text = placeName

        // 更新 currentTemperaturePart
        val currentTemperaturePart = binding.currentTemperaturePart
        val curTemperatureText = realtimeRes.realtime.temperature.toInt().toString()
        val maxMinTemperatureText = WeatherDescriptionUtils.skycon2desInfo[realtimeRes.realtime.skycon]?.des + " " +
                dailyRes.daily.temperature[0].min.toInt().toString() + getString(R.string.temperature_simple_sign) +
                dailyRes.daily.temperature[0].max.toInt().toString() + getString(R.string.temperature_simple_sign)
        currentTemperaturePart.currentTemperatureTv.text = curTemperatureText
        currentTemperaturePart.maxMinTemperatureTv.text = maxMinTemperatureText

        // 更新 futureDaysTemperaturePart
        val futureDaysTemperaturePart = binding.futureDaysTemperaturePart
        futureDaysTemperaturePart.futureLl.removeAllViews()

        // 动态地往里面添加SingleDayInfo
        for (idx in 0 until 3){
            val viewBinding = SingleDayInfoRowBinding.inflate(layoutInflater, futureDaysTemperaturePart.root, false)
            WeatherDescriptionUtils.skycon2desInfo[dailyRes.daily.skycon[idx].value]?.let {
                viewBinding.todayIconIv.setImageResource(it.ic)
                val des = DateDescriptionUtils.idx2dayDes[idx] + "  " + it.des
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
    }

    fun refreshPage(location: Location?=null, placeName: String?=null){
        placeName?.let {
            viewModel.placeName = placeName
        }
        if (location != null){
            viewModel.location = location
            viewModel.getWeatherInfo(location)
        } else {
            viewModel.getWeatherInfo(viewModel.location)
        }
    }
}