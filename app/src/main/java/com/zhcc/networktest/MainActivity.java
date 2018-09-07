package com.zhcc.networktest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.SAXParserFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
            //原生android 实现网络请求
            //sendRequestWithHttpURLConnection();

            //同样的功能 用开源okhttp实现
            sendRequestWithOkhttp();
        }
    }

    private void sendRequestWithOkhttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client=new OkHttpClient();
                    Request request = new Request.Builder().url("http://192.168.164.2/get_data.json").build();
                    Response r=client.newCall(request).execute();
                    String rData=r.body().string();

                    //showResponse(rData);

                    //PULL解析xml
                    //parseXMLWithPull(rData);

                    //SAX解析xml
                    //parseXMLWithSAX(rData);

                    parseJSONWithJSONObject(rData);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseJSONWithJSONObject(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                String version = jsonObject.getString("version");
                Log.d("main", "id is " + id);
                Log.d("main", "name is " + name);
                Log.d("main", "version is " + version);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseXMLWithSAX(String xmlData) {
        try {

            SAXParserFactory factory= SAXParserFactory.newInstance();
            XMLReader xmlReader=factory.newSAXParser().getXMLReader();
            MyHandler myHandler=new MyHandler();
            xmlReader.setContentHandler(myHandler); //myHandler实例设置到XMLRader中
            xmlReader.parse(new InputSource(new StringReader(xmlData)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseXMLWithPull(String xmlData) {
        try {
            XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser=factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType=xmlPullParser.getEventType();
            String id="";
            String name="";
            String version="";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName=xmlPullParser.getName();
                switch (eventType) {
                    //开始解析结点
                    case XmlPullParser.START_TAG:{
                        if ("id".equals(nodeName)) {
                            id=xmlPullParser.nextText();
                        } else if ("name".equals(nodeName)) {
                            name=xmlPullParser.nextText();
                        } else if ("version".equals(nodeName)) {
                            version=xmlPullParser.nextText();
                        }
                        break;
                    }
                    //完成解析节点
                    case XmlPullParser.END_TAG:{
                        if ("app".equals(nodeName)) {
                            Log.d("main", "id is " + id);
                            Log.d("main", "name is " + name);
                            Log.d("main", "version is " + version);
                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType=xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
