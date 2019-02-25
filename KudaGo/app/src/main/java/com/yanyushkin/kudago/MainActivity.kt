package com.yanyushkin.kudago

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import com.yanyushkin.kudago.adapters.DataAdapter
import com.yanyushkin.kudago.models.Event
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : AppCompatActivity() {
    var events: ArrayList<Event> = ArrayList<Event>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        var recyclerView = findViewById<RecyclerView>(R.id.recyclerViewMain)

        /*Create adapter*/
        var adapter = DataAdapter(events)

        /*Set a adapter for list*/
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        init()


    }

    fun init() {
        events.add(Event("МУЗЫКАЛЬНЫЙ ФЕСТИВАЛЬ LIVEFEST SUMMER", "Первый фестиваль LiveFest на курорте Роза Хутор собрал перспективные музыкальные группы этого года", "ЦПКиО им. Горького", "10-11 августа", "1 200 - 1 500 Р", R.drawable.ic_photo_camera_black_24dp))
        events.add(Event("Рестобар Чеширский кот", "Из центра Москвы - прямиком в Зазеркалье! В рестобаре вас встретят улыбчивый кот и другие", "ул. Кузнецкий Мост, д. 19/1", "", "2 500 Р", R.drawable.ic_photo_camera_black_24dp))
        events.add(Event("Ночь музеев в Москве", "В ночь с субботы на воскресенье 19 и 20 мая музеи столицы будут открыты с шести вечера до", "Все музеи Москвы", "10-11 августа", "1 200 - 1 500 Р", R.drawable.ic_photo_camera_black_24dp))
    }
}
