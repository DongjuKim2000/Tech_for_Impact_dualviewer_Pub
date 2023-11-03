package com.nightscout.nightviewer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.net.URL
lateinit var prefs: SharedPreferences
lateinit var bgprefs: SharedPreferences

class StartURLActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_urlactivity)
        prefs = getSharedPreferences("root_preferences", Context.MODE_PRIVATE)
        bgprefs = getSharedPreferences("prefs_bghistory", MODE_PRIVATE)

        if(prefs.getString("ns_url", "defaultURL")!="defaultURL"){
            val intent = Intent(this, FullscreenActivity::class.java)
            startActivity(intent)
            finish()
        }

        val urlEditText = findViewById<EditText>(R.id.urlEditText)
        val confirmButton = findViewById<Button>(R.id.confirmButton)

        confirmButton.setOnClickListener(){
            val urlText = urlEditText.text.toString()
            Log.d("first", urlText)
            if(isValidInput(urlText)){
                with(prefs.edit()) {
                    putString("ns_url", urlText)
                    apply()
                }
                val intent = Intent(this, FullscreenActivity::class.java)
//                intent.putExtra("urlText", urlText)
                startActivity(intent)
                finish()
            }
        }

    }

    private fun isValidInput(text: String): Boolean {
        val url = try{
            URL("${text}/api/v2/properties/bgnow,delta,direction,buckets,iob,cob,basal")
        }
        catch(e: Exception){
            null
        }
        val ret = (url != null)
        if(!ret)
            showErrorMessage(this, "올바르지 않은 URL입니다.")
        return ret
    }

//    private fun saveTextToPreference(text: String) {
//        with(prefs.edit()) {
//            putString("ns_url", text)
//            apply()
//        }
//    }
}