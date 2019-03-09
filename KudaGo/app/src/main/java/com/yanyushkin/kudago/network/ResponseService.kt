package com.yanyushkin.kudago.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ResponseService {
    @GET("events/?fields=id,title,description,body_text,dates,place,price,images&page_size=50&expand=dates,place&order_by=-publication_date")
    fun getEvents(@Query("actual_since") actual_since: Long, @Query("lang") lang: String, @Query("location") location: String, @Query("page") page: Int): Call<EventsResponse>
    @GET("locations/?order_by=name")
    fun getCities(@Query("lang") lang: String): Call<ArrayList<CitiesResponse>>
}