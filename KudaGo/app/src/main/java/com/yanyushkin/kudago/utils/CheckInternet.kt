package com.yanyushkin.kudago.utils

import android.content.Context
import android.net.ConnectivityManager
import java.lang.Exception

class CheckInternet {

    companion object {
        /*check network*/
        private fun isNetworkConnectivity(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo

            return netInfo != null && netInfo.isConnected
        }

        /*check Internet*/
        fun isHasInternet(context: Context): Boolean {//http://qaru.site/questions/13922/how-to-check-internet-access-on-android-inetaddress-never-times-out
            if (isNetworkConnectivity(context)) {
                try {
                    val p1 = Runtime.getRuntime().exec("ping -c 1 www.google.com")
                    val returnVal = p1.waitFor()
                    return returnVal == 0
                }
                catch (e: Exception){

                }
                return false
            } else
                return false
        }
    }
}