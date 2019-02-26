package com.yanyushkin.kudago.models

class Event(
    private var name: String,
    private var description: String,
    private var location: String,
    private var day: String,
    private var price: String,
    private var idEventImage: Int
) {

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
}