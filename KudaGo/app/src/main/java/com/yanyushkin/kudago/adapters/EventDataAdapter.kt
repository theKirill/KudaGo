package com.yanyushkin.kudago.adapters

import android.os.Build
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.Html
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

    override fun getItemCount(): Int {
        return events.size
    }

    /*init of ViewHolder*/
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.card_view, p0, false)
        return ViewHolder(view)
    }

    /*full data for each element of RV*/
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val event = events[p1]
        if (p1 == 0) {
            p0.fL.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1.0f
            )
        }
        p0.titleEvent.text = event.titleInfo

        val descriptionEventWithHtml = event.descriptionInfo

        /*replacing tags*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            p0.descriptionEvent.text = Html.fromHtml(descriptionEventWithHtml, Html.FROM_HTML_MODE_LEGACY)
        } else {
            p0.descriptionEvent.text = Html.fromHtml(descriptionEventWithHtml)
        }

        /*if we have the right data, insert it in views and show, else not show*/
        if (event.placeInfo != "") {
            p0.placeEvent.text = event.placeInfo
            p0.fLLocation.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
        }
        if (event.datesInfo != "") {
            p0.dayEvent.text = event.datesInfo
            p0.fLDate.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
        }
        if (event.priceInfo != "") {
            p0.priceEvent.text = event.priceInfo
            p0.fLPrice.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
        }

        Glide.with(p0.cv).load(event.imageURLInfo).into(p0.imageEvent)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var cv: CardView
        var fL: FrameLayout
        var fLLocation: FrameLayout
        var fLDate: FrameLayout
        var fLPrice: FrameLayout
        var titleEvent: TextView
        var descriptionEvent: TextView
        var placeEvent: TextView
        var dayEvent: TextView
        var priceEvent: TextView
        var imageEvent: AppCompatImageView

        /*initializing views for elements of RecyclerView*/
        init {
            cv = v.findViewById(R.id.cv_of_event)
            fL = v.findViewById(R.id.container_for_text_kudaGo)
            fLLocation = v.findViewById(R.id.container_for_location)
            fLDate = v.findViewById(R.id.container_for_date)
            fLPrice = v.findViewById(R.id.container_for_price)
            titleEvent = v.findViewById(R.id.textTitle)
            descriptionEvent = v.findViewById(R.id.textDescription)
            placeEvent = v.findViewById(R.id.textLocation)
            dayEvent = v.findViewById(R.id.textDay)
            priceEvent = v.findViewById(R.id.textPrice)
            imageEvent = v.findViewById(R.id.imagePhoto)

            /*set clickListener on elements RV*/
            v.setOnClickListener {
                clickListener.onCardViewClick(adapterPosition)
            }
        }
    }
}