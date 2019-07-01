package com.yanyushkin.kudago.utils

import android.content.Context
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.yanyushkin.kudago.R
import com.yanyushkin.kudago.getBitmapDescriptorFromVector

class Maps(private val lat: Double, private val lon: Double, private val context: Context) : OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    /*callback of receiving the map*/
    override fun onMapReady(_googleMap: GoogleMap?) {
        _googleMap?.let {
            googleMap = _googleMap
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            addMarkerOnMap()
        }
    }

    fun createMapView(mapFragment: SupportMapFragment): Unit = mapFragment.getMapAsync(this)

    private fun addMarkerOnMap() {
        val position = LatLng(lat, lon)

        /*set icon, position and other settings (snap to marker) for marker*/
        val marker =
            MarkerOptions().icon(getBitmapDescriptorFromVector(context, R.drawable.ic_marker)).position(position)
                .draggable(false).flat(true)

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15.0F))
        googleMap.addMarker(marker)
    }
}