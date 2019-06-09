package com.yanyushkin.kudago.di

import com.yanyushkin.kudago.activities.MainActivity
import com.yanyushkin.kudago.viewmodels.CitiesViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface AppComponent {

    fun injectsMainActivity(mainActivity: MainActivity)

    fun injectsCitiesViewModel(citiesViewModel: CitiesViewModel)
}