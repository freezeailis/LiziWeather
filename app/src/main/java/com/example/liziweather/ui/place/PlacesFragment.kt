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
import com.example.liziweather.databinding.FragmentPlacesBinding
import com.example.liziweather.makeToast
import com.example.liziweather.ui.weather.WeatherActivity


class PlacesFragment : Fragment() {
    lateinit var binding: FragmentPlacesBinding
    val viewModel by lazy {
        ViewModelProvider(this).get(PlacesViewModel::class.java)
    }
    var searchState = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().lifecycle.addObserver(object : DefaultLifecycleObserver{
            // 在其依附的activity的onCreate执行后执行
            override fun onCreate(owner: LifecycleOwner) {
                val layoutManager = LinearLayoutManager(activity!!)
                binding.recyclerView.layoutManager = layoutManager
                val adapter = PlacesItemAdapter(this@PlacesFragment, viewModel.placesList)
                binding.recyclerView.adapter = adapter

                // 注册对搜索框的监听
                binding.searchTextView.addTextChangedListener {
                    if (binding.searchTextView.text.isEmpty()){
                        binding.recyclerView.visibility = View.GONE
                        binding.bgImageView.visibility = View.VISIBLE
                    } else {
                        viewModel.getPlacesInfo(binding.searchTextView.text.toString())
                    }
                }

                // 注册对搜索所得到的结果places的监听
                viewModel.places.observe(this@PlacesFragment, Observer { result ->
                    val places = result.getOrNull()
                    if (places == null || binding.searchTextView.text.isEmpty()){
                        if (binding.searchTextView.text.isNotEmpty()){
                            "No Result Found".makeToast()
                            searchState = false
                            result.exceptionOrNull()?.printStackTrace()
                        }
                        binding.bgImageView.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    } else {
                        searchState = true
                        viewModel.placesList.clear()
                        viewModel.placesList.addAll(places)
                        adapter.notifyDataSetChanged()
                    }
                 })
                // 注册对搜索按钮的监听
                binding.searchButton.setOnClickListener {
                    if (binding.searchTextView.text.isNotEmpty() && searchState){
                        binding.bgImageView.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                    }

                }

                // 读取城市缓存
                if(viewModel.hasPlaceCache()){
                    Log.d("cache", "has cache")
                    viewModel.getPlaceCache()
                    Log.d("cache", "after getCache: " + viewModel.placeCacheLiveData.value.toString())
                    viewModel.placeCacheLiveData.observe(this@PlacesFragment, Observer {
                        Log.d("cache", "before if" + viewModel.placeCacheLiveData.value.toString())
                        val place = it.getOrNull()
                        if (place != null){
                            Log.d("cache", "in if" + viewModel.placeCacheLiveData.value.toString())
                            WeatherActivity.startActivity(this@PlacesFragment.requireContext(),
                                place)
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
        binding = FragmentPlacesBinding.inflate(layoutInflater)
        return binding.root
    }
}