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

import com.com.vanpeng.Adapter.qiTiAdapter;
import com.vanpeng.javabeen.QiTiBaoJing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/4.
 * 气体历史和气体报警
 */

public class QiTiFragment extends Fragment {
    private ListView BJ_Gas;
    private String ID="";//泵站ID
    private String Name="";//泵站名称
    private String EndTime="";//结束时间
    private String StartTime="";//开始时间
    private MyProgressDialog progressDialog;
    private String bool="";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflater=LayoutInflater.from(getActivity());
        View view=inflater.inflate(R.layout.history_qiti,container,false);
        BJ_Gas=(ListView)view.findViewById(R.id.BJ_Gas);
        getBundleData();
        return view;
    }
    private void getBundleData(){
        Bundle bundle =getArguments();
        ID = bundle.getString("ID");
        Name = bundle.getString("Name");
        EndTime =bundle.getString("EndTime");
        StartTime=bundle.getString("StartTime");
        bool=bundle.getString("bool");
        if(bool.equals("0")){
                RequestWaterData();
            }else if(bool.equals("1")){
            GasData();
        }
    }
    //报警请求数据
    private void RequestWaterData(){
        progressDialog = new MyProgressDialog(getActivity(),false,"数据加载中");
        if(ID.equals("")||Name.equals("")||EndTime.equals("")||StartTime.equals("")){
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "查找的条件不足", Toast.LENGTH_SHORT).show();
            return;
        }else{
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {

                    String methodName = "GasStateWarningResult";//方法名
                    String path=Path.get_WebServicesURL();//请求地址
                    String SoapFileName = "assets/qitibaojing.xml";//读取的soap协议xml文件名称
                    String soap = CommonMethod.ReadSoap(SoapFileName);
                    soap=soap.replaceAll("string1",ID);
                    soap=soap.replaceAll("string2",StartTime);
                    soap=soap.replaceAll("string3",EndTime);
                    Log.e("warn",soap);
                    byte [] date=soap.getBytes();//soap协议转为字符数组
                    String result=CommonMethod.Request(path,date,methodName);
                    Message msg = Message.obtain();
                    msg.obj=result;
                    handler.sendMessage(msg);
                    }catch (Exception e){
                        Message msg = Message.obtain();
                        msg.obj="999999";
                        handler.sendMessage(msg);
                    }
                }
            }.start();
        }
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String val=(String) msg.obj;
            Log.e("warn",val);
            if(val.equals("")){
                Toast.makeText(getActivity(),"无信息",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }else if(val.equals("999999")){
                Toast.makeText(getActivity(),"网络异常",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
            else{
                List<QiTiBaoJing> list=DataFenGe(val);
                qiTiAdapter adapter = new qiTiAdapter(getActivity(),list,Name);
                BJ_Gas.setAdapter(adapter);
                progressDialog.dismiss();
            }
        }
    };
    private List<QiTiBaoJing> DataFenGe(String val){
        List<QiTiBaoJing> list= new ArrayList<QiTiBaoJing>();
        String [] arr=val.split("\\|\\|");
        for(int i=0;i<arr.length;i++){
            String [] arr1= arr[i].split(",");
            if(arr1[1].equals("65535")&&arr1[2].equals("65535")&&arr1[3].equals("65535")&&arr1[4].equals("65535")){

            }else{
            QiTiBaoJing qt=new QiTiBaoJing();
            qt.setTime(arr1[0]);
            qt.setYiYangHuaTan(arr1[1]);
            qt.setLiuHuaQing(arr1[2]);
            qt.setJiaWan(arr1[3]);
            qt.setAnQi(arr1[4]);
            list.add(qt);
            }
        }
        return list;
    }
    //历史记录气体信息
    private void GasData(){
        progressDialog = new MyProgressDialog(getActivity(),false,"数据加载中");
        if(ID.equals("")||Name.equals("")||EndTime.equals("")||StartTime.equals("")){
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "查找的条件不足", Toast.LENGTH_SHORT).show();
            return;
        }else{
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    String methodName = "GetGasHisInfoResult";//方法名
                    String path=Path.get_WebServicesURL();//请求地址
                    String SoapFileName = "assets/qitilishi.xml";//读取的soap协议xml文件名称
                    String soap = CommonMethod.ReadSoap(SoapFileName);
                    soap=soap.replaceAll("string1",ID);
                    soap=soap.replaceAll("string2",StartTime);
                    soap=soap.replaceAll("string3",EndTime);
                    Log.e("warn",soap);
                    byte [] date=soap.getBytes();//soap协议转为字符数组
                    String result=CommonMethod.Request(path,date,methodName);
                    Message msg = Message.obtain();
                    msg.obj=result;
                    handler.sendMessage(msg);
                }
            }.start();
        }
    }
}
