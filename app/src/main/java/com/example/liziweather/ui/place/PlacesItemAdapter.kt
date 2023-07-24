package com.example.liziweather.ui.place

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.liziweather.MainActivity
import com.example.liziweather.R
import com.example.liziweather.logic.model.Place
import com.example.liziweather.ui.weather.WeatherActivity
import com.google.android.material.card.MaterialCardView

class PlacesItemAdapter(private val fragment: PlacesFragment, private val placesList: List<Place>): Adapter<PlacesItemAdapter.ViewHolder>() {
    lateinit var parent: ViewGroup

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val placeNameTextView: TextView = itemView.findViewById(R.id.placeName)
        val placeInfoTextView: TextView = itemView.findViewById(R.id.placeInfo)
        val addBtnContainer: MaterialCardView = itemView.findViewById(R.id.addBtnContainer_mvc)
        val removeBtnContainer: MaterialCardView = itemView.findViewById(R.id.removeBtnContainer_mcv)
        val addBtn: Button = itemView.findViewById(R.id.addToFollowed_btn)
        val removeBtn: Button = itemView.findViewById(R.id.removeFromFollowed_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.places_item, parent, false)
        this.parent = parent
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.placeNameTextView.text = placesList[position].name
        holder.placeInfoTextView.text = placesList[position].address
        val place = placesList[holder.absoluteAdapterPosition]
        val viewModel = fragment.viewModel


        // 如果已经在关注列表中, 只显示移除按钮
        // 否则只显示增加按钮
        if (viewModel.ifInFollowedList(place)){
            holder.addBtnContainer.visibility = View.GONE
            holder.removeBtnContainer.visibility = View.VISIBLE
        } else {
            holder.addBtnContainer.visibility = View.VISIBLE
            holder.removeBtnContainer.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            // 当点选目标城市的时候
            // 跳转至目标城市并保存进 placeCache 和 placeListCache
        // placeCache

            viewModel.savePlaceCache(place)
        // placesListCache
            viewModel.addPlace2FollowedCache(place)
            viewModel.savePlaceCacheList()

            // 点击后根据点击的城市跳转|刷新天气页面(singleTask 模式)
            // 同时收起 Drawer
            WeatherActivity.startActivity(parent.context, place)

            // 根据依附的 activity 的不同使用不同的处理逻辑
            val weatherActivity = fragment.activity as? WeatherActivity
            weatherActivity?.apply{
                refreshPage(place.location, place.name)
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }

            val mainActivity = fragment.activity as? MainActivity
            mainActivity?.apply {

            }

//            fragment.activity?.
        }
    }

    override fun getItemCount(): Int {
        return placesList.size

    }
}