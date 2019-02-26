package com.yanyushkin.kudago.adapters

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.models.City

class CityDataAdapter (private var cities: ArrayList<City>) : RecyclerView.Adapter<CityDataAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.card_view_separate_city, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val city = cities.get(p1)
        p0.textnameCity.text = city.nameInfo
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var cv: CardView
        var textnameCity: TextView

        init {
            cv = v.findViewById(R.id.cvCity)
            textnameCity = v.findViewById(R.id.text_nameCity)
        }
    }
}