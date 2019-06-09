package com.example.a20190418test

import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView


class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {
    var items: ArrayList<WeatherDTO> = ArrayList()

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, i: Int): WeatherAdapter.ViewHolder {
        var itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_weather, parent, false)

        var viewHolder = ViewHolder(itemView)

        return viewHolder
    }

    @NonNull
    override fun onBindViewHolder(viewHolder: WeatherAdapter.ViewHolder, position: Int) {
        var item: WeatherDTO = items.get(position)



        viewHolder.weather_Temp.setText(item.temp.toString())
        viewHolder.weather_Humi.setText(item.humi.toString())
        viewHolder.weather_Pressure.setText(item.temp.toString())
        viewHolder.weather_Weather.setText(item.mainWeather)
        viewHolder.weather_DES.setText(item.descriptionWeather)

    }

    override fun getItemCount(): Int {
        return items.size
    }

//    }

    inner class ViewHolder : RecyclerView.ViewHolder {
        lateinit var ivWeather:ImageView
        lateinit var weather_Temp:TextView
        lateinit var weather_Humi:TextView
        lateinit var weather_Pressure:TextView
        lateinit var weather_Weather:TextView
        lateinit var weather_DES:TextView

        constructor(itemView:View) : super(itemView) {
            ivWeather = itemView.findViewById(R.id.weather_Image)
            weather_Temp = itemView.findViewById(R.id.weather_Temp)
            weather_Humi = itemView.findViewById(R.id.weather_Humi)
            weather_Pressure = itemView.findViewById(R.id.weather_Pressure)
            weather_Weather = itemView.findViewById(R.id.weather_Weather)
            weather_DES = itemView.findViewById(R.id.weather_DES)

        }

    }

}