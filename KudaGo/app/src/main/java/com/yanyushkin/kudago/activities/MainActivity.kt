package com.yanyushkin.kudago.activities

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.view.View
import android.widget.Toast
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.adapters.EventDataAdapter
import com.yanyushkin.kudago.models.Event
import com.yanyushkin.kudago.network.*
import com.yanyushkin.kudago.utils.CheckInternet
import com.yanyushkin.kudago.utils.ErrorSnackBar
import com.yanyushkin.kudago.utils.OnEventClickListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.card_view.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : AppCompatActivity() {
    var events: ArrayList<Event> = ArrayList<Event>()
    val BROADCAST_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
    private val REQUEST_CODE_MESSAGE = 1
    private val intentFilter = IntentFilter(BROADCAST_ACTION)
    private var collapsed = false

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
            initData()

            initRecyclerView()
        }

        // указываем слушатель свайпов пользователя
        swipeRefreshLayout.setOnRefreshListener {
            // указываем, что мы уже сделали все, что нужно было
            progressBar.visibility = View.VISIBLE
            events = ArrayList<Event>()
            initData()
            swipeRefreshLayout.isRefreshing = false
            //обновляем
            initRecyclerView()
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

        textCity.setOnClickListener { chooseCity() }
        buttonChoiceCity.setOnClickListener { chooseCity() }
    }

    fun chooseCity() {
        val myIntent = Intent(this, CitiesListActivity::class.java)
        startActivityForResult(myIntent, REQUEST_CODE_MESSAGE)
    }

    override fun onPause() {
        unregisterReceiver(receiver)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, intentFilter)
    }

    fun cardOnClick(v: View) {
       /* val myIntent = Intent(this, DetailingEventActivity::class.java)
        startActivity(myIntent)*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_MESSAGE -> {
                    textCity.text = data!!.getStringExtra("city")
                }
            }
        } else {
            textCity.text = "Москва"
            Toast.makeText(this, "Произошла непредвиденная ошибка!", Toast.LENGTH_SHORT).show()
        }
    }

    fun initRecyclerView() {
        /*Clear RV*/
        recyclerViewMain.removeAllViews()

        recyclerViewMain.apply {
            /*Create adapter*/
            val adapter = EventDataAdapter(events, object: OnEventClickListener{
                override fun onEventCardViewClick(position: Int) {
                    val intentDetailingEvent = Intent(this@MainActivity, DetailingEventActivity::class.java)
                    intentDetailingEvent.putExtra("id", events[position].id)
                    intentDetailingEvent.putExtra("title", events[position].title)
                    intentDetailingEvent.putExtra("description", events[position].description)
                    intentDetailingEvent.putExtra("full_description", events[position].full_description)
                    intentDetailingEvent.putExtra("place", events[position].place)
                    intentDetailingEvent.putExtra("date", events[position].dates)
                    intentDetailingEvent.putExtra("price", events[position].price)
                    intentDetailingEvent.putExtra("images", events[position].images)
                    startActivity(intentDetailingEvent)
                }
            })
            /*Set a adapter for list*/
            recyclerViewMain.adapter = adapter
        }

        //val adapter = EventDataAdapter(events, )

    }

    fun initEventCardViewClickListener() {
        recyclerViewMain.apply {
            val adapter = EventDataAdapter(events, object: OnEventClickListener{
                override fun onEventCardViewClick(position: Int) {
                    val myIntent = Intent(this@MainActivity, DetailingEventActivity::class.java)
                    startActivity(myIntent)
                }
            })
        }
    }

    fun initData() {
        EventsRepository.instance.getEvents(
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
                    }

                    progressBar.visibility = View.INVISIBLE
                    initRecyclerView()
                }

                override fun onFailure(errorMessage: String) {
                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            },
            System.currentTimeMillis() / 1000L,
            "ru",
            "spb"
        )//1549040271 берем только те события, которые начинаются с сегодня, на таком-то языке и в таком-то городе
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
            if (resDate.length > 0)
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

    // apiResponse.events[i].description.replace("<p>", "").replace("<?p>", "").replace(
    //                                "</p>",
    //                                ""
    //                            )
}
