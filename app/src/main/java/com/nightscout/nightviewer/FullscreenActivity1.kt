package com.nightscout.nightviewer
import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.nightscout.nightviewer.databinding.ActivityFullscreen1Binding
import java.text.SimpleDateFormat
import android.os.CountDownTimer
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout

// 멀티스크린을 위한 액티비티입니다.
class FullscreenActivity1 : AppCompatActivity() {

    lateinit var binding: ActivityFullscreen1Binding
    val showinfobr = ShowinfoBR()  //ShowinfoBR() 클래스. 이 프로그램의 유일한 리시버
    private var updateTimer: CountDownTimer? = null
    private lateinit var fullscreenContent: ConstraintLayout
    private val hideHandler = Handler(Looper.myLooper()!!)
    private var isFullscreen: Boolean = false

    inner class ShowinfoBR : BroadcastReceiver()
    {
        override fun onReceive(context: Context?, intent: Intent?) {
            showinfo()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // 메뉴 만들고 수행
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 메뉴에서 고른 항목별 액티비티 실행
        when (item.itemId) {
            R.id.menu_preference -> {
                val i = Intent(this, PreferencesActivity::class.java)
                startActivity(i) // preference 설정 페이지로 넘어감
                return true
            }
            R.id.menu_about -> {

                val image = ImageView(this)
                image.setImageResource(R.drawable.kst1d)
                // 다이얼로그로 아래 메시지 출력
                var msg = "Made by 김해서연아빠<br>"
                msg += "Icon directed by 광명셀리나맘 Icon made by 광명셩키<br>"
                msg += "Thanks to 박상미 강서연 강지유 시조새팬클럽<br>"
                msg += "and 한국1형당뇨병환우회<br><br>"
                msg += "환우회 링크   :&nbsp;"
                msg += "<a href=\"http://kst1d.org\">홈페이지</a>&nbsp;&nbsp;&nbsp;"
                msg += "<a href=\"https://cafe.naver.com/t1d\">공식카페(슈거트리)</a><br>"
                msg += "<a href=\"https://blog.naver.com/kst1diabetes\">블로그</a>&nbsp;&nbsp;&nbsp;"
                msg += "<a href=\"https://www.youtube.com/channel/UCyO4LR8XD-UzCdsjAWRGlNQ?view_as=subscriber\">유튜브</a>&nbsp;&nbsp;&nbsp;"
                msg += "<a href=\"https://www.instagram.com/kst1diabetes\">인스타그램</a>&nbsp;&nbsp;&nbsp;"
                msg += "<a href=\"https://www.facebook.com/%ED%95%9C%EA%B5%AD1%ED%98%95%EB%8B%B9%EB%87%A8%EB%B3%91%ED%99%98%EC%9A%B0%ED%9A%8C-509826469456836\">페이스북</a>"

                var newmsg = Html.fromHtml(msg)

                val d: AlertDialog = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogTheme))

                    .setPositiveButton("OK", null)   //버튼
                    .setIcon(R.mipmap.ic_main_round)    //메시지 밑에 아이콘
                    .setTitle("Nightviewer v1.11")
                    .setMessage(newmsg)
                    .setView(image)
                    .create()

                d.show()

                val positiveButton: Button = d.getButton(AlertDialog.BUTTON_POSITIVE)
                positiveButton.setTextColor(Color.parseColor("#515151"))

                (d.findViewById(android.R.id.message) as TextView).movementMethod = LinkMovementMethod.getInstance()
                return true
            }
            R.id.menu_exit -> { // 설정 버튼 중 exit(나가기) 누름
                finish()  // 앱 (완전히) 종료
                return true
            }
            else -> return false // 지정된 버튼이 아닌 다른 곳 누름
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onOptionsMenuClosed(menu: Menu?) {
        super.onOptionsMenuClosed(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreen1Binding.inflate(layoutInflater) //??

        Log.d("Activity1","onCreate")
        setContentView(binding.root) // xml 파일 지정


        if (Build.VERSION.SDK_INT >= 26) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.icon)
        }
        isFullscreen = true

        fullscreenContent = binding.mainlayout1
        fullscreenContent.setOnClickListener { toggle() }

        val filter = IntentFilter()  // 인텐트 지정
        filter.addAction("showinfo") //수신할 action 종류 넣기
        registerReceiver(showinfobr, filter) //브로드캐스트리시버 등록

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

        Log.d("Activity1","onCreate 끝")

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        Log.d("Activity1","onPostCreate")
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
    }


    private fun toggle() { //터치시 상단바
        Log.d("toggle", "toggle 작동")
        if (isFullscreen) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {

        // Hide UI first
        supportActionBar?.hide()
        //fullscreenContentControls.visibility = View.GONE
        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.post(hidePart2Runnable)
        isFullscreen = false
    }

    private fun show() {

        // Show the system bar
        supportActionBar?.show()
        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.post(showPart2Runnable)
        isFullscreen = true

}

    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())

        } else {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            fullscreenContent.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        }
    }
    private val showPart2Runnable = Runnable {
        supportActionBar?.show()
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


        val bgData = BGData(this)
        val bgInfo = bgData.BGInfo()
        //bgData.get_EntireBGInfo()
        val current_bgInfo = bgInfo.bginfo
        Log.d("current_info", "${current_bgInfo.toString()}")


        val currentTime : Long = System.currentTimeMillis() // ms로 반환

        var mins: Long = 0
        var displayMins: String = ""
        var info : String = ""
        var int_bg =0
        var sdf = SimpleDateFormat("HH:mm")
        if (pref_timeformat == "timeformat12"){ sdf = SimpleDateFormat("a hh:mm") }
        val displayTime: String = sdf.format(currentTime)
        info = "$displayTime   $displayMins"
        if(current_bgInfo!=null){
            var displayIOB = current_bgInfo.iob

            if (current_bgInfo.iob != ""){
                displayIOB = "   \uD83C\uDD58${current_bgInfo.iob}U"
                info += displayIOB
            }
            var displayCOB = current_bgInfo.cob

            if (current_bgInfo.cob != ""){
                displayCOB = "   \uD83C\uDD52${current_bgInfo.cob}g"
                info += displayCOB
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

    private fun setLineChartInitialization() {


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