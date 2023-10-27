package com.nightscout.nightviewer

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.appcompat.app.AlertDialog
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

fun listToString(list: List<String>, delimiter: String = ", "): String {
    return list.joinToString(delimiter)
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

fun showErrorMessage(context: Context) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Error")
    builder.setMessage("인터넷에 연결되어 있지 않습니다. 인터넷 연결 상태를 확인해주세요.")
    builder.setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
    val dialog: AlertDialog = builder.create()
    dialog.show()
}