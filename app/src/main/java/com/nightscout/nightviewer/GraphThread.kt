package com.example.dualviewer

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.nightscout.nightviewer.SharedPreferencesUtil
import com.nightscout.nightviewer.TimeAxisValueFormat
import com.nightscout.nightviewer.TimeCalculator
import com.nightscout.nightviewer.prefs

class GraphThread(private val lineChart: LineChart, private val context: Context): Thread() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun run() {

        val input = SharedPreferencesUtil.getRecent10BGValues(context) //bg 데이터 리스트 가져오기
        // Entry 배열 생성
        var entries: ArrayList<Entry> = ArrayList()
        // Entry 배열 초기값 입력
        entries.add(Entry(0F , 0F))
        // 그래프 구현을 위한 LineDataSet 생성
        var dataset: LineDataSet = LineDataSet(entries, "input")

        chartSetting(dataset) //그래프 모양 설정하는 함수, 아래에 정의 있음

        // 그래프 data 생성 -> 최종 입력 데이터
        var data: LineData = LineData(dataset)
        // xml에 배치된 lineChart에 데이터 연결
        lineChart.data = data

        runOnUiThread {
            // 그래프 생성
            lineChart.animateXY(1, 1)
        }

        if(input.isEmpty()){

        }
        else if(input.size<10){ //10개 미만인 경우 빈 데이터 넣기
            val sizeSub = 10-input.size
            val startMin = ((TimeCalculator(input[0].time).time()/5)*5)%1000
            for(i in 0 until sizeSub){
                val currentMin = startMin - 5*(sizeSub-i)
                if(i==0){
                    lineChart.xAxis.axisMinimum = currentMin.toFloat()
                }
                data.addEntry(Entry(currentMin.toFloat(), 0.toFloat()), 0)
                data.notifyDataChanged()
                lineChart.notifyDataSetChanged()
                lineChart.invalidate()
            }
            for(i in input.indices){
                val currentMin = ((TimeCalculator(input[i].time).time()/5)*5)%1000
                if(i==(input.size-1)){
                    lineChart.xAxis.axisMaximum = currentMin.toFloat()
                }
                data.addEntry(Entry(currentMin.toFloat(), input[i].bg.toFloat()), 0)
                data.notifyDataChanged()
                lineChart.notifyDataSetChanged()
                lineChart.invalidate()
            }
        }
        else{
            for (i in 0 until 10){
                val currentMin = ((TimeCalculator(input[i].time).time()/5)*5)%1000
                if(i==0){
                    lineChart.xAxis.axisMinimum = currentMin.toFloat()
                }
                if(i==9){
                    lineChart.xAxis.axisMaximum = currentMin.toFloat()
                }
                data.addEntry(Entry(currentMin.toFloat(), input[i].bg.toFloat()), 0) //bg 데이터
                data.notifyDataChanged()
                lineChart.notifyDataSetChanged()
                lineChart.invalidate()
            }
        }
    }

    private fun runOnUiThread(action: () -> Unit) {
        Handler(Looper.getMainLooper()).post(action)
    }

    private fun chartSetting(dataset: LineDataSet){
        val xAxis = lineChart.xAxis
        val yAxisLeft = lineChart.axisLeft

        val urgentHighValue = prefs.getString("urgent_high_value", "260")
        val highLimitValue = prefs.getString("high_value", "180")
        val lowLimitValue = prefs.getString("low_value", "80")
        val urgentLowValue = prefs.getString("urgent_low_value", "55")
        val limitLineWidth = prefs.getString("limitlinewidth", "1")

        val BGpointSize = prefs.getString("chartbgpointsize", "4")
        val limitLineColor = prefs.getString("chartlinecolorhighlow", "#FCFFFF00")
        val urgentLineColor = prefs.getString("chartlinecolorurgenthighlow", "#FCFF0000")
        val chartLineWidth = prefs.getString("chartlinewidth", "0.3")
        val xaxisEnable = prefs.getBoolean("xaxis_enable", true)
        val chartBGMax = prefs.getString("chartBG_max", "400")
        val chartBGMin = prefs.getString("chartBG_min", "40")

        dataset.apply {
            color = if(chartLineWidth=="0")
                Color.argb(0, 0, 0, 0)
            else
                Color.WHITE //그래프 선 색
            lineWidth = chartLineWidth?.toFloat() ?: 0.3f
            valueTextSize = 0f //값 출력 안되도록
            setCircleColor(Color.WHITE)
            circleHoleColor = Color.WHITE
            circleRadius = BGpointSize?.toFloat() ?: 4f
        }

        lineChart.apply {
            //오른쪽 y축 안보이게
            axisRight.isEnabled = false
            setTouchEnabled(false)
            legend.isEnabled = false //라벨 없애기
            description.isEnabled = false //설명 없애기
            xAxis.isEnabled = xaxisEnable
        }

        xAxis.apply {
            //x축 그래프 아래에 표시
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = TimeAxisValueFormat()
            textColor = Color.WHITE
        }

        //주의 혈당 기준선
        val lowLimit = LimitLine(lowLimitValue?.toFloat() ?: 80f, lowLimitValue).apply {
            lineWidth = limitLineWidth?.toFloat() ?: 1f
            lineColor = Color.parseColor(limitLineColor)
            enableDashedLine(5f, 10f, 0f)
            textColor = Color.WHITE
        }
        val highLimit = LimitLine(highLimitValue?.toFloat() ?: 180f, highLimitValue).apply {
            lineWidth = limitLineWidth?.toFloat() ?: 1f
            lineColor = Color.parseColor(limitLineColor)
            enableDashedLine(5f, 10f, 0f)
            textColor = Color.WHITE
        }

        //위험 혈당 기준선
        val urgentLow = LimitLine(urgentLowValue?.toFloat() ?: 55f, urgentLowValue).apply {
            lineWidth = limitLineWidth?.toFloat() ?: 1f
            lineColor = Color.parseColor(urgentLineColor)
            enableDashedLine(5f, 10f, 0f)
            textColor = Color.WHITE
        }
        val urgentHigh = LimitLine(urgentHighValue?.toFloat() ?: 260f, urgentHighValue).apply {
            lineWidth = limitLineWidth?.toFloat() ?: 1f
            lineColor = Color.parseColor(urgentLineColor)
            enableDashedLine(5f, 10f, 0f)
            textColor = Color.WHITE
        }

        yAxisLeft.apply {
            axisMaximum = chartBGMax?.toFloat()?: 400f //y축 최댓값
            axisMinimum = chartBGMin?.toFloat()?: 40f //y축 최솟값
            setDrawLabels(false) //레이블 비활성화
            setDrawAxisLine(false) //축 비활성화
            setDrawGridLines(false) //그리드 비활성화
            //기준 선들 그리기
            removeAllLimitLines()
            addLimitLine(lowLimit)
            addLimitLine(highLimit)
            addLimitLine(urgentLow)
            addLimitLine(urgentHigh)
        }

    }
}