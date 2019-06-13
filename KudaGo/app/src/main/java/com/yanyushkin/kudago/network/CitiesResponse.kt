package com.yanyushkin.kudago.network

import com.google.gson.annotations.SerializedName
import com.yanyushkin.kudago.models.City

data class CitiesResponse(
    @SerializedName("slug")
    val slug: String,
    @SerializedName("name")
    val name: String
) {
    fun transform(): City {
        return City(
            this.name,
            this.slug
        )
    }
}