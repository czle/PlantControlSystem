package com.example.a20190418test

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.activity_graph_humi.*
import kotlinx.android.synthetic.main.activity_graph_temp.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class GraphHumi : AppCompatActivity() {

    lateinit var jArray: JSONArray
    lateinit var dto: DATADTO
    lateinit var jObj: JSONObject

    lateinit var Humi: ArrayList<Double>
    lateinit var Date: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph_humi)

        var thGraph = myGraph()

        thGraph.start()
        btnHumiTable.setOnClickListener {
            finish()
            startActivity<TableHumi>()
        }
        btnGraphHumiHome.setOnClickListener {
            finish()
            startActivity<MainActivity>()
        }
        btnGraphHumiRefresh.setOnClickListener {
            toast("그래프를 새로고침 합니다.")
            DataLine(Humi, Date)
        }
    }


    inner class myGraph : Thread() {
        override fun run() {
            val sb = StringBuilder()

            try {
                val page = "http://$ip:8088/PCSDatabase/PCS/PCSGRAPHHUMI.jsp"
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
                jArray = jObj.get("sendhumiGraph") as JSONArray
                Humi = ArrayList()
                Date = ArrayList()
                for (i in 0 until jArray.length()) {
                    val row = jArray.getJSONObject(i)
                    dto = DATADTO()
                    dto.Humi = row.getDouble("Humi")
                    Humi.add(dto.Humi)
                    dto.DateStamp = row.getString("Date")
                    Date.add(dto.DateStamp)
                }
                DataLine(Humi, Date)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun DataLine(HUM: ArrayList<Double>, DAT: ArrayList<String>) {
        listGraphHumi.clear()
        var HUMGraph: ArrayList<Entry> = ArrayList()
        var DATEGraph: ArrayList<String> = ArrayList()

        for (i in 0 until 7) {
            HUMGraph.add(Entry(i.toFloat(), HUM.get(i).toFloat()))
        }
        for (i in 0 until 7) {

            DATEGraph.add(DAT.get(i))
        }
        var HUMLineDataset = LineDataSet(HUMGraph, "습도")
        HUMLineDataset.axisDependency = YAxis.AxisDependency.RIGHT
        HUMLineDataset.lineWidth = 1.5f
        HUMLineDataset.circleRadius = 5.5f
        HUMLineDataset.highLightColor = Color.rgb(244, 117, 117)
        HUMLineDataset.color = Color.rgb(13, 150, 199)
        HUMLineDataset.valueTextSize = 10f
        HUMLineDataset.setDrawValues(true)
        var xAxis: XAxis = listGraphHumi.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(DATEGraph)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 10f
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.setLabelCount(10, false)
        var yLaxis: YAxis = listGraphHumi.axisLeft
        yLaxis.textColor = Color.rgb(100, 150, 210)
        yLaxis.gridColor = Color.rgb(100, 150, 210)
        var yRaxis: YAxis = listGraphHumi.axisRight
        yRaxis.setDrawGridLines(false)
        yRaxis.setDrawLabels(false)
        yRaxis.setDrawAxisLine(false)
        var dataSet: ArrayList<ILineDataSet> = ArrayList()
        dataSet.add(HUMLineDataset)
        var graphData: LineData = LineData(dataSet)
        listGraphHumi.data = graphData
        listGraphHumi.animateX(1000)
        listGraphHumi.invalidate()
    }
}
