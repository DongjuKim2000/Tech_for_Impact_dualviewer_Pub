package com.nightscout.nightviewer

import com.google.gson.Gson
//json 형식에 따라 만들었음.
data class OpenapsData(
    val bgnow: String,
    val delta: String,
    val direction: String,
    val buckets:String,
    val iob:String,
    val cob:String,
    val basal:String

)

