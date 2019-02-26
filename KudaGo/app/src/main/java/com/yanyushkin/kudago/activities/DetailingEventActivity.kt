package com.yanyushkin.kudago.activities

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.adapters.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_detailing_event.*

class DetailingEventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailing_event)

        val viewPager = pager
        val viewPagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter

        val circleIndicator = indicator
        circleIndicator.setViewPager(viewPager)

        //main_layout_event.addView(buttonBack, 0)

        buttonShowRoute.setOnClickListener {
            val address = Uri.parse("geo:geo:-0.45609946,-90.26607513")
            val intentMap = Intent(Intent.ACTION_VIEW, address)
            startActivity(intentMap)
        }
        buttonBack.setOnClickListener { onBackPressed() }
    }
}
