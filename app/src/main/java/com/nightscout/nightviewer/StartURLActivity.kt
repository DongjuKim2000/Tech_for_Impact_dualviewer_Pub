package com.nightscout.nightviewer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.URL
lateinit var prefs: SharedPreferences
lateinit var bgprefs: SharedPreferences
var url_text = "defaultURL"
class StartURLActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_urlactivity)
        supportActionBar?.hide()
        prefs = getSharedPreferences("root_preferences", Context.MODE_PRIVATE)
        bgprefs = getSharedPreferences("prefs_bghistory", MODE_PRIVATE)

        if(prefs.getString("ns_url", "defaultURL")!="defaultURL"){
            val intent = Intent(this, FullscreenActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 텍스트뷰 배열 생성
        val textViews = arrayOf(findViewById<TextView>(R.id.text1), findViewById<TextView>(R.id.text2))
        // 애니메이션셋 생성
        val alphaAnimation = AlphaAnimation(1.0f, 0.0f).apply {
            duration = 1000L // 애니메이션 지속 시간 설정 (1초)
        }

        val translateAnimation = TranslateAnimation(0f, 0f, 0f, 100f).apply {
            duration = 1000L // 애니메이션 지속 시간 설정 (1초)
        }

        val animationSet = AnimationSet(false).apply {
            addAnimation(alphaAnimation)
            addAnimation(translateAnimation)
        }

        // 애니메이션 리스너 설정
        var i = 0
        animationSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                i++
                if (i < textViews.size) {
                    textViews[i].startAnimation(animationSet)
                }
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })

        textViews[0].startAnimation(animationSet)


        val urlEditText = findViewById<EditText>(R.id.urlEditText)
        val confirmButton = findViewById<Button>(R.id.confirmButton)

        confirmButton.setOnClickListener(){
            val urlText = urlEditText.text.toString()
            Log.d("first", urlText)
            url_text = urlText

            if(isValidInput(urlText)) {
                val isValidInput = runBlocking {
                    isValidInputAsync(urlText)
                }


                if (isValidInput) {
                    val pref = PreferenceManager.getDefaultSharedPreferences(this)
                    pref.edit().putString("ns_url", urlText)
                    with(prefs.edit()) {
                        putString("ns_url", url_text)
                        apply()
                    }
                    Log.d("starturl", "${url_text}")
                    val intent = Intent(this, FullscreenActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else{
                    showErrorMessage(this, "올바르지 않은 URL입니다.")
                }
            }
        }

    }


    private suspend fun getBgInfoAsync(url: String): BGData.BGInfo? {
        return BGData(this).get_BGInfoFromURL(url)
    }


    private suspend fun isValidInputAsync(text: String): Boolean {
        if (text.isNotEmpty() ){
            if(text.last()=='/'){
                url_text =text.dropLast(1)
            }
            else url_text = text
        }
        val url = try {
            URL("${url_text}/api/v2/properties/bgnow,delta,direction,buckets,iob,cob,basal")

        } catch (e: Exception) {
            null
        }
        Log.d("isvalid", "${text.toString()}")


        val editor = prefs.edit()
        editor.putString("ns_url", url_text)
        editor.apply()


        if (url == null) {
            showErrorMessage(this, "올바르지 않은 URL입니다.")
            Log.d("starturl", "incorrect")
            return false
        } else {
            var isValid = false

            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val bgInfo = getBgInfoAsync(url.toString())
                    isValid = (bgInfo != null)
                    if (!isValid) {
                        runOnUiThread {
                            showErrorMessage(this@StartURLActivity, "올바르지 않은 URL입니다.")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.join()

            return isValid
        }
    }

    private fun isValidInput(text: String): Boolean {
        if (text.isNotEmpty() ){
            if(text.last()=='/'){
            url_text =text.dropLast(1)
            }
            else url_text = text
        }
        Log.d("url_text", "${url_text}")
        val url = try{
            URL("${url_text}/api/v2/properties/bgnow,delta,direction,buckets,iob,cob,basal")
        }
        catch(e: Exception){
            null
        }
        val ret = (url != null)
        if(!ret)
            showErrorMessage(this, "올바르지 않은 URL입니다.")
        return ret
    }

}