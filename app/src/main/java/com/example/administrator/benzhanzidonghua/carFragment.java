package com.example.administrator.benzhanzidonghua;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.drm.DrmStore;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.vanpeng.javabeen.TongJIYuWaterBeng;
import com.vanpeng.javabeen.onLinecarOrOffcar;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/3/28.
 */

public class carFragment extends Fragment {
    private View view;
    private TextView haoOil_TJ;
    private TextView LiCheng_TJ;
    private YongOil_TongJi_Fragment YOTJF;
    private LiCheng_TongJiFragment LTYJF;
    private ProgressBar left_bar;
    private ProgressBar right_bar;
    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        inflater = LayoutInflater.from(getActivity());
        view = inflater.inflate(R.layout.carfragmentlayout, container, false);
        init();
        return view;
    }

    private void init() {
        left_bar = (ProgressBar)view.findViewById(R.id.left_bar);
        right_bar = (ProgressBar)view.findViewById(R.id.right_bar);
        haoOil_TJ = (TextView) view.findViewById(R.id.haoOil_TJ);//用油统计
        haoOil_TJ.setOnClickListener(new carFragmentListener());
        LiCheng_TJ= (TextView) view.findViewById(R.id.LiCheng_TJ);//里程统计
        LiCheng_TJ.setOnClickListener(new carFragmentListener());
        YOTJF = new YongOil_TongJi_Fragment();
        LTYJF = new  LiCheng_TongJiFragment();

        switchFragment(LTYJF,YOTJF);
        haoOil_TJ.setBackgroundColor(getResources().getColor(R.color.tj6));

        new Thread(top_data).start();

       // fixedThreadPool.execute(top_data);车辆信息
        //假数据 可直接删
        /*left_bar.setProgress(10);
        right_bar.setProgress(32);
        TextView TJ_carNum = (TextView)view.findViewById(R.id.TJ_carNum);
        TJ_carNum.setText(42+"");
        TextView online_carNum = (TextView) view.findViewById(R.id.online_carNum);
        online_carNum.setText("10台");
        TextView offline_carNum = (TextView) view.findViewById(R.id.offline_carNum);
        offline_carNum.setText("32台");*/
    }
    private class carFragmentListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.haoOil_TJ:
                                    haoOil_TJMethod();
                                    break;
                case R.id.LiCheng_TJ:
                                    LiCheng_TJMethod();
                                    break;
            }
        }
    }
    private void haoOil_TJMethod(){
        switchFragment(LTYJF,YOTJF);
        haoOil_TJ.setBackgroundColor(getResources().getColor(R.color.tj6));
        LiCheng_TJ.setBackgroundColor(getResources().getColor(R.color.blue));
    }
    private void LiCheng_TJMethod(){
        switchFragment(YOTJF,LTYJF);
        haoOil_TJ.setBackgroundColor(getResources().getColor(R.color.blue));
        LiCheng_TJ.setBackgroundColor(getResources().getColor(R.color.tj6));
    }
    public void switchFragment(Fragment from, Fragment to) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        //设置切换的效果
        if (!to.isAdded()) {    // 先判断是否被add过
            if (from.isAdded()) {
                transaction.hide(from);
            } //隐藏当前的fragment
            transaction.add(R.id.TJ_carReplace, to).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            if (from.isAdded()) {
                transaction.hide(from);
            } //隐藏当前的fragment
            transaction.show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
        }
    }
    //获取车辆数据
    private Runnable top_data = new Runnable() {
        private Object object;
        @Override
        public void run() {
            try{
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Get_CarWheterOnline";
                // EndPoint
                String endPoint =Path.get_ZanShibeidouPath();
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_CarWheterOnline";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数日期
                /*String data = water_year.getText().toString()+"-"+water_Month.getText().toString()+"-"+water_day.getText().toString();
                rpc.addProperty("",data);*/
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
                    Message msg = Message.obtain();
                    msg.what=2;
                    hanlder.sendMessage(msg);
                    return;
                }
                if(count1>0)
                {

                    Log.e("warn","-----------------------------");
                    SoapObject soapProvince = (SoapObject) envelope.bodyIn;

                    Log.e("warn",soapProvince.getProperty("Get_CarWheterOnlineResult").toString()+":返回id");//dataset数据类型
                    String str = soapProvince.getProperty("Get_CarWheterOnlineResult").toString();

                    Message msg = Message.obtain();
                    msg.what=1;
                    msg.obj=str;
                    hanlder.sendMessage(msg);
                }
            } catch (Exception e){
                Message msg = Message.obtain();
                msg.what=0;
                hanlder.sendMessage(msg);
            }


        }
    };

    private Handler hanlder  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if(i==0){
                //加载dialog小时
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "获取车辆信息失败", Toast.LENGTH_SHORT).show();
                }
            }
            if(i==1){ //获取顶部数据
                String arr[];
                String str = (String)msg.obj;
                Log.e("warn",str);
                int index = str.indexOf("{");
                int index1= str.length();
                String str2 = str.substring(index+1,index1-1);
                Log.e("warn",str2);
                arr =str2.split(";");
                for(int j=0;j<arr.length;j++){
                    arr[j]=arr[j].substring(arr[j].indexOf("=")+1);
                    Log.e("warn",arr[j]);
                }
                int sum = Integer.parseInt(arr[0])+Integer.parseInt(arr[1]);
                left_bar.setMax(sum);
                right_bar.setMax(sum);
                left_bar.setProgress(Integer.parseInt(arr[0]));
                right_bar.setProgress(Integer.parseInt(arr[1]));
                TextView TJ_carNum = (TextView)view.findViewById(R.id.TJ_carNum);
                TJ_carNum.setText(sum+"");
                TextView online_carNum = (TextView) view.findViewById(R.id.online_carNum);
                online_carNum.setText(arr[0]+"台");
                TextView offline_carNum = (TextView) view.findViewById(R.id.offline_carNum);
                offline_carNum.setText(arr[1]+"台");
            }
            if(i==2){
                //加载dialog小时
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "无车辆状态信息", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    /*private void JiShui() {


        Float[] yl = new Float[3];//雨量
        String[] appName = new String[3];//区域名

        float start = 0f;
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        yeWeiChart.getXAxis().setAxisMinValue(start);

        List<Integer> list = new ArrayList<Integer>();//装载柱状图颜色
        list.add(getResources().getColor(R.color.yl03));//绿色
        list.add(getResources().getColor(R.color.yl08));//黄色
        list.add(getResources().getColor(R.color.red));//红色
        int[] myColors = new int[3];//取颜色长度
        String[] lbls = new String[3];//取比量值长度
        String arr [ ] ={"01","02","03"};
        for (int i = 0; i < 3; i++) {//取数据
            lbls[i] = arr [i]  ;
            myColors[i] = ColorTemplate.MATERIAL_COLORS[i];
        }
        String arr1 [] = {"01","02","03"};
        for (int i = 0; i < 3; i++) {

            yl[i] = Float.parseFloat("0");//雨量值
            appName[i] = arr1[i].toString();//区域名

            yVals1.add(new BarEntry(yl[i],i));

        }

        yeWeiChart.setDrawBarShadow(false);
        yeWeiChart.setDrawValueAboveBar(true);
        yeWeiChart.setDescription("");
        yeWeiChart.setMaxVisibleValueCount(60);
        yeWeiChart.setPinchZoom(false);
        yeWeiChart.setDrawGridBackground(false);
        // 是否可以缩放
        yeWeiChart.setScaleEnabled(true);
        // 集双指缩放
        yeWeiChart.setPinchZoom(false);
        // 隐藏右边的坐标轴
        yeWeiChart.getAxisRight().setEnabled(false);
        //动画
        yeWeiChart.animateY(1000);

        //X轴
        XAxis xAxis = yeWeiChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(8);
        xAxis.setTextColor(Color.rgb(103,175,205));//设置x轴数据颜色

        //Y轴
        YAxis leftAxis = yeWeiChart.getAxisLeft();
        leftAxis.setTextColor(Color.rgb(103, 175, 205));//设置y轴数据颜色
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

            *//*Legend l = yeWeiChart.getLegend();//设置比例图
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setForm(Legend.LegendForm.CIRCLE);
            l.setFormSize(8f);
            l.setTextSize(8f);
            l.setXEntrySpace(4f);
            l.setCustom(myColors, lbls);*//*

        BarDataSet set1;
        //判断图表中原来是否有数据
        if (yeWeiChart.getData() != null && yeWeiChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) yeWeiChart.getData().getDataSetByIndex(0);
            //set1 = new BarDataSet(yVals1, "");
            set1.setYVals(yVals1);
            yeWeiChart.getData().notifyDataChanged();
            yeWeiChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "积水液位");
            //set1.setColors(ColorTemplate.MATERIAL_COLORS);//设置柱状图的各种颜色，
            set1.setBarSpacePercent(50f);//设置柱间空白的宽度
            set1.setColors(list);
            //set1.setBarSpacePercent(10);
            // set custom labels and colors
            //设置柱状图颜色，第一个color.rgb为第一个柱状图颜色。以此类推
            //set1.setColors(new int[]{Color.rgb(255,241,226),Color.rgb(155,241,226)});
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            //BarData data = new BarData(dataSets);
            BarData data = new BarData(appName,dataSets);
            data.setValueTextSize(7f);
            data.setDrawValues(true);
            //data.setValueTextColor(Color.BLACK);
            data.setValueTextColor(Color.rgb(255,255,255));//设置数据颜色
            //data.setValueFormatter(new CustomerValueFormatter());
            //data.setValueTypeface(mTfLight);
            //data.setBarWidth(0.8f);
            //mChart.setExtraOffsets(0,0,0,200);//此种方法可以一次设置上下左右偏移量。根据自己数据哪个地方显示不全，对应调用方法。
            yeWeiChart.setData(data);// 设置数据
        }
    }*/
}