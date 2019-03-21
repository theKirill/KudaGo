package com.yanyushkin.kudago.ViewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.yanyushkin.kudago.models.City
import com.yanyushkin.kudago.network.CitiesResponse
import com.yanyushkin.kudago.network.Repository
import com.yanyushkin.kudago.network.ResponseCallback

class CitiesViewModel(private val lang: String): ViewModel() {
    private lateinit var cities: MutableLiveData<ArrayList<City>>

    init {
        if (!::cities.isInitialized) {
            cities = MutableLiveData()
            loadCities()
        }
    }

    fun getCities(): LiveData<ArrayList<City>> = cities

    private fun loadCities() {
        val citiesFromServer: ArrayList<City> = ArrayList()
        /*make request (async)*/
        Repository.instance.getCities(object : ResponseCallback<java.util.ArrayList<CitiesResponse>> {
            override fun onSuccess(apiResponse: java.util.ArrayList<CitiesResponse>) {
                /*init data for RV from request*/
                apiResponse.forEach {
                    citiesFromServer.add(City(it.name, it.slug))
                }
                cities.value=citiesFromServer
            }

            override fun onFailure(errorMessage: String) {
            }

        }, lang)
    }
}