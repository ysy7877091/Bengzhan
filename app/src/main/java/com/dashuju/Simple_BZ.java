package com.dashuju;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.com.vanpeng.Adapter.DaShuJuAdapter;
import com.example.administrator.benzhanzidonghua.MainActivity;
import com.example.administrator.benzhanzidonghua.MyProgressDialog;
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
 * Created by wypqwer on 2017/1/7.
 */

public class Simple_BZ extends AppCompatActivity {
    private MyProgressDialog progressDialog;
    private ListView lv_history;
    private Context mContent;
    private List<qiTiClass> qt_list;
    private String startTime;
    private String endTime;
    private String name;
    private String id;
    private TextView tv_topBZname;
    private ImageView iv_topleft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.dashuju_qiti);
        mContent = this;

        lv_history = (ListView) findViewById(R.id.lv_history);
        tv_topBZname = (TextView) findViewById(R.id.tv_topBZname);
        //iv_topleft = (ImageView) findViewById(R.id.iv_topleft);
        Button BZK_button = (Button)findViewById(R.id.BZK_button);
        BZK_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        qt_list = new ArrayList<>();

        startTime = getIntent().getStringExtra("StartTime");
        endTime = getIntent().getStringExtra("EndTime");
        name = getIntent().getStringExtra("Name");
        id = getIntent().getStringExtra("ID");

        tv_topBZname.setText(name);
        progressDialog = new MyProgressDialog(Simple_BZ.this, true, "加载中..");
        new Thread() {

            @Override
            public void run() {
                String soap = readSoap();
                soap = soap.replaceAll("string1", id);
                soap = soap.replaceAll("string2", startTime);
                soap = soap.replaceAll("string3", endTime);
                byte[] date = soap.getBytes();
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
            if (result.toString().equals("999999")) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "获取泵站失败,网络或者服务器异常", Toast.LENGTH_SHORT).show();
                return;
            } else if (result.equals("")) {
                progressDialog.dismiss();
                Toast.makeText(Simple_BZ.this, "暂无数据", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.dismiss();
                String[] objects = result.split("\\|\\|");
                for (int i = 0; i < objects.length; i++) {
                    qiTiClass qtc = new qiTiClass();
                    if (objects[i].length() > 0) {

                        String[] values = objects[i].split(",");
                        if (values.length > 1) {
                            if(values[2].equals("65535")&&values[3].equals("65535")&&values[4].equals("65535")&&values[5].equals("65535")) {
                                qtc.setShiJian(values[0]);
                                qtc.setYiYangHuaTan("");
                                qtc.setLiuHuaQin("");
                                qtc.setJiaWan("");
                                qtc.setAnQi("");
                            }else{
                                qtc.setShiJian(values[0]);
                                qtc.setYiYangHuaTan(values[2]);
                                qtc.setLiuHuaQin(values[3]);
                                qtc.setJiaWan(values[4]);
                                qtc.setAnQi(values[5]);
                            }
                            qtc.setShuiwei(values[1]);

                            qt_list.add(qtc);
                        }
                    }
                    lv_history.setAdapter(new DaShuJuAdapter(mContent, qt_list));
                }
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