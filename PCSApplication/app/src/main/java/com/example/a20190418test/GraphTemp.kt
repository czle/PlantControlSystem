package com.example.a20190418test

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.actiivity_row.*
import kotlinx.android.synthetic.main.activity_graph.*
import kotlinx.android.synthetic.main.activity_graph_temp.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class GraphTemp : AppCompatActivity() {

    lateinit var items: ArrayList<DATADTO>

    lateinit var jArray: JSONArray
    lateinit var dto: DATADTO
    lateinit var jObj: JSONObject
    lateinit var Temp: ArrayList<Double>
    lateinit var Date: ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph_temp)
        var thGraph = myGraph()

        thGraph.start()

        btnTempTable.setOnClickListener {
            toast("표로 이동합니다.")
            startActivity<TableTemp>()
        }
        btnGraphTempHome.setOnClickListener {
            startActivity<MainActivity>()
        }
        btnGraphTempRefresh.setOnClickListener {
            toast("그래프를 새로고침 합니다.")
            DataLine(Temp, Date)
        }

    }

    inner class myGraph : Thread() {
        override fun run() {
            val sb = StringBuilder()

            try {
                val page = "http://$ip:8088/PCSDatabase/PCS/PCSGRAPHTEMP.jsp"
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
                jArray = jObj.get("sendTempGraph") as JSONArray
                Temp = ArrayList()
                Date = ArrayList()
                for (i in 0 until jArray.length()) {
                    val row = jArray.getJSONObject(i)
                    dto = DATADTO()
                    dto.Temp = row.getDouble("Temp")
                    Temp.add(dto.Temp)
                    dto.DateStamp = row.getString("Date")
                    Date.add(dto.DateStamp)
                }
                DataLine(Temp, Date)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun DataLine(TEM: ArrayList<Double>, DAT: ArrayList<String>) {

        listGraphTemp.clear()
        var TEMGraph: ArrayList<Entry> = ArrayList()
        var DATEGraph: ArrayList<String> = ArrayList()

        for (i in 0 until 7) {
            TEMGraph.add(Entry(i.toFloat(), TEM.get(i).toFloat()))
        }

        for (i in 0 until 7) {

            DATEGraph.add(DAT.get(i))
        }

        var TEMLineDataset = LineDataSet(TEMGraph, "온도")
        TEMLineDataset.axisDependency = YAxis.AxisDependency.RIGHT
        TEMLineDataset.lineWidth = 1.5f
        TEMLineDataset.circleRadius = 5.5f
        TEMLineDataset.highLightColor = Color.rgb(244, 117, 117)
        TEMLineDataset.color = Color.rgb(123, 123, 123)
        TEMLineDataset.valueTextSize = 10f
        TEMLineDataset.setDrawValues(true)
        Log.d("asd", TEMLineDataset.entryCount.toString())

        var xAxis: XAxis = listGraphTemp.xAxis

        xAxis.valueFormatter = IndexAxisValueFormatter(DATEGraph)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.textSize = 10f
        xAxis.isGranularityEnabled = true
        xAxis.setLabelCount(10, false)

        var yLaxis: YAxis = listGraphTemp.axisLeft
        yLaxis.textColor = getColor(R.color.red)
        yLaxis.gridColor = getColor(R.color.red)


        var yRaxis: YAxis = listGraphTemp.axisRight
        yRaxis.setDrawGridLines(false)
        yRaxis.setDrawLabels(false)
        yRaxis.setDrawAxisLine(false)


        var dataSet: ArrayList<ILineDataSet> = ArrayList()
        dataSet.add(TEMLineDataset)

        var graphData: LineData = LineData(dataSet)

        listGraphTemp.data = graphData
        listGraphTemp.animateX(1000)

        listGraphTemp.invalidate()

    }
}