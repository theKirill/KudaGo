package com.yanyushkin.kudago.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

const val BASE_URL = "https://kudago.com/public-api/v1.4/"

interface EventsService {
    @GET("events/?fields=id,title,description,body_text,dates,place,price,images&page_size=50&expand=dates,place&order_by=-publication_date&text_format=text")
    fun getEvents(@Query("actual_since") actual_since: Long, @Query("lang") lang: String, @Query("location") location: String): Call<EventsResponse>
}