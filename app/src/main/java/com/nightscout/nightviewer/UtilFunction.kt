package com.nightscout.nightviewer

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.appcompat.app.AlertDialog
import java.text.SimpleDateFormat
import java.util.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities


fun convertUtcToLocal(utcTimestamp: String): String {
    val sdfUtc = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    sdfUtc.timeZone = TimeZone.getTimeZone("UTC")
    val lcoaltimeZone = TimeZone.getDefault()
    val utcDate = sdfUtc.parse(utcTimestamp)
    val localCalendar = Calendar.getInstance(lcoaltimeZone)
    localCalendar.time = utcDate
    val sdfLocal = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    sdfLocal.timeZone = lcoaltimeZone
    return sdfLocal.format(localCalendar.time)
}

fun calculateDelta(glucoseData: List<BG>): String {
    // 최소한 두 개의 BG 항목이 필요
    if (glucoseData.size < 2) {
        return "-"
    }

    val latestBg = glucoseData.last().bg.toFloat()
    val previousBg = glucoseData[glucoseData.size - 2].bg.toFloat()

    val glucoseChange = latestBg - previousBg
    Log.d("delta", "${glucoseChange.toString()}")
    val float_glucosechange = glucoseChange.toFloat()
    val int_glucoseChange = float_glucosechange.toInt()

    return if (glucoseChange >= 0) {
        "+${int_glucoseChange}"
    } else {
        "${int_glucoseChange}"
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
        return "!!"
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
        rateOfChange > 3.0 || changeIn30Mins > 90 -> "⇈"
        rateOfChange in 2.0..3.0 || changeIn30Mins in 60.0..90.0 -> "↑"
        rateOfChange in 1.0..2.0 || changeIn30Mins in 30.0..60.0 -> "↗"
        rateOfChange in -1.0..1.0 || changeIn30Mins in -30.0..30.0 -> "→"
        rateOfChange in -2.0..-1.0 || changeIn30Mins in -30.0..-60.0 -> "↘"
        rateOfChange in -3.0..-2.0 || changeIn30Mins in -60.0..-90.0 -> "↓"
        rateOfChange < -3.0 || changeIn30Mins < -90 -> "⇊"
        else -> ".."
    }
}

fun doVibrate(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE)
                as VibratorManager
        vibratorManager.defaultVibrator;
    } else {
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val vibrationEffect = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(vibrationEffect)
    } else {
        // 안드로이드 O 이전 버전에서는 deprecated된 메서드를 사용할 수 있습니다.
        vibrator.vibrate(1000)
    }
    return
}

fun doRingtone(context: Context) {
    val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val ringtone = RingtoneManager.getRingtone(context, notification)
    ringtone.play()
}

fun showErrorMessage(context: Context, msg: String) {
    val alertDialog: AlertDialog = AlertDialog.Builder(context)
        .setTitle("Error")
        .setMessage(msg)
        .setPositiveButton("확인", null)
        .create()
    alertDialog.show()
}
fun showMessage(context: Context, msg: String) {
    val alertDialog: AlertDialog = AlertDialog.Builder(context)
        .setTitle("Connected")
        .setMessage(msg)
        .setPositiveButton("확인", null)
        .create()
    alertDialog.show()
}
fun isOnline(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        return networkInfo.isConnected
    }
}
