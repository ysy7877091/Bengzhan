package com.vanpeng.javabeen;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016-12-17.
 */
public class RequestWebService {
    /**
     * @param nameSpace       	//命名空间
     * @param methodName    	//方法名
     * @param webServiceURL 	//webService 访问地址
     * @param parameterName 	//参数名集合
     * @param parameterList 	//参数值集合
     * @return
     */
    public static String ServiceRequest(String nameSpace,
                                            String methodName,
                                            String webServiceURL,
                                            ArrayList<String> parameterName,
                                            ArrayList parameterValue){

        SoapObject request = new SoapObject(nameSpace,methodName);
        for(int i = 0;i<parameterName.size();i++){
            request.addProperty(parameterName.get(i),parameterValue.get(i));
        }
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        AndroidHttpTransport ht = new AndroidHttpTransport(webServiceURL);
        ht.debug=true;
        (new MarshalBase64()).register(envelope);
        try {
            ht.call(nameSpace + methodName, envelope);
            System.out.println(nameSpace+methodName);
            Object object = (Object) envelope.getResponse();
            Log.i("mylog", "服务返回结果-->" + object);
            Log.e("warn","R48");
            return object.toString();


        } catch (IOException e) {
            return "999999";
        } catch (XmlPullParserException e) {
            return  "999999";
        } catch(Exception e){
            return  "999999";
        }

        //return "999999";
    }
}
