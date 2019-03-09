package com.yanyushkin.kudago.activities

import android.animation.Animator
import android.animation.LayoutTransition
import android.app.Activity
import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Toast
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.adapters.EventDataAdapter
import com.yanyushkin.kudago.models.Event
import com.yanyushkin.kudago.network.*
import com.yanyushkin.kudago.utils.CheckInternet
import com.yanyushkin.kudago.utils.ErrorSnackBar
import com.yanyushkin.kudago.utils.OnClickListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private var events: ArrayList<Event> = ArrayList()
    private val BROADCAST_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
    private val intentFilter = IntentFilter(BROADCAST_ACTION)
    private val REQUEST_CODE_MESSAGE = 1000
    private var collapsed = false
    private var imagesOfEvents: ArrayList<ArrayList<String>> = ArrayList()
    private var placeOfEvents: ArrayList<Place?> = ArrayList()
    private var isLoading = false
    private var page = 1
    private val APP_PREFERENCES = "settings"
    private val APP_PREFERENCES_NAME_CITY = "nameOfCurrentCity"
    private val APP_PREFERENCES_SHORTNAME_CITY = "shortEnglishNameOfCurrentCity"
    private lateinit var nameOfCurrentCity: String
    private lateinit var shortEnglishNameOfCurrentCity: String
    private lateinit var pref: SharedPreferences
    private var lang = "en"

    /*receiver for monitoring connection changes*/
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent!!.action

            when (action) {
                BROADCAST_ACTION -> {
                    if (!CheckInternet.isHasInternet(this@MainActivity)) {
                        main_layout.visibility = View.INVISIBLE
                        relative_layout.visibility = View.VISIBLE
                        val sbError = ErrorSnackBar(relative_layout)
                        sbError.show(this@MainActivity)
                    } else {
                        if (events.size == 0) {
                            events = ArrayList()
                            imagesOfEvents = ArrayList()
                            placeOfEvents = ArrayList()
                            page = 1
                            initData()
                        }
                        main_layout.visibility = View.VISIBLE
                        relative_layout.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        if (Locale.getDefault().language == "ru") {
            lang = "ru"
        }

        getCurrentCity()


        /*Check Internet*/
        if (!CheckInternet.isHasInternet(this@MainActivity)) {
            main_layout.visibility = View.INVISIBLE
            relative_layout.visibility = View.VISIBLE
            progressBar.visibility = View.INVISIBLE

            val sbError = ErrorSnackBar(relative_layout)
            sbError.show(this)
        } else {
            main_layout.visibility = View.VISIBLE
            relative_layout.visibility = View.INVISIBLE
            events = ArrayList()
            imagesOfEvents = ArrayList()
            placeOfEvents = ArrayList()
            initData()
        }

        swipeRefreshLayout.setColorSchemeResources(R.color.colorRed)
        // указываем слушатель свайпов пользователя
        swipeRefreshLayout.setOnRefreshListener {
            // указываем, что мы уже сделали все, что нужно было
            progressBar.visibility = View.VISIBLE
            events = ArrayList()
            imagesOfEvents = ArrayList()
            placeOfEvents = ArrayList()
            page = 1
            initData()
        }

        appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
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

        textCity.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                textCity.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, R.anim.anim_alpha))
                buttonChoiceCity.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, R.anim.anim_alpha))
                return true
            }
        })

        buttonChoiceCity.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                 textCity.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, R.anim.anim_alpha))
                 buttonChoiceCity.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, R.anim.anim_alpha))
                return true
            }
        })

        textCity.setOnClickListener { selectCity() }
        buttonChoiceCity.setOnClickListener { selectCity() }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        //сохранение настроек приложения (выбранный город)
        val editor = pref.edit()
        editor.putString(APP_PREFERENCES_NAME_CITY, nameOfCurrentCity)
        editor.putString(APP_PREFERENCES_SHORTNAME_CITY, shortEnglishNameOfCurrentCity)
        editor.apply()

        unregisterReceiver(receiver)
        super.onPause()
    }

    fun getCurrentCity() {
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

    fun selectCity() {
        val intentSelectCity = Intent(this, CitiesListActivity::class.java)
        intentSelectCity.putExtra("currentCity", shortEnglishNameOfCurrentCity)
        startActivityForResult(intentSelectCity, REQUEST_CODE_MESSAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_MESSAGE -> {
                    nameOfCurrentCity = data!!.getStringExtra("nameOfSelectedCity")
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

    fun initData() {
        progressBar.visibility = View.VISIBLE

        isLoading = true

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
                                translatePlace(it.place),
                                translateDate(
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

                        imagesOfEvents.add(urls)
                        placeOfEvents.add(it.place)
                    }
                    isLoading = false
                    initRecyclerView()
                }

                override fun onFailure(errorMessage: String) {
                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                    isLoading = false
                }
            },
            System.currentTimeMillis() / 1000L,
            lang,
            shortEnglishNameOfCurrentCity,
            page
        )//1549040271 берем только те события, которые начинаются с сегодня, на таком-то языке и в таком-то городе с такой-то страницы
    }

    fun initRecyclerView() {
        val layoutManagerForRV = LinearLayoutManager(this)
        layoutManagerForRV.orientation = LinearLayout.VERTICAL
        recyclerViewMain.layoutManager = layoutManagerForRV

        /*Clear RV*/
        recyclerViewMain.removeAllViews()

        recyclerViewMain.apply {
            /*Create adapter*/
            val adapter = EventDataAdapter(events, object : OnClickListener {
                override fun onCardViewClick(position: Int) {
                    val intentDetailingEvent = Intent(this@MainActivity, DetailingEventActivity::class.java)
                    intentDetailingEvent.putExtra("id", events[position].id)
                    intentDetailingEvent.putExtra("title", events[position].title)
                    intentDetailingEvent.putExtra("description", events[position].description)
                    intentDetailingEvent.putExtra("fullDescription", events[position].fullDescription)
                    intentDetailingEvent.putExtra("place", events[position].place)
                    intentDetailingEvent.putExtra("date", events[position].dates)
                    intentDetailingEvent.putExtra("price", events[position].price)
                    intentDetailingEvent.putExtra("images", imagesOfEvents[position])
                    intentDetailingEvent.putExtra("coords", getLatAndLon(placeOfEvents[position]))
                    startActivity(intentDetailingEvent)
                }
            })
            /*Set a adapter for rv*/
            recyclerViewMain.adapter = adapter
        }

        recyclerViewMain.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemsCount = layoutManagerForRV.childCount//смотрим сколько элементов на экране
                val totalItemsCount = layoutManagerForRV.itemCount//сколько всего элементов
                val positionOfFirstVisibleItem =
                    layoutManagerForRV.findFirstVisibleItemPosition()//какая позиция первого элемента

                if (!isLoading) {
                    if ((visibleItemsCount + positionOfFirstVisibleItem) >= totalItemsCount) {
                        page++
                        progressBar.visibility = View.VISIBLE
                        initData()
                    }
                }
            }
        })

        swipeRefreshLayout.isRefreshing = false
        progressBar.visibility = View.INVISIBLE
    }

    fun translateDate(badStartDate: String?, badEndDate: String?): String {
        val months: Array<String> = arrayOf(
            "января",
            "февраля",
            "марта",
            "апреля",
            "мая",
            "июня",
            "июля",
            "августа",
            "сентября",
            "октября",
            "ноября",
            "декабря"
        )
        var resDate: String = ""

        if (badStartDate != null)
            resDate += badStartDate.substring(8) + " " + months[Integer.parseInt(
                badStartDate.substring(
                    5,
                    7
                )
            ) - 1] + " " + badStartDate.substring(0, 4)

        if (badEndDate != null) {
            if (resDate.isNotEmpty())
                resDate += " - "
            else
                resDate += "до "

            resDate += badEndDate.substring(8) + " " + months[Integer.parseInt(
                badEndDate.substring(
                    5,
                    7
                )
            ) - 1] + " " + badEndDate.substring(0, 4);
        }

        return resDate
    }

    fun translatePlace(badPlace: Place?): String {
        if (badPlace != null) {
            if (badPlace.title != null)
                return badPlace.title
            if (badPlace.address != null)
                return badPlace.address
        }

        return ""
    }

    fun getLatAndLon(place: Place?): ArrayList<Double> {
        val res: ArrayList<Double> = ArrayList()

        if (place != null && place.coords != null) {
            if (place.coords.lat != null)
                res.add(place.coords.lat)

            if (place.coords.lon != null)
                res.add(place.coords.lon)
        }

        return res
    }
}
