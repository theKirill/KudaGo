package com.yanyushkin.kudago.activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.adapters.CityDataAdapter
import com.yanyushkin.kudago.models.City
import kotlinx.android.synthetic.main.card_view_separate_city.*
import kotlinx.android.synthetic.main.toolbar_cities.*

class CitiesListActivity : AppCompatActivity() {
    var cities: ArrayList<City> = ArrayList<City>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cities_list)
        setSupportActionBar(toolbar_cities)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_cities)

        /*Create adapter*/
        val adapter = CityDataAdapter(cities)

        /*Set a adapter for list*/
        recyclerView.adapter = adapter

        closeButtonCities.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    fun chooseCityOnClick(v: View) {
        choiceCity.visibility = View.VISIBLE
        var intentRes = Intent()
        intentRes.putExtra("city", "something")
        setResult(Activity.RESULT_OK, intentRes)
        finish()
    }

    fun init() {
        cities.add(
            City(
                "Москва"
            )
        )
        cities.add(
            City(
                "Санкт-Петербург"
            )
        )
        cities.add(
            City(
                "Казань"
            )
        )
        cities.add(
            City(
                "Новосибирск"
            )
        )
        cities.add(
            City(
                "Екатеринбург"
            )
        )
        cities.add(
            City(
                "Нижний Новгород"
            )
        )
        cities.add(
            City(
                "Самара"
            )
        )
        cities.add(
            City(
                "Уфа"
            )
        )
        cities.add(
            City(
                "Красноярск"
            )
        )
        cities.add(
            City(
                "Киров"
            )
        )
        cities.add(
            City(
                "Пермь"
            )
        )
        cities.add(
            City(
                "Владивосток"
            )
        )
        cities.add(
            City(
                "Калининград"
            )
        )
    }
}
