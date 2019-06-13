package com.yanyushkin.kudago.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.yanyushkin.kudago.App
import com.yanyushkin.kudago.models.City
import com.yanyushkin.kudago.network.CitiesResponse
import com.yanyushkin.kudago.network.Repository
import com.yanyushkin.kudago.network.ResponseCallback
import javax.inject.Inject

class CitiesViewModel(private val application: App, private val lang: String) : ViewModel() {
    private lateinit var cities: MutableLiveData<ArrayList<City>>
    @Inject
    lateinit var repository: Repository

    init {
        if (!::cities.isInitialized) {
            cities = MutableLiveData()
            loadCitiesFromServer()
        }
    }

    fun getCities(): LiveData<ArrayList<City>> = cities

    private fun loadCitiesFromServer() {
        val citiesFromServer: ArrayList<City> = ArrayList()

        application.getAppComponent().injectsCitiesViewModel(this)

        /*make request (async)*/
        repository.getCities(object : ResponseCallback<java.util.ArrayList<CitiesResponse>> {
            override fun onFailure(errorMessage: String) {
            }

            override fun onSuccess(apiResponse: java.util.ArrayList<CitiesResponse>) {
                /*init data for RV from request*/
                apiResponse.forEach {
                    citiesFromServer.add(it.transform())
                }
                cities.value = citiesFromServer
            }

        }, lang)
    }
}