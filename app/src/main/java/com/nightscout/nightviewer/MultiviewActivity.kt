package com.nightscout.nightviewer
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.Guideline
import androidx.lifecycle.lifecycleScope
import com.example.dualviewer.GraphThread
import com.github.mikephil.charting.charts.LineChart
import com.nightscout.nightviewer.databinding.MultiviewScreenBinding
import java.text.SimpleDateFormat

// 멀티스크린을 위한 액티비티입니다.
class MultiviewActivity : CommonActivity() {

    lateinit var binding: MultiviewScreenBinding

    private var prev_alarm: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MultiviewScreenBinding.inflate(layoutInflater) //??

        setContentView(binding.root) // xml 파일 지정

        val filter = IntentFilter()  // 인텐트 지정
        filter.addAction("showinfo") //수신할 action 종류 넣기

        val fadeOut = ObjectAnimator.ofFloat(binding.screenDirection, "alpha", 1.0f, 0.0f).apply {
            duration = 1000L // 애니메이션 지속 시간 설정 (1초)
            repeatCount = ObjectAnimator.INFINITE // 애니메이션 반복 횟수를 무한으로 설정
            repeatMode = ObjectAnimator.REVERSE // 애니메이션 반복 모드를 REVERSE로 설정
        }
        fadeOut.start()

        if (Build.VERSION.SDK_INT >= 26) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.icon)
        }
        isFullscreen = true

        fullscreenContent = binding.mainlayout1
        fullscreenContent.setOnClickListener { toggle() }

        // 인터넷 연결 상태를 감지하는 Receiver
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(internetBroadcaster, filter)

        val updateIntervalMillis: Long = 5000
        var reconnected = true
        lifecycleScope.launchWhenStarted {
            Data_Courutine(applicationContext).dataFlow.collect { newData ->
                // 여기에서 newData를 활용하여 화면에 업데이트
                if (!isOnline(this@MultiviewActivity)) {
                    fadeOut.start()
                    showErrorMessage(this@MultiviewActivity, "인터넷 연결 X")
                    reconnected = false
                }
                else {
                    if (reconnected==false) {
                        showMessage(this@MultiviewActivity, "인터넷 연결 재개")
                    }

                    try{showinfo(newData)
                        fadeOut.cancel()
                        binding.screenDirection.alpha = 1.00f
                        reconnected = true } catch(e:Exception){
                        reconnected = false
                        showErrorMessage(this@MultiviewActivity, "인터넷 연결이 안되어 있습니다")
                    }
                }
            }
        }


    }
    override fun onResume() {
        super.onResume()
        if(Build.VERSION.SDK_INT >= 24) {
            if (isInMultiWindowMode) {
//                Log.d("Activity1", "multi window mode")
//                Log.d("FullscreenActivity","스타트 activity1")

            } else {
                val i = Intent(this, SingleviewActivity::class.java)
                startActivity(i) // 멀티 윈도우 모드로 진행
//                Log.d("Activity2", "not multi window")
            }
        }
        val currenttime : Long = System.currentTimeMillis()


    }

    override fun onDestroy() {
        super.onDestroy()
        try{unregisterReceiver(internetBroadcaster)} catch (e: Exception){}
    }

    override fun onBackPressed() {
        // 현재 액티비티를 종료하고, 앱을 백그라운드로 보냅니다.
        moveTaskToBack(true)
        finish()
    }



    private fun showinfo(current_bgInfo: BG?) {

        //설정

        val pref_timeformat = prefs.getString("preftimeformat", "timeformat24")
        val pref_urgenthighvalue = prefs.getString ("urgent_high_value", "260")?.toFloat() ?: 260f
        val pref_highvalue = prefs.getString ("high_value", "180")?.toFloat() ?: 180f
        val pref_lowvalue = prefs.getString ("low_value", "80")?.toFloat() ?: 80f
        val pref_urgentlowvalue = prefs.getString ("urgent_low_value", "55")?.toFloat() ?: 55f

        val pref_bgfont = prefs.getString ("bg_font", "200")?.toFloat() ?: 200f
        val pref_directionfont = prefs.getString ("direction_font", "100")?.toFloat() ?: 100f
        val pref_timeinfofont = prefs.getString ("timeinfo_font", "30")?.toFloat() ?: 30f

        val pref_fontcolornormal = prefs.getString ("fontcolornormal", "#FCFFFFFF").toString()
        val pref_fontcolorhighlow = prefs.getString ("fontcolorhighlow", "#FCFFFFFF").toString()
        val pref_fontcolorurgenthighlow = prefs.getString ("fontcolorurgenthighlow", "#FCFFFFFF").toString()

        val IOBEnable = prefs.getBoolean("iob_enable", true)
        val COBEnable = prefs.getBoolean("cob_enable", true)
        val BasalEnable = prefs.getBoolean("basal_enable", true)

//        Log.d("current_info", "${current_bgInfo.toString()}")


        val currentTime : Long = System.currentTimeMillis() // ms로 반환

        var mins: Long = 0

        var info : String = ""
      
        var int_bg = 100
        var sdf = SimpleDateFormat("HH:mm")
        if (pref_timeformat == "timeformat12"){ sdf = SimpleDateFormat("a hh:mm") }
        val displayTime: String = sdf.format(currentTime)
        info = "$displayTime"

        if(current_bgInfo!=null){
            var displayMins = current_bgInfo.time

            if (current_bgInfo.time != ""){
                val timeGap = ((currentTime/60000) - TimeCalculator(current_bgInfo.time).time())%1000
                displayMins = "   $timeGap min"
                info += displayMins
            }

            var displayIOB = current_bgInfo.iob

            if ((current_bgInfo.iob != "")&&IOBEnable){
                displayIOB = "   \uD83C\uDD58${current_bgInfo.iob}U"
                info += displayIOB
            }
            var displayCOB = current_bgInfo.cob

            if ((current_bgInfo.cob != "")&&COBEnable){
                displayCOB = "   \uD83C\uDD52${current_bgInfo.cob}g"
                info += displayCOB
            }
            var displayBasal = current_bgInfo.basal
            if ((current_bgInfo.basal != "")&&BasalEnable) {
                displayBasal = "   \uD83C\uDD51${current_bgInfo.basal}"
                info += displayBasal
            }
            // xml 구성 관련 부분
            var  bg_value : String = current_bgInfo.bg
            var float_bg = bg_value.toFloat()
            int_bg = float_bg.toInt()
            binding.screenBg.text = int_bg.toString()

            binding.screenDirection.text ="${current_bgInfo.arrow} ${current_bgInfo.delta}"
            binding.screenInfo.text = info

            binding.screenBg.textSize = pref_bgfont.toFloat()
            binding.screenDirection.textSize = pref_directionfont.toFloat()
            binding.screenInfo.textSize = pref_timeinfofont.toFloat()
        }

        //그래프 표시
        val lineChart: LineChart = findViewById(R.id.lineChart)
        val guideline3: Guideline = findViewById(R.id.guideline3)
        val chartEnable = prefs.getBoolean("chart_enable", true)
        if(chartEnable){
            val thread = GraphThread(lineChart, baseContext)
            thread.start()
        }
        else{
            lineChart.visibility = View.GONE
            guideline3.visibility = View.GONE
        }


        if (isFullscreen) { hide() }

        fun getComplementaryColor(color: Int): Int {
            val alpha = color shr 24 and 0xFF
            val red = 255 - (color shr 16 and 0xFF)
            val green = 255 - (color shr 8 and 0xFF)
            val blue = 255 - (color and 0xFF)
            return alpha shl 24 or (red shl 16) or (green shl 8) or blue
        }

        var fontcolor : Int = Color.WHITE

        try {

            val bgInt : Int = int_bg
            val vibrateEnable = prefs.getBoolean("vibrate_enable", true)
            val ringtoneEnable = prefs.getBoolean("ringtone_enable", true)

            //일반혈당
            if(current_bgInfo != null && prev_alarm != current_bgInfo.time) {
                if (pref_lowvalue <= bgInt && bgInt <= pref_highvalue) {
                    fontcolor = Color.parseColor(pref_fontcolornormal)
                } else if (pref_urgentlowvalue <= bgInt && bgInt <= pref_urgenthighvalue) {
                    fontcolor = Color.parseColor(pref_fontcolorhighlow)
                    if (ringtoneEnable)
                        playNotificationSound(this)
                    if (vibrateEnable)
                        doVibrate(this)
                } else {
                    fontcolor = Color.parseColor(pref_fontcolorurgenthighlow)
                    if (ringtoneEnable)
                        playNotificationSound(this)
                    if (vibrateEnable)
                        doVibrate(this)
                }
                prev_alarm = current_bgInfo.time
            }
            else if(current_bgInfo != null){
                if (pref_lowvalue <= bgInt && bgInt <= pref_highvalue) {
                    fontcolor = Color.parseColor(pref_fontcolornormal)
                } else if (pref_lowvalue<= bgInt && bgInt <= pref_highvalue) {
                    fontcolor = Color.parseColor(pref_fontcolorhighlow)
                } else {
                    fontcolor = Color.parseColor(pref_fontcolorurgenthighlow)
                }
            }
        }
        catch (e: Exception ) {}

        binding.screenBg.setTextColor(fontcolor)
        binding.screenBg.setBackgroundColor(getComplementaryColor(fontcolor))

    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }

}