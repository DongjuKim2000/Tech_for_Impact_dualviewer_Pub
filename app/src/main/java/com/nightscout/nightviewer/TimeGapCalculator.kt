package com.nightscout.nightviewer

import java.text.SimpleDateFormat
import java.util.Locale

class TimeGapCalculator (private val datetimeString1: String, private val datetimeString2: String){
    private fun calculateTimeGap(): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        // 문자열을 Date 객체로 파싱
        val date1 = dateFormat.parse(datetimeString1)
        val date2 = dateFormat.parse(datetimeString2)

        // 간격 계산
        val duration = date2.time - date1.time

        // 분 단위로 변환하여 반환
        return duration / (60 * 1000)
    }

    fun timeGap(): Long {
        return calculateTimeGap()
    }
}