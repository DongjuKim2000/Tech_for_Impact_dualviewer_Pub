package com.nightscout.nightviewer
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.nightscout.nightviewer.databinding.ActivityFullscreenBinding


private lateinit var binding: ActivityFullscreenBinding

class PreferencesActivity : AppCompatActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setContentView(R.layout.preferences_activity)
        if (savedInstanceState == null) { // 시작화면
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.latout_preferences, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val numberPreference1: EditTextPreference? = findPreference("urgent_high_value")
            numberPreference1?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }

            val numberPreference2: EditTextPreference? = findPreference("high_value")
            numberPreference2?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }

            val numberPreference3: EditTextPreference? = findPreference("low_value")
            numberPreference3?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }

            val numberPreference4: EditTextPreference? = findPreference("urgent_low_value")
            numberPreference4?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }

            val numberPreference5: EditTextPreference? = findPreference("bg_font")
            numberPreference5?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }

            val numberPreference6: EditTextPreference? = findPreference("direction_font")
            numberPreference6?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }

            val numberPreference7: EditTextPreference? = findPreference("timeinfo_font")
            numberPreference7?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }

            val numberPreference8: EditTextPreference? = findPreference("chartbgpointsize")
            numberPreference8?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }

            val numberPreference9: EditTextPreference? = findPreference("chartlinewidth")
            numberPreference9?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        saveSharedPreference()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveSharedPreference()


        val intent = Intent(this, FullscreenActivity2::class.java)
        Log.d("pref_act", "새로운 액티비티 시작")
        startActivity(intent)
        finish()

        Log.d("pref_act", "exit")


    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        // or call onBackPressed()
        return true
    }

    private fun saveSharedPreference() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)

        val pref_layout = pref.getString("pref_layout", "1")
        val pref_enablenoti = true
        val pref_readfromns = true
        //val ns_url = pref.getString("ns_url", "https://{yoursite}.herokuapp.com")
        val ns_url = "https://pkd7320591.my.nightscoutpro.com/"

        //val units = pref.getString("units", "mgdl")
        val urgent_high_value = pref.getString("urgent_high_value", "260")
        val high_value = pref.getString("high_value", "180")
        val low_value = pref.getString("low_value", "80")
        val urgent_low_value = pref.getString("urgent_low_value", "55")

        val pref_timeformat = pref.getString("preftimeformat", "timeformat24")
        val iob_enable = pref.getBoolean("iob_enable", false)
        val cob_enable = pref.getBoolean("cob_enable", false)
        val basal_enable = pref.getBoolean("basal_enable", false)

        val fontcolornormal = pref.getString("fontcolornormal", "#FCFFFFFF")
        val fontcolorhighlow = pref.getString("fontcolorhighlow", "#FCFFFFFF")
        val fontcolorurgenthighlow = pref.getString("fontcolorurgenthighlow", "#FCFFFFFF")

        val chartbgcolornormal = pref.getString("chartbgcolornormal", "#FC00FF00")
        val chartbgcolorhighlow = pref.getString("chartbgcolorhighlow", "#FCFFFF00")
        val chartbgcolorurgenthighlow = pref.getString("chartbgcolorurgenthighlow", "#FCFF0000")
        val chartbgpointsize = pref.getString("chartbgpointsize", "5")

        val chartlinecolorhighlow = pref.getString("chartlinecolorhighlow", "#FCFFFF00")
        val chartlinecolorurgenthighlow = pref.getString("chartlinecolorurgenthighlow", "#FCFF0000")
        val chartlinewidth = pref.getString("chartlinewidth", "1")

        binding.apply {
            //val sharedPreference =  getSharedPreferences("root_preferences",MODE_PRIVATE)
            var editor = prefs.edit()
            editor.putString("pref_layout", pref_layout)
            editor.putBoolean("enablenoti", pref_enablenoti)
            editor.putBoolean("readfromns", pref_readfromns)
            editor.putString("ns_url", ns_url)

            editor.putString("urgent_high_value", urgent_high_value)
            editor.putString("high_value", high_value)
            editor.putString("low_value", low_value)
            editor.putString("urgent_low_value", urgent_low_value)

            editor.putString("preftimeformat", pref_timeformat)
            editor.putBoolean("iob_enable", iob_enable)
            editor.putBoolean("cob_enable", cob_enable)
            editor.putBoolean("basal_enable", basal_enable)

            editor.putString("fontcolornormal", fontcolornormal)
            editor.putString("fontcolorhighlow", fontcolorhighlow)
            editor.putString("fontcolorurgenthighlow", fontcolorurgenthighlow)

            editor.putString("chartbgcolornormal", chartbgcolornormal)
            editor.putString("chartbgcolorhighlow", chartbgcolorhighlow)
            editor.putString("chartbgcolorurgenthighlow", chartbgcolorurgenthighlow)
            editor.putString("chartbgpointsize", chartbgpointsize)

            editor.putString("chartlinecolorhighlow", chartlinecolorhighlow)
            editor.putString("chartlinecolorurgenthighlow", chartlinecolorurgenthighlow)
            editor.putString("chartlinewidth", chartlinewidth)

            editor.apply()
            //log.d("설정변경 : ","${ns_url} ${pref_layout} ${iob_enable} ${cob_enable} ${basal_enable}" )
        }
    }
}