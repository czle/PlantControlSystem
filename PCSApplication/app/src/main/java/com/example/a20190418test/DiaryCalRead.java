package com.example.a20190418test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.a20190418test.DiaryDTO.ip;

public class DiaryCalRead extends Activity {

    ArrayList<DiaryDTO> haha; // 내용 뿌릴적에
    ArrayList<String> resultItem;
    JSONObject jObj;
    JSONArray jArray;
    DiaryDTO dto;
    TextView testTXT;
    TextView testTXT2;
    String NO;
    String Content;
    static boolean testflag = false;


    // 순서 <1> --------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_cal_read);
        Intent intent = getIntent();
        String date = intent.getExtras().getString("Date");


        searchDate searchDate = new searchDate();
        searchDate.execute(date);

        Button btnMo = (Button) findViewById(R.id.btnModify);

        TextView CalreadDate = findViewById(R.id.CalreadDate);

        CalreadDate.setText(date);

        btnMo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent2 = new Intent(getApplicationContext(), DiaryModify.class);
                intent2.putExtra("Content", testTXT2.getText().toString());
                intent2.putExtra("NO", testTXT.getText().toString());
                startActivity(intent2);

            }
        });

        Button btnDelete = (Button) findViewById(R.id.btnDelete);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDiary delete = new deleteDiary();
                delete.execute(NO);
                Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
    public class searchDate extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... strings) {
            try {

                String str;
                URL url = new URL("http://" + ip + ":8088/PCSDatabase/PCS/PCSDIARYREAD.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "DATESTAMP=" + strings[0];
                osw.write(sendMsg);
                osw.flush();
                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();

                    // jsp에서 보낸 값을 받는 부분
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);

                        try {
                            testTXT = (TextView) findViewById(R.id.testTXT);
                            testTXT2 = (TextView) findViewById(R.id.testTXT2);
                            jObj = new JSONObject(buffer.toString());
                            jArray = (JSONArray) jObj.get("sendreadDiary");
                            Log.d("mew", jArray.toString());

                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject row = jArray.getJSONObject(i);
                                NO = row.getString("NO");
                                testTXT.setText(NO.toString());
                                Content = row.getString("CONTENT");
                                testTXT2.setText(Content.toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    receiveMsg = buffer.toString();
                    Log.d("won", buffer + "");
                    Log.d("won", receiveMsg + "");
                    reader.close();
                } else {
                    // 통신 실패
                    Log.d("mew", "bow");
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }
    }

    public class deleteDiary extends AsyncTask<String, Void, String> {
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://" + ip + ":8088/PCSDatabase/PCS/PCSDIARYDELETE.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "NO=" + strings[0];
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
                    }
                    receiveMsg = buffer.toString();
                    reader.close();
                } else {
                    // 통신 실패
                    Log.d("mew", "bow");
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }
    }
}