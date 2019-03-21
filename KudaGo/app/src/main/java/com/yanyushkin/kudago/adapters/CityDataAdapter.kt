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
import kotlinx.android.synthetic.main.card_view_separate_city.view.*

class CityDataAdapter(private var cities: ArrayList<City>, private val clickListener: OnClickListener, private val currentCity: String) :
    RecyclerView.Adapter<CityDataAdapter.ViewHolder>() {

    override fun getItemCount() = cities.size

    /*init of ViewHolder*/
    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.card_view_separate_city, viewGroup, false))
    }

    /*full data for each element of RV*/
    override fun onBindViewHolder(viewHolder: ViewHolder, poistion: Int) {
        val city = cities[poistion]
        viewHolder.bind(city)
    }

    fun setItems(_cities: ArrayList<City>) {
        cities = _cities
        notifyDataSetChanged()
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        init {
            /*set clickListener on elements RV*/
            v.setOnClickListener {
                clickListener.onCardViewClick(adapterPosition)
            }
        }

        fun bind(city: City){
            itemView.nameCity.text=city.nameInfo

            if (city.shortEnglishNameInfo == currentCity) {
                itemView.selectedCity.visibility = View.VISIBLE
            }
            else{
                itemView.selectedCity.visibility = View.INVISIBLE
            }
        }
    }
}