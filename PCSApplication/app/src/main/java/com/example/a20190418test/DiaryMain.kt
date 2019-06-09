package com.example.a20190418test

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.ActionBar
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_diary_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DiaryMain : AppCompatActivity() {
    internal var data2: String? = null
    lateinit var jArray: JSONArray
    lateinit var items: ArrayList<DiaryDTO>
    lateinit var today: String
    lateinit var dto: DiaryDTO
    lateinit var jObj: JSONObject
    var sb: StringBuffer = StringBuffer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_main)

        var calendarView = findViewById<View>(R.id.calendar) as CalendarView // 캘린더뷰 인스턴스 만들기
        val actionbar: ActionBar? = supportActionBar
        actionbar?.hide()
        var flag = false
        btnWrite.setOnClickListener {
            textView13.visibility = View.VISIBLE
            editTxtContent.visibility = View.VISIBLE
            lottieSubmit.visibility = View.VISIBLE
            toast("일지를 적습니다.")
        }

        btnCalHome.setOnClickListener {
            toast("시작화면으로 이동합니다.")
            startActivity<MainActivity>()
        }
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->

            editTxtContent.setText("")
            flag = true
            today = "$year-${month + 1}-$dayOfMonth"
            toast(today)
            textView13.text = today + " 일지"

            lottieSubmit.setOnClickListener {
                lottieSubmit.playAnimation()
                if (editTxtContent.length() == 0) {
                    toast("내용을 입력해주세요")
                } else {
                    try {
                        data2 = editTxtContent.text.toString()
                        today = "$year-${month + 1}-$dayOfMonth"
                        var task = DiaryConnect()
                        task.execute(data2, today)
                        toast(DiaryConnect.insertResult)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                editTxtContent.text.clear()
                Handler().postDelayed({
                    lottieSubmit.cancelAnimation()
                },2000)
            }
        }
        btnRead.setOnClickListener {
            if (!flag) {
                toast("날짜가 클릭안됨")
            } else {
                checkRead().start()
                if (sb.toString().contains(today)) {
                    var intent: Intent = Intent(applicationContext, DiaryCalRead::class.java)
                    intent.putExtra("Date", today)
                    startActivity(intent)
                } else {
                    toast("해당 날짜에 데이터가 없습니다.")
                }
            }
        }
    }

    inner class checkRead() : Thread() {
        override fun run() {
            try {
                sb = StringBuffer()
                items = ArrayList()
                val page = "http://$ip:8088/PCSDatabase/PCS/PCSDIARYCHECK.jsp"
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
                            Log.d("asd1", sb.toString())
                        }
                        br.close()
                    }
                    conn.disconnect() // 접속 종료.
                }
                jObj = JSONObject(sb.toString())
                jArray = jObj.get("sendCheckDiary") as JSONArray

                for (i in 0 until jArray.length()) {
                    val row = jArray.getJSONObject(i)
                    dto = DiaryDTO()
                    dto.DATESTAMP = row.getString("DATESTAMP")
                    items.add(dto)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
