package com.yanyushkin.kudago.database.dbmodels

import io.realm.RealmList
import io.realm.RealmObject

open class CityDB(
    var name: String = "",
    var shortEnglishName: String = "",
    var events: RealmList<EventDB> = RealmList()
) : RealmObject() {
}