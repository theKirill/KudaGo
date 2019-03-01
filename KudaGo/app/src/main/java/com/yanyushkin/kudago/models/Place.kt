package com.yanyushkin.kudago.models

import com.google.gson.annotations.SerializedName

data class Place (
    @SerializedName("title")
    val title: String
){
    val titleInfo: String
        get() = title
}