package com.yanyushkin.kudago.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EventsRepository {
    private object Holder { val INSTANCE = EventsRepository() }

    companion object {
        val instance: EventsRepository by lazy { Holder.INSTANCE }
    }

    fun getEvents(responseCallback: ResponseCallback<EventsResponse>, actual_since: Long, lang: String, location: String) {
        //retrofit async
        NetworkService.instance.service.getEvents(actual_since, lang, location).enqueue(object : Callback<EventsResponse> {
            override fun onFailure(call: Call<EventsResponse>, t: Throwable) {
                responseCallback.onFailure("Getting events error")
            }

            override fun onResponse(call: Call<EventsResponse>, response: Response<EventsResponse>) {
                val eventsResponse = response.body()
                if (eventsResponse != null && response.isSuccessful) {
                    responseCallback.onSuccess(eventsResponse)
                } else {
                    responseCallback.onFailure("Getting events error")
                }
            }
        })
    }
}