package com.yanyushkin.kudago.models

class Event(
    private val id: Int,
    private val title: String,
    private val description: String,
    private val fullDescription: String,
    private val place: String,
    private val dates: String,
    private val price: String,
    private val imageURL: String
) {

    val idInfo: Int
        get() = id

    val titleInfo: String
        get() = title

    val descriptionInfo: String
        get() = description

    val fullDescriptionInfo: String
        get() = fullDescription

    val placeInfo: String
        get() = place

    val datesInfo: String
        get() = dates

    val priceInfo: String
        get() = price

    val imageURLInfo: String
        get() = imageURL
}