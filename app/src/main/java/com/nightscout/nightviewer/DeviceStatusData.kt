package com.nightscout.nightviewer

//json 형식에 따라 만들었음.
data class GlucoseData(
    val _id: String,
    val device: String,
    val date: Long,
    val dateString: String,
    val sgv: Int,
    val delta: Float,
    val direction: String,
    val type: String,
    val filtered: Int,
    val unfiltered: Int,
    val rssi: Int,
    val noise: Int,
    val sysTime: String,
    val utcOffset: Int,
    val mills: Long
)