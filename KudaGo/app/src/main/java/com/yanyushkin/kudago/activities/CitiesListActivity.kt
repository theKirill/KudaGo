package com.yanyushkin.kudago.activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.adapters.CityDataAdapter
import com.yanyushkin.kudago.models.City
import com.yanyushkin.kudago.network.CitiesResponse
import com.yanyushkin.kudago.network.Repository
import com.yanyushkin.kudago.network.ResponseCallback
import com.yanyushkin.kudago.utils.CheckInternet
import com.yanyushkin.kudago.utils.ErrorSnackBar
import com.yanyushkin.kudago.utils.OnClickListener
import kotlinx.android.synthetic.main.activity_cities_list.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_cities.*
import java.util.*

class CitiesListActivity : AppCompatActivity() {
    private var cities: ArrayList<City> = ArrayList()
    private var lang = "en"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cities_list)
        setSupportActionBar(toolbar_cities)

        if (Locale.getDefault().language == "ru") {
            lang = "ru"
        }

        /*Check Internet*/
        if (!CheckInternet.isHasInternet(this@CitiesListActivity)) {
            main_layout_cities.visibility = View.INVISIBLE
            relative_layout_cities.visibility = View.VISIBLE
            progressBar_cities.visibility = View.INVISIBLE

            val sbError = ErrorSnackBar(relative_layout)
            sbError.show(this)
        } else {
            main_layout_cities.visibility = View.VISIBLE
            relative_layout_cities.visibility = View.INVISIBLE
            initData()
        }

        closeButtonCities.setOnClickListener { finish() }
    }

    fun initData() {
        progressBar_cities.visibility = View.VISIBLE

        Repository.instance.getCities(object : ResponseCallback<ArrayList<CitiesResponse>> {
            override fun onSuccess(apiResponse: ArrayList<CitiesResponse>) {
                apiResponse.forEach {
                    cities.add(City(it.name, it.slug))
                }
                initRecyclerView()
            }

            override fun onFailure(errorMessage: String) {
                Toast.makeText(this@CitiesListActivity, errorMessage, Toast.LENGTH_LONG).show()
            }

        }, lang)
    }

    fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_cities)

        val arguments = intent.extras
        val currentCity = arguments.getString("currentCity")

        /*Create adapter*/
        val adapter = CityDataAdapter(cities, object : OnClickListener {
            override fun onCardViewClick(position: Int) {
                val intentRes = Intent()
                intentRes.putExtra("nameOfSelectedCity", cities[position].name)
                intentRes.putExtra("shortEnglishNameOfSelectedCity", cities[position].shortEnglishName)
                setResult(Activity.RESULT_OK, intentRes)
                finish()
            }
        }, currentCity)

        recyclerView.adapter = adapter
        progressBar_cities.visibility = View.INVISIBLE
    }
}
