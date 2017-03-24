package com.example.administrator.benzhanzidonghua;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.com.vanpeng.Adapter.BZAdapter;
import com.vanpeng.javabeen.BengZhanClass;
import com.vanpeng.javabeen.VideoMonitoring;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/19.
 * 监控fragment
 */

public class JianKongFragment extends Fragment {
    private MyProgressDialog progressDialog;
    private View view;
    private GridView gv;
    private BZAdapter bzAdapter;//gridview适配器
    private List<BengZhanClass> listBengZhan;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =LayoutInflater.from(getActivity()).inflate(R.layout.jiankong_layout,null);
        ShuiWeiGetDate();
        return view;
    }
    private void ShuiWeiGetDate(){
        progressDialog = new MyProgressDialog(getActivity(), true, "加载中..");

        Runnable networkGetBengZhanInfoNew = new Runnable() {
            @Override
            public void run() {
                String path = Path.get_WebServicesURL();//webservice url
                String methodName = "AppGetBengZhanListResult";//返回的方法名称
                String SoapFileName = "assets/appgetbenzhanlist.xml";//soap协议文件名称
                String soap = CommonMethod.ReadSoap(SoapFileName);//读取soap协议
                try {
                    Log.e("warn",soap);
                    byte [] date=soap.getBytes();//soap协议转为字符数组
                    String result=CommonMethod.Request(path,date,methodName);
                    Message msg = Message.obtain();
                    msg.obj=result;
                    handlerGetBengZhanListNew.sendMessage(msg);
                    Log.d("DEBUG", "获取泵站列表WebService结果：" + result.toString());
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.obj="999999";
                    handlerGetBengZhanListNew.sendMessage(msg);
                    //Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
                }
            }
        };
        new Thread(networkGetBengZhanInfoNew).start();
    }
    //获取到监控数据
    public Handler handlerGetBengZhanListNew = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String val =(String)msg.obj;
            //String val = data.getString("value");
            Log.e("warn","泵站"+val);
            if (val.toString().equals("999999")) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "获取泵站失败,网络或者服务器异常", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.dismiss();
                gv = (GridView)view.findViewById(R.id.bz_gridView);
                listBengZhan = new ArrayList<BengZhanClass>();
                //Toast.makeText(MainActivity.this,val, Toast.LENGTH_SHORT).show();
                //List<String> name = new ArrayList<String>();//监控站点名称集合
                List<VideoMonitoring> Video = new ArrayList<VideoMonitoring>();//监控站点名称集合
                String[] objects = val.split("\\|");
                for (int i = 0; i < objects.length; i++) {

                    if (objects[i].length() > 0) {
                        BengZhanClass bengzhanObject = new BengZhanClass();
                        String[] values = objects[i].split(",");
                        Log.d("DEBUG", "WebService结果_回调函数values.length：" + values.length);
                        if (values.length > 1) {
                                //泵站名
                                bengzhanObject.setName(values[1].toString());

                            if (values.length > 17) {
                                //泵站视频信息
                                //String[] video = values[17].split("\\[-\\]");
                                List<VideoMonitoring> videoMonitoringList = new ArrayList<VideoMonitoring>();
                                String[] videoMonitoringValues = values[17].split("\\[-\\]");//多个视频
                                for (int j = 0; j < videoMonitoringValues.length; j++) {
                                    VideoMonitoring videoMonitoring = new VideoMonitoring();
                                    String[] video = videoMonitoringValues[j].split("\\<-\\>");
                                    videoMonitoring.setId(video[0]);
                                    videoMonitoring.setName(video[1]);
                                    videoMonitoring.setBengZhanID(video[2]);
                                    videoMonitoring.setIp(video[3]);
                                    videoMonitoring.setPort(video[4]);
                                    videoMonitoring.setLoginName(video[5]);
                                    videoMonitoring.setLoginPWD(video[6]);
                                    videoMonitoring.setChannelID(video[7]);
                                    videoMonitoring.setSortX(video[8]);
                                    videoMonitoringList.add(videoMonitoring);
                                }
                                //泵站的多个视频
                                bengzhanObject.setVideoMonitoringList(videoMonitoringList);
                            }
                            listBengZhan.add(bengzhanObject);
                        }

                    }
                }
                bzAdapter = new BZAdapter(getActivity(),listBengZhan);
                gv.setAdapter(bzAdapter);
                gv.setOnItemClickListener(new GridViewListener());
            }
        }
    };
    private class GridViewListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            //gv.setBackground(getResources().getDrawable(R.color.green));
            //view是当前item的view，通过它可以获得该项中的各个组件。
            //i是当前item的ID。这个id根据你在适配器中的写法可以自己定义。
            //l是当前的item在listView中的相对位置！
            VideoMonitoring in=listBengZhan.get(i).getVideoMonitoringList().get(0);
            VideoMonitoring out=listBengZhan.get(i).getVideoMonitoringList().get(1);
            Intent intent = new Intent(getActivity(),JianKongList.class);
            //室内传递的参数
            intent.putExtra("in_VideoMonitoring", in);
            intent.putExtra("out_VideoMonitoring",out);
            //intent.putExtra("bz_Name",listBengZhan.get(i).getName());
            getActivity().startActivity(intent);
        }
    }
}
