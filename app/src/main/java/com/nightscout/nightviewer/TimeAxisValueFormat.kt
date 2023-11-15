package com.nightscout.nightviewer

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

class TimeAxisValueFormat() : IndexAxisValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        var valueToMin= TimeUnit.MINUTES.toMillis(value.toLong())
        var timeMin = Date(valueToMin)
        var formMin = SimpleDateFormat("HH:mm")

        return formMin.format(timeMin)
    }
}