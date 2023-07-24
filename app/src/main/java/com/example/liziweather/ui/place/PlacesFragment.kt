package com.example.liziweather.ui.place

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.liziweather.MainActivity
import com.example.liziweather.databinding.FragmentPlacesBinding
import com.example.liziweather.logic.model.Place
import com.example.liziweather.makeToast
import com.example.liziweather.ui.weather.WeatherActivity


class PlacesFragment : Fragment() {
    var mBinding: FragmentPlacesBinding? = null
    val binding get() = mBinding!!
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
                // 默认使用 cacheAdapter(自己添加的城市)
                val responseAdapter = PlacesItemAdapter(this@PlacesFragment, viewModel.responsePlacesList)
                val cacheAdapter = PlacesItemAdapter(this@PlacesFragment, viewModel.followedPlacesList)
                // 每次点击城市子项都会跳转至 WeatherActivity, 所以又会重置 adapter 到 cacheAdapter
                binding.placesListRv.adapter = cacheAdapter
                Log.d("placesListRv", "change adapter 2 cacheAdapter")
                
                /**
                 *  关注城市缓存
                 *@author aris
                 *@time 2023/6/20 20:49
                */
                // 读取关注城市缓存, 并在未搜索时显示
                if (viewModel.hasPlacesListCache()){
                    viewModel.getPlacesListCache()
                }
                // 读取到关注城市缓存后将其显示出来
                viewModel.followedPlacesLiveData.observe(this@PlacesFragment, Observer { result->
                    val placesList = result.getOrNull()
                    Log.d("followed", "got followed List")
                    if (placesList != null){
                        viewModel.clearFollowedCache()
                        for (p in placesList){
                            viewModel.addPlace2FollowedCache(p)
                            Log.d("followed", p.name)
                        }
                        cacheAdapter.notifyDataSetChanged()
                    }
                })
                
                
                /**
                 *  搜索框
                 *@author aris
                 *@time 2023/6/20 20:49
                */
                // 注册对搜索框的监听
                // 只要发生变化就先去请求一下
                binding.searchTv.addTextChangedListener {
                    viewModel.getPlacesInfo(binding.searchTv.text.toString())
                }
                // 搜索确认后, 切换显示目标, 收起软键盘
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                binding.searchButton.setOnClickListener {
                    binding.placesListRv.adapter = responseAdapter
                    imm.hideSoftInputFromWindow(activity?.window?.decorView?.windowToken, 0)
                }
                // 监听回车
                binding.searchTv.setOnEditorActionListener { v, actionId, event ->
                    if (actionId == EditorInfo.IME_NULL) {
                        if (binding.searchTv.text.isNotEmpty()) {
                            binding.placesListRv.adapter = responseAdapter
                        }
                        imm.hideSoftInputFromWindow(activity?.window?.decorView?.windowToken, 0)
                    }
                    false
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
                        responseAdapter.notifyDataSetChanged()
                    }
                 })
                
                
                /**
                 *  上次显示的城市
                 *@author aris
                 *@time 2023/6/20 20:50
                */
                // 读取城市缓存
                // 如果有缓存并且处在程序刚启动的阶段, 自动跳转到天气的详情界面
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

            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPlacesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}