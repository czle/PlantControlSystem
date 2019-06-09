//package com.example.a20190418test
//
//import android.util.Log
//import android.widget.TextView
//import org.json.JSONArray
//import org.json.JSONObject
//import java.io.BufferedReader
//import java.io.InputStreamReader
//import java.net.HttpURLConnection
//import java.net.URL
//
//
//class LocationWeather : Thread() {
//    lateinit var dto: WeatherDTO
//    lateinit var jObj: JSONObject
//    var temp: Double = 0.0
//    var humi: Double = 0.0
//    var pressure: Int = 0
//    lateinit var main: String
//    lateinit var des: String
//    lateinit var JArray: JSONArray
//    lateinit var weatherItems: ArrayList<WeatherDTO>
//    lateinit var currentWeather: WeatherDTO
//
//    override fun run() {
//        val sb = StringBuilder()
//        try {
//            val page =
//                "http://api.openweathermap.org/data/2.5/weather?lat=37.45&lon=126.73&appid=6b43b6a5b48ed582f6d6ca1e37f96493"
//            val url = URL(page)
//            val conn = url.openConnection() as HttpURLConnection
//            if (conn != null) {
//                conn.connectTimeout = 10000 // 10초
//                conn.useCaches = false // 캐시를 쓰지 않겠다.
//                if (conn.responseCode == 200) { // 응답코드가 200이면 접속 성공.
//                    val br = BufferedReader(
//
//                        InputStreamReader(conn.inputStream, "UTF-8")
//                    )
//                    while (true) {
//                        val line = br.readLine() ?: break // 한줄 읽기
//                        sb.append(line + "\n")
//                    }
//                    br.close()
//                }
//                conn.disconnect() // 접속 종료.
//            }
//
//            jObj = JSONObject(sb.toString())
//            var weatherData: JSONObject = JSONObject(jObj.getString("main"))
//            temp = weatherData.getDouble("temp") - 273.15
//            humi = weatherData.getDouble("humidity")
//            pressure = weatherData.getInt("pressure")
//
//            JArray = jObj.get("weather") as JSONArray
//            for (i in 0 until JArray.length()) {
//                val row = JArray.getJSONObject(i)
//                main = row.getString("main")
//                des = row.getString("description")
//            }
//
//
////            currentWeather = WeatherDTO(temp, humi, pressure, main, des)
//
//
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//}