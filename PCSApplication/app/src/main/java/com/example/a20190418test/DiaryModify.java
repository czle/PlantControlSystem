package com.example.a20190418test;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static com.example.a20190418test.DiaryDTO.ip;


public class DiaryModify extends Activity {

    EditText editContent;
    TextView hideNO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_update);
        Intent getIntent = getIntent();
        String intentContent = getIntent.getExtras().getString("Content");
        String intentNo = getIntent.getExtras().getString("NO");


        hideNO = findViewById(R.id.hideNO);
        hideNO.setText(intentNo);
        editContent = findViewById(R.id.editContnet);
        editContent.setText(intentContent);

        Button btnUpdate = findViewById(R.id.btnUpdate);

        final String hide = hideNO.getText().toString();


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                String editcon = editContent.getText().toString();
                modifyDiary update = new modifyDiary();
                update.execute(hide, editcon);
                Toast.makeText(getApplicationContext(),"달력으로 이동합니다..",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), DiaryMain.class);
                startActivity(intent);

            }
        });
    }
    public class modifyDiary extends AsyncTask<String, Void, String> {
        StringBuilder sb = new StringBuilder();
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {

            try {
                String str;
                String page = "http://" + ip + ":8088/PCSDatabase/PCS/PCSDIARYUPDATE.jsp";

                URL url = new URL(page);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "NO=" + strings[0] + "&CONTENT=" + strings[1];
                osw.write(sendMsg);
                osw.flush();
                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();

                    // jsp에서 보낸 값을 받는 부분
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();

                } else {
                    // 통신 실패
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
