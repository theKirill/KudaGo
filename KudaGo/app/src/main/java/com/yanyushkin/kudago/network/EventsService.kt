package com.yanyushkin.kudago.network

import retrofit2.Call
import retrofit2.http.GET

const val BASE_URL = "https://kudago.com/public-api/v1.4/"

interface EventsService {
    @GET("events/?fields=id,title,description,dates,place,price,images&expand=dates,place&location=spb")
    fun getEvents(): Call<EventsResponse>
}