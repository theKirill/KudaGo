package com.yanyushkin.kudago.models

import com.google.gson.annotations.SerializedName

data class Event(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("full_description")
    val full_description: String,
    @SerializedName("place")
    val place: String,
    @SerializedName("dates")
    val dates: String,
    @SerializedName("price")
    val price: String,
    @SerializedName("images")
    val images: String
) /*{

    val nameInfo: String
        get() = name

    val descriptionInfo: String
        get() = description

    val locationInfo: String
        get() = location

    val dayInfo: String
        get() = day

    val priceInfo: String
        get() = price

    val idEventImageInfo: Int
        get() = idEventImage
}*/