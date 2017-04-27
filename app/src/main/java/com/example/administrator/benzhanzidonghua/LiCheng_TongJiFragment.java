package com.example.administrator.benzhanzidonghua;

import android.app.Fragment;
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
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.vanpeng.javabeen.UserOilTongJi;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/3/28.
 * 里程统计
 */

public class LiCheng_TongJiFragment extends Fragment {
    private View view;
    private BarChart dayLicheng;
    private  BarChart Monthlicheng;
    private BarChart Jilicheng;

    private ProgressBar day_bar;
    private ProgressBar month_bar;
    private ProgressBar jidu_bar;
    private String CurrentTime = "";
    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflater = LayoutInflater.from(getActivity());
        view = inflater.inflate(R.layout.lichengtongjifragment_layout, container, false);
        init();
        return view;
    }
    private void init(){
        dayLicheng  = (BarChart)view.findViewById(R.id.dayLicheng);
        dayLicheng.setNoDataText("");//更改图表无数据时，图表上显示的内容(no chart data available)
        Monthlicheng  = (BarChart)view.findViewById(R.id.Monthlicheng);
        Monthlicheng.setNoDataText("");//更改图表无数据时，图表上显示的内容(no chart data available)
        Jilicheng  = (BarChart)view.findViewById(R.id.Jilicheng);
        Jilicheng.setNoDataText("");//更改图表无数据时，图表上显示的内容(no chart data available)

        day_bar = (ProgressBar)view.findViewById(R.id.day_bar);

        month_bar = (ProgressBar)view.findViewById(R.id.month_bar);

        jidu_bar= (ProgressBar)view.findViewById(R.id.jidu_bar);


        Calendar c = Calendar.getInstance();
        String  year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf(c.get(Calendar.MONTH)+1);
        String  day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        CurrentTime = year+"-"+month+"-"+day;





        fixedThreadPool.execute(dayLiCheng);//月总用油量
        fixedThreadPool.execute(MonthLiCheng);//季度总用油量
        //fixedThreadPool.execute(jiduLiCheng);//季度总用油量

        //setdayLicheng();
        //setMonthLicheng();
       // setjiduLicheng();

    }
    Runnable dayLiCheng = new Runnable() {
        @Override
        public void run() {
            try{
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Get_DayMainTongJi";
                // EndPoint
                String endPoint =Path.get_ZanShibeidouPath();
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_DayMainTongJi";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数日期
                /*String data = water_year.getText().toString()+"-"+water_Month.getText().toString()+"-"+water_day.getText().toString();
                rpc.addProperty("",data);*/
                rpc.addProperty("time",CurrentTime);
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
                if(count1>0) {
                    StringBuffer sb = new StringBuffer();
                    for(int i=0;i<count1;i++) {
                        Log.e("warn", "-----------------------------");
                        SoapObject soapProvince = (SoapObject) object.getProperty(i);

                        Log.e("warn", soapProvince.getProperty("Day").toString() + ":");
                        sb.append(soapProvince.getProperty("Day").toString() + ",");

                        Log.e("warn", soapProvince.getProperty("Mile").toString() + ":");
                        if (i == count1 - 1) {
                            sb.append(soapProvince.getProperty("Mile").toString());
                        } else {
                            sb.append(soapProvince.getProperty("Mile").toString() + "|");
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
    private List<UserOilTongJi> day_lsit;
    private Handler hanlder =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if(i==0){
                //加载dialog小时
                day_bar.setVisibility(View.INVISIBLE);
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "获取日里程信息失败", Toast.LENGTH_SHORT).show();
                }
            }
            if(i==2){
                day_bar.setVisibility(View.INVISIBLE);
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "无日里程信息", Toast.LENGTH_SHORT).show();
                }
            }
            if(i==1){ //获取顶部数据
                String str = (String) msg.obj;
                Log.e("warn", "water:" + str);
                day_lsit = new ArrayList<>();
                String arr[] = str.split("\\|");
                for (int j =arr.length-1; j >= 0; j--) {
                    UserOilTongJi oil = new UserOilTongJi();
                    String arr1[] = arr[j].split(",");
                    oil.setTime(arr1[0]);
                    oil.setOilNum(arr1[1]);
                    day_lsit.add(oil);
                }
                day_bar.setVisibility(View.INVISIBLE);
                setdayLicheng();
            }
        }
    };
    private void setdayLicheng(){


        Float[] yl = new Float[day_lsit.size()];//数据用油量
        String[] appName = new String[day_lsit.size()];//x轴时间

        float start = 0f;
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        dayLicheng.getXAxis().setAxisMinValue(start);

        List<Integer> list = new ArrayList<Integer>();//装载柱状图颜色


        for(int i =0;i<day_lsit.size();i++){
            list.add(ColorTemplate.SINGLE_COLORS[0]);
        }

        for (int i = 0; i < day_lsit.size(); i++) {

            yl[i] = Float.parseFloat(day_lsit.get(i).getOilNum());//雨量值
            appName[i] = day_lsit.get(i).getTime();//x轴数值
            yVals1.add(new BarEntry(yl[i],i));
        }

        dayLicheng.setDrawBarShadow(false);
        dayLicheng.setDrawValueAboveBar(true);
        dayLicheng.setDescription("");
        dayLicheng.setMaxVisibleValueCount(60);
        dayLicheng.setPinchZoom(false);
        dayLicheng.setDrawGridBackground(false);
        // 是否可以缩放
        dayLicheng.setScaleEnabled(true);
        // 集双指缩放
        dayLicheng.setPinchZoom(false);
        // 隐藏右边的坐标轴
        dayLicheng.getAxisRight().setEnabled(false);
        //动画
        dayLicheng.animateY(1000);

        //X轴
        XAxis xAxis = dayLicheng.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(8);
        xAxis.setTextColor(Color.rgb(103,175,205));//设置x轴数据颜色
        xAxis.setAxisMinValue(-0.5f); //设置x轴坐标起始值为-0.5 防止其实条形图被切去一半
        //Y轴
        YAxis leftAxis = dayLicheng.getAxisLeft();
        leftAxis.setTextColor(Color.rgb(103, 175, 205));//设置y轴数据颜色
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

        Legend mLegend = dayLicheng.getLegend(); //设置比例图
        mLegend.setEnabled(false);//不绘制图例
          /* Legend l = dayLicheng.getLegend().;//设置比例图
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setForm(Legend.LegendForm.CIRCLE);
            l.setFormSize(8f);
            l.setTextSize(8f);
            l.setXEntrySpace(4f);
            l.setCustom(myColors, lbls);*/

        BarDataSet set1;
        //判断图表中原来是否有数据
        if (dayLicheng.getData() != null && dayLicheng.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) dayLicheng.getData().getDataSetByIndex(0);
            //set1 = new BarDataSet(yVals1, "");
            set1.setYVals(yVals1);
            dayLicheng.getData().notifyDataChanged();
            dayLicheng.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "");
            //set1.setColors(ColorTemplate.MATERIAL_COLORS);//设置柱状图的各种颜色，
            set1.setBarSpacePercent(50f);//设置柱间空白的宽度
            set1.setColors(list);//设置柱状图的各种颜色
            //set1.setBarSpacePercent(10);
            // set custom labels and colors
            //设置柱状图颜色，第一个color.rgb为第一个柱状图颜色。以此类推
            //set1.setColors(new int[]{Color.rgb(255,241,226),Color.rgb(155,241,226)});
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            //BarData data = new BarData(dataSets);
            BarData data = new BarData(appName, dataSets);
            data.setValueTextSize(7f);
            data.setDrawValues(true);
            //data.setValueTextColor(Color.BLACK);
            data.setValueTextColor(Color.rgb(255, 255, 255));//设置数据颜色
            //data.setValueFormatter(new CustomerValueFormatter());
            //data.setValueTypeface(mTfLight);
            //data.setBarWidth(0.8f);
            //mChart.setExtraOffsets(0,0,0,200);//此种方法可以一次设置上下左右偏移量。根据自己数据哪个地方显示不全，对应调用方法。
            dayLicheng.setData(data);// 设置数据
            dayLicheng.setVisibleXRangeMaximum(10);//设置x坐标轴最多显示多少个数据，超出的部分需要滑动才能看到
        }
    }


    //季度总好油量
    Runnable MonthLiCheng = new Runnable() {
        @Override
        public void run() {
            try{
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Get_MonthMainTongJi";
                // EndPoint
                String endPoint =Path.get_ZanShibeidouPath();
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_MonthMainTongJi";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数日期
                /*String data = water_year.getText().toString()+"-"+water_Month.getText().toString()+"-"+water_day.getText().toString();
                rpc.addProperty("",data);*/
                rpc.addProperty("time",CurrentTime);
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
                    Monthhanlder.sendMessage(msg);
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
                    Monthhanlder.sendMessage(msg);
                    return;
                }
                if(count1>0) {
                    StringBuffer sb = new StringBuffer();
                    for(int i=0;i<count1;i++) {
                        Log.e("warn", "-----------------------------");
                        SoapObject soapProvince = (SoapObject) object.getProperty(i);

                        Log.e("warn", soapProvince.getProperty("Month").toString() + ":");
                        sb.append(soapProvince.getProperty("Month").toString() + ",");

                        Log.e("warn", soapProvince.getProperty("Mile").toString() + ":");
                        if (i == count1 - 1) {
                            sb.append(soapProvince.getProperty("Mile").toString());
                        } else {
                            sb.append(soapProvince.getProperty("Mile").toString() + "|");
                        }
                    }
                    Message msg = Message.obtain();
                    msg.what=1;
                    msg.obj=sb.toString();
                    Monthhanlder.sendMessage(msg);
                }
            } catch (Exception e){
                Message msg = Message.obtain();
                msg.what=0;
                Monthhanlder.sendMessage(msg);
            }
        }
    };
    private List<UserOilTongJi> Month_lsit;
    private Handler Monthhanlder =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if(i==0){
                //加载dialog小时
                month_bar.setVisibility(View.INVISIBLE);
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "获取本季度用油信息失败", Toast.LENGTH_SHORT).show();
                }
            }
            if(i==2){
                month_bar.setVisibility(View.INVISIBLE);
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "无本季度用油信息", Toast.LENGTH_SHORT).show();
                }
            }
            if(i==1){ //获取顶部数据
                String str = (String) msg.obj;
                Log.e("warn", "water:" + str);
                Month_lsit= new ArrayList<>();
                String arr[] = str.split("\\|");
                for (int j =arr.length-1; j>=0; j--) {
                    UserOilTongJi oil = new UserOilTongJi();
                    String arr1[] = arr[j].split(",");
                    oil.setTime(arr1[0]);
                    oil.setOilNum(arr1[1]);
                    Month_lsit.add(oil);
                }
                month_bar.setVisibility(View.INVISIBLE);
                setMonthLicheng();
                //季度
                setJiDuData();
            }
        }
    };
    private void  setMonthLicheng(){



        Float[] yl = new Float[Month_lsit.size()];//数据用油量
        String[] appName = new String[Month_lsit.size()];//x轴时间

        float start = 0f;
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        Monthlicheng.getXAxis().setAxisMinValue(start);

        List<Integer> list = new ArrayList<Integer>();//装载柱状图颜色


        for(int i =0;i<Month_lsit.size();i++){
            list.add(ColorTemplate.SINGLE_COLORS[0]);
        }

        for (int i = 0; i < Month_lsit.size(); i++) {

            yl[i] = Float.parseFloat(Month_lsit.get(i).getOilNum());//雨量值
            appName[i] = Month_lsit.get(i).getTime();//x轴数值
            yVals1.add(new BarEntry(yl[i],i));
        }

        Monthlicheng.setDrawBarShadow(false);
        Monthlicheng.setDrawValueAboveBar(true);
        Monthlicheng.setDescription("");
        Monthlicheng.setMaxVisibleValueCount(60);
        Monthlicheng.setPinchZoom(false);
        Monthlicheng.setDrawGridBackground(false);
        // 是否可以缩放
        Monthlicheng.setScaleEnabled(true);
        // 集双指缩放
        Monthlicheng.setPinchZoom(false);
        // 隐藏右边的坐标轴
        Monthlicheng.getAxisRight().setEnabled(false);
        //动画
        Monthlicheng.animateY(1000);

        //X轴
        XAxis xAxis = Monthlicheng.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(8);
        xAxis.setTextColor(Color.rgb(103,175,205));//设置x轴数据颜色
        xAxis.setAxisMinValue(-0.5f); //设置x轴坐标起始值为-0.5 防止其实条形图被切去一半
        //Y轴
        YAxis leftAxis = Monthlicheng.getAxisLeft();
        leftAxis.setTextColor(Color.rgb(103, 175, 205));//设置y轴数据颜色
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

        Legend mLegend = Monthlicheng.getLegend(); //设置比例图
        mLegend.setEnabled(false);//不绘制图例
            /*Legend l = Monthlicheng.getLegend();//设置比例图
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setForm(Legend.LegendForm.CIRCLE);
            l.setFormSize(8f);
            l.setTextSize(8f);
            l.setXEntrySpace(4f);
            l.setCustom(myColors, lbls);*/

        BarDataSet set1;
        //判断图表中原来是否有数据
        if (Monthlicheng.getData() != null && Monthlicheng.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) Monthlicheng.getData().getDataSetByIndex(0);
            //set1 = new BarDataSet(yVals1, "");
            set1.setYVals(yVals1);
            Monthlicheng.getData().notifyDataChanged();
            Monthlicheng.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "");
            //set1.setColors(ColorTemplate.MATERIAL_COLORS);//设置柱状图的各种颜色，
            set1.setBarSpacePercent(50f);//设置柱间空白的宽度
            set1.setColors(list);//设置柱状图的各种颜色
            //set1.setBarSpacePercent(10);
            // set custom labels and colors

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            //BarData data = new BarData(dataSets);
            BarData data = new BarData(appName, dataSets);
            data.setValueTextSize(7f);
            data.setDrawValues(true);
            //data.setValueTextColor(Color.BLACK);
            data.setValueTextColor(Color.rgb(255, 255, 255));//设置数据颜色
            //data.setValueFormatter(new CustomerValueFormatter());
            //data.setValueTypeface(mTfLight);
            //data.setBarWidth(0.8f);
            //mChart.setExtraOffsets(0,0,0,200);//此种方法可以一次设置上下左右偏移量。根据自己数据哪个地方显示不全，对应调用方法。
            Monthlicheng.setData(data);// 设置数据
            Monthlicheng.setVisibleXRangeMaximum(10);//设置x坐标轴最多显示多少个数据，超出的部分需要滑动才能看到
        }
    }
/*
    //季度总好油量
    Runnable jiduLiCheng = new Runnable() {
        @Override
        public void run() {
            try{
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Get_QuarterMainTongJi";
                // EndPoint
                String endPoint =Path.get_ZanShibeidouPath();
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_QuarterMainTongJi";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数日期
                *//*String data = water_year.getText().toString()+"-"+water_Month.getText().toString()+"-"+water_day.getText().toString();
                rpc.addProperty("",data);*//*
                rpc.addProperty("time",CurrentTime);
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
                    jiduhanlder.sendMessage(msg);
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
                    jidu_bar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(), "暂无本季度里程信息数据", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(count1>0) {
                    StringBuffer sb = new StringBuffer();
                    for(int i=0;i<count1;i++) {
                        Log.e("warn", "-----------------------------");
                        SoapObject soapProvince = (SoapObject) object.getProperty(i);

                        Log.e("warn", soapProvince.getProperty("Quarter").toString() + ":");
                        sb.append(soapProvince.getProperty("Quarter").toString() + ",");

                        Log.e("warn", soapProvince.getProperty("Main").toString() + ":");
                        if (i == count1 - 1) {
                            sb.append(soapProvince.getProperty("Main").toString());
                        } else {
                            sb.append(soapProvince.getProperty("Main").toString() + "|");
                        }
                    }
                    Message msg = Message.obtain();
                    msg.what=1;
                    msg.obj=sb.toString();
                    jiduhanlder.sendMessage(msg);
                }
            } catch (Exception e){
                Message msg = Message.obtain();
                msg.what=0;
                jiduhanlder.sendMessage(msg);
            }
        }
    };
    private List<UserOilTongJi> jidu_lsit;
    private Handler jiduhanlder =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if(i==0){
                //加载dialog小时
                jidu_bar.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(), "获取本季度里程信息失败", Toast.LENGTH_SHORT).show();
            }
            if(i==1){ //获取顶部数据
                String str = (String) msg.obj;
                Log.e("warn", "water:" + str);
                jidu_lsit = new ArrayList<>();
                String arr[] = str.split("\\|");
                for (int j = 0; j < arr.length; j++) {
                    UserOilTongJi oil = new UserOilTongJi();
                    String arr1[] = arr[j].split(",");
                    oil.setTime(arr1[0]);
                    oil.setOilNum(arr1[1]);
                    jidu_lsit.add(oil);
                }
                jidu_bar.setVisibility(View.INVISIBLE);
                setjiduLicheng();
            }
        }
    };*/
    private void  setjiduLicheng(){


        Float[] yl = new Float[jidu_lsit.size()];//数据用油量
        String[] appName = new String[jidu_lsit.size()];//x轴时间

        float start = 0f;
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        Jilicheng.getXAxis().setAxisMinValue(start);

        List<Integer> list = new ArrayList<Integer>();//装载柱状图颜色


        for(int i =0;i<jidu_lsit.size();i++){
            list.add(ColorTemplate.SINGLE_COLORS[0]);
        }

        for (int i = 0; i < jidu_lsit.size(); i++) {

            yl[i] = Float.parseFloat(jidu_lsit.get(i).getOilNum());//雨量值
            appName[i] =jidu_lsit.get(i).getTime();//x轴数值
            yVals1.add(new BarEntry(yl[i],i));
        }

        Jilicheng.setDrawBarShadow(false);
        Jilicheng.setDrawValueAboveBar(true);
        Jilicheng.setDescription("");
        Jilicheng.setMaxVisibleValueCount(60);
        Jilicheng.setPinchZoom(false);
        Jilicheng.setDrawGridBackground(false);
        // 是否可以缩放
        Jilicheng.setScaleEnabled(true);
        // 集双指缩放
        Jilicheng.setPinchZoom(false);
        // 隐藏右边的坐标轴
        Jilicheng.getAxisRight().setEnabled(false);
        //动画
        Jilicheng.animateY(1000);

        //X轴
        XAxis xAxis = Jilicheng.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(8);
        xAxis.setTextColor(Color.rgb(103,175,205));//设置x轴数据颜色
        xAxis.setAxisMinValue(-0.5f); //设置x轴坐标起始值为-0.5 防止其实条形图被切去一半
        //Y轴
        YAxis leftAxis = Jilicheng.getAxisLeft();
        leftAxis.setTextColor(Color.rgb(103, 175, 205));//设置y轴数据颜色
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)


        Legend mLegend = Jilicheng.getLegend(); //设置比例图
        mLegend.setEnabled(false);//不绘制图例

            /*Legend l = Jilicheng.getLegend();//设置比例图
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setForm(Legend.LegendForm.CIRCLE);
            l.setFormSize(8f);
            l.setTextSize(8f);
            l.setXEntrySpace(4f);
            l.setCustom(myColors, lbls);*/

        BarDataSet set1;
        //判断图表中原来是否有数据
        if (Jilicheng.getData() != null && Jilicheng.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) Jilicheng.getData().getDataSetByIndex(0);
            //set1 = new BarDataSet(yVals1, "");
            set1.setYVals(yVals1);
            Jilicheng.getData().notifyDataChanged();
            Jilicheng.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "");
            //set1.setColors(ColorTemplate.MATERIAL_COLORS);//设置柱状图的各种颜色，
            set1.setBarSpacePercent(50f);//设置柱间空白的宽度
            set1.setColors(list);//设置柱状图的各种颜色
            //set1.setBarSpacePercent(10);
            // set custom labels and colors
            //设置柱状图颜色，第一个color.rgb为第一个柱状图颜色。以此类推
            //set1.setColors(new int[]{Color.rgb(255,241,226),Color.rgb(155,241,226)});
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            //BarData data = new BarData(dataSets);
            BarData data = new BarData(appName, dataSets);
            data.setValueTextSize(7f);
            data.setDrawValues(true);
            //data.setValueTextColor(Color.BLACK);
            data.setValueTextColor(Color.rgb(255, 255, 255));//设置数据颜色
            //data.setValueFormatter(new CustomerValueFormatter());
            //data.setValueTypeface(mTfLight);
            //data.setBarWidth(0.8f);
            //mChart.setExtraOffsets(0,0,0,200);//此种方法可以一次设置上下左右偏移量。根据自己数据哪个地方显示不全，对应调用方法。
            Jilicheng.setData(data);// 设置数据
            Jilicheng.setVisibleXRangeMaximum(10);//设置x坐标轴最多显示多少个数据，超出的部分需要滑动才能看到
        }
    }
    private List<UserOilTongJi> jidu_lsit;
    private void setJiDuData(){

        jidu_lsit = new ArrayList<>();
        UserOilTongJi uo = new UserOilTongJi();
        uo.setTime("一季度");
        float one = Float.valueOf(Month_lsit.get(0).getOilNum())+ Float.valueOf(Month_lsit.get(1).getOilNum())+ Float.valueOf(Month_lsit.get(2).getOilNum());
        uo.setOilNum(one+"");
        jidu_lsit.add(uo);
        UserOilTongJi uo1 = new UserOilTongJi();
        uo1.setTime("二季度");
        float two = Float.valueOf(Month_lsit.get(3).getOilNum())+ Float.valueOf(Month_lsit.get(4).getOilNum())+ Float.valueOf(Month_lsit.get(5).getOilNum());
        uo1.setOilNum(two+"");
        jidu_lsit.add(uo1);
        UserOilTongJi uo2 = new UserOilTongJi();
        uo2.setTime("三季度");
        float three = Float.valueOf(Month_lsit.get(6).getOilNum())+ Float.valueOf(Month_lsit.get(7).getOilNum())+ Float.valueOf(Month_lsit.get(8).getOilNum());
        uo2.setOilNum(three+"");
        jidu_lsit.add(uo2);
        UserOilTongJi uo3= new UserOilTongJi();
        uo3.setTime("四季度");
        float four = Float.valueOf(Month_lsit.get(9).getOilNum())+ Float.valueOf(Month_lsit.get(10).getOilNum())+ Float.valueOf(Month_lsit.get(11).getOilNum());
        uo3.setOilNum(four+"");
        jidu_lsit.add(uo3);
        jidu_bar.setVisibility(View.INVISIBLE);
        setjiduLicheng();


    }
}
