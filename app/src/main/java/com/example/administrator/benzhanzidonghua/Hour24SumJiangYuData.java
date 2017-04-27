package com.example.administrator.benzhanzidonghua;

import android.content.Context;
import android.util.Log;

import com.vanpeng.javabeen.JiangYuFragmentBeen;
import com.vanpeng.javabeen.PublicInterface;
import com.vanpeng.javabeen.YuLiangfragmentInterface;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/25.
 */

public class Hour24SumJiangYuData implements YuLiangfragmentInterface{

    private List<JiangYuFragmentBeen> list = new ArrayList<>();
    @Override
    public void getShopsData(final PublicInterface getDataListener) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Log.e("warn", "30");
                    // 命名空间
                    String nameSpace = "http://tempuri.org/";
                    // 调用的方法名称Get_OilAlarmInfo_List
                    String methodName = "Get_RealTimeRainfall_List";
                    // EndPoint
                    String endPoint = Path.get_ZanShibeidouPath();
                    // SOAP Action
                    String soapAction = "http://tempuri.org/Get_RealTimeRainfall_List";
                    // 指定WebService的命名空间和调用的方法名
                    SoapObject rpc = new SoapObject(nameSpace, methodName);
                    //设置需调用WebService接口需要传入的参数CarNum
                    // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(rpc);

                    AndroidHttpTransport ht = new AndroidHttpTransport(endPoint);
                    ht.debug = true;
                    try {
                        // 调用WebService
                        ht.call(soapAction, envelope);
                    } catch (Exception e) {
                        getDataListener.onGetDataError("网络或服务器异常");
                    }
                    SoapObject object;
                    object = (SoapObject) envelope.getResponse();
                    // 得到服务器传回的数据 数据时dataset类型的
                    int count1 = object.getPropertyCount();
                    if (count1 == 0) {
                        getDataListener.onGetDataError("无雨量数据");
                        return;
                    }
                    Log.e("warn", String.valueOf(count1));
                    if (count1 > 0) {

                        for (int i = 0; i < count1; i++) {
                            JiangYuFragmentBeen jy = new JiangYuFragmentBeen();

                            SoapObject soapProvince = (SoapObject) object.getProperty(i);

                            jy.setValueX(soapProvince.getProperty("ValueX").toString());
                            Log.e("warn",soapProvince.getProperty("ValueX").toString());
                            jy.setDevName(soapProvince.getProperty("DevName").toString());
                            Log.e("warn",soapProvince.getProperty("DevName").toString());
                            list.add(jy);
                        }
                        getDataListener.onGetDataSuccess(list);
                    }
                } catch (Exception e) {
                    getDataListener.onGetDataError("网络或服务器异常");
                }
            }

        }.start();


    }
}
