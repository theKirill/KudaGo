package com.yanyushkin.kudago.adapters

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.models.Event

class DataAdapter : RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private var events: ArrayList<Event>

    constructor(events: ArrayList<Event>) {
        this.events = events
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.card_view, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val event = events.get(p1)
        p0.nameEvent.setText(event.nameInfo)
        p0.descriptionEvent.setText(event.descriptionInfo)
        p0.locationEvent.setText(event.locationInfo)
        p0.dayEvent.setText(event.dayInfo)
        p0.priceEvent.setText(event.priceInfo)
        p0.eventImageEvent.setImageResource(event.idEventImageInfo)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var cv: CardView
        var nameEvent: TextView
        var descriptionEvent: TextView
        var locationEvent: TextView
        var dayEvent: TextView
        var priceEvent: TextView
        var eventImageEvent: ImageView

        init {
            cv = v.findViewById(R.id.cv)
            nameEvent = v.findViewById(R.id.textNameEvent)
            descriptionEvent = v.findViewById(R.id.textDescriptionEvent)
            locationEvent = v.findViewById(R.id.textLocation)
            dayEvent = v.findViewById(R.id.textDay)
            priceEvent = v.findViewById(R.id.textPrice)
            eventImageEvent = v.findViewById(R.id.imageEventPhoto)
        }
    }
}