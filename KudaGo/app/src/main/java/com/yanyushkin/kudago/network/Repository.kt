package com.yanyushkin.kudago.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(private val dataApi: DataApi) {

    fun getEvents(
        responseCallback: ResponseCallback<EventsResponse>,
        actual_since: Long,
        lang: String,
        location: String,
        page: Int
    ) {
        /*async data acquisition*/
        dataApi.getEvents(actual_since, lang, location, page)
            .enqueue(object : Callback<EventsResponse> {
                override fun onFailure(call: Call<EventsResponse>, t: Throwable) {
                }

                override fun onResponse(call: Call<EventsResponse>, response: Response<EventsResponse>) {
                    if (response.isSuccessful) {
                        val eventsResponse = response.body()

                        eventsResponse?.let { responseCallback.onSuccess(eventsResponse) }//take the parsed data
                    } else {
                        responseCallback.onFailure("Getting events error")
                    }
                }
            })
    }

    fun getCities(responseCallback: ResponseCallback<ArrayList<CitiesResponse>>, lang: String) {
        /*async data acquisition*/
        dataApi.getCities(lang).enqueue(object : Callback<ArrayList<CitiesResponse>> {
            override fun onFailure(call: Call<ArrayList<CitiesResponse>>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<ArrayList<CitiesResponse>>,
                response: Response<ArrayList<CitiesResponse>>
            ) {
                if (response.isSuccessful) {
                    val citiesResponse = response.body()

                    citiesResponse?.let { responseCallback.onSuccess(citiesResponse) } //take the parsed data
                } else {
                    responseCallback.onFailure("Getting cities error")
                }
            }
        })
    }
}