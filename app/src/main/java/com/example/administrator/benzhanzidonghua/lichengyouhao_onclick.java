package com.example.administrator.benzhanzidonghua;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.com.vanpeng.Adapter.LiChengYouHaoTongJiAdapter;
import com.vanpeng.javabeen.BeiDouCarLieBiaoBeen;
import com.vanpeng.javabeen.BengZhanClass;
import com.vanpeng.javabeen.LiChengYouHaoTongJiJavaBean;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/25.
 */

public class lichengyouhao_onclick extends AppCompatActivity {

    private MyProgressDialog progressDialog;
    private List<LiChengYouHaoTongJiJavaBean> list;
    private ListView lv_liChengYouHaoTongJi_listview;
    private Context mcontext;
    TextView tv_LiCheng_CarNumber;
    TextView tv_LiCheng_YouHao;
    TextView tv_LiCheng_PingJunYouHao;
    TextView tv_LiCheng_LiCheng;
    TextView tv_LiCheng_GongZuoShiJian;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.lichengyouhao_onclick);
        mcontext=this;

        Button licheng_back =(Button)findViewById(R.id.licheng_back);
        licheng_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lv_liChengYouHaoTongJi_listview = (ListView) findViewById(R.id.lv_LiChengYouHaoTongJi_Listview);

        progressDialog = new MyProgressDialog(lichengyouhao_onclick.this, false, "加载中...");
        new Thread(RequestYouLiangInfo).start();
       // lv_liChengYouHaoTongJi_listview.setAdapter(new LiChengYouHaoTongJiAdapter(mcontext,listLiChengYouHaoTongJi));
    }
//    private void init(String arr []){
//        tv_LiCheng_CarNumber=(TextView)findViewById(R.id.tv_LiChengCarNumber);
//
//        tv_LiCheng_YouHao=(TextView)findViewById(R.id.tv_LiCheng_YouHao);
//
//        tv_LiCheng_PingJunYouHao=(TextView)findViewById(R.id.tv_LiCheng_PingJunYouHao);
//
//        tv_LiCheng_LiCheng=(TextView)findViewById(R.id.tv_LiCheng_LiCheng);
//
//        tv_LiCheng_GongZuoShiJian=(TextView)findViewById(R.id.tv_LiCheng_GongZuoShiJian);
//
//
//
//    }
    Runnable RequestYouLiangInfo = new Runnable() {
    StringBuffer sb;
        @Override
        public void run() {

            try{
                Log.e("warn","30");
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Get_MileAndUseOilInfo";
                // EndPoint
                String endPoint =Path.get_ZanShibeidouPath();
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_MileAndUseOilInfo";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);

                //设置需调用WebService接口需要传入的参数CarNum
                rpc.addProperty("carNum",getIntent().getStringExtra("Name").toString());
                rpc.addProperty("StartTime",getIntent().getStringExtra("StartTime").toString());//getIntent().getStringExtra("StartTime").toString()
                rpc.addProperty("EndTime",getIntent().getStringExtra("EndTime").toString());//getIntent().getStringExtra("EndTime").toString()
                // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);

                AndroidHttpTransport ht = new AndroidHttpTransport(endPoint);
                ht.debug=true;
                Log.e("warn","50");
                try {
                    // 调用WebService
                    ht.call(soapAction, envelope);
                } catch (Exception e) {
                    Message msg = Message.obtain();
                    msg.what=0;
                    hanlder.sendMessage(msg);
                }

                SoapObject object;
                // 开始调用远程方法
                Log.e("warn","60");

                object = (SoapObject) envelope.getResponse();
                Log.e("warn","64");
                // 得到服务器传回的数据 返回的数据时集合 每一个count是一个及集合的对象
                int count1 = object.getPropertyCount();
                Log.e("warn",String.valueOf(count1));
                if(String.valueOf(count1).equals("0")){
                    Toast.makeText(lichengyouhao_onclick.this, "无信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(count1>0)
                {
                    sb = new StringBuffer();

                    for (int i = 0; i < count1; i++) {
                        Log.e("warn","-----------------------------");
                        SoapObject soapProvince = (SoapObject)object.getProperty(i);

                        Log.e("warn",soapProvince.getProperty("CARNUM").toString()+":");
                        sb.append(soapProvince.getProperty("CARNUM").toString()+",");

                        Log.e("warn",soapProvince.getProperty("MILE").toString()+":");
                        sb.append(soapProvince.getProperty("MILE").toString()+",");
                        //lieBiao.setD_NUM(soapProvince.getProperty("D_NUM").toString());

                        Log.e("warn",soapProvince.getProperty("USEOIL").toString()+":");
                        sb.append(soapProvince.getProperty("USEOIL").toString()+",");
                        //lieBiao.setD_OFFNUM(soapProvince.getProperty("D_OFFNUM").toString());

                        Log.e("warn",soapProvince.getProperty("AVEUSEOIL").toString()+":");
                        sb.append(soapProvince.getProperty("AVEUSEOIL").toString()+",");
                        //lieBiao.setD_ONLINE(soapProvince.getProperty("D_ONLINE").toString());

                        Log.e("warn",soapProvince.getProperty("TIME").toString()+":");

                        //lieBiao.setD_CARNUMBER(soapProvince.getProperty("D_CARNUMBER").toString());
                        if(i==count1-1){
                            sb.append(soapProvince.getProperty("TIME").toString());
                        }else{
                            sb.append(soapProvince.getProperty("TIME").toString()+"|");
                        }

                    }
                    Message msg = Message.obtain();
                    msg.what=1;
                    msg.obj=sb.toString();
                    hanlder.sendMessage(msg);
                }
            } catch (Exception e){
                Message msg = Message.obtain();
                msg.what=0;
                hanlder.sendMessage(msg);
            }
        }
    };
    private Handler hanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str = (String) msg.obj;
            int i = msg.what;
            if (str == null) {
                progressDialog.dismiss();
                Toast.makeText(lichengyouhao_onclick.this,"无信息", Toast.LENGTH_SHORT).show();

            } else  {
                //LiChengYouHaoTongJiJavaBean
                list = new ArrayList<>();
                progressDialog.dismiss();
                Log.e("warn", "3055555↑↑↑↑" + "↓↓↓");
                Log.e("warn", str);
                if (str.contains("|")) {
                    String arr[] = str.split("\\|");

                    for (int j = 0; j < arr.length; j++) {
                        String arr1[] = arr[j].split(",");
                        LiChengYouHaoTongJiJavaBean l = new LiChengYouHaoTongJiJavaBean();
                        Log.e("warn","里程卡卡："+arr1[0]+";"+arr1[1]+";"+arr1[2]+";"+arr1[3]+";"+arr1[4]);
                        l.setLiChengCarNumber(arr1[0]);
                        l.setLiCheng(arr1[1]);
                        l.setYouHao(arr1[2]);
                        l.setPingJunYouHao(arr1[3]);
                        l.setGongZuoShiJian(arr1[4]);
                        list.add(l);
                    }

                } else {
                    String arr2[] = str.split(",");
                    LiChengYouHaoTongJiJavaBean l = new LiChengYouHaoTongJiJavaBean();
                    l.setLiChengCarNumber(arr2[0]);
                    l.setLiCheng(arr2[1]);
                    l.setYouHao(arr2[2]);
                    l.setPingJunYouHao(arr2[3]);
                    l.setGongZuoShiJian(arr2[4]);
                    list.add(l);
                }

                lv_liChengYouHaoTongJi_listview.setAdapter(new LiChengYouHaoTongJiAdapter(mcontext,list));



            }
        }
        };


}
