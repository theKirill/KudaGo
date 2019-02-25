package com.yanyushkin.kudago.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.adapters.DataAdapter
import com.yanyushkin.kudago.models.Event
import com.yanyushkin.kudago.utils.CheckInternet
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : AppCompatActivity() {
    var events: ArrayList<Event> = ArrayList<Event>();
    val BROADCAST_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
    private val intentFilter = IntentFilter(BROADCAST_ACTION)

    /*receiver for monitoring connection changes*/
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent!!.action

            when (action) {
                BROADCAST_ACTION -> {
                    if (!CheckInternet.isHasInternet(this@MainActivity)) {
                        coordinator_layout.visibility = View.INVISIBLE
                        relative_layout.visibility = View.VISIBLE
                    } else {
                        coordinator_layout.visibility = View.VISIBLE
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

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewMain)

        /*Create adapter*/
        val adapter = DataAdapter(events)

        /*Set a adapter for list*/
        recyclerView.adapter = adapter

        /*Check Internet*/
        if (!CheckInternet.isHasInternet(this@MainActivity)) {
            coordinator_layout.visibility = View.INVISIBLE
            relative_layout.visibility = View.VISIBLE

            //УЗНАТЬ!!!!!!!
            val sb = Snackbar.make(relative_layout, R.string.noConnectionBottom, Snackbar.LENGTH_LONG)
            val snackbarView = sb.view
            snackbarView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorError))
            sb.show()
        }
    }

    override fun onPause() {
        unregisterReceiver(receiver)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        init()
        registerReceiver(receiver, intentFilter)
    }

    fun cardOnClick(v: View){
        var myIntent = Intent(this, DetailingEventActivity::class.java)
        startActivity(myIntent)
    }

    fun init() {
        events.add(
            Event(
                "МУЗЫКАЛЬНЫЙ ФЕСТИВАЛЬ LIVEFEST SUMMER",
                "Первый фестиваль LiveFest на курорте Роза Хутор собрал перспективные музыкальные группы этого года",
                "ЦПКиО им. Горького",
                "10-11 августа",
                "1 200 - 1 500 Р",
                R.drawable.ic_photo_camera_black_24dp
            )
        )
        events.add(
            Event(
                "Рестобар Чеширский кот",
                "Из центра Москвы - прямиком в Зазеркалье! В рестобаре вас встретят улыбчивый кот и другие",
                "ул. Кузнецкий Мост, д. 19/1",
                "",
                "2 500 Р",
                R.drawable.ic_photo_camera_black_24dp
            )
        )
        events.add(
            Event(
                "Ночь музеев в Москве",
                "В ночь с субботы на воскресенье 19 и 20 мая музеи столицы будут открыты с шести вечера до",
                "Все музеи Москвы",
                "10-11 августа",
                "1 200 - 1 500 Р",
                R.drawable.ic_photo_camera_black_24dp
            )
        )
    }
}
