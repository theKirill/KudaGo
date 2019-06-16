package com.yanyushkin.kudago.database

import com.yanyushkin.kudago.database.dbmodels.CityDB
import com.yanyushkin.kudago.database.dbmodels.EventDB
import com.yanyushkin.kudago.models.City
import com.yanyushkin.kudago.models.Event
import io.realm.Realm
import io.realm.RealmResults

class DatabaseService(private val realm: Realm) {
    private val SHORT_ENGLISH_NAME_KEY = "shortEnglishName"
    private val CITY_KEY = "city.shortEnglishName"

    private fun requestThisCityFromDB(city: City): RealmResults<CityDB> =
        realm.where(CityDB::class.java).equalTo(SHORT_ENGLISH_NAME_KEY, city.shortEnglishNameInfo).findAll()

    fun addEvents(city: City, events: ArrayList<Event>) {
        realm.executeTransaction {
            lateinit var cityRealm: CityDB

            val query = requestThisCityFromDB(city)

            if (query.size == 0) {
                cityRealm = it.createObject(CityDB::class.java)
                cityRealm.name = city.nameInfo
                cityRealm.shortEnglishName = city.shortEnglishNameInfo
            } else {
                cityRealm = query.first()!!
            }

            for (i in 0 until events.size) {
                val eventRealm = it.createObject(EventDB::class.java)
                eventRealm.id = events[i].idInfo
                eventRealm.title = events[i].titleInfo
                eventRealm.description = events[i].descriptionInfo
                eventRealm.fullDescription = events[i].fullDescriptionInfo
                eventRealm.dates = events[i].datesInfo
                eventRealm.place = events[i].placeInfo
                eventRealm.price = events[i].priceInfo
                eventRealm.city = cityRealm

                cityRealm.events.add(eventRealm)
            }
        }
    }

    fun getEventsFromCity(city: City): ArrayList<Event> {
        val events = ArrayList<Event>()

        val eventsRealm = realm.where(EventDB::class.java).equalTo(CITY_KEY, city.shortEnglishNameInfo).findAll()

        eventsRealm.forEach {
            events.add(
                Event(
                    it.id,
                    it.title,
                    it.description,
                    it.fullDescription,
                    it.place,
                    it.dates,
                    it.price,
                    arrayListOf(),
                    arrayListOf()
                )
            )
        }

        return events
    }

    fun deleteOldEventsFromCity(city: City) {
        val oldEventsFromDB =
            realm.where(EventDB::class.java).equalTo(CITY_KEY, city.shortEnglishNameInfo).findAll()

        try {
            realm.executeTransaction {
                oldEventsFromDB.deleteAllFromRealm()
            }
        } catch (e: Exception) {
        }
    }

    fun getCities(): ArrayList<City> {
        val cities = ArrayList<City>()

        val citiesRealm = realm.where(CityDB::class.java).findAll()

        citiesRealm.forEach {
            cities.add(City(it.name, it.shortEnglishName))
        }

        return cities
    }
}