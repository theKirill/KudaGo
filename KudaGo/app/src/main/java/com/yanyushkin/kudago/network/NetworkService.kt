package com.yanyushkin.kudago.network

import com.yanyushkin.kudago.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkService {

    private val okHttpClient: OkHttpClient
    val service: ResponseService

    private object Holder {
        val INSTANCE = NetworkService()
    }

    companion object {
        val instance: NetworkService by lazy { Holder.INSTANCE }
        const val BASE_URL = "https://kudago.com/public-api/v1.4/"
    }

    init {
        okHttpClient = getHttpClient()
        service = makeService(okHttpClient)
    }

    private fun getHttpClient(): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClientBuilder.addInterceptor(interceptor)
        }
        return okHttpClientBuilder.build()
    }

    private fun makeService(okHttpClient: OkHttpClient): ResponseService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        return retrofit.create(ResponseService::class.java)
    }
}