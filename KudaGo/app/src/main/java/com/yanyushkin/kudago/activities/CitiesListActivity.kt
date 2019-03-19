package com.yanyushkin.kudago.activities

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
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
import kotlinx.android.synthetic.main.toolbar_cities.*
import kotlinx.coroutines.*
import java.util.*

class CitiesListActivity : AppCompatActivity() {
    private var cities: ArrayList<City> = ArrayList()
    private var lang = "en"
    private val CITIES = "cities"
    private val BROADCAST_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
    private val intentFilter = IntentFilter(BROADCAST_ACTION)

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action: String

            if (intent != null) {
                action = intent.action

                when (action) {
                    BROADCAST_ACTION -> {
                        var isHasInternet = false

                        val check = CoroutineScope(Dispatchers.IO).async {
                            //using Coroutines
                            CheckInternet.isHasInternet(this@CitiesListActivity)
                        }

                        GlobalScope.launch(Dispatchers.Main) {
                            isHasInternet = check.await()
                            if (!isHasInternet) {
                                showErrorNoInternet()
                            } else {
                                showCities()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cities_list)
        setSupportActionBar(toolbar_cities)

        /*getting of current language of system
        * if Russian language, we will make requests in Russian
        * else in English*/
        if (Locale.getDefault().language == "ru") {
            lang = "ru"
        }

        /*check for saved state*/
        if (savedInstanceState != null && savedInstanceState.containsKey(CITIES)) {
            cities = savedInstanceState.getParcelableArrayList(CITIES)
        }

        button_closeCities.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        unregisterReceiver(receiver)
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        /*save state*/
        if (outState != null) {
            outState.clear()
            outState.putParcelableArrayList(CITIES, cities)
            initRecyclerView()
        }
    }

    private fun showErrorNoInternet() {
        layout_main_cities.visibility = View.INVISIBLE
        layout_error_internet_cities.visibility = View.VISIBLE
        progressBar_cities.visibility = View.INVISIBLE
        val sbError = ErrorSnackBar(layout_error_internet_cities)
        sbError.show(this)
    }

    private fun showCities() {
        if (cities.size == 0) {
            cities = ArrayList()
            initData()
        }
        layout_main_cities.visibility = View.VISIBLE
        layout_error_internet_cities.visibility = View.INVISIBLE
    }

    private fun initData() {
        progressBar_cities.visibility = View.VISIBLE

        /*make request (async)*/
        Repository.instance.getCities(object : ResponseCallback<ArrayList<CitiesResponse>> {
            override fun onSuccess(apiResponse: ArrayList<CitiesResponse>) {
                /*init data for RV from request*/
                apiResponse.forEach {
                    cities.add(City(it.name, it.slug))
                }
                initRecyclerView()
            }

            override fun onFailure(errorMessage: String) {
                progressBar_cities.visibility = View.INVISIBLE
            }

        }, lang)
    }

    private fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_cities)

        val arguments = intent.extras
        val currentCity: String = arguments.getString("currentCity")

        /*Create adapter with listener of click on element*/
        val adapter = CityDataAdapter(cities, object : OnClickListener {
            override fun onCardViewClick(position: Int) {
                val intentRes = Intent()
               // val selectedCity = City(cities[position].nameInfo, cities[position].shortEnglishNameInfo)
                intentRes.putExtra("nameOfSelectedCity", cities[position].nameInfo)
                intentRes.putExtra("shortEnglishNameOfSelectedCity", cities[position].shortEnglishNameInfo)
                //intentRes.putExtra("", selectedCity)
                /*remember the selected city and pass to main activity*/
                setResult(Activity.RESULT_OK, intentRes)
                finish()
            }
        }, currentCity)

        recyclerView.adapter = adapter
        progressBar_cities.visibility = View.INVISIBLE
    }
}
