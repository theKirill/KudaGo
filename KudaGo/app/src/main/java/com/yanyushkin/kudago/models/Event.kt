package com.yanyushkin.kudago.models

import java.io.Serializable

class Event(
    private val id: Int,
    private val title: String,
    private val description: String,
    private val fullDescription: String,
    private val place: String,
    private val dates: String,
    private val price: String,
    private val imagesURL: ArrayList<String>,
    private val coords: ArrayList<Double>
): @kotlin.jvm.Transient Serializable {

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

    val imagesURLInfo: ArrayList<String>
        get() = imagesURL

    val coordsInfo: ArrayList<Double>
        get() = coords
}