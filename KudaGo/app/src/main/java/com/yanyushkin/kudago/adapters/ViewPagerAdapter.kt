package com.yanyushkin.kudago.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class ViewPagerAdapter(_context: Context) : PagerAdapter() {
    val context: Context = _context

    override fun getCount(): Int {
        return 1
    }

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == (p1 as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val tv = TextView(context)
        tv.text = position.toString()
        val vP = container as ViewPager
        vP.addView(tv, 0)
        return tv
    }
}