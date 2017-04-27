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

import com.com.vanpeng.Adapter.ShuiWeiBaoJingListViewAdapter;
import com.vanpeng.javabeen.ShuiWeiBaoJing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/4.
 * 水位历史和报警fragment
 */

public class ShuiWeiBaoJingFragment extends Fragment{
    private String ID="";//泵站ID
    private String Name="";//泵站名称
    private String EndTime="";//结束时间
    private String StartTime="";//开始时间
    private ListView BJ_Water;
    private MyProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflater=LayoutInflater.from(getActivity());
        View view=inflater.inflate(R.layout.shuiweibaojing_layout,container,false);
        BJ_Water=(ListView)view.findViewById(R.id.BJ_Water);
        getBundleData();
        return view;
    }
    private void getBundleData(){
        Bundle bundle =getArguments();
        ID = bundle.getString("ID");
        Name = bundle.getString("Name");
        EndTime =bundle.getString("EndTime");
        StartTime=bundle.getString("StartTime");
        RequestWaterData();
    }
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
                    String methodName = "YeWeiStateWarningResult";//方法名
                    String path=Path.get_WebServicesURL();//请求地址
                    String SoapFileName = "assets/shuiweibaojing.xml";//读取的soap协议xml文件名称
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
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String val=(String) msg.obj;
            Log.e("warn",val);
            if(val.equals("")){
                Toast.makeText(getActivity(),"无报警信息",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }else {
                List<ShuiWeiBaoJing> list=Spl(val);
                ShuiWeiBaoJingListViewAdapter adapter =new ShuiWeiBaoJingListViewAdapter(Name,list,getActivity());
                BJ_Water.setAdapter(adapter);
                progressDialog.dismiss();
            }
        }
    };
    private List<ShuiWeiBaoJing> Spl(String val){
        List<ShuiWeiBaoJing> list = new ArrayList<ShuiWeiBaoJing>();
        String [] arr=val.split("\\|\\|");
        for(int i=0;i<arr.length;i++){
            ShuiWeiBaoJing swbj= new ShuiWeiBaoJing();
            String [] arr1=arr[i].split(",");
            swbj.setShiJian(arr1[0]);
            swbj.setShuiWei(arr1[1]);
            list.add(swbj);
        }
        return list;
        }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(progressDialog!=null) {
            if(progressDialog.isShowing()){
                progressDialog.dismiss();

            }
            progressDialog = null;
        }
    }
}
