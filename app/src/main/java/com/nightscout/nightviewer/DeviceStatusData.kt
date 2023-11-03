package com.nightscout.nightviewer

//json 형식에 따라 만들었음.
data class OpenapsData(
//    val openaps: Openaps,
//    val mills: Long
    val bgnow: String,
    val delta: String,
    val direction: String,
    val buckets:String,
    val iob:String,
    val cob:String,
    val basal:String
)

data class Openaps(
    val suggested: Suggested,
    val iob: Iob
)

data class Suggested(
    val bg: Double,
    val COB: Int,
    val IOB: Int,
    val timestamp: String
)

data class Iob(
    val basaliob: Int
)