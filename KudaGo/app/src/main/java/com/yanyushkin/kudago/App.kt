package com.yanyushkin.kudago

import android.app.Application
import com.yanyushkin.kudago.di.AppComponent
import com.yanyushkin.kudago.di.DaggerAppComponent
import com.yanyushkin.kudago.di.NetworkModule

class App: Application() {
    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()
       component= DaggerAppComponent.builder()
           .networkModule(NetworkModule())
           .build()
    }

    fun getAppComponent(): AppComponent
    {
        return component
    }
}