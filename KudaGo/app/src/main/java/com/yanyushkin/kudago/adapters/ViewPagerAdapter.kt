package com.yanyushkin.kudago.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.AppCompatImageView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.yanyushkin.kudago.R

class ViewPagerAdapter(private val context: Context, private val images: ArrayList<String>) : PagerAdapter() {

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, any: Any): Boolean {
        return view == (any as View)
    }

    /*adding imageView with a photo to ViewPager on each page*/
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = AppCompatImageView(context)
        imageView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGray))
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP

        val vP = container as ViewPager
        vP.addView(imageView)

        Picasso.get().load(images[position])
            .error(R.drawable.ic_error_photo).into(imageView)

        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as AppCompatImageView)
    }

}