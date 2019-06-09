package com.example.a20190418test

import android.annotation.SuppressLint
import android.app.ListActivity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_table_ill.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class TableIll : ListActivity() {
    lateinit var items: ArrayList<DATADTO>
    lateinit var adapter: IllAdapter
    lateinit var jArray: JSONArray
    lateinit var dto: DATADTO
    lateinit var jObj: JSONObject
    var cnt = 0
    internal var handler: Handler =

        @SuppressLint("HandlerLeak")
        object : Handler() {

            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                adapter = IllAdapter(
                    this@TableIll, R.layout.actiivity_row, items
                )
                listAdapter = adapter
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table_ill)
        btnIllTableSearch.setOnClickListener {
            var th = myIllTable()
            th.start()
            if (cnt > 0)
                toast(txtIllFind.text.toString() + " 을/를 검색합니다.")
            cnt++
        }
        btnIllTableSearch.performClick()
        btnIllTableRefresh.setOnClickListener {
            toast("새로고침 합니다.")
            txtIllFind.text.clear()
            var th = myIllTable()
            th.start()
        }
        btnIllTableBack.setOnClickListener {
            finish()
            startActivity<GraphIll>()
        }
        btnIllTableHome.setOnClickListener {
            finish()
            startActivity<MainActivity>()
        }
    }

    inner class myIllTable : Thread() {
        override fun run() {
            val sb = StringBuilder()

            try {
                items = ArrayList()
                val page = "http://$ip:8088/PCSDatabase/PCS/PCSTABLEILL.jsp"
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
                jArray = jObj.get("sendIllTable") as JSONArray

                var find = txtIllFind.text.toString()

                for (i in 0 until jArray.length()) {
                    val row = jArray.getJSONObject(i)
                    dto = DATADTO()
                    dto.Ill = row.getDouble("Ill")
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

    inner class IllAdapter
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
                val txtIll = v!!.findViewById(R.id.txtData) as TextView
                val txtDate = v!!.findViewById(R.id.txtDateData) as TextView

                txtIll.text = dto.Ill.toString() + " lx"
                txtDate.text = dto.DateStamp

            }
            return v!!
        }
    }
}
