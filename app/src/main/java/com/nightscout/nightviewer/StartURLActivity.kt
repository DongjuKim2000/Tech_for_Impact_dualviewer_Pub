package com.nightscout.nightviewer

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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
lateinit var url_text :String
class StartURLActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_urlactivity)
        supportActionBar?.hide()
        prefs = getSharedPreferences("root_preferences", Context.MODE_PRIVATE)
        bgprefs = getSharedPreferences("prefs_bghistory", MODE_PRIVATE)
        url_text = prefs.getString("ns_url", "defaultURL")?: "defaultURL"

        if(prefs.getString("ns_url", "defaultURL")!="defaultURL"){
            val intent = Intent(this, InitializeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val textViews = arrayOf(findViewById<TextView>(R.id.text1), findViewById(R.id.text2))

        findViewById<TextView>(R.id.text2).alpha = 0f

        fun startAnimation(textView: TextView) {
            textView.apply {
                alpha = 0f
                translationY = -100f
            }

            val alphaAnimation = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f).apply {
                duration = 1000L // 애니메이션 지속 시간 설정 (1초)
            }

            val translateAnimation = ObjectAnimator.ofFloat(textView, "translationY", -100f, 0f).apply {
                duration = 1000L // 애니메이션 지속 시간 설정 (1초)
            }

            AnimatorSet().apply {
                playTogether(alphaAnimation, translateAnimation)
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator?) {}

                    override fun onAnimationEnd(animation: Animator?) {
                        val nextIndex = textViews.indexOf(textView) + 1
                        if (nextIndex < textViews.size) {
                            startAnimation(textViews[nextIndex])
                        }
                    }

                    override fun onAnimationCancel(animation: Animator?) {}

                    override fun onAnimationRepeat(animation: Animator?) {}
                })
                start()
            }
        }
        startAnimation(textViews[0])

        val urlEditText = findViewById<EditText>(R.id.urlEditText)
        val confirmButton = findViewById<Button>(R.id.confirmButton)

        confirmButton.setOnClickListener(){
            val urlText = urlEditText.text.toString()
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
                    val intent = Intent(this, InitializeActivity::class.java)
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

        val editor = prefs.edit()
        editor.putString("ns_url", url_text)
        editor.apply()


        if (url == null) {
            showErrorMessage(this, "올바르지 않은 URL입니다.")
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