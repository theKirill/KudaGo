package com.yanyushkin.kudago.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Html
import android.widget.LinearLayout
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.adapters.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_detailing_event.*
import kotlinx.android.synthetic.main.toolbar_detailing_event.*

class DetailingEventActivity : AppCompatActivity(), OnMapReadyCallback {
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailing_event)

        fillingActivity()

        /*open app to show route (from current location to the destination)*/
        button_showRoute.setOnClickListener {
            val address = Uri.parse(
                "http://maps.google.com/maps?saddr=My+Location&daddr=" +
                        lat.toString() + "," + lon.toString()
            )
            val intentMap = Intent(Intent.ACTION_VIEW, address)
            startActivity(intentMap)
        }

        button_back.setOnClickListener { onBackPressed() }
    }

    private fun fillingActivity() {
        /*get data from main activity about selected event*/
        val arguments = intent.extras

        /*filling views*/
        if (arguments != null) {
            val images = arguments.get("images") as ArrayList<String>
            if (images.size > 0) {
                val viewPager = pager

                /*create adapter for ViewPager with received images*/
                val viewPagerAdapter = ViewPagerAdapter(this, images)
                viewPager.adapter = viewPagerAdapter

                val circleIndicator = indicator
                circleIndicator.setViewPager(viewPager)
            }

            title_event.text = arguments.getString("title")

            val shortDescriptionWithHtml = arguments.getString("description")
            /*replacing tags*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                shortDescription_event.text = Html.fromHtml(shortDescriptionWithHtml, Html.FROM_HTML_MODE_LEGACY)
            } else {
                shortDescription_event.text = Html.fromHtml(shortDescriptionWithHtml)
            }

            val fullDescriptionWithHtml = arguments.getString("fullDescription")
            /*replacing tags*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                fullDescription_event.text = Html.fromHtml(fullDescriptionWithHtml, Html.FROM_HTML_MODE_LEGACY)
            } else {
                fullDescription_event.text = Html.fromHtml(fullDescriptionWithHtml)
            }

            /*if we have the right data, insert it in views and show, else not show*/
            val place = arguments.getString("place")
            if (place != "") {
                textLocation_event.text = place
                container_for_location_event.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
                )
            }

            val date = arguments.getString("date")
            if (date != "") {
                textDay_event.text = date
                container_for_date_event.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
                )
            }

            val price = arguments.getString("price")
            if (price != "") {
                textPrice_event.text = price
                container_for_price_event.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
                )
            }

            val coords = arguments.get("coords") as ArrayList<Double>
            if (coords.size != 0) {
                lat = coords[0]
                lon = coords[1]
                createMapView()
            }
        }

    }

    private fun createMapView() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /*callback of receiving the map*/
    override fun onMapReady(p0: GoogleMap?) {
        if (p0 != null) {
            googleMap = p0
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            addMarkerOnMap()
        }
    }

    private fun addMarkerOnMap() {
        val position = LatLng(lat, lon)

        /*set icon, position and other settings (snap to marker) for marker*/
        val marker = MarkerOptions().icon(getBitmapDescriptorFromVector(R.drawable.ic_marker)).position(position)
            .draggable(false).flat(true)

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15.0F))
        googleMap.addMarker(marker)
    }

    /*method for translate Vector to Bitmap (using for setting the marker)*/
    private fun getBitmapDescriptorFromVector(id: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(this, id)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)

        val bitmap =
            Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
