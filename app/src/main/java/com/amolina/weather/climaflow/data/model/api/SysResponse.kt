package com.amolina.weather.climaflow.data.model.api

data class SysResponse (

    val type : Int,
    val id : Int,
    val message : Double,
    val country : String,
    val sunrise : Int,
    val sunset : Int
)