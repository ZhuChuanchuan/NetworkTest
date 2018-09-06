package com.zhcc.networktest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    
    TextView responseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button sendRequest = (Button) findViewById(R.id.send_request);
        responseText = (TextView) findViewById(R.id.response_text);
        sendRequest.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.send_request) {
            sendRequestWithHttpURLConnection();
        }
    }

    private void sendRequestWithHttpURLConnection() {
        //开启线程发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection =null;
                BufferedReader reader=null;
                try {
//                    URL serverUrl = new URL("http://baidu.com");
//                    HttpURLConnection conn = (HttpURLConnection) serverUrl
//                            .openConnection();
//                    conn.setRequestMethod("GET");
//                    // 必须设置false，否则会自动redirect到Location的地址
//                    conn.setInstanceFollowRedirects(false);
//
//                    conn.addRequestProperty("Accept-Charset", "UTF-8;");
//                    conn.addRequestProperty("User-Agent",
//                            "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.8) Firefox/3.6.8");
//                    conn.addRequestProperty("Referer", "http://zuidaima.com/");
//                    conn.connect();
//                    String location = conn.getHeaderField("Location");

                    URL url=new URL("http://sina.com");

                    connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5*1000);
                    int code=connection.getResponseCode();
                    InputStream in=null;
                    if (code == 200) {
                        in=connection.getInputStream();
                    }
                    //下面对获取到的输入流进行读取
                    reader = new BufferedReader(new InputStreamReader(in));

                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    showResponse(response.toString());
                    Log.d("main", response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }

            }
        }).start();
    }

    private void showResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //UI操作，将结果显示到界面上
                responseText.setText(response);
            }
        });
    }
}
