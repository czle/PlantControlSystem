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
import kotlinx.android.synthetic.main.activity_graph_ill.*
import kotlinx.android.synthetic.main.activity_graph_temp.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class GraphIll : AppCompatActivity() {

    lateinit var jArray: JSONArray
    lateinit var dto: DATADTO
    lateinit var jObj: JSONObject

    lateinit var Ill: ArrayList<Double>
    lateinit var Date: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph_ill)
        var thGraph = myGraph()

        thGraph.start()


        btnGraphIllHome.setOnClickListener {
            startActivity<MainActivity>()
        }
        btnGraphIllRefresh.setOnClickListener {
            toast("그래프를 새로고침 합니다.")
            DataLine(Ill, Date)
        }
        btnIllTable.setOnClickListener {
            toast("표로 이동합니다.")
            startActivity<TableIll>()
        }
    }

    inner class myGraph : Thread() {
        override fun run() {
            val sb = StringBuilder()
            try {
                val page = "http://$ip:8088/PCSDatabase/PCS/PCSGRAPHILL.jsp"
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
                jArray = jObj.get("sendillGraph") as JSONArray

                Ill = ArrayList()
                Date = ArrayList()

                for (i in 0 until jArray.length()) {
                    val row = jArray.getJSONObject(i)
                    dto = DATADTO()
                    dto.Ill = row.getDouble("Ill")
                    Ill.add(dto.Ill)
                    dto.DateStamp = row.getString("Date")
                    Date.add(dto.DateStamp)
                }
                DataLine(Ill, Date)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun DataLine(ILL: ArrayList<Double>, DAT: ArrayList<String>) {
        listGraphIll.clear()
        var IllGraph: ArrayList<Entry> = ArrayList()
        var DATEGraph: ArrayList<String> = ArrayList()

        for (i in 0 until 7) {
            IllGraph.add(Entry(i.toFloat(), ILL.get(i).toFloat()))
        }
        for (i in 0 until 7) {

            DATEGraph.add(DAT.get(i))
        }

        var IllineDataset = LineDataSet(IllGraph, "조도")
        IllineDataset.axisDependency = YAxis.AxisDependency.RIGHT
        IllineDataset.lineWidth = 1.5f
        IllineDataset.circleRadius = 5.5f
        IllineDataset.highLightColor = Color.rgb(244, 117, 117)
        IllineDataset.color = Color.rgb(200, 150, 23)
        IllineDataset.setDrawValues(true)
        IllineDataset.valueTextSize = 10f

        var xAxis: XAxis = listGraphIll.xAxis

        xAxis.valueFormatter = IndexAxisValueFormatter(DATEGraph)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.textSize = 10f
        xAxis.isGranularityEnabled = true
        xAxis.setLabelCount(10, false)

        var yLaxis: YAxis = listGraphIll.axisLeft
        yLaxis.textColor = Color.rgb(200, 150, 20)
        yLaxis.gridColor = Color.rgb(200, 150, 20)

        var yRaxis: YAxis = listGraphIll.axisRight
        yRaxis.setDrawGridLines(false)
        yRaxis.setDrawLabels(false)
        yRaxis.setDrawAxisLine(false)

        var dataSet: ArrayList<ILineDataSet> = ArrayList()
        dataSet.add(IllineDataset)
        var graphData: LineData = LineData(dataSet)
        listGraphIll.data = graphData
        listGraphIll.animateX(1000)
        listGraphIll.invalidate()
    }
}
