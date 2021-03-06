package com.amolina.weather.climaflow.utils

import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.amolina.weather.climaflow.ui.show.ShowAdapter
import com.amolina.weather.climaflow.ui.show.ShowItemModel

import com.bumptech.glide.Glide


import java.util.ArrayList


/**
 * Created by Amolina on 02/02/17.
 */

object BindingUtils {

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun setImageUrl(imageView: ImageView, url: String) {
        val context = imageView.context
        Glide.with(context).load(url).into(imageView)
    }

    @JvmStatic
    @BindingAdapter("adapter")
    fun addWeatherItems(
        recyclerView: RecyclerView,
        showItems: ArrayList<ShowItemModel>
    ) {
        val adapter = recyclerView.adapter as ShowAdapter
        adapter.clearItems()
        adapter.addItems(showItems)
    }


}// This class is not publicly instantiable
