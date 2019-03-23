package com.yanyushkin.kudago.network

import com.google.gson.annotations.SerializedName
import com.yanyushkin.kudago.models.City
import com.yanyushkin.kudago.utils.Tools

data class CitiesResponse(
    @SerializedName("slug")
    val slug: String,
    @SerializedName("name")
    val name: String
) {
    fun transfrom(): City {
        return City(
            this.name,
            this.slug
        )
    }
}