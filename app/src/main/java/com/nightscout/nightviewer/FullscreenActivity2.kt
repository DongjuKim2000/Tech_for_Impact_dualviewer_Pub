package com.nightscout.nightviewer
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import com.example.dualviewer.GraphThread
import com.github.mikephil.charting.charts.LineChart
import com.nightscout.nightviewer.databinding.ActivityFullscreen2Binding
import java.text.SimpleDateFormat
import android.content.res.Configuration
// 풀스크린 모드
class FullscreenActivity2 : CommonActivity() {

    lateinit var binding: ActivityFullscreen2Binding
    val showinfobr = ShowinfoBR()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("Activity2","onCreate")

        binding = ActivityFullscreen2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= 26) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.icon)
        }
        isFullscreen = true
        // Set up the user interaction to manually show or hide the system UI.
        fullscreenContent = binding.mainlayout2
        fullscreenContent.setOnClickListener { toggle() }

        val filter = IntentFilter()
        filter.addAction("showinfo") //수신할 action 종류 넣기
        registerReceiver(showinfobr, filter) //브로드캐스트리시버 등록

        adjustGuidelineBasedOnChartEnable()
        showinfo()

        val updateIntervalMillis: Long = 10000

        updateTimer = object : CountDownTimer(Long.MAX_VALUE, updateIntervalMillis) {
            override fun onTick(millisUntilFinished: Long) {
                showinfo()
            }
            override fun onFinish() {
                // 타이머가 무한대로 설정되었으므로 onFinish는 호출되지 않음
            }
        }
        updateTimer?.start()

        // 인터넷 연결 상태를 감지하는 Receiver
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        this.registerReceiver(internetBroadcaster, filter)

        Log.d("Activity2","onCreate 끝")

    }
    inner class ShowinfoBR : BroadcastReceiver()
    {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent?.action == "showinfo")
                showinfo()
        }
    }
    override fun onResume() {
        super.onResume()
        adjustGuidelineBasedOnChartEnable()
        if(Build.VERSION.SDK_INT >= 24) {
            if (isInMultiWindowMode) {
                Log.d("Activity", "multi window mode")
                val i = Intent(this, FullscreenActivity1::class.java)
                startActivity(i)
                Log.d("FullscreenActivity","스타트 activity1")

            } else {
                Log.d("Activity2", "not multi window")
            }
        }
    }
    override fun onDestroy() {
        Log.d("Activity2","onDetroy  시작")
        super.onDestroy()
        try{unregisterReceiver(showinfobr)} catch (e: Exception){}
        try{unregisterReceiver(internetBroadcaster)} catch (e: Exception){}
    }

    override fun onBackPressed() {
        // 현재 액티비티를 종료하고, 앱을 백그라운드로 보냅니다.
        moveTaskToBack(true)
        finish()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        adjustGuidelineBasedOnChartEnable()
    }

    private fun adjustGuidelineBasedOnChartEnable() {
        val chartEnable = prefs.getBoolean("chart_enable", true)
        val layoutParams = binding.guideline3.layoutParams as ConstraintLayout.LayoutParams

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val layoutParams = binding.guideline1.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.guidePercent = if (chartEnable) 0.4f else 1f // 가로 모드일 때
            binding.guideline1.layoutParams = layoutParams
        } else {
            val layoutParams = binding.guideline3.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.guidePercent = if (chartEnable) 0.6f else 1f // 세로 모드일 때
            binding.guideline3.layoutParams = layoutParams
        }



        // LineChart의 가시성도 업데이트
        binding.lineChart.visibility = if (chartEnable) View.VISIBLE else View.GONE

        // chartEnable이 true일 때만 그래프 스레드 시작
        if (chartEnable) {
            val thread = GraphThread(binding.lineChart, baseContext)
            thread.start()
        }
    }


    private fun showinfo() {

        //설정
        val pref_timeformat = prefs.getString("preftimeformat", "timeformat24")
        val pref_urgenthighvalue = prefs.getString("urgent_high_value", "260")?.toFloat() ?: 260f
        val pref_highvalue = prefs.getString("high_value", "180")?.toFloat() ?: 180f
        val pref_lowvalue = prefs.getString("low_value", "80")?.toFloat() ?: 80f
        val pref_urgentlowvalue = prefs.getString("urgent_low_value", "55")?.toFloat() ?: 55f
        val pref_bgfont = prefs.getString("bg_font", "200")?.toFloat() ?: 200f
        val pref_directionfont = prefs.getString("direction_font", "100")?.toFloat() ?: 100f
        val pref_timeinfofont = prefs.getString("timeinfo_font", "30")?.toFloat() ?: 30f
        val pref_fontcolornormal = prefs.getString("fontcolornormal", "#FCFFFFFF").toString()
        val pref_fontcolorhighlow = prefs.getString("fontcolorhighlow", "#FF0000").toString()
        val pref_fontcolorurgenthighlow =
            prefs.getString("fontcolorurgenthighlow", "#FFFF00").toString()



        val bgData = BGData(this)

        if(bgData == null)
            finish()

        val bgInfo = bgData.BGInfo()
        //bgData.get_EntireBGInfo()
        val current_bgInfo = bgInfo.bginfo
        Log.d("current_info", "${current_bgInfo.toString()}")


        val currentTime: Long = System.currentTimeMillis() // ms로 반환

        var mins: Long = 0
        var displayMins: String = ""
        var info: String = ""
        var int_bg = 0
        var sdf = SimpleDateFormat("HH:mm")
        if (pref_timeformat == "timeformat12") {
            sdf = SimpleDateFormat("a hh:mm")
        }
        val displayTime: String = sdf.format(currentTime)
        info = "$displayTime   $displayMins"
        if (current_bgInfo != null) {
            var displayIOB = current_bgInfo.iob

            if (current_bgInfo.iob != "") {
                displayIOB = "   \uD83C\uDD58${current_bgInfo.iob}U"
                info += displayIOB
            }
            var displayCOB = current_bgInfo.cob

            if (current_bgInfo.cob != "") {
                displayCOB = "   \uD83C\uDD52${current_bgInfo.cob}g"
                info += displayCOB
            }
            Log.d("showinfo", "iob cob 끝")
            // xml 구성 관련 부분
            var bg_value: String = current_bgInfo.bg
            var float_bg = bg_value.toFloat()
            int_bg = float_bg.toInt()
            binding.screenBg.text = int_bg.toString()
            Log.d("showinfo", "bg값 끝")

            binding.screenDirection.text =
                "${current_bgInfo.arrow} ${current_bgInfo.delta}"
            binding.screenInfo.text = info

            binding.screenBg.textSize = pref_bgfont.toFloat()
            binding.screenDirection.textSize =
                pref_directionfont.toFloat()
            binding.screenInfo.textSize = pref_timeinfofont.toFloat()
        }

        //그래프 표시
        val lineChart: LineChart = findViewById(R.id.lineChart)
        val chartEnable = prefs.getBoolean("chart_enable", true)
        adjustGuidelineBasedOnChartEnable()
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

            //일반혈당
            if (pref_lowvalue <= bgInt && bgInt <= pref_highvalue) {
                fontcolor = Color.parseColor(pref_fontcolornormal)
            }
            else if (pref_urgentlowvalue <= bgInt && bgInt <= pref_urgenthighvalue) {
                fontcolor = Color.parseColor(pref_fontcolorhighlow)
            }
            else {
                fontcolor = Color.parseColor(pref_fontcolorurgenthighlow)
            }
        }
        catch (e: Exception ) {
            fontcolor = Color.WHITE
        }
        Log.d("color", "${fontcolor.toString()}")

        binding.screenBg.setTextColor(fontcolor)
        binding.screenBg.setBackgroundColor(getComplementaryColor(fontcolor))


    }
}