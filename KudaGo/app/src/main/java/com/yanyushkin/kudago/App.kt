package com.yanyushkin.kudago

import android.app.Application
import com.yanyushkin.kudago.di.AppComponent
import com.yanyushkin.kudago.di.DaggerAppComponent
import com.yanyushkin.kudago.di.DatabaseModule
import com.yanyushkin.kudago.di.NetworkModule
import io.realm.Realm

class App : Application() {
    private lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        component = DaggerAppComponent.builder()
            .networkModule(NetworkModule())
            .databaseModule(DatabaseModule())
            .build()
    }

    fun getAppComponent(): AppComponent {
        return component
    }
}