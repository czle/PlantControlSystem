package com.example.a20190418test

import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class ConnectMethod(JSP: String) : Thread() {
    var JSP = JSP
    override fun run() {

        val sb = StringBuilder()
        try {
            val page = JSP
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


        } catch (e: Exception) {
            e.printStackTrace()
        }


    }
}