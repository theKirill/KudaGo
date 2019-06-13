package com.yanyushkin.kudago.adapters

import android.os.Build
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
import kotlinx.android.synthetic.main.card_view.view.*

class EventDataAdapter(private var events: ArrayList<Event>, private val clickListener: OnClickListener) :
    RecyclerView.Adapter<EventDataAdapter.ViewHolder>() {

    override fun getItemCount() = events.size

    /*init of ViewHolder*/
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.card_view, viewGroup, false))

    /*full data for each element of RV*/
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(position)
    }

    fun setItems(_events: ArrayList<Event>) {
        events = _events
        notifyDataSetChanged()
    }

    fun addItems() {
        notifyItemInserted(events.size - 1)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        init {
            /*set clickListener on elements RV*/
            v.setOnClickListener {
                clickListener.onCardViewClick(adapterPosition)
            }
        }

        fun bind(position: Int) {
            val event = events[position]
            setHeader(position)
            setImage(event)
            setTitle(event)
            setDescription(event)
            setPlace(event)
            setDate(event)
            setPrice(event)
        }

        private fun setHeader(position: Int) {
            /*Visibility of Header*/
            if (position == 0) {
                itemView.text_header.visibility = View.VISIBLE
            } else {
                itemView.text_header.visibility = View.GONE
            }
        }

        private fun setImage(event: Event) {
            if (event.imagesURLInfo.size > 0) {
                Glide.with(itemView.cv_of_event).load(event.imagesURLInfo[0]).into(itemView.imagePhoto)
            } else {
                itemView.imagePhoto.visibility = View.GONE
            }
        }

        private fun setTitle(event: Event) {
            itemView.textTitle.text = event.titleInfo
        }

        private fun setDescription(event: Event) {
            val descriptionEventWithHtml = event.descriptionInfo

            /*replacing tags*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                itemView.textDescription.text = Html.fromHtml(descriptionEventWithHtml, Html.FROM_HTML_MODE_LEGACY)
            } else {
                itemView.textDescription.text = Html.fromHtml(descriptionEventWithHtml)
            }
        }

        private fun setPlace(event: Event) {
            /*if we have the right data, insert it in views and show, else not show*/
            if (event.placeInfo != "") {
                itemView.textLocation.text = event.placeInfo
                itemView.container_for_location.visibility = View.VISIBLE
            } else {
                itemView.container_for_location.visibility = View.GONE
            }
        }

        private fun setDate(event: Event) {
            if (event.datesInfo != "") {
                itemView.textDay.text = event.datesInfo
                itemView.container_for_date.visibility = View.VISIBLE
            } else {
                itemView.container_for_date.visibility = View.GONE
            }
        }

        private fun setPrice(event: Event) {
            if (event.priceInfo != "") {
                itemView.textPrice.text = event.priceInfo
                itemView.container_for_price.visibility = View.VISIBLE
            } else {
                itemView.container_for_price.visibility = View.GONE
            }
        }
    }
}