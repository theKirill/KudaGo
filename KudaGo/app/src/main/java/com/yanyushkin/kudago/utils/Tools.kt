package com.yanyushkin.kudago.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.support.v4.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.yanyushkin.kudago.models.Event
import com.yanyushkin.kudago.network.EventsResponse
import com.yanyushkin.kudago.network.Place
import java.util.*

class Tools {
    companion object {
        /*method for translate Vector to Bitmap (using for setting the marker)*/
        fun getBitmapDescriptorFromVector(context: Context, id: Int): BitmapDescriptor {
            val vectorDrawable = ContextCompat.getDrawable(context, id)
            vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)

            val bitmap =
                Bitmap.createBitmap(
                    vectorDrawable.intrinsicWidth,
                    vectorDrawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )

            val canvas = Canvas(bitmap)
            vectorDrawable.draw(canvas)

            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }

        @SuppressLint("InlinedApi")
        fun translateDate(badStartDate: String?, badEndDate: String?): String {
            var resDate: String = ""

            val calendar = Calendar.getInstance()

            if (badStartDate != null) {
                calendar.set(
                    Calendar.MONTH, Integer.parseInt(
                        badStartDate.substring(
                            5,
                            7
                        )
                    ) - 1
                )

                resDate += badStartDate.substring(8) + " " + calendar.getDisplayName(
                    Calendar.MONTH,
                    Calendar.LONG_FORMAT,
                    Locale("ru")
                ) + " " + badStartDate.substring(0, 4)
            }

            if (badEndDate != null) {
                if (resDate.isNotEmpty())
                    resDate += " - "
                else
                    resDate += "до "

                calendar.set(
                    Calendar.MONTH, Integer.parseInt(
                        badEndDate.substring(
                            5,
                            7
                        )
                    ) - 1
                )

                resDate += badEndDate.substring(8) + " " + calendar.getDisplayName(
                    Calendar.MONTH,
                    Calendar.LONG_FORMAT,
                    Locale("ru")
                ) + " " + badEndDate.substring(0, 4)
            }

            return resDate
        }

        fun translatePlace(badPlace: Place?): String {
            if (badPlace != null) {
                if (badPlace.title != null)
                    return badPlace.title
                if (badPlace.address != null)
                    return badPlace.address
            }

            return ""
        }

        fun getLatAndLon(place: Place?): ArrayList<Double> {
            val res: ArrayList<Double> = ArrayList()

            if (place != null && place.coords != null) {
                if (place.coords.lat != null)
                    res.add(place.coords.lat)

                if (place.coords.lon != null)
                    res.add(place.coords.lon)
            }

            return res
        }
    }
}