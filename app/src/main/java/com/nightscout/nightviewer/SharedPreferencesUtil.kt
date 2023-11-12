package com.nightscout.nightviewer

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.util.Log

object SharedPreferencesUtil {
    private const val PREF_NAME = "BG_db" //Preference Name
    private const val KEY_USERS = "BGdata"
    private const val MAX_BG_ENTRIES = 25 // 최대 BGData MAX_BG_ENTRIES 만큼 저장 => 메모리 무한으로 잡아먹지 못하게
    private val gson = Gson()

    fun saveBGDatas(context: Context, BGdatas: List<BG>) { //여러개의 BGData를 저장
        val lastNEntries = BGdatas.takeLast(MAX_BG_ENTRIES)
        val json = gson.toJson(lastNEntries)
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_USERS, json).apply()
        Logr.d("saveBGDatas", BGdatas.toString())
    }

    fun addBGData(context: Context, BGData: BG) { //하나의 BGData를 저장
        val existingUsers = getBGDatas(context).toMutableList()
        existingUsers.add(BGData)
        val lastNEntries = existingUsers.takeLast(MAX_BG_ENTRIES)
        saveBGDatas(context, lastNEntries)
    }

    fun getBGDatas(context: Context): List<BG> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(KEY_USERS, null)
        val type = object : TypeToken<List<BG>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
    fun getLatestBGData(context: Context): BG? {
        val datas = getBGDatas(context)
        //Logr.d("lastBGData", datas.toString())
        return try {
            //datas.last().arrow = getGlucoseTrend(datas) //마지막 arrow를 불러올때 계산하기.
            if (datas.last() != null){
                datas.last().delta = calculateDelta(datas)
            }
            datas.last()
        } catch (e: NoSuchElementException) { // 리스트가 비어있을 경우
            null
        }
    }
    fun getRecent10BGValues(context: Context): List<BG> {
        val bgList = getBGDatas(context) // getBGDatas 함수로 모든 BG 데이터를 가져옵니다.

        // 최근 10개의 BG 데이터를 추출합니다. 만약 BG 데이터가 10개 미만이면 전체 데이터를 반환합니다.
        val recent10BGs = if (bgList.size <= 10) {
            bgList
        } else {
            bgList.subList(bgList.size - 10, bgList.size)
        }

        // 최근 10개 BG 데이터에서 a 필드를 추출한 문자열 리스트를 반환합니다.
        //return recent10BGs.map { it.bg }
        return recent10BGs
    }

}
object Logr {
    fun d(tag: String, msg: String) {
        if(msg.length > 100) {
            Log.d(tag, msg.substring(0, 100))
            Logr.d(tag, msg.substring(100))
        } else {
            Log.d(tag, msg)
        }
    }
}