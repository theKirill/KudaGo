package com.yanyushkin.kudago.database.dbmodels

import io.realm.RealmObject

open class EventDB(
    var id: Int = 0,
    var title: String = "",
    var description: String = "",
    var fullDescription: String = "",
    var place: String = "",
    var dates: String = "",
    var price: String = "",
    var city: CityDB? = null
) : RealmObject() {
}