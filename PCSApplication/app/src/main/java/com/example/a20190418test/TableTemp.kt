package com.example.a20190418test

import android.annotation.SuppressLint
import android.app.ListActivity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_table_temp.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class TableTemp : ListActivity() {
    lateinit var items: ArrayList<DATADTO>
    lateinit var adapter: TempAdapter
    lateinit var jArray: JSONArray
    lateinit var dto: DATADTO
    lateinit var jObj: JSONObject
    var cnt = 0
    internal var handler: Handler =

        @SuppressLint("HandlerLeak")
        object : Handler() {

            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                adapter = TempAdapter(
                    this@TableTemp, R.layout.actiivity_row, items
                )
                listAdapter = adapter
            }
        }


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table_temp)

        btnTempTableSearch.setOnClickListener {
            var th = myTempTable()
            th.start()
            if(cnt>0)
                toast(txtTempFind.text.toString() + " 을/를 검색합니다.")
            cnt++
        }
        btnTempTableSearch.performClick()


        btnTempTableRefresh.setOnClickListener {
            toast("새로고침 합니다.")
            txtTempFind.text.clear()
            var th = myTempTable()
            th.start()
        }
        btnTempTableBack.setOnClickListener {
            finish()
            startActivity<GraphTemp>()
        }
        btnTempTableHome.setOnClickListener {
            finish()
            startActivity<MainActivity>()
        }

    }

    inner class myTempTable : Thread() {
        override fun run() {
            val sb = StringBuilder()
            try {
                items = ArrayList()
                val page = "http://$ip:8088/PCSDatabase/PCS/PCSTABLETEMP.jsp"
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
                jArray = jObj.get("sendTampTable") as JSONArray
                Log.d("TES1T", "씨부레7")

                var find = txtTempFind.text.toString()

                for (i in 0 until jArray.length()) {
                    val row = jArray.getJSONObject(i)
                    dto = DATADTO()
                    dto.Temp = row.getDouble("Temp")
                    dto.DateStamp = row.getString("Date")

                    if (dto.DateStamp.contains(find)) {
                        items.add(dto)
                    } else if (find == "") {
                        items.add(dto)
                    }
                }
                handler.sendEmptyMessage(0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    inner class TempAdapter
        (context: Context, resource: Int, objects: List<DATADTO>) :
        ArrayAdapter<DATADTO>(context, resource, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var v = convertView
            if (v == null) {
                val li = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                v = li.inflate(R.layout.actiivity_row, null)
            }
            dto = items[position]

            if (dto != null) {
                val txtTemp = v!!.findViewById(R.id.txtData) as TextView
                val txtDate = v!!.findViewById(R.id.txtDateData) as TextView
                txtTemp.text = dto.Temp.toString() + " ℃"
                txtDate.text = dto.DateStamp

            }
            return v!!
        }
    }
}
