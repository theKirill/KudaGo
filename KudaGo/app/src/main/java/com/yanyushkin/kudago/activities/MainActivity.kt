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
import com.google.gson.Gson
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.adapters.EventDataAdapter
import com.yanyushkin.kudago.models.Event
import com.yanyushkin.kudago.network.*
import com.yanyushkin.kudago.utils.CheckInternet
import com.yanyushkin.kudago.utils.ErrorSnackBar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        /*Create adapter*/
        var adapter = EventDataAdapter(events)

        /*Check Internet*/
        if (!CheckInternet.isHasInternet(this@MainActivity)) {
            main_layout.visibility = View.INVISIBLE
            relative_layout.visibility = View.VISIBLE

            val sbError = ErrorSnackBar(relative_layout)
            sbError.show(this)
        } else {
            main_layout.visibility = View.VISIBLE
            relative_layout.visibility = View.INVISIBLE
            initData()
            /*Set a adapter for list*/
            recyclerViewMain.adapter = adapter
        }

        // указываем слушатель свайпов пользователя
        swipeRefreshLayout.setOnRefreshListener {
            /*events = ArrayList<Event>()
            initData()
            events.add(
                Event(
                    4,
                    "ЕЩЕ КАКОЕ-ТО СОБЫТИЕ",
                    "Первый фестиваль LiveFest на курорте Роза Хутор собрал перспективные музыкальные группы этого года",
                    "ЦПКиО им. Горького",
                    "10-11 августа",
                    "1 200 - 1 500 Р",
                    R.drawable.ic_photo_camera_black_24dp
                )
            )*/
            // указываем, что мы уже сделали все, что нужно было
            swipeRefreshLayout.isRefreshing = false

            /*val rv = recyclerViewMain is RecyclerView
            rv.adapter!!.notifyDataSetChanged()*/
            adapter = EventDataAdapter(events)
            recyclerViewMain.removeAllViews()
            recyclerViewMain.adapter = adapter
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
        val myIntent = Intent(this, DetailingEventActivity::class.java)
        startActivity(myIntent)
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

    fun initData() {
          /*events.add(
              Event(1,
                  "МУЗЫКАЛЬНЫЙ ФЕСТИВАЛЬ LIVEFEST SUMMER",
                  "Первый фестиваль LiveFest на курорте Роза Хутор собрал перспективные музыкальные группы этого года",
                  "ЦПКиО им. Горького",
                  "10-11 августа",
                  "1 200 - 1 500 Р",
                  R.drawable.muz
              )
          )
          events.add(
              Event(2,
                  "Рестобар Чеширский кот",
                  "Из центра Москвы - прямиком в Зазеркалье! В рестобаре вас встретят улыбчивый кот и другие",
                  "ул. Кузнецкий Мост, д. 19/1",
                  "",
                  "2 500 Р",
                  R.drawable.kek
              )
          )
          events.add(
              Event(3,
                  "Ночь музеев в Москве",
                  "В ночь с субботы на воскресенье 19 и 20 мая музеи столицы будут открыты с шести вечера до",
                  "Все музеи Москвы",
                  "10-11 августа",
                  "1 200 - 1 500 Р",
                  R.drawable.ic_photo_camera_black_24dp
              )
          )
*/
        EventsRepository.instance.getEvents(object : ResponseCallback<EventsResponse> {

            override fun onSuccess(apiResponse: EventsResponse) {
                textCity.text = apiResponse.events.get(0).title
                for (i in 0..apiResponse.events.size-1)
                events.add(
                    Event(apiResponse.events[i].id,
                        apiResponse.events[i].title,
                        apiResponse.events[i].description.replace("<p>", "").replace("<?p>", "").replace("</p>", ""),
                        "",
                        apiResponse.events[i].date[0].start_date+" до "+apiResponse.events[i].date[0].end_date,
                        apiResponse.events[i].price,
                        apiResponse.events[i].images[0].image
                    )
                )
                var adapter = EventDataAdapter(events)
                adapter = EventDataAdapter(events)
                recyclerViewMain.removeAllViews()
                recyclerViewMain.adapter = adapter
            }

            override fun onFailure(errorMessage: String) {
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }
}
