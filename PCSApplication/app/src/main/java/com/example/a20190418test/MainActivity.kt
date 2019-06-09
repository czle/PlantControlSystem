package com.example.a20190418test

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import github.hellocsl.cursorwheel.CursorWheelLayout
import kotlinx.android.synthetic.main.activity_diary_main.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), CursorWheelLayout.OnMenuSelectedListener {

    lateinit var wheel_text: CursorWheelLayout
    lateinit var firstText: ArrayList<MenuItemData>
    lateinit var items: ArrayList<DATADTO>
    lateinit var jArray: JSONArray
    lateinit var dto: DATADTO
    lateinit var jObj: JSONObject
    var cnt = 0;
    var temp: Double = 0.0
    var humi: Double = 0.0
    lateinit var main: String
    var btnLottieCnt = 0
    var autoCnt = 17;
    var fanCnt = 0;
    var pumpCnt = 0
    var ledCnt = 0;


    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sendArduino(autoCnt).start()

        var task = MyWeather()
        task.start()

        initViews()

        LoadData()

        wheel_text.setOnMenuSelectedListener(this)
        val flag = true
        if (flag) {
            btnFan.setOnClickListener {
                var i = 0
                i = 11
                when (fanCnt % 2) {
                    0 -> {
                        sendArduino(i).start()
                        toast("팬이 켜집니다.")
                    }
                    1 -> {
                        sendArduino(i).start()
                        toast("팬이 꺼집니다.")
                    }
                }
                fanCnt++
            }
            btnDiode.setOnClickListener {
                var i = 0
                i = 15
                when (ledCnt % 2) {
                    0 -> {
                        sendArduino(i).start()
                        toast("LED가 켜집니다.")
                    }
                    1 -> {
                        sendArduino(i).start()
                        toast("LED가 꺼집니다.")
                    }
                }
                ledCnt++
            }
            btnWatering.setOnClickListener {
                var i = 0
                i = 13
                when (pumpCnt % 2) {
                    0 -> {
                        sendArduino(i).start()
                        toast("펌프가 켜집니다.")
                    }
                    1 -> {
                        sendArduino(i).start()
                        toast("펌프가 꺼집니다.")
                    }
                }
                pumpCnt++
            }
            btnRefresh.setOnClickListener {
                var task = MyWeather()
                task.start()
                toast("날씨가 갱신됩니다.")
            }
            mainLottie.setOnClickListener {
                sendArduino(autoCnt).start()
                when (btnLottieCnt % 2) {
                    0 -> {
                        mainLottie.playAnimation()
                        Handler().postDelayed({
                            mainLottie.pauseAnimation()
                        }, 500)
                        toast("수동으로 전환합니다.")
                        txtAuto.visibility = View.INVISIBLE
                        btnFan.visibility = View.VISIBLE
                        btnDiode.visibility = View.VISIBLE
                        btnWatering.visibility = View.VISIBLE
                    }
                    1 -> {
                        mainLottie.resumeAnimation()
                        toast("자동으로 전환합니다 .")
                        txtAuto.visibility = View.VISIBLE
                        btnFan.visibility = View.INVISIBLE
                        btnDiode.visibility = View.INVISIBLE
                        btnWatering.visibility = View.INVISIBLE
                    }
                }
                btnLottieCnt++
            }

        }
    }

    inner class MyThread : Thread() {
        override fun run() {
            val sb = StringBuilder()
                try {
                    items = ArrayList()
                    val page = "http://$ip:8088/PCSDatabase/PCS/PCSRead.jsp"
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
                jArray = jObj.get("sendData") as JSONArray

                for (i in 0 until jArray.length()) {
                    val row = jArray.getJSONObject(i)
                    dto = DATADTO()
                    dto.Id = row.getInt("ID")
                    dto.Temp = row.getDouble("TEMP")
                    dto.Humi = row.getDouble("HUMI")
                    dto.Ill = row.getDouble("ILL")
                    dto.DateStamp = row.getString("DATESTAMP")
                    // String을 Date로 바꿔보는중.
                    items.add(dto)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            dto = items[jArray.length() - 1]
        }
    }

    inner class sendArduino(var i: Int) : Thread() {
        override fun run() {
            try {
                var sendMsg: String?
                val url2: URL =
                    URL("http://$ip:8088/PCSDatabase/PCS/PCSArduinoSend.jsp")
                val conn2: HttpURLConnection =
                    url2.openConnection() as HttpURLConnection
                conn2.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                conn2.requestMethod = "POST"
                val osw2 = OutputStreamWriter(conn2.outputStream)
                sendMsg = "pinID=" + i // String 형태로 숫자를 넣으면 입력된다.
                osw2.write(sendMsg)
                osw2.flush()
                if (conn2.responseCode == HttpURLConnection.HTTP_OK) {
                    val tmp = InputStreamReader(conn2.inputStream, "UTF-8")
                    val reader = BufferedReader(tmp)
                    val buffer = StringBuffer()
                    var str2: String? = reader.readLine()
                    while (str2 != null) {
                        buffer.append(str2)
                    }
                } else// 통신 실패
                    Log.d("mew", "bow")
            } catch (e: ProtocolException) {
                e.printStackTrace()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun LoadData() {
        firstText = ArrayList()
        firstText.add(MenuItemData("다이어리"))
        firstText.add(MenuItemData("온도"))
        firstText.add(MenuItemData("습도"))
        firstText.add(MenuItemData("조도"))
        var adapter: WheelAdapter = WheelAdapter(this, firstText)
        wheel_text.setAdapter(adapter)
    }

    private fun initViews() {
        wheel_text = findViewById<CursorWheelLayout>(R.id.wheel_Text)
    }

    @SuppressLint("SetTextI18n")
    override fun onItemSelected(parent: CursorWheelLayout?, view: View?, pos: Int) {
        if (parent?.id == R.id.wheel_Text) {
            if (firstText.get(pos).mTitle.equals("다이어리")) {
                cnt = 0
                var format: SimpleDateFormat = SimpleDateFormat("MM/dd")
                var time: Date = Date()
                var now = format.format(time)
                MyThread().start()
                TextShow.text = now.toString()
                resultImage.setImageResource(R.drawable.diary)

            } else if (firstText.get(pos).mTitle.equals("온도")) {
                cnt = 1
                MyThread().start()
                TextShow.text = dto.Temp.toString() + " ℃"
                resultImage.setImageResource(R.drawable.temp2)
            } else if (firstText.get(pos).mTitle.equals("습도")) {
                cnt = 2
                MyThread().start()
                TextShow.text = dto.Humi.toString() + " %"
                resultImage.setImageResource(R.drawable.humidity2)
            } else if (firstText.get(pos).mTitle.equals("조도")) {
                cnt = 3
                MyThread().start()
                TextShow.text = dto.Ill.toString() + " lx"
                resultImage.setImageResource(R.drawable.illu)
            }
            TextShow.setOnClickListener {
                if (cnt == 0) {
                    TextShow.setOnClickListener {
                        startActivity<DiaryMain>()
                    }

                } else if (cnt == 1) {
                    TextShow.setOnClickListener {
                        startActivity<GraphTemp>()
                    }

                } else if (cnt == 2) {
                    TextShow.setOnClickListener {
                        startActivity<GraphHumi>()
                    }

                } else if (cnt == 3) {
                    TextShow.setOnClickListener {
                        startActivity<GraphIll>()
                    }

                }
            }

        }
        toast(firstText.get(pos).mTitle)
    }

    inner class MyWeather : Thread() { // 얘는 새로고침할적에 쓰는걸로 하자.
        @SuppressLint("SetTextI18n")
        override fun run() {
            val sb = StringBuilder()

            try {
                items = ArrayList()
                val page =
                    "http://api.openweathermap.org/data/2.5/weather?lat=37.45&lon=126.73&appid=6b43b6a5b48ed582f6d6ca1e37f96493"
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
                var weatherData: JSONObject = JSONObject(jObj.getString("main"))
                temp = weatherData.getDouble("temp") - 273.15
                humi = weatherData.getDouble("humidity")

                jArray = jObj.get("weather") as JSONArray
                for (i in 0 until jArray.length()) {
                    val row = jArray.getJSONObject(i)
                    main = row.getString("main")
                }
                textView11.text = String.format("%.1f", temp) + " ℃"
                textView12.text = humi.toString() + " %"

                if (main.equals("Haze") || main.equals("Fog") || main.equals("Mist")) {
                    imageView2.setImageResource(R.drawable.haze)
                } else if (main.equals("Clear"))
                    imageView2.setImageResource(R.drawable.sun)
                else if (main.equals("Rain")||main.equals("Shower rain"))
                    imageView2.setImageResource(R.drawable.rain)
                else if (main.equals("Clouds"))
                    imageView2.setImageResource(R.drawable.clouds)


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



}