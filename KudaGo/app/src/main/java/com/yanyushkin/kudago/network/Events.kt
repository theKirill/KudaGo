package com.yanyushkin.kudago.network

import com.yanyushkin.kudago.models.Event
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class Events @Inject constructor(
    repository: Repository
) {
    var events: ArrayList<Event> = ArrayList()

    init {
        repository.getEvents(
            object : ResponseCallback<EventsResponse> {
                override fun onSuccess(apiResponse: EventsResponse) {
                    apiResponse.events.forEach {
                        val currentEvent = it.transfrom()
                        events.add(currentEvent)

                        /* realm.executeTransactionAsync {
                             val eventRealm = it.createObject(EventObj::class.java)
                             eventRealm.id = currentEvent.idInfo
                             eventRealm.title = currentEvent.titleInfo
                             eventRealm.description = currentEvent.descriptionInfo
                             eventRealm.fullDescription = currentEvent.fullDescriptionInfo
                             eventRealm.dates = currentEvent.datesInfo
                             eventRealm.place = currentEvent.placeInfo
                             eventRealm.price = currentEvent.priceInfo

                             /*val cityRealm = realm.createObject(CityObj::class.java)
                             cityRealm.name = nameOfCurrentCity
                             cityRealm.shortEnglishName = shortEnglishNameOfCurrentCity

                             eventRealm.city = cityRealm*/
                         }*/
                    }

                }

                override fun onFailure(errorMessage: String) {
                }
            },
            System.currentTimeMillis() / 1000L,
            "ru",
            "spb",
            1
        )
    }
}