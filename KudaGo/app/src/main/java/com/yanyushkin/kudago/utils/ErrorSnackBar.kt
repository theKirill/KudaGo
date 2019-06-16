package com.yanyushkin.kudago.utils

import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import com.yanyushkin.kudago.R

class ErrorSnackBar(v: View) {
    private val sb: Snackbar
    private val snackbarView: View

    init {
        sb = Snackbar.make(v, R.string.noConnectionBottom, Snackbar.LENGTH_LONG)
        snackbarView = sb.view
    }

    fun show(context: Context) {
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorError))

        val snackTextView = snackbarView.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        snackTextView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
        snackTextView.textSize = 12.0f

        sb.duration = 5000
        sb.show()
    }
}