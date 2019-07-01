package com.yanyushkin.kudago.activities

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.yanyushkin.kudago.App
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.viewmodels.BaseViewModelFactory
import com.yanyushkin.kudago.viewmodels.CitiesViewModel
import com.yanyushkin.kudago.adapters.CityDataAdapter
import com.yanyushkin.kudago.models.City
import com.yanyushkin.kudago.database.DatabaseService
import com.yanyushkin.kudago.utils.CheckInternet
import com.yanyushkin.kudago.utils.ErrorSnackBar
import com.yanyushkin.kudago.utils.OnClickListener
import kotlinx.android.synthetic.main.activity_cities_list.*
import kotlinx.android.synthetic.main.toolbar_cities.*
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class CitiesListActivity : AppCompatActivity() {

    @Inject
    lateinit var service: DatabaseService
    private lateinit var viewModel: CitiesViewModel
    private var cities: ArrayList<City> = ArrayList()
    private var lang = "en"
    private val BROADCAST_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
    private val intentFilter = IntentFilter(BROADCAST_ACTION)
    private lateinit var adapter: CityDataAdapter
    private var mScrollY: Int = 0
    private var mStateScrollY: Int = 0
    private val CURRENT_CITY_KEY = "currentCity"
    private val CITY_KEY = "city"
    private val SCROLL_Y_KEY = "scrollY"

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

                            if (!isHasInternet)
                                doWithoutInternet()
                            else
                                showCities()
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

        (application as App).getAppComponent().injectsCitiesListActivity(this)

        initLang()

        initAdapter()

        if (savedInstanceState != null && savedInstanceState.containsKey(SCROLL_Y_KEY))
            mStateScrollY =
                savedInstanceState.getInt(SCROLL_Y_KEY, 0) //look where we stopped before the change of orientation
        else
            showProgress()

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
        outState?.let { outState.putInt(SCROLL_Y_KEY, mScrollY) }
    }

    private fun initLang() {
        /*getting of current language of system
       * if Russian language, we will make requests in Russian
       * else in English*/
        if (Locale.getDefault().language == "ru")
            lang = "ru"
    }

    private fun showErrorLayout() {
        layout_main_cities.visibility = View.INVISIBLE
        layout_error_internet_cities.visibility = View.VISIBLE
    }

    private fun showCitiesLayout() {
        layout_main_cities.visibility = View.VISIBLE
        layout_error_internet_cities.visibility = View.INVISIBLE
    }

    private fun doWithoutInternet() {
        val citiesFromDB = service.getCities()

        if (cities.size == 0 && citiesFromDB.size == 0)
            showErrorLayout()
        else
            if (cities.size == 0 && citiesFromDB.size >= 0) {
                cities = citiesFromDB
                adapter.setItems(cities)
                Toast.makeText(this, "Загружены последние сохранённые данные!", Toast.LENGTH_LONG).show()
                showCitiesLayout()
            } else {
                showCities()
            }

        hideProgress()
        val sbError = ErrorSnackBar(layout_error_internet_cities)
        sbError.show(this)
    }

    private fun showCities() {
        if (cities.size == 0) {
            cities = ArrayList()
            initViewModel()
        } else {
            hideProgress()
        }
        showCitiesLayout()
    }

    private fun showProgress() {
        progressBar_cities.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        progressBar_cities.visibility = View.INVISIBLE
    }

    private fun initViewModel() {
        viewModel =
            ViewModelProviders.of(this, BaseViewModelFactory { CitiesViewModel((application as App), lang) })
                .get(CitiesViewModel::class.java)

        viewModel.getCities().observe(this, object : Observer<ArrayList<City>> {
            override fun onChanged(citiesList: ArrayList<City>?) {
                citiesList?.let {
                    if (cities != citiesList) {
                        cities = citiesList
                        adapter.setItems(cities)
                    }
                }

                recyclerView_cities.scrollBy(0, mStateScrollY)
                hideProgress()
            }
        })
    }

    private fun initAdapter() {
        val arguments = intent.extras
        val currentCity: String = arguments.getString(CURRENT_CITY_KEY)

        /*Create adapter with listener of click on element*/
        adapter = CityDataAdapter(cities, object : OnClickListener {
            override fun onCardViewClick(position: Int) {
                val intentRes = Intent()
                intentRes.putExtra(CITY_KEY, cities[position])
                /*remember the selected city and pass to main activity*/
                setResult(Activity.RESULT_OK, intentRes)
                finish()
            }
        }, currentCity)

        recyclerView_cities.adapter = adapter

        recyclerView_cities.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                mScrollY += dy
            }
        })
    }
}
