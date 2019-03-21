package com.yanyushkin.kudago.adapters

import android.os.Build
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.models.Event
import com.yanyushkin.kudago.utils.OnClickListener

class EventDataAdapter(private var events: ArrayList<Event>, private val clickListener: OnClickListener) :
    RecyclerView.Adapter<EventDataAdapter.ViewHolder>() {

    override fun getItemCount() = events.size

    /*init of ViewHolder*/
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.card_view, viewGroup, false))
    }

    /*full data for each element of RV*/
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val event = events[position]

        setHeader(viewHolder, position)

        setImage(viewHolder, event)

        setTitle(viewHolder, event)

        setDescription(viewHolder, event)

        setPlace(viewHolder, event)

        setDate(viewHolder, event)

        setPrice(viewHolder, event)

    }

    fun setItems(_events: ArrayList<Event>) {
        events = _events
        notifyDataSetChanged()
    }

    private fun setHeader(viewHolder: ViewHolder, position: Int) {
        /*Visibility of Header*/
        if (position == 0) {
            viewHolder.fL.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
        } else {
            viewHolder.fL.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                0.0f
            )
        }
    }

    private fun setImage(viewHolder: ViewHolder, event: Event) {
        Glide.with(viewHolder.cv).load(event.imageURLInfo).into(viewHolder.imageEvent)
    }

    private fun setTitle(viewHolder: ViewHolder, event: Event) {
        viewHolder.titleEvent.text = event.titleInfo
    }

    private fun setDescription(viewHolder: ViewHolder, event: Event) {
        val descriptionEventWithHtml = event.descriptionInfo

        /*replacing tags*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            viewHolder.descriptionEvent.text = Html.fromHtml(descriptionEventWithHtml, Html.FROM_HTML_MODE_LEGACY)
        } else {
            viewHolder.descriptionEvent.text = Html.fromHtml(descriptionEventWithHtml)
        }
    }

    private fun setPlace(viewHolder: ViewHolder, event: Event) {
        /*if we have the right data, insert it in views and show, else not show*/
        if (event.placeInfo != "") {
            viewHolder.placeEvent.text = event.placeInfo
            viewHolder.fLLocation.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
        } else {
            viewHolder.fLLocation.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                0.0f
            )
        }
    }

    private fun setDate(viewHolder: ViewHolder, event: Event) {
        if (event.datesInfo != "") {
            viewHolder.dayEvent.text = event.datesInfo
            viewHolder.fLDate.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
        } else {
            viewHolder.fLDate.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                0.0f
            )
        }
    }

    private fun setPrice(viewHolder: ViewHolder, event: Event) {
        if (event.priceInfo != "") {
            viewHolder.priceEvent.text = event.priceInfo
            viewHolder.fLPrice.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
        } else {
            viewHolder.fLPrice.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                0.0f
            )
        }
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