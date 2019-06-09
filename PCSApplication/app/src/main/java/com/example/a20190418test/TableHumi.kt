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
import kotlinx.android.synthetic.main.activity_table_humi.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class TableHumi : ListActivity() {
    lateinit var items: ArrayList<DATADTO>
    lateinit var adapter: HumiAdapter
    lateinit var jArray: JSONArray
    lateinit var dto: DATADTO
    lateinit var jObj: JSONObject
    var cnt = 0
    internal var handler: Handler =

        @SuppressLint("HandlerLeak")
        object : Handler() {

            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                adapter = HumiAdapter(
                    this@TableHumi, R.layout.actiivity_row, items
                )
                listAdapter = adapter
            }
        }

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table_humi)
        btnHumiTableSearch.setOnClickListener {
            var th = myHumiTable()
            th.start()
            if (cnt > 0)
                toast(txtHumiFind.text.toString() + " 을/를 검색합니다.")
            cnt++
        }
        btnHumiTableSearch.performClick()
        btnHumiTableRefresh.setOnClickListener {
            toast("새로고침 합니다.")
            txtHumiFind.text.clear()
            var th = myHumiTable()
            th.start()
        }
        btnHumiTableBack.setOnClickListener {
            finish()
            startActivity<GraphHumi>()
        }
        btnHumiTableHome.setOnClickListener {
            finish()
            startActivity<MainActivity>()
        }
    }

    inner class myHumiTable : Thread() {
        override fun run() {
            val sb = StringBuilder()
            try {
                items = ArrayList()
                val page = "http://$ip:8088/PCSDatabase/PCS/PCSTABLEHUMI.jsp"
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
                jArray = jObj.get("sendHumiTable") as JSONArray
                var find = txtHumiFind.text.toString()
                for (i in 0 until jArray.length()) {
                    val row = jArray.getJSONObject(i)
                    dto = DATADTO()
                    dto.Humi = row.getDouble("Humi")
                    dto.DateStamp = row.getString("Date")

                    if (dto.DateStamp.contains(find))
                        items.add(dto)
                    else if (find == "")
                        items.add(dto)
                }
                handler.sendEmptyMessage(0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    inner class HumiAdapter
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
                val txtHumi = v!!.findViewById(R.id.txtData) as TextView
                val txtDate = v!!.findViewById(R.id.txtDateData) as TextView

                txtHumi.text = dto.Humi.toString() + " %"
                txtDate.text = dto.DateStamp

            }
            return v!!
        }
    }
}
