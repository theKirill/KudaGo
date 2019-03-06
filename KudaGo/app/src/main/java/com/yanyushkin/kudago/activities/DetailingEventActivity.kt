package com.yanyushkin.kudago.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.adapters.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_detailing_event.*
import kotlinx.android.synthetic.main.card_view.*
import kotlinx.android.synthetic.main.toolbar_detailing_event.*

class DetailingEventActivity : AppCompatActivity(), OnMapReadyCallback {
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailing_event)

        fillingActivity()
    }

    fun fillingActivity() {
        /*получаем данные из первой активити (id кликнутого события)*/
        val arguments = intent.extras

        val images = arguments.get("images") as ArrayList<String>

        if (images.size > 0) {
            val viewPager = pager
            val viewPagerAdapter = ViewPagerAdapter(this, images)
            viewPager.adapter = viewPagerAdapter

            val circleIndicator = indicator
            circleIndicator.setViewPager(viewPager)
        }

        /*заполянем нужными данными*/
        if (arguments != null) {
            titleEvent.text = arguments.getString("title")
            shortDescriptionEvent.text = arguments.getString("description")
            fullDescriptionEvent.text = arguments.getString("fullDescription")

            val place = arguments.getString("place")
            if (place != "") {
                textLocationEvent.text = place
                container_for_text_location2.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
                )
            }

            val date = arguments.getString("date")
            if (date != "") {
                textDayEvent.text = date
                container_for_text_date2.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
                )
            }

            val price = arguments.getString("price")
            if (price != "") {
                textPriceEvent.text = price
                container_for_text_price2.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
                )
            }

            val coords: ArrayList<Double> = arguments.get("coords") as ArrayList<Double>
            if (coords[0] != -1.0 && coords[1] != -1.0) {
                lat = coords[0]
                lon = coords[1]
                createMapView()
            }
        }

        buttonShowRoute.setOnClickListener {
            val address = Uri.parse(
                "http://maps.google.com/maps?saddr=My+Location&daddr=" +
                        lat.toString() + "," + lon.toString()
            )
            val intentMap = Intent(Intent.ACTION_VIEW, address)
            startActivity(intentMap)
        }

        buttonBack.setOnClickListener { onBackPressed() }
    }

    fun createMapView() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap?) {
        if (p0 != null) {
            googleMap = p0
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            addMarkerOnMap()
        }
    }

    fun addMarkerOnMap() {
        val position = LatLng(lat, lon)
        val marker = MarkerOptions().icon(getBitmapDescriptorFromVector(R.drawable.ic_marker)).position(position)
            .draggable(false).flat(true)//нельзя маркер таскать, привязка к карте
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15.0F))
        googleMap.addMarker(marker)
    }

    fun getBitmapDescriptorFromVector(id: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(this, id)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap =
            Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
