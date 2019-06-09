package com.example.a20190418test

import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.util.EventLogTags
import android.util.Log
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.activity_graph.*
import org.jetbrains.anko.startActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class Graph : AppCompatActivity() {
    lateinit var jArray: JSONArray
    lateinit var dto: DATADTO
    lateinit var jObj: JSONObject

    lateinit var avgTemp: ArrayList<Float>
    lateinit var avgHumi: ArrayList<Float>
    lateinit var avgIll: ArrayList<Float>
    lateinit var avgDate: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)


        var thGraph = myGraph()

        thGraph.start()

    }

    inner class myGraph : Thread() {
        override fun run() {
            val sb = StringBuilder()

            try {
                val page = "http://$ip:8088/PCSDatabase/PCS/PCSGraph.jsp"
                val url = URL(page)
                val conn = url.openConnection() as HttpURLConnection
                if (conn != null) {
                    conn.connectTimeout = 10000 // 10초
                    conn.useCaches = false // 캐시를 쓰지 않겠다.
                    if (conn.responseCode == 200) { // 응답코드가 200이면 접속 성공.
                        val br = BufferedReader(
                            InputStreamReader(conn.inputStream, "UTF-8")
                        )
                        while (true) {
                            val line = br.readLine() ?: break // 한줄 읽기
                            sb.append(line + "\n")
                        }
                        br.close()
                    }
                    conn.disconnect() // 접속 종료.
                }
                jObj = JSONObject(sb.toString())
                jArray = jObj.get("sendAvgData") as JSONArray

                avgTemp = ArrayList()
                avgHumi = ArrayList()
                avgIll = ArrayList()
                avgDate = ArrayList()

                for (i in 0 until jArray.length()) {
                    val row = jArray.getJSONObject(i)
                    dto = DATADTO()
                    dto.Temp = row.getDouble("avgTemp")
                    avgTemp.add(dto.Temp.toFloat())
                    dto.Humi = row.getDouble("avgHumi")
                    avgHumi.add(dto.Humi.toFloat())
                    dto.Ill = row.getDouble("avgIll")
                    avgIll.add(dto.Ill.toFloat())
                    dto.DateStamp = row.getString("avgDate")
                    avgDate.add(dto.DateStamp)
                }
                DataLine(avgTemp, avgHumi, avgIll, avgDate)

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

    private fun DataLine(TEM: ArrayList<Float>, HUM: ArrayList<Float>, ILL: ArrayList<Float>, DAT: ArrayList<String>) {

        listGraph.clear()
        var TEMGraph: ArrayList<Entry> = ArrayList()
        var HUMGraph: ArrayList<Entry> = ArrayList()
        var IllGraph: ArrayList<Entry> = ArrayList()
        var DATEGraph: ArrayList<String> = ArrayList()

        for (i in 0 until avgTemp.size) {
            TEMGraph.add(Entry(i.toFloat(), TEM.get(i)))
            HUMGraph.add(Entry(i.toFloat(), HUM.get(i)))
            IllGraph.add(Entry(i.toFloat(), ILL.get(i)))
        }

        for (i in 0 until DAT.size) {

            DATEGraph.add(DAT.get(i))
        }
        Log.d("mew", DATEGraph.toString())


        var TEMLineDataset = LineDataSet(TEMGraph, "온도")
        TEMLineDataset.axisDependency = YAxis.AxisDependency.RIGHT
        TEMLineDataset.lineWidth = 2.5f
        TEMLineDataset.circleRadius = 4.5f
        TEMLineDataset.highLightColor = Color.rgb(244, 117, 117)
        TEMLineDataset.color = Color.rgb(123, 123, 123)
        TEMLineDataset.setDrawValues(true)

        var HUMLineDataSet = LineDataSet(HUMGraph, "습도")
        HUMLineDataSet.axisDependency = YAxis.AxisDependency.RIGHT
        HUMLineDataSet.lineWidth = 2.5f
        HUMLineDataSet.circleRadius = 4.5f
        HUMLineDataSet.highLightColor = Color.rgb(100, 100, 100)
        HUMLineDataSet.color = Color.rgb(244, 117, 117)
        HUMLineDataSet.setDrawValues(true)

        var IllLineDataSet = LineDataSet(IllGraph, "조도")
        IllLineDataSet.axisDependency = YAxis.AxisDependency.RIGHT
        IllLineDataSet.lineWidth = 2.5f
        IllLineDataSet.circleRadius = 4.5f

        IllLineDataSet.highLightColor = Color.rgb(200, 117, 255)
        IllLineDataSet.setDrawValues(true)
        IllLineDataSet.valueTextColor = Color.RED

        var xAxis: XAxis = listGraph.xAxis

        xAxis.valueFormatter = IndexAxisValueFormatter(DATEGraph)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.setLabelCount(7,false)

        var xAxisLeft:XAxis.XAxisPosition = XAxis.XAxisPosition.BOTTOM

        var dataSet: ArrayList<ILineDataSet> = ArrayList()
        dataSet.add(TEMLineDataset)
        dataSet.add(HUMLineDataSet)
        dataSet.add(IllLineDataSet)

        var graphData: LineData = LineData(dataSet)

        listGraph.data = graphData

        listGraph.invalidate()

    }


}

