package com.example.administrator.benzhanzidonghua;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.com.vanpeng.Adapter.BaoJingFragmentAdapter;
import com.vanpeng.javabeen.BaoJingFragmentJavaBeen;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/28.
 * 基础设施fragment
 */

public class BaoJingFragment extends Fragment {
    private View view;
    private List<BaoJingFragmentJavaBeen> list;
    private MyProgressDialog myProgressDialog=null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflater=LayoutInflater.from(getActivity());
        view=inflater.inflate(R.layout.baojingfragment_layout,container,false);
        if(getActivity()!=null){
            myProgressDialog = new MyProgressDialog(getActivity(),false,"数据加载中...");
        }
        new Thread(sum_water ).start();
        return view;
    }
    private void init(){
        ListView JiChuSheShi = (ListView)view.findViewById(R.id.JiChuSheShi);
        JiChuSheShi.setAdapter(new BaoJingFragmentAdapter(list,getActivity()));
    }
    BaoJingFragmentJavaBeen bj;
    SoapObject soapProvince;
    //获取基础设施数据
    private Runnable sum_water = new Runnable() {
        @Override
        public void run() {

            try{
                Log.e("warn","30");
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "GET_ConstantNameAndNum";
                // EndPoint
                String endPoint = Path.get_ZanShibeidouPath();
                // SOAP Action
                String soapAction = "http://tempuri.org/GET_ConstantNameAndNum";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数日期
                //String data = water_year.getText().toString()+"-"+water_Month.getText().toString()+"-"+water_day.getText().toString();
                //rpc.addProperty("",data);
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
                // 得到服务器传回的数据 数据时dataset类型的
                int count1 = object.getPropertyCount();
                Log.e("warn",String.valueOf(count1));
                if(count1==0){
                    closeDialog();
                    Message msg = Message.obtain();
                    msg.what=2;
                    hanlder.sendMessage(msg);
                    return;
                }
                if(count1>0)
                {
                    list = new ArrayList<>();
                    for(int i=0;i<count1;i++){

                        soapProvince = (SoapObject)object.getProperty(i);
                        bj = new BaoJingFragmentJavaBeen();
                        Log.e("warn",soapProvince.getProperty("NAME").toString()+":");

                        Log.e("warn",soapProvince.getProperty("VALUE").toString()+":");

                        Log.e("warn",soapProvince.getProperty("UNIT").toString()+":");
                        bj.setName(soapProvince.getProperty("NAME").toString());
                        bj.setNum(soapProvince.getProperty("VALUE").toString());
                        bj.setDanwei(soapProvince.getProperty("UNIT").toString());
                        list.add(bj);
                    }
                    Message msg = Message.obtain();
                    msg.what=1;
                    hanlder.sendMessage(msg);
                }
            } catch (Exception e){
                Message msg = Message.obtain();
                msg.what=0;
                hanlder.sendMessage(msg);
            }
        }
    };
    private Handler hanlder = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if(i==0){
                //加载dialog小时
                closeDialog();
                Toast.makeText(getActivity(), "网络或服务器异常", Toast.LENGTH_SHORT).show();
            }
            if(i==2){
                Toast.makeText(getActivity(), "无数据", Toast.LENGTH_SHORT).show();
            }
            if(i==1){ //获取泵站数据
                closeDialog();
                /*String str = (String)msg.obj;
                int index = str.indexOf("{");
                int index1= str.length();
                String str2 = str.substring(index+1,index1-1);
                Log.e("warn",str2);
                String arr[] =str2.split(";");
                for(int j=0;j<arr.length;j++){
                    arr[j]=arr[j].substring(arr[j].indexOf("=")+1);
                    Log.e("warn",arr[j]);
                }
*/
                init();

            }
        }
    };
private void closeDialog(){
    if(myProgressDialog!=null){
        myProgressDialog.dismiss();
        myProgressDialog=null;
    }
}

}
