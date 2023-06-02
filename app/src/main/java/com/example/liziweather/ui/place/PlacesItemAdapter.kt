package com.example.liziweather.ui.place

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.liziweather.R
import com.example.liziweather.logic.model.Place
import com.example.liziweather.ui.weather.WeatherActivity

class PlacesItemAdapter(private val fragment: PlacesFragment, private val placesList: List<Place>): Adapter<PlacesItemAdapter.ViewHolder>() {
    lateinit var parent: ViewGroup

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val placeNameTextView: TextView = itemView.findViewById(R.id.placeName)
        val placeInfoTextView: TextView = itemView.findViewById(R.id.placeInfo)
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
        holder.itemView.setOnClickListener {
            fragment.viewModel.savePlaceCache(place)
            WeatherActivity.startActivity(parent.context, place)
            fragment.activity?.finish()
        }
    }

    override fun getItemCount(): Int {
        return placesList.size

    }
}