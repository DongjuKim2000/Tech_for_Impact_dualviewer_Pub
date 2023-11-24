package com.nightscout.nightviewer
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager


//var ChartValueDateTime = ArrayList<Long>()
//var ChartValue = ArrayList<Int>()
//var lastrequestdatatime : Long = 0
//var lastrequestalldatatime : Long = 0
class InitializeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        val bgData = BGData(this)
        bgData.initializeBG_db()

        WorkManager.getInstance(this).cancelAllWork() // this: 현재클래스
        WorkManager.getInstance(this).enqueue(OneTimeWorkRequest.Builder(TimeWorker::class.java).build())


        val pref_layout = prefs.getString ("pref_layout", "2").toString()
        when (pref_layout)
        {
            "1" -> {val i = Intent(this, MultiviewActivity::class.java)
                startActivity(i)
//                Log.d("FullscreenActivity","스타트 activity1")
            }
            "2" -> {val i = Intent(this, SingleviewActivity::class.java)
                startActivity(i)
//                Log.d("FullscreenActivity","스타트 activity1")
            }

        }
        //메인액티비티종료
        finish()
    }

}