package com.dashuju;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Xml;
import android.widget.ListView;
import android.widget.TextView;

import com.com.vanpeng.Adapter.DaShuJuAdapter;
import com.example.administrator.benzhanzidonghua.MainActivity;
import com.example.administrator.benzhanzidonghua.Path;
import com.example.administrator.benzhanzidonghua.R;
import com.vanpeng.javabeen.qiTiClass;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wypqwer on 2017/1/6.
 */

public class simpleBZ extends AppCompatActivity {

    private ListView lv_history;
    private Context mContent;
    private List<qiTiClass> qt_list;
    private String startTime;
    private String endTime;
    private String name;
    private String id;
    private TextView tv_topBZname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.dashuju_qiti);
        mContent = this;

        lv_history = (ListView) findViewById(R.id.lv_history);
        tv_topBZname = (TextView) findViewById(R.id.tv_topBZname);

        qt_list = new ArrayList<>();

        startTime = getIntent().getStringExtra("StartTime");
        endTime = getIntent().getStringExtra("EndTime");
        name = getIntent().getStringExtra("Name");
        id = getIntent().getStringExtra("ID");

        tv_topBZname.setText(name);

        new Thread() {

            @Override
            public void run() {
                String soap = readSoap();
                soap = soap.replaceAll("string1", id);
                soap = soap.replaceAll("string2", startTime);
                soap = soap.replaceAll("string3", endTime);
                byte[] date = soap.getBytes();
                Log.e("warn", "run: " + soap);

                String httpUrl = Path.get_WebServicesURL();

                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(httpUrl).openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
                    conn.setRequestProperty("Content-Length", String.valueOf(date.length));
                    conn.getOutputStream().write(date);
                    if (conn.getResponseCode() == 200) {
                        Log.e("warn", String.valueOf(conn.getResponseCode()));
                        Message msg = new Message();
                        msg.obj = parseXml(conn.getInputStream());
                        handler1.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    Handler handler1 = new Handler() {

        private String result;

        public void handleMessage(Message msg) {

            result = (String) msg.obj;

            String[] objects = result.split("\\|\\|");
            for (int i = 0; i < objects.length; i++) {
                qiTiClass qtc = new qiTiClass();
                if (objects[i].length() > 0) {

                    String[] values = objects[i].split(",");
                    if (values.length > 1) {

                        qtc.setShiJian(values[0]);
                        qtc.setYiYangHuaTan(values[2]);
                        qtc.setLiuHuaQin(values[3]);
                        qtc.setJiaWan(values[4]);
                        qtc.setAnQi(values[5]);
                        qtc.setShuiwei(values[1]);

                        qt_list.add(qtc);

                    }
                }
                lv_history.setAdapter(new DaShuJuAdapter(mContent, qt_list));
            }
        }
    };

    // 读取xml soap文件
    public String readSoap() {
        InputStream ios = MainActivity.class.getClassLoader().getResourceAsStream("assets/dashujuchaxun.xml");
        byte[] b = new byte[1024];
        StringBuffer sb = new StringBuffer();
        int len;
        try {
            while ((len = ios.read(b)) != -1) {
                String str = new String(b, 0, len, "UTF-8");
                sb.append(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    // xml的解析
    private String parseXml(InputStream is) throws XmlPullParserException, IOException {

        XmlPullParser xpp = Xml.newPullParser();
        xpp.setInput(is, "UTF-8");
        int event = xpp.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                // 文档的开始标签
                case XmlPullParser.START_TAG:
                    if (xpp.getName().equals("GetBengZhanInfoResult")) {

                        return xpp.nextText();
                    }
                    break;
            }
            // 往下解析
            event = xpp.next();
        }
        return null;
    }

}
