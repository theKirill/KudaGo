package com.yanyushkin.kudago.activities

import android.app.Activity
import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.adapters.EventDataAdapter
import com.yanyushkin.kudago.models.City
import com.yanyushkin.kudago.models.Event
import com.yanyushkin.kudago.network.*
import com.yanyushkin.kudago.utils.CheckInternet
import com.yanyushkin.kudago.utils.ErrorSnackBar
import com.yanyushkin.kudago.utils.OnClickListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private var events: ArrayList<Event> = ArrayList()
    private val BROADCAST_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
    private val intentFilter = IntentFilter(BROADCAST_ACTION)
    private val REQUEST_CODE_MESSAGE = 1000
    private var isLoading = false
    private lateinit var pref: SharedPreferences
    private val APP_PREFERENCES = "settings"
    private val APP_PREFERENCES_NAME_CITY = "nameOfCurrentCity"
    private val APP_PREFERENCES_SHORTNAME_CITY = "shortEnglishNameOfCurrentCity"
    private val APP_EVENTS = "events"
    private lateinit var nameOfCurrentCity: String
    private lateinit var shortEnglishNameOfCurrentCity: String
    private var page = 1
    private var lang = "en"
    private var actualSince: Long = 0
    private lateinit var adapter: EventDataAdapter
    private val repository: Repository = Repository.instance
    private var isHasInternet: Boolean = false
    private var positionOfFirstVisibleItem = 0
    private val ARGS_SCROLL_POSITION = "position"

    /*receiver for monitoring connection changes*/
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action: String

            if (intent != null) {
                action = intent.action

                when (action) {
                    BROADCAST_ACTION -> {
                        isHasInternet = false

                        val check = CoroutineScope(Dispatchers.IO).async {
                            //using Coroutines
                            CheckInternet.isHasInternet(this@MainActivity)
                        }

                        GlobalScope.launch(Dispatchers.Main) {
                            isHasInternet = check.await()
                            if (!isHasInternet) {
                                showErrorNoInternet()
                            } else {
                                showEvents()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        setCharacteristicsForRequests()

        getSavedState(savedInstanceState)

        initSwipeRefreshListener()

        textCity.setOnClickListener { selectCity() }
        button_choiceCity.setOnClickListener { selectCity() }

        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, intentFilter)
    }

    /*saving the settings of app (selected city)*/
    override fun onPause() {
        val editor = pref.edit()
        editor.putString(APP_PREFERENCES_NAME_CITY, nameOfCurrentCity)
        editor.putString(APP_PREFERENCES_SHORTNAME_CITY, shortEnglishNameOfCurrentCity)
        editor.apply()
        unregisterReceiver(receiver)
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        if (outState != null) {
            outState.clear()
            outState.putSerializable(APP_PREFERENCES_NAME_CITY, nameOfCurrentCity)
            outState.putSerializable(APP_EVENTS, events)
            val layoutManagerForRV = recyclerView_events.layoutManager as LinearLayoutManager
            outState.putInt(ARGS_SCROLL_POSITION, layoutManagerForRV.findFirstVisibleItemPosition())
        }
    }

    private fun showErrorLayout() {
        layout_main_events.visibility = View.INVISIBLE
        layout_error_internet_events.visibility = View.VISIBLE
    }

    private fun showEventsLayout() {
        layout_main_events.visibility = View.VISIBLE
        layout_error_internet_events.visibility = View.INVISIBLE
    }

    private fun hideErrorLayout() {
        layout_error_internet_events.visibility = View.INVISIBLE
    }

    private fun hideMainLayout() {
        layout_main_events.visibility = View.INVISIBLE
    }

    private fun showProgress() {
        if (!layout_swipe_events.isRefreshing)
            progressBar_events.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        layout_swipe_events.isRefreshing = false
        progressBar_events.visibility = View.INVISIBLE
    }

    private fun showErrorNoInternet() {
        if (events.size == 0)
            showErrorLayout()
        hideProgress()
        val sbError = ErrorSnackBar(layout_error_internet_events)
        sbError.show(this)
    }

    private fun showEvents() {
        if (events.size == 0) {
            events = ArrayList()
            page = 1
            initData()
        } else {
            adapter.setItems(events)
            showEventsLayout()
            recyclerView_events.adapter = adapter
        }
        recyclerView_events.scrollToPosition(positionOfFirstVisibleItem)
    }

    private fun setCharacteristicsForRequests() {
        /*remember the actual time for requests (POSIX time)*/
        actualSince = System.currentTimeMillis() / 1000L

        /*getting of current language of system
        * if Russian language, we will make requests in Russian
        * else in English*/
        if (Locale.getDefault().language == "ru") {
            lang = "ru"
        }
    }

    fun getSavedState(savedInstanceState: Bundle?) {
        /*check for saved state*/
        if (savedInstanceState == null || !!savedInstanceState.containsKey(APP_PREFERENCES_NAME_CITY)) {
            getSavedLastSelectedCity()
        } else {
            textCity.text = savedInstanceState.getSerializable(APP_PREFERENCES_NAME_CITY).toString()
        }
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("events")) {
                events = savedInstanceState.getSerializable(APP_EVENTS) as ArrayList<Event>
            }
            if (savedInstanceState.containsKey(ARGS_SCROLL_POSITION)) {
                positionOfFirstVisibleItem =
                    savedInstanceState.getInt(ARGS_SCROLL_POSITION) //look where we stopped before the change of orientation
            }
        }
    }

    /*find out last selected city from SharedPreferences (if it exists)*/
    private fun getSavedLastSelectedCity() {
        pref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

        if (pref.contains(APP_PREFERENCES_NAME_CITY) && pref.contains(APP_PREFERENCES_SHORTNAME_CITY)) {
            nameOfCurrentCity = pref.getString(APP_PREFERENCES_NAME_CITY, getString(R.string.nameCityBegin))
            shortEnglishNameOfCurrentCity =
                pref.getString(APP_PREFERENCES_SHORTNAME_CITY, getString(R.string.shortNameCityBegin))
        } else {
            nameOfCurrentCity = getString(R.string.nameCityBegin)
            shortEnglishNameOfCurrentCity = getString(R.string.shortNameCityBegin)
        }
        textCity.text = nameOfCurrentCity
    }

    private fun initSwipeRefreshListener() {
        layout_swipe_events.setColorSchemeResources(R.color.colorRed)
        layout_swipe_events.setOnRefreshListener {
            if (isHasInternet) {
                events = ArrayList()
                page = 1
                initData()
            } else
                layout_swipe_events.isRefreshing = false
        }
    }

    private fun selectCity() {
        val intentSelectCity = Intent(this, CitiesListActivity::class.java)
        intentSelectCity.putExtra("currentCity", shortEnglishNameOfCurrentCity)
        startActivityForResult(intentSelectCity, REQUEST_CODE_MESSAGE)
    }

    /*remember new selected city and execute the request*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_MESSAGE -> {
                    if (data != null) {
                        val selectedCity = data.getSerializableExtra("city") as City
                        nameOfCurrentCity = selectedCity.nameInfo
                        shortEnglishNameOfCurrentCity = selectedCity.shortEnglishNameInfo
                        textCity.text = nameOfCurrentCity
                        events = ArrayList()
                        page = 1
                        hideMainLayout()
                        initData()
                    }
                }
            }
        }
    }

    private fun getEvents() {
        /*get actual events on installed language from selected city from necessary page and add it to ArrayList of events*/
        repository.getEvents(
            object : ResponseCallback<EventsResponse> {
                override fun onSuccess(apiResponse: EventsResponse) {
                    apiResponse.events.forEach {
                        events.add(it.transfrom())
                    }

                    isLoading = false

                    if (page > 1) {
                        /*don`t move to top of the RV, because we add data to end*/
                        adapter.addItems()

                    } else {
                        adapter.setItems(events)
                        recyclerView_events.adapter = adapter
                    }

                    showEventsLayout()
                    hideProgress()
                }

                override fun onFailure(errorMessage: String) {
                    hideProgress()
                    isLoading = false
                }
            },
            actualSince,
            lang,
            shortEnglishNameOfCurrentCity,
            page
        )
    }

    private fun initData() {
        hideErrorLayout()
        showProgress()
        isLoading = true
        getEvents()
    }

    private fun initAdapter() {
        /*Create adapter with listener of click on element*/
        adapter = EventDataAdapter(events, object : OnClickListener {
            override fun onCardViewClick(position: Int) {
                val intentDetailingEvent = Intent(this@MainActivity, DetailingEventActivity::class.java)
                intentDetailingEvent.putExtra("event", events[position])
                startActivity(intentDetailingEvent)
            }
        })
    }

    private fun initRecyclerView() {
        val layoutManagerForRV = LinearLayoutManager(this)
        layoutManagerForRV.orientation = LinearLayout.VERTICAL
        recyclerView_events.layoutManager = layoutManagerForRV

        recyclerView_events.removeAllViews()

        recyclerView_events.apply {
            /*Set a adapter for rv*/
            initAdapter()
            recyclerView_events.adapter = adapter
        }

        /*listener of the end of the list (add data, new request from the next page)*/
        recyclerView_events.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemsCount = layoutManagerForRV.childCount//how many elements on the screen
                val totalItemsCount = layoutManagerForRV.itemCount//how many elements total
                positionOfFirstVisibleItem =
                    layoutManagerForRV.findFirstVisibleItemPosition()//position of the 1st element

                if (!isLoading && isHasInternet) {
                    if ((visibleItemsCount + positionOfFirstVisibleItem) >= totalItemsCount) {
                        page++
                        showProgress()
                        initData()
                    }
                }
            }
        })
        hideProgress()
    }
}