package com.yanyushkin.kudago.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import com.google.android.gms.maps.*
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.adapters.ViewPagerAdapter
import com.yanyushkin.kudago.models.Event
import com.yanyushkin.kudago.utils.Maps
import kotlinx.android.synthetic.main.activity_detailing_event.*
import kotlinx.android.synthetic.main.toolbar_detailing_event.*

class DetailingEventActivity : AppCompatActivity() {

    private val EVENT_KEY = "event"
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
        event = arguments.get(EVENT_KEY) as Event

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
        val images = event.imagesURLInfo

        if (images.size > 0) {
            val viewPager = pager

            /*create adapter for ViewPager with received images*/
            val viewPagerAdapter = ViewPagerAdapter(this, images)
            viewPager.adapter = viewPagerAdapter

            val circleIndicator = indicator
            circleIndicator.setViewPager(viewPager)
        } else {
            pager.visibility = View.GONE
        }
    }

    private fun setTitle() {
        title_event.text = event.titleInfo
    }

    private fun setShortDescription() {
        val shortDescriptionWithHtml = event.descriptionInfo

        /*replacing tags*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            shortDescription_event.text = Html.fromHtml(shortDescriptionWithHtml, Html.FROM_HTML_MODE_LEGACY)
        else
            shortDescription_event.text = Html.fromHtml(shortDescriptionWithHtml)

        /*to follow links*/
        shortDescription_event.movementMethod = LinkMovementMethod.getInstance()
        shortDescription_event.text = shortDescription_event.text.trim()
    }

    private fun setFullDescription() {
        val fullDescriptionWithHtml = event.fullDescriptionInfo

        /*replacing tags*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            fullDescription_event.text = Html.fromHtml(fullDescriptionWithHtml, Html.FROM_HTML_MODE_LEGACY)
        else
            fullDescription_event.text = Html.fromHtml(fullDescriptionWithHtml)

        /*to follow links*/
        fullDescription_event.movementMethod = LinkMovementMethod.getInstance()
        fullDescription_event.text = fullDescription_event.text.trim()
    }

    private fun setPlace() {
        /*if we have the right data, insert it in views and show, else not show*/
        val place = event.placeInfo

        if (place != "") {
            textLocation_event.text = place
            container_for_location_event.visibility = View.VISIBLE
        }
    }

    private fun setDate() {
        val date = event.datesInfo

        if (date != "") {
            textDay_event.text = date
            container_for_date_event.visibility = View.VISIBLE
        }
    }

    private fun setPrice() {
        val price = event.priceInfo

        if (price != "") {
            textPrice_event.text = price
            container_for_price_event.visibility = View.VISIBLE
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
        } else {
            container_for_map.visibility = View.GONE
            container_for_bottom.visibility = View.VISIBLE
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
