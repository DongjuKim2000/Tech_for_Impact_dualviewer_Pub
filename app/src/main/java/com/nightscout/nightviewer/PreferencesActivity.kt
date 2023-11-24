package com.nightscout.nightviewer
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.nightscout.nightviewer.databinding.RootScreenBinding


private lateinit var binding: RootScreenBinding

class PreferencesActivity : AppCompatActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RootScreenBinding.inflate(layoutInflater)
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

            val nsUrlPreference:Preference? = findPreference<EditTextPreference>("ns_url")


            nsUrlPreference?.setOnPreferenceClickListener {
                // 새로운 액티비티 시작
//                var editor = prefs.edit()
//                editor.remove("ns_url")
//                editor.apply()
//                editor.putString("ns_url", "defaultURL")
//                editor.apply()
                prefs.edit().clear().apply()
                bgprefs.edit().clear().apply()

                val intent = Intent(activity, StartURLActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
//                activity?.finish()
                true // 이벤트 처리 완료
            }




            // 숫자 패드 띄우기

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

            numberPreference1?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                val urgentHighValue = newValue.toString().toInt()
                val highValue = numberPreference2?.text?.toInt() ?: 0
                urgentHighValue >= highValue
            }

            numberPreference2?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                val urgentHighValue = numberPreference1?.text?.toInt() ?: 0
                val highValue = newValue.toString().toInt()
                val lowValue = numberPreference3?.text?.toInt() ?: 0

                highValue in lowValue..urgentHighValue
            }

            numberPreference3?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                val highValue = numberPreference2?.text?.toInt() ?: 0
                val lowValue = newValue.toString().toInt()
                val urgentLowValue = numberPreference4?.text?.toInt() ?: 0

                lowValue in urgentLowValue..highValue
            }

            numberPreference4?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                val lowValue = numberPreference3?.text?.toInt() ?: 0
                val urgentLowValue = newValue.toString().toInt()

                lowValue >= urgentLowValue
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

            val numberPreference10: EditTextPreference? = findPreference("chartBG_max")
            numberPreference10?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }

            val numberPreference11: EditTextPreference? = findPreference("chartBG_min")
            numberPreference11?.setOnBindEditTextListener { editText ->
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

        val intent = Intent(this, SingleviewActivity::class.java)
        startActivity(intent)
        finish()

    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        // or call onBackPressed()
        return true
    }

    private fun saveSharedPreference() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)

        val pref_enablenoti = true
        val pref_readfromns = true
        val ns_url = url_text


        //val units = pref.getString("units", "mgdl")
        val urgent_high_value = pref.getString("urgent_high_value", "260")
        val high_value = pref.getString("high_value", "180")
        val low_value = pref.getString("low_value", "80")
        val urgent_low_value = pref.getString("urgent_low_value", "55")

        val pref_timeformat = pref.getString("preftimeformat", "timeformat24")
        val iob_enable = pref.getBoolean("iob_enable", true)
        val cob_enable = pref.getBoolean("cob_enable", true)
        val basal_enable = pref.getBoolean("basal_enable", true)
        val chart_enable = pref.getBoolean("chart_enable", true)

        val vibrate_enable = pref.getBoolean("vibrate_enable", true)
        val ringtone_enable = pref.getBoolean("ringtone_enable", true)

        val fontcolornormal = pref.getString("fontcolornormal", "#FCFFFFFF")
        val fontcolorhighlow = pref.getString("fontcolorhighlow", "#FCFFFFFF")
        val fontcolorurgenthighlow = pref.getString("fontcolorurgenthighlow", "#FCFFFFFF")

        val chartbgpointsize = pref.getString("chartbgpointsize", "4")
        val chartlinecolorhighlow = pref.getString("chartlinecolorhighlow", "#FCFFFF00")
        val chartlinecolorurgenthighlow = pref.getString("chartlinecolorurgenthighlow", "#FCFF0000")
        val chartlinewidth = pref.getString("chartlinewidth", "1")
        val xaxis_enable = pref.getBoolean("chart_xaxis_enable", true)
        val chartBGMax = pref.getString("chartBG_max", "400")
        val chartBGMin = pref.getString("chartBG_min", "40")


        binding.apply {
            //val sharedPreference =  getSharedPreferences("root_preferences",MODE_PRIVATE)
            var editor = prefs.edit()

            editor.putString("ns_url", ns_url)

            editor.putString("urgent_high_value", urgent_high_value)
            editor.putString("high_value", high_value)
            editor.putString("low_value", low_value)
            editor.putString("urgent_low_value", urgent_low_value)

            editor.putString("preftimeformat", pref_timeformat)
            editor.putBoolean("iob_enable", iob_enable)
            editor.putBoolean("cob_enable", cob_enable)
            editor.putBoolean("basal_enable", basal_enable)
            editor.putBoolean("chart_enable", chart_enable)

            editor.putBoolean("vibrate_enable", vibrate_enable)
            editor.putBoolean("ringtone_enable", ringtone_enable)

            editor.putString("fontcolornormal", fontcolornormal)
            editor.putString("fontcolorhighlow", fontcolorhighlow)
            editor.putString("fontcolorurgenthighlow", fontcolorurgenthighlow)


            editor.putString("chartbgpointsize", chartbgpointsize)
            editor.putString("chartlinecolorhighlow", chartlinecolorhighlow)
            editor.putString("chartlinecolorurgenthighlow", chartlinecolorurgenthighlow)
            editor.putString("chartlinewidth", chartlinewidth)
            editor.putBoolean("xaxis_enable", xaxis_enable)
            editor.putString("chartBG_max", chartBGMax)
            editor.putString("chartBG_min", chartBGMin)

            editor.apply()
        }
    }
}