package com.yanyushkin.kudago.network

import retrofit2.Call
import retrofit2.http.GET

const val BASE_URL = "https://kudago.com/public-api/v1.4/"

interface EventsService {
    @GET("events/?fields=id,title,description,dates,place,price,images&page_size=50&expand=dates,place&order_by=-publication_date&text_format=text&actual_since=1549040271")
    fun getEvents(): Call<EventsResponse>
}