package com.nightscout.nightviewer
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import com.example.dualviewer.GraphThread
import com.github.mikephil.charting.charts.LineChart
import com.nightscout.nightviewer.databinding.ActivityFullscreen1Binding
import java.text.SimpleDateFormat

// 멀티스크린을 위한 액티비티입니다.
class FullscreenActivity1 : CommonActivity() {

    lateinit var binding: ActivityFullscreen1Binding
    val showinfobr = ShowinfoBR()

    private var alarmed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreen1Binding.inflate(layoutInflater) //??

        Log.d("Activity1","onCreate")
        setContentView(binding.root) // xml 파일 지정

        val filter = IntentFilter()  // 인텐트 지정
        filter.addAction("showinfo") //수신할 action 종류 넣기
        registerReceiver(showinfobr, filter) //브로드캐스트리시버 등록

        if (Build.VERSION.SDK_INT >= 26) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.icon)
        }
        isFullscreen = true

        fullscreenContent = binding.mainlayout1
        fullscreenContent.setOnClickListener { toggle() }


        showinfo()
        // 인터넷 연결 상태를 감지하는 Receiver
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(internetBroadcaster, filter)

        val updateIntervalMillis: Long = 5000
        var reconnected = true
        updateTimer = object : CountDownTimer(Long.MAX_VALUE, updateIntervalMillis) {
            override fun onTick(millisUntilFinished: Long) {

                if (!isOnline(this@FullscreenActivity1)) {
                    showErrorMessage(this@FullscreenActivity1, "인터넷 연결 X")

                    reconnected = false
                }
                else {
                    if (reconnected==false) {
                        showMessage(this@FullscreenActivity1, "인터넷 연결 재개")
                    }

                    try{showinfo()
                        reconnected = true } catch(e:Exception){
                        reconnected = false
                        showErrorMessage(this@FullscreenActivity1, "인터넷 연결이 안되어 있습니다")
                    }
                }
            }
            override fun onFinish() {
                // 타이머가 무한대로 설정되었으므로 onFinish는 호출되지 않음
            }
        }
        updateTimer?.start()


        Log.d("Activity1","onCreate 끝")

    }
    override fun onResume() {
        Log.d("Activity1","onResume")
        super.onResume()
        if(Build.VERSION.SDK_INT >= 24) {
            if (isInMultiWindowMode) {
                Log.d("Activity1", "multi window mode")
                Log.d("FullscreenActivity","스타트 activity1")

            } else {
                val i = Intent(this, FullscreenActivity2::class.java)
                startActivity(i) // 멀티 윈도우 모드로 진행
                Log.d("Activity2", "not multi window")
            }
        }
        val currenttime : Long = System.currentTimeMillis()


    }

    override fun onDestroy() {
        Log.d("Activity1","onDetroy  시작")
        super.onDestroy()
        try{unregisterReceiver(showinfobr)} catch (e: Exception){}
        try{unregisterReceiver(internetBroadcaster)} catch (e: Exception){}
    }

    override fun onBackPressed() {
        // 현재 액티비티를 종료하고, 앱을 백그라운드로 보냅니다.
        moveTaskToBack(true)
        finish()
    }

    inner class ShowinfoBR : BroadcastReceiver()
    {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent?.action == "showinfo")
                showinfo()
        }
    }

    private fun showinfo() {

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

        val IOBEnable = prefs.getBoolean("iob_enable", false)
        val COBEnable = prefs.getBoolean("cob_enable", false)
        val BasalEnable = prefs.getBoolean("basal_enable", false)


        val bgData = BGData(this)

        if(bgData == null)
            finish()

        val bgInfo = bgData.BGInfo()
        //bgData.get_EntireBGInfo()
        val current_bgInfo = bgInfo.bginfo

        Log.d("current_info", "${current_bgInfo.toString()}")


        val currentTime : Long = System.currentTimeMillis() // ms로 반환

        var mins: Long = 0

        var info : String = ""
      
        var int_bg =0
        var sdf = SimpleDateFormat("HH:mm")
        if (pref_timeformat == "timeformat12"){ sdf = SimpleDateFormat("a hh:mm") }
        val displayTime: String = sdf.format(currentTime)
//        var displayMins: String = ""
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
            Log.d("showinfo", "iob cob 끝")
            // xml 구성 관련 부분
            var  bg_value : String = current_bgInfo.bg
            var float_bg = bg_value.toFloat()
            int_bg = float_bg.toInt()
            binding.screenBg.text = int_bg.toString()
            Log.d("showinfo", "bg값 끝")

            binding.screenDirection.text ="${current_bgInfo.arrow} ${current_bgInfo.delta}"
            binding.screenInfo.text = info

            binding.screenBg.textSize = pref_bgfont.toFloat()
            binding.screenDirection.textSize = pref_directionfont.toFloat()
            binding.screenInfo.textSize = pref_timeinfofont.toFloat()
        }

        //그래프 표시
        val lineChart: LineChart = findViewById(R.id.lineChart)
        val chartEnable = prefs.getBoolean("chart_enable", true)
        if(chartEnable){
            val thread = GraphThread(lineChart, baseContext)
            thread.start()
        }
        else
            lineChart.visibility = View.GONE

        if (isFullscreen) { hide() }

        fun getComplementaryColor(color: Int): Int {
            val alpha = color shr 24 and 0xFF
            val red = 255 - (color shr 16 and 0xFF)
            val green = 255 - (color shr 8 and 0xFF)
            val blue = 255 - (color and 0xFF)
            return alpha shl 24 or (red shl 16) or (green shl 8) or blue
        }

        var fontcolor : Int

        try {

            val bgInt : Int = int_bg
            val vibrateEnable = prefs.getBoolean("vibrate_enable", true)
            val ringtoneEnable = prefs.getBoolean("ringtone_enable", true)

            //일반혈당
            if (pref_lowvalue <= bgInt && bgInt <= pref_highvalue) {
                fontcolor = Color.parseColor(pref_fontcolornormal)
                alarmed = false
            }
            else if (pref_urgentlowvalue <= bgInt && bgInt <= pref_urgenthighvalue) {
                fontcolor = Color.parseColor(pref_fontcolorhighlow)
                if(!alarmed){
                    if(ringtoneEnable)
                        playNotificationSound(this)
                    if(vibrateEnable)
                        doVibrate(this)
                }
                alarmed = true
            }
            else {
                fontcolor = Color.parseColor(pref_fontcolorurgenthighlow)
                if(ringtoneEnable)
                    playNotificationSound(this)
                if(vibrateEnable)
                    doVibrate(this)
            }
        }
        catch (e: Exception ) {
            fontcolor = Color.WHITE
        }

        Log.d("color", "${fontcolor.toString()}")

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