package com.example.administrator.benzhanzidonghua;

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

public class YuliangQuyu implements YuLiangfragmentInterface {
    private PublicInterface DataListener;
    private List<JiangYuFragmentBeen> list =new ArrayList<>();
    @Override
    public void getShopsData(PublicInterface getDataListener) {
        this.DataListener =getDataListener;
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Log.e("warn", "30");
                    // 命名空间
                    String nameSpace = "http://tempuri.org/";
                    // 调用的方法名称Get_OilAlarmInfo_List
                    String methodName = "Get_RainStationName_List";
                    // EndPoint
                    String endPoint = Path.get_ZanShibeidouPath();
                    // SOAP Action
                    String soapAction = "http://tempuri.org/Get_RainStationName_List";
                    // 指定WebService的命名空间和调用的方法名
                    SoapObject rpc = new SoapObject(nameSpace, methodName);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(rpc);

                    AndroidHttpTransport ht = new AndroidHttpTransport(endPoint);
                    ht.debug = true;
                    try {
                        // 调用WebService
                        ht.call(soapAction, envelope);
                    } catch (Exception e) {
                        DataListener.onGetDataError("网络或服务器异常");
                    }
                    SoapObject object;
                    object = (SoapObject) envelope.getResponse();
                    // 得到服务器传回的数据 数据时dataset类型的
                    int count1 = object.getPropertyCount();
                    if (count1 == 0) {
                        DataListener.onGetDataError("无雨量数据");
                        return;
                    }
                    Log.e("warn", String.valueOf(count1));
                    if (count1 > 0) {

                        for (int i = 0; i < count1; i++) {
                            JiangYuFragmentBeen jy = new JiangYuFragmentBeen();

                            SoapObject soapProvince = (SoapObject) object.getProperty(i);

                            jy.setID(soapProvince.getProperty("ID").toString());
                            Log.e("warn",soapProvince.getProperty("ID").toString());
                            jy.setNAME(soapProvince.getProperty("NAME").toString());
                            Log.e("warn",soapProvince.getProperty("NAME").toString());
                            list.add(jy);
                        }
                        DataListener.onGetDataSuccess(list);
                    }
                } catch (Exception e) {
                    DataListener.onGetDataError("网络或服务器异常");
                }
            }

        }.start();

    }
}
