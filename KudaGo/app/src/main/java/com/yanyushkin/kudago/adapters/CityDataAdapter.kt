package com.yanyushkin.kudago.adapters

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.models.City
import com.yanyushkin.kudago.utils.OnClickListener

class CityDataAdapter(private var cities: ArrayList<City>, private val clickListener: OnClickListener, private val currentCity: String) :
    RecyclerView.Adapter<CityDataAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.card_view_separate_city, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val city = cities[p1]
        p0.nameCity.text = city.name

        if (city.shortEnglishName == currentCity) {
            p0.selectedCity.visibility = View.VISIBLE
        }
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var cv: CardView
        var nameCity: TextView
        var selectedCity: AppCompatImageView

        init {
            cv = v.findViewById(R.id.cvCity)
            nameCity = v.findViewById(R.id.nameCity)
            selectedCity = v.findViewById(R.id.selectedCity)

            v.setOnClickListener {
                clickListener.onCardViewClick(adapterPosition)
            }
        }
    }
}