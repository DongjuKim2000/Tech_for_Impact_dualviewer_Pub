package com.nightscout.nightviewer
import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit



class TimeWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {


    override fun doWork(): Result {
        // 여기에서 실제 데이터 업데이트 작업을 수행
//        Log.d("TimeWorker", "Executed")
        if (isOnline(applicationContext)) {
            getBG()
        }


            // 1분 후에 다음 작업 예약
            val nextWork = OneTimeWorkRequest.Builder(TimeWorker::class.java)
                .setInitialDelay(30, TimeUnit.SECONDS)
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
        val bgData = BGData(applicationContext)
        bgData.get_EntireBGInfo()
    }

}
