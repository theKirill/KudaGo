package com.yanyushkin.kudago.activities

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.adapters.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_detailing_event.*
import kotlinx.android.synthetic.main.card_view.*
import kotlinx.android.synthetic.main.toolbar_detailing_event.*

class DetailingEventActivity : AppCompatActivity() {

    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailing_event)

        val viewPager = pager
        val viewPagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter

        val circleIndicator = indicator
        circleIndicator.setViewPager(viewPager)

        fillingActivity()
        //main_layout_event.addView(buttonBack, 0)


    }

    fun fillingActivity() {
        /*получаем данные из первой активити (id кликнутого события)*/
        val arguments = intent.extras

        /*заполянем нужными данными*/
        if (arguments != null) {
            titleEvent.text = arguments.getString("title")
            shortDescriptionEvent.text = arguments.getString("description")
            fullDescriptionEvent.text = arguments.getString("full_description")

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

            val coords : ArrayList<Double> = arguments.get("coords") as ArrayList<Double>
            if (coords[0]!=-1.0 && coords[1]!=-1.0){
                lat=coords[0]
                lon=coords[1]
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

    fun createMapView(){
        if (googleMap==null){
            mapFragment=supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
          // mapFragment.getMapAsync(mapFragment)
        }
    }

    fun addMarkerOnMap(){
        val l=LatLng(0.0,0.0)
        val mO=MarkerOptions().position(l).title("Mark").draggable(false)
        if (googleMap!=null){
            googleMap.addMarker(mO)
        }
    }
}
