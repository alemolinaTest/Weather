package com.amolina.weather.climaflow.data.local.db

import android.arch.persistence.room.TypeConverter
import com.amolina.weather.climaflow.data.model.db.City

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.lang.reflect.Type
import java.util.ArrayList

object DataConverters {
    @TypeConverter
    fun fromString(value: String): ArrayList<Long>? {
        val listType = object : TypeToken<ArrayList<Long>>() {

        }.type
        return Gson().fromJson<ArrayList<Long>>(value, listType)
    }

    @TypeConverter
    fun fromArrayLisr(list: ArrayList<Long>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun cityFromString(value: String): List<City>? {
        val listType = object : TypeToken<List<City>>() {

        }.type
        return Gson().fromJson<List<City>>(value, listType)
    }
}