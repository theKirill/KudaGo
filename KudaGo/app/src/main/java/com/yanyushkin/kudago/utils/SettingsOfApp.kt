package com.yanyushkin.kudago.utils

import android.content.Context
import android.content.SharedPreferences
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.models.City

object SettingsOfApp {

    private lateinit var pref: SharedPreferences
    private const val APP_PREFERENCES_KEY = "settings"
    private const val APP_PREFERENCES_NAME_CITY_KEY = "nameOfCurrentCity"
    private const val APP_PREFERENCES_SHORTNAME_CITY_KEY = "shortEnglishNameOfCurrentCity"

    /*find out last selected city from SharedPreferences (if it exists)*/
    fun getSavedLastSelectedCity(context: Context): City {
        val nameOfCurrentCity: String
        val shortEnglishNameOfCurrentCity: String

        pref = context.getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE)

        if (pref.contains(APP_PREFERENCES_NAME_CITY_KEY) && pref.contains(APP_PREFERENCES_SHORTNAME_CITY_KEY)) {
            nameOfCurrentCity = pref.getString(APP_PREFERENCES_NAME_CITY_KEY, context.getString(R.string.nameCityBegin))
            shortEnglishNameOfCurrentCity =
                pref.getString(APP_PREFERENCES_SHORTNAME_CITY_KEY, context.getString(R.string.shortNameCityBegin))
        } else {
            nameOfCurrentCity = context.getString(R.string.nameCityBegin)
            shortEnglishNameOfCurrentCity = context.getString(R.string.shortNameCityBegin)
        }

        return City(nameOfCurrentCity, shortEnglishNameOfCurrentCity)
    }

    fun saveSelectedCity(name: String, shortName: String) {
        val editor = pref.edit()
        editor.putString(APP_PREFERENCES_NAME_CITY_KEY, name)
        editor.putString(APP_PREFERENCES_SHORTNAME_CITY_KEY, shortName)
        editor.apply()
    }
}