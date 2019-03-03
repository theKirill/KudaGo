package com.yanyushkin.kudago.network

import com.google.gson.annotations.SerializedName

interface ApiResponse

data class EventsResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("results")
    val events: List<EventNew>
) : ApiResponse

data class EventNew(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("place")
    var place: Place?,
    @SerializedName("dates")
    var date: List<Date>,
    @SerializedName("price")
    var price: String,
    @SerializedName("images")
    val images: List<Image>
)

data class Place(
    @SerializedName("title")
    val title: String?,
    @SerializedName("address")
    val address: String?
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