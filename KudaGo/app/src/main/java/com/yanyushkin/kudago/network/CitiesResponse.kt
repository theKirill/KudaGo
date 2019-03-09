package com.yanyushkin.kudago.network

import com.google.gson.annotations.SerializedName

data class CitiesResponse(
    @SerializedName("slug")
    val slug: String,
    @SerializedName("name")
    val name: String
)