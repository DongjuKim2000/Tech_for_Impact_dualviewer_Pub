package com.nightscout.nightviewer
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.*
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit



class TimeWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {


    override fun doWork(): Result {
        // 여기에서 실제 데이터 업데이트 작업을 수행
        getBG()
        Log.d("TimeWorker", "Executed")
        // 1분 후에 다음 작업 예약
        val nextWork = OneTimeWorkRequest.Builder(TimeWorker::class.java)
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(applicationContext).cancelAllWorkByTag("TimeWorker")
        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "TimeWorker", // 고유한 이름 지정
            ExistingWorkPolicy.REPLACE, // 동일한 이름을 사용할 경우 대체
            nextWork
        )
        return Result.success()
    }

    private fun getBG() {
        try{
            val bgData = BGData(applicationContext)
            bgData.get_EntireBGInfo()
        }catch(e: Exception){
            Log.d("TimeWorker", "Internet Error")
        }

    }

}




/*
class TimeWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {

    override fun doWork(): Result { //백그라운드 작업 수행

        val mywork = OneTimeWorkRequest.Builder(TimeWorker::class.java)
            .setInitialDelay(60, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(applicationContext).enqueue(mywork)

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        //log.d("타이머", sdf.format(System.currentTimeMillis()))
        val bgs = BGData(applicationContext).get_Recent10BGValues()

        UpdateNotification()
        return Result.success()

    }

    private fun UpdateNotification() {

        //텍스트뷰
        val bgs =  BGData(applicationContext)

        val currentbg=bgs.get_Recent10BGValues()[9]

        val currentTime : Long = System.currentTimeMillis() // ms로 반환
        Log.d("time", "bgtime")
        val bgTime: Long = currentbg.time.toLong()
        val minago_long = currentTime - bgTime
        val mins: Long = minago_long / (1000 * 60)
        var bglevel : Int
        try { bglevel = currentbg.bg.toInt() }
        catch (e: Exception){ bglevel = -1 }

        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        //notificationManager.notify(1, notification.build())

    }

}*/