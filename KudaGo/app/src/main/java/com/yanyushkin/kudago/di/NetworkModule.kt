package com.yanyushkin.kudago.di

import com.yanyushkin.kudago.BuildConfig
import com.yanyushkin.kudago.network.BASE_URL
import com.yanyushkin.kudago.network.DataApi
import com.yanyushkin.kudago.network.Repository
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttp(): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClientBuilder.addInterceptor(interceptor)
        }
        return okHttpClientBuilder.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()


    @Singleton
    @Provides
    fun provideEventsApi(retrofit: Retrofit): DataApi = retrofit.create(DataApi::class.java)


    @Singleton
    @Provides
    fun provideRepository(dataApi: DataApi): Repository = Repository(dataApi)
}