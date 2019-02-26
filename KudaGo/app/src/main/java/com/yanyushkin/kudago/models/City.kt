package com.yanyushkin.kudago.models

class City(_name: String) {
    private var name: String = _name

    val nameInfo: String
        get() = name
}