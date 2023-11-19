package com.nightscout.nightviewer
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager


var ChartValueDateTime = ArrayList<Long>()
var ChartValue = ArrayList<Int>()
var lastrequestdatatime : Long = 0
var lastrequestalldatatime : Long = 0
class InitializeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        Log.d("FullscreenActivity","onCreate 시작")

        val bgData = BGData(this)
        bgData.initializeBG_db()

        WorkManager.getInstance(this).cancelAllWork() // this: 현재클래스
        WorkManager.getInstance(this).enqueue(OneTimeWorkRequest.Builder(TimeWorker::class.java).build())


        val pref_layout = prefs.getString ("pref_layout", "2").toString()
        when (pref_layout)
        {
            "1" -> {val i = Intent(this, MultiviewActivity::class.java)
                startActivity(i)
                Log.d("FullscreenActivity","스타트 activity1")
            }
            "2" -> {val i = Intent(this, SingleviewActivity::class.java)
                startActivity(i)
                Log.d("FullscreenActivity","스타트 activity1")
            }

        }
        //메인액티비티종료
        finish()
    }

//    private fun showInputTextDialog() {
//        val editText = EditText(this)
//        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
//            .setTitle("주소를 입력하세요.")
//            .setView(editText)
//            .setPositiveButton("저장") { _, _ ->
//                val inputText = editText.text.toString()
//                if (isValidInput(inputText)) {
//                    saveTextToPreference(inputText)
//                } else {
//                    showInputTextDialog()
//                }
//            }
//            .create()
//        dialog.show()
//    }
//
//    private fun isValidInput(text: String): Boolean {
//        val url = try{
//            URL("${text}/api/v2/properties/bgnow,delta,direction,buckets,iob,cob,basal")
//        }
//        catch(e: Exception){
//            null
//        }
//        val ret = (url != null)
//        if(!ret)
//            showErrorMessage(this, "올바르지 않은 URL입니다.")
//        return ret
//    }
//
//    private fun saveTextToPreference(text: String) {
//        with(prefs.edit()) {
//            putString("ns_url", text)
//            apply()
//        }
//    }

//    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean){
//
//        Log.d("Activity", "onMultiWindowModeChanged")
//        super.onMultiWindowModeChanged(isInMultiWindowMode)
//        if (isInMultiWindowMode)
//            Log.d("Activity", "multinow!")
//    }
}