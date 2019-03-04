package com.yanyushkin.kudago.activities

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.adapters.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_detailing_event.*
import kotlinx.android.synthetic.main.card_view.*
import kotlinx.android.synthetic.main.toolbar_detailing_event.*

class DetailingEventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailing_event)

        val viewPager = pager
        val viewPagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter

        val circleIndicator = indicator
        circleIndicator.setViewPager(viewPager)

        val arguments = intent.extras//получаем данные из первой активити (id кликнутого события)

        if (arguments != null) {
            titleEvent.text = arguments.getString("title")
            shortDescriptionEvent.text = arguments.getString("description")
            fullDescriptionEvent.text = arguments.getString("full_description")
            textLocationEvent.text = arguments.getString("place")
            textDayEvent.text = arguments.getString("date")
            textPriceEvent.text = arguments.getString("price")
        }

        //main_layout_event.addView(buttonBack, 0)

        buttonShowRoute.setOnClickListener {
            val address = Uri.parse("http://maps.google.com/maps?saddr=My+Location&daddr=" +
                    (55.73324899999993).toString() + "," + (37.64659699999999).toString())
            val intentMap = Intent(Intent.ACTION_VIEW, address)
            startActivity(intentMap)
        }
        buttonBack.setOnClickListener { onBackPressed() }
    }
}
