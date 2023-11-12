package com.nightscout.nightviewer

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Locale

class TimeCalculator (private val datetimeString: String){

    private fun dateToMin(): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        // 문자열을 Date 객체로 파싱
        val date = dateFormat.parse(datetimeString)
        Log.d("TimeCalculator", (date.time/60000).toString())
        // 분 단위로 변환하여 반환
        return date.time / (60 * 1000)
    }

    fun time(): Long {
        return dateToMin()
    }
}