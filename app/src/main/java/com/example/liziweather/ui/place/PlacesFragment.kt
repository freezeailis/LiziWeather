package com.example.liziweather.ui.place

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.liziweather.MainActivity
import com.example.liziweather.databinding.FragmentPlacesBinding
import com.example.liziweather.makeToast
import com.example.liziweather.ui.weather.WeatherActivity


class PlacesFragment : Fragment() {
    lateinit var binding: FragmentPlacesBinding
    val viewModel by lazy {
        ViewModelProvider(this).get(PlacesViewModel::class.java)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().lifecycle.addObserver(object : DefaultLifecycleObserver{

            // 在其依附的activity的onCreate执行后执行
            override fun onCreate(owner: LifecycleOwner) {
                val layoutManager = LinearLayoutManager(activity!!)
                binding.placesListRv.layoutManager = layoutManager
                val adapter = PlacesItemAdapter(this@PlacesFragment, viewModel.responsePlacesList)
                binding.placesListRv.adapter = adapter
                // 注册对搜索框的监听
                binding.searchTv.addTextChangedListener {
                    viewModel.getPlacesInfo(binding.searchTv.text.toString())
                }


                // 注册对搜索所得到的结果places的监听
                viewModel.responsePlacesLiveData.observe(this@PlacesFragment, Observer { result ->
                    val places = result.getOrNull()
                    if (places == null || binding.searchTv.text.isEmpty()){
                        if (binding.searchTv.text.isNotEmpty()){
                            "No Result Found".makeToast()
                            result.exceptionOrNull()?.printStackTrace()
                        }
                    } else {
                        viewModel.responsePlacesList.clear()
                        viewModel.responsePlacesList.addAll(places)
                        adapter.notifyDataSetChanged()
                    }
                 })

                // 读取城市缓存
                // 只有当该组件被嵌在main activity中, 并且城市缓存不为空, 才会自动跳转
                if(viewModel.hasPlaceCache() && activity is MainActivity){
                    Log.d("cache", "has cache")
                    viewModel.getPlaceCache()
                    Log.d("cache", "after getCache: " + viewModel.placeCacheLiveData.value.toString())
                    viewModel.placeCacheLiveData.observe(this@PlacesFragment, Observer {
                        Log.d("cache", "before if" + viewModel.placeCacheLiveData.value.toString())
                        val place = it.getOrNull()
                        if (place != null){
                            Log.d("cache", "in if" + viewModel.placeCacheLiveData.value.toString())
                            WeatherActivity.startActivity(this@PlacesFragment.requireContext(), place)
                            (activity as MainActivity).finish()
                        }
                    })
                }

//                if(activity is MainActivity){
//                    WeatherActivity.startActivity(this@PlacesFragment.requireContext(), Place("", "--",
//                        "--", Location(100.0, 100.0), ""
//                    ))
//                    (activity as MainActivity).finish()
//                }

            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlacesBinding.inflate(layoutInflater)
        return binding.root
    }
}