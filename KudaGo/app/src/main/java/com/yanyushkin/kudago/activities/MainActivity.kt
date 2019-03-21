package com.yanyushkin.kudago.activities

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.ViewModels.BaseViewModelFactory
import com.yanyushkin.kudago.adapters.EventDataAdapter
import com.yanyushkin.kudago.models.Event
import com.yanyushkin.kudago.network.*
import com.yanyushkin.kudago.utils.CheckInternet
import com.yanyushkin.kudago.utils.ErrorSnackBar
import com.yanyushkin.kudago.utils.OnClickListener
import com.yanyushkin.kudago.utils.Tools
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
    private var collapsed = false
    private var imagesOfEvents: ArrayList<ArrayList<String>> = ArrayList()
    private var placeOfEvents: ArrayList<Place?> = ArrayList()
    private var isLoading = false
    private lateinit var pref: SharedPreferences
    private val APP_PREFERENCES = "settings"
    private val APP_PREFERENCES_NAME_CITY = "nameOfCurrentCity"
    private val APP_PREFERENCES_SHORTNAME_CITY = "shortEnglishNameOfCurrentCity"
    private lateinit var nameOfCurrentCity: String
    private lateinit var shortEnglishNameOfCurrentCity: String
    private var page = 1
    private var lang = "en"
    private var actualSince: Long = 0
    private lateinit var adapter: EventDataAdapter

    /*receiver for monitoring connection changes*/
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

        /*check for saved state*/
        if (savedInstanceState == null || !!savedInstanceState.containsKey(APP_PREFERENCES_NAME_CITY)) {
            getSavedLastSelectedCity()
        } else {
            textCity.text = savedInstanceState.getSerializable(APP_PREFERENCES_NAME_CITY).toString()
        }

        initSwipeRefreshListener()

        changingLogoSize()

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
        }
    }

    private fun showErrorNoInternet() {
        layout_main_events.visibility = View.INVISIBLE
        layout_error_internet_events.visibility = View.VISIBLE
        progressBar_events.visibility = View.INVISIBLE
        val sbError = ErrorSnackBar(layout_error_internet_events)
        sbError.show(this)
    }

    private fun showEvents() {
        if (events.size == 0) {
            events = ArrayList()
            imagesOfEvents = ArrayList()
            placeOfEvents = ArrayList()
            page = 1
            initData()
        }
        layout_main_events.visibility = View.VISIBLE
        layout_error_internet_events.visibility = View.INVISIBLE
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
            events = ArrayList()
            imagesOfEvents = ArrayList()
            placeOfEvents = ArrayList()
            page = 1
            initData()
        }
    }

    /*changing logo size when scrolling*/
    private fun changingLogoSize() {
        appbar_events.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (Math.abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                if (!collapsed) {
                    collapsed = true
                    imageLogo.scaleX = 0.9f
                    imageLogo.scaleY = 0.9f
                }
            } else {
                if (collapsed) {
                    collapsed = false
                    imageLogo.scaleX = 1.0f
                    imageLogo.scaleY = 1.0f
                }
            }
        })
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
                        nameOfCurrentCity = data.getStringExtra("nameOfSelectedCity")
                        shortEnglishNameOfCurrentCity = data.getStringExtra("shortEnglishNameOfSelectedCity")
                        textCity.text = nameOfCurrentCity
                        events = ArrayList()
                        imagesOfEvents = ArrayList()
                        placeOfEvents = ArrayList()
                        page = 1
                        initData()
                    }
                }
            }
        }
    }

    private fun initData() {
        progressBar_events.visibility = View.VISIBLE

        isLoading = true
        /*get actual events on installed language from selected city from necessary page and add it to ArrayList of events*/
        Repository.instance.getEvents(
            object : ResponseCallback<EventsResponse> {
                override fun onSuccess(apiResponse: EventsResponse) {
                    apiResponse.events.forEach {
                        events.add(
                            Event(
                                it.id,
                                it.title,
                                it.description,
                                it.body_text,
                                Tools.translatePlace(it.place),
                                Tools.translateDate(
                                    it.date[0].start_date,
                                    it.date[0].end_date
                                )
                                ,
                                it.price,
                                it.images[0].image
                            )
                        )

                        val urls = ArrayList<String>()
                        it.images.forEach {
                            urls.add(it.image)
                        }

                        /*remember images and place for each event*/
                        imagesOfEvents.add(urls)
                        placeOfEvents.add(it.place)
                    }

                    isLoading = false

                    if (page > 1) {
                        /*don`t move to top of the RV, because we add data to end*/
                        recyclerView_events.adapter!!.notifyItemInserted(events.size - 1)
                    } else {
                        adapter.setItems(events)
                        recyclerView_events.adapter=adapter
                    }
                    layout_swipe_events.isRefreshing = false
                    progressBar_events.visibility = View.INVISIBLE
                }

                override fun onFailure(errorMessage: String) {
                    progressBar_events.visibility = View.INVISIBLE
                    isLoading = false
                }
            },
            actualSince,
            lang,
            shortEnglishNameOfCurrentCity,
            page
        )
    }

    private fun initAdapter() {
        /*Create adapter with listener of click on element*/
        adapter = EventDataAdapter(events, object : OnClickListener {
            override fun onCardViewClick(position: Int) {
                val intentDetailingEvent = Intent(this@MainActivity, DetailingEventActivity::class.java)
                intentDetailingEvent.putExtra("id", events[position].idInfo)
                intentDetailingEvent.putExtra("title", events[position].titleInfo)
                intentDetailingEvent.putExtra("description", events[position].descriptionInfo)
                intentDetailingEvent.putExtra("fullDescription", events[position].fullDescriptionInfo)
                intentDetailingEvent.putExtra("place", events[position].placeInfo)
                intentDetailingEvent.putExtra("date", events[position].datesInfo)
                intentDetailingEvent.putExtra("price", events[position].priceInfo)
                intentDetailingEvent.putExtra("images", imagesOfEvents[position])
                 intentDetailingEvent.putExtra("coords", Tools.getLatAndLon(placeOfEvents[position]))
                //intentDetailingEvent.putExtra(Event::class.java.simpleName, events[position])
                /*pass the necessary data to detailing activity*/
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
                val positionOfFirstVisibleItem =
                    layoutManagerForRV.findFirstVisibleItemPosition()//position of the 1st element

                if (!isLoading) {
                    if ((visibleItemsCount + positionOfFirstVisibleItem) >= totalItemsCount) {
                        page++
                        progressBar_events.visibility = View.VISIBLE
                        initData()
                    }
                }
            }
        })

        layout_swipe_events.isRefreshing = false
        progressBar_events.visibility = View.INVISIBLE
    }
}