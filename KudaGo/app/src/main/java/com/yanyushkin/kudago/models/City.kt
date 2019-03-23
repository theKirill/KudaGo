package com.yanyushkin.kudago.models

import java.io.Serializable

class City(private var name: String, private var shortEnglishName: String) : Serializable {
    val nameInfo: String
        get() = name

    val shortEnglishNameInfo: String
        get() = shortEnglishName
}