package com.nightscout.nightviewer

import com.google.gson.Gson
//json 형식에 따라 만들었음.
data class OpenapsData(
    val openaps: Openaps,
    val mills: Long
)

data class Openaps(
    val suggested: Suggested,
    val iob: Iob
)

data class Suggested(
    val bg: Double,
    val COB: Int,
    val IOB: Int,
    val tick: Int,
    val timestamp: String
)

data class Iob(
    val basaliob: Int
)