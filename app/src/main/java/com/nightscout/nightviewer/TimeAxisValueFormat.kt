package com.nightscout.nightviewer

import android.os.Build
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.concurrent.TimeUnit

class TimeAxisValueFormat() : IndexAxisValueFormatter() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getFormattedValue(value: Float): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedDateTime = currentDateTime.format(formatter)
        val timeLong = TimeCalculator(formattedDateTime).time()

        val valueToMin= TimeUnit.MINUTES.toMillis(value.toLong()+(timeLong/1000)*1000)
        val timeMin = Date(valueToMin)
        val formMin = SimpleDateFormat("HH:mm")

        return formMin.format(timeMin)
    }
}