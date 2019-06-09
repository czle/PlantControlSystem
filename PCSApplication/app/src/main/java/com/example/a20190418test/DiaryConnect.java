package com.example.a20190418test;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static com.example.a20190418test.DiaryDTO.ip;


public class DiaryConnect extends AsyncTask<String, Void, String> {

    String sendMsg, receiveMsg;
    static String insertResult;

    @Override
    protected String doInBackground(String... strings) {

        try {
            String str;
            // 접속할 서버 주소 (이클립스에서 android.jsp 실행시 웹브라우저 주소)
            URL url = new URL("http://" + ip + ":8088/PCSDatabase/PCS/PCSDIARYINSERT.jsp");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            sendMsg = "CONTENT=" + strings[0] + "&DATESTAMP=" + strings[1];
            osw.write(sendMsg);
            osw.flush();

            //jsp와 통신 성공 시 수행
            if (conn.getResponseCode() == conn.HTTP_OK) {
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();

                // jsp에서 보낸 값을 받는 부분
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                    insertResult = buffer.toString();
                }
                receiveMsg = buffer.toString();

            } else {
                // 통신 실패
            }
        } catch (ProtocolException e) {
            Log.d("won", "10");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("won", "11");


        return receiveMsg;

    }
}
