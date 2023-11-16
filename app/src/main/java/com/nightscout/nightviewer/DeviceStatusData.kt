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
    val sysTime: String,
    val mills: Long
)