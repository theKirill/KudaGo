package com.yanyushkin.kudago.di

import com.yanyushkin.kudago.activities.CitiesListActivity
import com.yanyushkin.kudago.activities.MainActivity
import com.yanyushkin.kudago.viewmodels.CitiesViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class, NetworkModule::class])
interface AppComponent {

    fun injectsMainActivity(mainActivity: MainActivity)

    fun injectsCitiesListActivity(citiesListActivity: CitiesListActivity)

    fun injectsCitiesViewModel(citiesViewModel: CitiesViewModel)
}