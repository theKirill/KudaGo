package com.yanyushkin.kudago.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository {
    private object Holder {
        val INSTANCE = Repository()
    }

    companion object {
        val instance: Repository by lazy { Holder.INSTANCE }
    }

    fun getEvents(
        responseCallback: ResponseCallback<EventsResponse>,
        actual_since: Long,
        lang: String,
        location: String,
        page: Int
    ) {
        /*async data acquisition*/
        NetworkService.instance.service.getEvents(actual_since, lang, location, page)
            .enqueue(object : Callback<EventsResponse> {
                override fun onFailure(call: Call<EventsResponse>, t: Throwable) {
                    responseCallback.onFailure("Getting events error")
                }

                override fun onResponse(call: Call<EventsResponse>, response: Response<EventsResponse>) {
                    val eventsResponse = response.body()

                    if (eventsResponse != null && response.isSuccessful) {
                        responseCallback.onSuccess(eventsResponse)//take the parsed data
                    } else {
                        responseCallback.onFailure("Getting events error")
                    }
                }
            })
    }

    fun getCities(responseCallback: ResponseCallback<ArrayList<CitiesResponse>>, lang: String) {
        /*async data acquisition*/
        NetworkService.instance.service.getCities(lang).enqueue(object : Callback<ArrayList<CitiesResponse>> {
            override fun onFailure(call: Call<ArrayList<CitiesResponse>>, t: Throwable) {
                responseCallback.onFailure("Getting cities error")
            }

            override fun onResponse(
                call: Call<ArrayList<CitiesResponse>>,
                response: Response<ArrayList<CitiesResponse>>
            ) {
                val citiesResponse = response.body()

                if (citiesResponse != null && response.isSuccessful) {
                    responseCallback.onSuccess(citiesResponse)//take the parsed data
                } else {
                    responseCallback.onFailure("Getting cities error")
                }
            }
        })
    }
}