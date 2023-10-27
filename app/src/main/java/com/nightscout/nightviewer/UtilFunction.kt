package com.nightscout.nightviewer

import java.text.SimpleDateFormat
import java.util.*

fun convertUtcToKst(utcTimestamp: String): String {
    val sdfUtc = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    sdfUtc.timeZone = TimeZone.getTimeZone("UTC")

    val utcDate = sdfUtc.parse(utcTimestamp)
    val kstCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
    kstCalendar.time = utcDate

    val sdfKst = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    sdfKst.timeZone = TimeZone.getTimeZone("Asia/Seoul")

    return sdfKst.format(kstCalendar.time)
}

fun calculateDelta(glucoseData: List<BG>): String {
    // 최소한 두 개의 BG 항목이 필요
    if (glucoseData.size < 2) {
        return "-"
    }

    val latestBg = glucoseData.last().bg.toFloat()
    val previousBg = glucoseData[glucoseData.size - 2].bg.toFloat()

    val glucoseChange = latestBg - previousBg

    return if (glucoseChange >= 0) {
        "+${glucoseChange}"
    } else {
        "${glucoseChange}"
    }
}

fun listToString(list: List<String>, delimiter: String = ", "): String {
    return list.joinToString(delimiter)
}
fun getGlucoseTrend(glucoseData: List<BG>): String {
    // glucoseData의 마지막 요소가 가장 최근의 혈당이라고 가정하고,
    // 길이가 10인 리스트라고 가정합니다.

    // 데이터 개수가 7개보다 작을 경우 "XX"를 반환합니다.
    if (glucoseData.size < 7) {
        return "XX"
    }

    // Format for parsing timestamps
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    // Convert timestamps to Date objects
    val lastTimestamp = dateFormat.parse(glucoseData.last().time)
    val firstTimestamp = dateFormat.parse(glucoseData[glucoseData.size - 7].time)
    val last_lastTimestamp = dateFormat.parse(glucoseData[glucoseData.size - 2].time)

    // Calculate the time difference in minutes
    val timeDifferenceMillis = lastTimestamp.time - firstTimestamp.time
    val timeDifferenceMinutes = Math.round(timeDifferenceMillis.toDouble() / (1000 * 60)).toInt()
    val timeDifferenceMillis_2 = lastTimestamp.time - last_lastTimestamp.time
    val timeDifferenceMinutes_2 = Math.round(timeDifferenceMillis_2.toDouble() / (1000 * 60)).toInt()

    // Calculate the change in blood glucose over time
    val lastBg = glucoseData.last().bg.toFloat()
    val firstBg = glucoseData[glucoseData.size - 7].bg.toFloat()
    val last_lastBg = glucoseData[glucoseData.size - 2].bg.toFloat()
    val changeIn30Mins = lastBg - firstBg
    val changeCurrent = lastBg - last_lastBg
    // Calculate the rate of change
    val rateOfChange = changeCurrent / timeDifferenceMinutes_2
    // 화살표로 나타내는 혈당 추세를 결정합니다.
    return when {
        rateOfChange > 3 || changeIn30Mins > 90 -> "⇈"
        rateOfChange in 2.0..3.0 || changeIn30Mins in 60.0..90.0 -> "↑"
        rateOfChange in 1.0..2.0 || changeIn30Mins in 30.0..60.0 -> "↗"
        rateOfChange in -1.0..1.0 && changeIn30Mins in -30.0..30.0 -> "→"
        rateOfChange in -2.0..-1.0 || changeIn30Mins in -30.0..-60.0 -> "↘"
        rateOfChange in -3.0..-2.0 || changeIn30Mins in -60.0..-90.0 -> "↓"
        rateOfChange < -3 || changeIn30Mins < -90 -> "⇊"
        else -> "XX"
    }
}
