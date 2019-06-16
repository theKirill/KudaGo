package com.yanyushkin.kudago.activities

import android.app.Activity
import android.content.*
import android.content.res.Resources
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.adapters.EventDataAdapter
import com.yanyushkin.kudago.App
import com.yanyushkin.kudago.models.City
import com.yanyushkin.kudago.models.Event
import com.yanyushkin.kudago.database.DatabaseService
import com.yanyushkin.kudago.network.*
import com.yanyushkin.kudago.utils.CheckInternet
import com.yanyushkin.kudago.utils.ErrorSnackBar
import com.yanyushkin.kudago.utils.OnClickListener
import com.yanyushkin.kudago.utils.SettingsOfApp
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import io.realm.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var repository: Repository
    @Inject
    lateinit var realm: Realm
    @Inject
    lateinit var serviceDB: DatabaseService
    private var events: ArrayList<Event> = ArrayList()
    private val BROADCAST_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
    private val intentFilter = IntentFilter(BROADCAST_ACTION)
    private val REQUEST_CODE_MESSAGE = 1000
    private var isLoading = false
    private val EVENTS_KEY = "events"
    private val EVENT_KEY = "event"
    private val CURRENT_CITY_KEY = "currentCity"
    private val CITY_KEY = "city"
    private lateinit var nameOfCurrentCity: String
    private lateinit var shortEnglishNameOfCurrentCity: String
    private var page = 1
    private var lang = "en"
    private var actualSince: Long = 0
    private lateinit var adapter: EventDataAdapter
    private var isHasInternet: Boolean = false
    private var positionOfFirstVisibleItem = 0
    private val SCROLL_POSITION_KEY = "position"

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
                                doWithoutInternet()
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

        /*get repository, realm, databaseService with dagger*/
        (application as App).getAppComponent().injectsMainActivity(this)

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
        SettingsOfApp.saveSelectedCity(nameOfCurrentCity, shortEnglishNameOfCurrentCity)
        unregisterReceiver(receiver)
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.let {
            outState.clear()
            outState.putSerializable(CURRENT_CITY_KEY, nameOfCurrentCity)
            outState.putSerializable(EVENTS_KEY, events)
            val layoutManagerForRV = recyclerView_events.layoutManager as LinearLayoutManager
            outState.putInt(SCROLL_POSITION_KEY, layoutManagerForRV.findFirstVisibleItemPosition())
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

    private fun doWithoutInternet() {
        val eventsFromDB = serviceDB.getEventsFromCity(City(nameOfCurrentCity, shortEnglishNameOfCurrentCity))

        if (events.size == 0 && eventsFromDB.size == 0)
            showErrorLayout()
        else
            if (events.size == 0 && eventsFromDB.size > 0) {
                events = eventsFromDB
                adapter.setItems(events)
                showEventsLayout()
                recyclerView_events.adapter = adapter
                Toast.makeText(this, "Загружены последние сохранённые данные!", Toast.LENGTH_LONG).show()
            } else
                showEvents()

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

    private fun getSavedState(savedInstanceState: Bundle?) {
        /*check for saved state*/
        if (savedInstanceState == null || !!savedInstanceState.containsKey(CURRENT_CITY_KEY)) {
            getSavedLastSelectedCity()
        } else {
            textCity.text = savedInstanceState.getSerializable(CURRENT_CITY_KEY).toString()
        }

        savedInstanceState?.let {
            if (savedInstanceState.containsKey(EVENTS_KEY)) {
                events = savedInstanceState.getSerializable(EVENTS_KEY) as ArrayList<Event>
            }
            if (savedInstanceState.containsKey(SCROLL_POSITION_KEY)) {
                positionOfFirstVisibleItem =
                    savedInstanceState.getInt(SCROLL_POSITION_KEY) //look where we stopped before the change of orientation
            }
        }
    }

    private fun getSavedLastSelectedCity() {
        val currentCity = SettingsOfApp.getSavedLastSelectedCity(this)
        nameOfCurrentCity = currentCity.nameInfo
        shortEnglishNameOfCurrentCity = currentCity.shortEnglishNameInfo
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
        intentSelectCity.putExtra(CURRENT_CITY_KEY, shortEnglishNameOfCurrentCity)
        startActivityForResult(intentSelectCity, REQUEST_CODE_MESSAGE)
    }

    /*remember new selected city and execute the request*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_MESSAGE -> {
                    data?.let {
                        val selectedCity = data.getSerializableExtra(CITY_KEY) as City
                        nameOfCurrentCity = selectedCity.nameInfo
                        shortEnglishNameOfCurrentCity = selectedCity.shortEnglishNameInfo
                        textCity.text = nameOfCurrentCity
                        events = ArrayList()
                        page = 1
                        hideMainLayout()
                    }
                }
            }
        }
    }

    private fun initData() {
        hideErrorLayout()
        showProgress()
        isLoading = true
        getEvents()
    }

    private fun getEvents() {
        if (page == 1) {
            serviceDB.deleteOldEventsFromCity(City(nameOfCurrentCity, shortEnglishNameOfCurrentCity))
        }

        /*get actual events on installed language from selected city from necessary page and add it to ArrayList of events*/
        repository.getEvents(
            object : ResponseCallback<EventsResponse> {
                override fun onSuccess(apiResponse: EventsResponse) {

                    apiResponse.events.forEach {
                        events.add(it.transform())
                    }

                    serviceDB.addEvents(City(nameOfCurrentCity, shortEnglishNameOfCurrentCity), events)

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
                        initData()
                    }
                }
            }
        })
        hideProgress()
    }

    private fun initAdapter() {
        /*Create adapter with listener of click on element*/
        adapter = EventDataAdapter(events, object : OnClickListener {
            override fun onCardViewClick(position: Int) {
                val intentDetailingEvent = Intent(this@MainActivity, DetailingEventActivity::class.java)
                intentDetailingEvent.putExtra(EVENT_KEY, events[position])
                startActivity(intentDetailingEvent)
            }
        })
    }
}