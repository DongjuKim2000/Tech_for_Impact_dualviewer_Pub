package com.nightscout.nightviewer

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

// 서버에서 데이터를 가져오는 함수 (1초마다 실행)
class Data_Courutine(private val context: Context) : ViewModel(){
    val dataFlow: Flow<BG?> = fetchDataPeriodically(context).flowOn(Dispatchers.IO) // 백그라운드 스레드에서 실행
    fun fetchDataPeriodically(context: Context): Flow<BG?> = flow {
        while (true) {
            // 여기에 실제 서버에서 데이터를 가져오는 비동기 작업을 수행
            val BGClass = BGData(context)
            if (isOnline(context)) {
                BGClass.get_EntireBGInfo()
            }
            val newData = BGClass.BGInfo().bginfo
            emit(newData) // 새로운 데이터를 플로우에 방출
            delay(5000)
        }
    }
}
