package com.example.administrator.benzhanzidonghua;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

/**
 * Created by Administrator on 2016/12/22.
 */

public class Text extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.benzhanlistview_layout);
        new Thread(networkTask).start();
    }
    StringBuffer sb;
    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            try{
                Log.e("warn","30");
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Get_PersonInfo";
                // EndPoint
                String endPoint = "http://172.21.75.1:801/GPSService.asmx";
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_PersonInfo";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数CarNum
                //rpc.addProperty("carNum","辽AJ1832");
                rpc.addProperty("id","1");
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
                    msg.what=2;
                    hanlder.sendMessage(msg);
                }

                SoapObject object;
                // 开始调用远程方法
                Log.e("warn","60");


                /*object = (SoapObject) envelope.getResponse();
                Log.e("warn","64");*/
                // 得到服务器传回的数据
                SoapObject soapObject = (SoapObject) envelope.bodyIn;
                soapObject.getProperty(0).toString();
                Log.e("warn",soapObject.getProperty(0).toString());


            } catch (Exception e){
              /*  Message msg = Message.obtain();
                msg.what=0;
                hanlder.sendMessage(msg);*/
                e.printStackTrace();
            }
        }
    };
    private android.os.Handler hanlder =new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            Log.e("warn", sb.toString());
        }

    };
}
