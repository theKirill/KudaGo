package com.yanyushkin.kudago.adapters

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import com.bumptech.glide.Glide
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.models.Event
import com.yanyushkin.kudago.utils.OnClickListener

class EventDataAdapter(private var events: ArrayList<Event>, private val clickListener: OnClickListener) :
    RecyclerView.Adapter<EventDataAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.card_view, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val event = events[p1]
        if (p1 == 0) {
            p0.fL.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.0f
            )
        }
        p0.titleEvent.text = event.title
        p0.descriptionEvent.text = event.description

        if (event.place != "") {
            p0.placeEvent.text = event.place
            p0.fLLocation.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
        }
        if (event.dates != "") {
            p0.dayEvent.text = event.dates
            p0.fLDate.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
        }
        if (event.price != "") {
            p0.priceEvent.text = event.price
            p0.fLPrice.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
        }
        Glide.with(p0.cv).load(event.imageURL).into(p0.imageEvent)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var cv: CardView
        var fL: FrameLayout
        var fLLocation: FrameLayout
        var fLDate: FrameLayout
        var fLPrice: FrameLayout
        var kudaGo: TextView
        var titleEvent: TextView
        var descriptionEvent: TextView
        var placeEvent: TextView
        var dayEvent: TextView
        var priceEvent: TextView
        var imageEvent: AppCompatImageView

        init {
            cv = v.findViewById(R.id.cvEvent)
            fL = v.findViewById(R.id.container_for_textview)
            fLLocation = v.findViewById(R.id.container_for_text_location)
            fLDate = v.findViewById(R.id.container_for_text_date)
            fLPrice = v.findViewById(R.id.container_for_text_price)
            kudaGo = v.findViewById(R.id.textKudaGo)
            titleEvent = v.findViewById(R.id.textNameEvent)
            descriptionEvent = v.findViewById(R.id.textDescriptionEvent)
            placeEvent = v.findViewById(R.id.textLocation)
            dayEvent = v.findViewById(R.id.textDay)
            priceEvent = v.findViewById(R.id.textPrice)
            imageEvent = v.findViewById(R.id.imagePhotoEvent)

            v.setOnClickListener {
                clickListener.onCardViewClick(adapterPosition)
            }
        }
    }
}