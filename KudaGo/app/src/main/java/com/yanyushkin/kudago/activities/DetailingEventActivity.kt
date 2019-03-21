package com.yanyushkin.kudago.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.LinearLayout
import com.google.android.gms.maps.*
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.adapters.ViewPagerAdapter
import com.yanyushkin.kudago.models.Event
import com.yanyushkin.kudago.utils.Maps
import kotlinx.android.synthetic.main.activity_detailing_event.*
import kotlinx.android.synthetic.main.toolbar_detailing_event.*

class DetailingEventActivity : AppCompatActivity() {
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private lateinit var event: Event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailing_event)

        fillingActivity()

        /*open app to show route (from current location to the destination)*/
        button_showRoute.setOnClickListener { showRoute() }

        button_back.setOnClickListener { onBackPressed() }
    }

    private fun fillingActivity() {
        /*get data from main activity about selected event*/
        val arguments = intent.extras
        event = arguments.get("event") as Event

        /*filling views*/
        setImages()
        setTitle()
        setShortDescription()
        setFullDescription()
        setPlace()
        setDate()
        setPrice()
        initMaps()
    }

    private fun setImages() {
        val images =event.imagesURLInfo
        if (images.size > 0) {
            val viewPager = pager

            /*create adapter for ViewPager with received images*/
            val viewPagerAdapter = ViewPagerAdapter(this, images)
            viewPager.adapter = viewPagerAdapter

            val circleIndicator = indicator
            circleIndicator.setViewPager(viewPager)
        }
    }

    private fun setTitle() {
        title_event.text = event.titleInfo
    }

    private fun setShortDescription() {
        val shortDescriptionWithHtml = event.descriptionInfo
        /*replacing tags*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            shortDescription_event.text = Html.fromHtml(shortDescriptionWithHtml, Html.FROM_HTML_MODE_LEGACY).trim()
        } else {
            shortDescription_event.text = Html.fromHtml(shortDescriptionWithHtml).trim()
        }
    }

    private fun setFullDescription() {
        val fullDescriptionWithHtml = event.fullDescriptionInfo
        //fullDescription_event.movementMethod = LinkMovementMethod.getInstance()
        /*replacing tags*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fullDescription_event.text = Html.fromHtml(fullDescriptionWithHtml, Html.FROM_HTML_MODE_LEGACY)
        } else {
            fullDescription_event.text = Html.fromHtml(fullDescriptionWithHtml).trim()
        }
    }

    private fun setPlace() {
        /*if we have the right data, insert it in views and show, else not show*/
        val place = event.placeInfo
        if (place != "") {
            textLocation_event.text = place
            container_for_location_event.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
        }
    }

    private fun setDate() {
        val date = event.datesInfo
        if (date != "") {
            textDay_event.text = date
            container_for_date_event.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
        }
    }

    private fun setPrice() {
        val price = event.priceInfo
        if (price != "") {
            textPrice_event.text = price
            container_for_price_event.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
        }
    }

    private fun initMaps() {
        val coords = event.coordsInfo
        if (coords.size != 0) {
            lat = coords[0]
            lon = coords[1]
            val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

            val maps = Maps(lat, lon, this)
            maps.createMapView(mapFragment)
        }
    }

    private fun showRoute() {
        val address = Uri.parse(
            "http://maps.google.com/maps?saddr=My+Location&daddr=" +
                    lat.toString() + "," + lon.toString()
        )
        val intentMap = Intent(Intent.ACTION_VIEW, address)
        startActivity(intentMap)
    }
}
