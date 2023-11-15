package com.nightscout.nightviewer

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.io.FileNotFoundException
import java.net.URL
import java.text.SimpleDateFormat

class BGData(private val context: Context){
    val pref_urlText = prefs.getString("ns_url", "defaultURL")
    //User_Prefs : 기본 Preferences (iob, cob, basal enable 등)
    //BG_db: 혈당 데이터베이스 (Preferences로 구현 sql로도 가능할듯)
    fun initializeBG_db(){ //최근 10개 데이터로 db initialize //delta,arrow정보는 제외
        Log.d("BGData.kt", "initialize 시작")
        Thread{
            val past10_EntireBGInfo = get_Past10_EntireBGInfo()
            Log.d("initialize", past10_EntireBGInfo.toString())
            SharedPreferencesUtil.saveBGDatas(context, past10_EntireBGInfo)
            get_EntireBGInfo()
        }.start()
    }

    fun get_EntireBGInfo() { //bg, time, delta, arrow, iob, cob, basal 데이터 전부 받기.
        Log.d("BGData.kt",  "getEntireData(1개) 시작")
        Thread{
            val newbg = get_BGInfoFromURL(pref_urlText)
            if(newbg != null) {
                newbg.LogBG()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val currentBGInfo = get_CurrentBGInfo()
                if (currentBGInfo != null) {
                    val currentTimeMillis = System.currentTimeMillis()
                    val bgTimeMillis = dateFormat.parse(currentBGInfo.time).time
                    val timeDifferenceInMinutes = ((currentTimeMillis - bgTimeMillis) / (1000 * 60)).toInt()
                    if (currentBGInfo.id != newbg.bginfo?.id ){
                        newbg.saveBG()
                    } else {
                        Log.d("getEntireBGInfo", "SKIPPED")
                    }
                } else {
                    if (SharedPreferencesUtil.getBGDatas(context).isEmpty())
                        newbg.saveBG()
                    else
                        Log.d("getEntireBGInfo", "BG 데이터를 가져오지 못했습니다.")
                }
            }
        }.start()
    }

    fun get_Past10_EntireBGInfo():List<BG>{ //과거 10개의 데이터 받아오기. !앱을 처음 켰을때만 실행.
        val BGList = mutableListOf<BG>()
        // Iterate over each object and extract the specified fields
        val url = URL("${pref_urlText}/api/v1/entries.json")
        val jsonresult = URL(url.toString()).readText()
        Log.d("get_past10", "${jsonresult}")


        val gson = Gson()
        val typeToken = object : TypeToken<List<GlucoseData>>() {}.type
        val openapsDataList: List<GlucoseData> = gson.fromJson(jsonresult, typeToken)
        for (data in openapsDataList) {
            val time: String = convertUnixTimeToDateTime(data.mills)
            val bg = data.sgv.toString()
            val id = data._id
            BGList.add(0, BG(bg, time, "XX", "0", "None", "None", "None", id)) //arrow, delta 데이터가 확인불가
        }
        val bgList = BGList.dropLast(1)
        return bgList
    }

    fun get_BGInfoFromURL(urlText:String?): BGInfo?{ //URL을 받아 BGInfo 한개를 리턴
        val currenttime: Long = System.currentTimeMillis()
        val url = try{
            URL("${urlText}/api/v2/properties/bgnow,delta,direction,buckets,iob,cob,basal")
        }
        catch(e: Exception){
            return null
        }
        if (url == null) {
            //Toast.makeText(context, "데이터를 불러올 수 없습니다. 확인을 누른 후 앱을 종료합니다.", Toast.LENGTH_SHORT).show()
            return null
        }
        var result = ""
        try{
            result = URL(url.toString()).readText()
        }catch(e: FileNotFoundException){
            return null
        }
//        val result = URL(url.toString()).readText()
        Log.d("get_bgurl", "${result}")
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currenttimedisplay : String = sdf.format(currenttime)
        //추후 try except 기능 추가 필요
        val bg = JSONObject(result).getJSONObject("bgnow").getString("last")
        val time_UTXmills = JSONObject(result).getJSONObject("bgnow").getJSONArray("sgvs").getJSONObject(0).getLong("mills")
        val time_LOCAL = convertUnixTimeToDateTime(time_UTXmills)
        val delta = JSONObject(result).getJSONObject("delta").getString("display")
        val arrow = JSONObject(result).getJSONObject("direction").getString("label")
        val iob = JSONObject(result).getJSONObject("iob").getString("display")
        val cob = JSONObject(result).getJSONObject("cob").getString("display")
        val basal = JSONObject(result).getJSONObject("basal").getString("display")
        val id = JSONObject(result).getJSONObject("bgnow").getJSONArray("sgvs").getJSONObject(0).getString("_id")
        Log.d("get_bgurl", "$id ,$basal")
        return BGInfo(bg, time_LOCAL, arrow, delta, iob, cob, basal, id)
    }

    fun get_CurrentBGInfo(): BG?{ //return 값 null가능
        val bgInfo = BGInfo()
        return bgInfo.bginfo
    }

    fun get_Recent10BGValues(): List<BG>{
        return SharedPreferencesUtil.getRecent10BGValues(context)
    }
    inner class BGInfo {
        var bginfo: BG?
        constructor(){ //현재 저장된 BGINFO 불러오기
            bginfo = SharedPreferencesUtil.getLatestBGData(context)
            if (bginfo == null) {

            }
        }
        constructor(bg: String, time: String, arrow: String, delta: String, iob: String, cob: String, basal: String, id: String){
            this.bginfo = BG(bg,time,arrow,delta,iob,cob,basal,id)
        }
        fun saveBG(){ //현재 BG 저장하기
            bginfo?.let { SharedPreferencesUtil.addBGData(context, it) }
        }
        fun LogBG(){ //받아온 데이터 Log
            Log.d("BGData.kt", this.bginfo.toString())
        }
    }
}