package com.yanyushkin.kudago.network

import com.google.gson.annotations.SerializedName
import com.yanyushkin.kudago.models.Event
import com.yanyushkin.kudago.utils.Tools

data class EventsResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("results")
    val events: List<EventFromServer>
)

data class EventFromServer(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("body_text")
    val body_text: String,
    @SerializedName("place")
    var place: Place?,
    @SerializedName("dates")
    var date: List<Date>,
    @SerializedName("price")
    var price: String,
    @SerializedName("images")
    val images: ArrayList<Image>
) {
    fun transfrom(): Event {
        val imagesUrlOfEvent: ArrayList<String> = ArrayList()
        this.images.forEach {
            imagesUrlOfEvent.add(it.image)
        }

        return Event(
            this.id,
            this.title,
            this.description,
            this.body_text,
            Tools.translatePlace(this.place),
            Tools.translateDate(
                this.date[0].start_date,
                this.date[0].end_date
            )
            ,
            this.price,
            imagesUrlOfEvent,
            Tools.getLatAndLon(this.place)
        )
    }
}

data class Place(
    @SerializedName("title")
    val title: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("coords")
    val coords: Coords?
)

data class Date(
    @SerializedName("start_date")
    var start_date: String,
    @SerializedName("end_date")
    var end_date: String
)

data class Image(
    @SerializedName("image")
    val image: String
)

data class Coords(
    @SerializedName("lat")
    val lat: Double?,
    @SerializedName("lon")
    val lon: Double?
)