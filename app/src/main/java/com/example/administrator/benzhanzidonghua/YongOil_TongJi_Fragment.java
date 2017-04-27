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
 * 用油统计
 */

public class YongOil_TongJi_Fragment extends Fragment {
    private View view;
    private BarChart monthHaoOil;
    private  BarChart jiduHaoOil;
    private BarChart yearHaoOil ;
    private ProgressBar Oil_month_bar;
    private ProgressBar Oil_jidu_bar;
    private ProgressBar Oil_year_bar;
    private String CurrentTime = "";
    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflater = LayoutInflater.from(getActivity());
        view = inflater.inflate(R.layout.yongyou_tongjifragment_layout, container, false);
        init();
        return view;
    }
    private void init(){
        monthHaoOil  = (BarChart)view.findViewById(R.id.dayHaoOil);
        monthHaoOil.setNoDataText("");//更改图表无数据时，图表上显示的内容(no chart data available)
        jiduHaoOil  = (BarChart)view.findViewById(R.id.MonthHaoOil);
        jiduHaoOil.setNoDataText("");//更改图表无数据时，图表上显示的内容(no chart data available)
        yearHaoOil  = (BarChart)view.findViewById(R.id.JiHaoOil);
        yearHaoOil.setNoDataText("");//更改图表无数据时，图表上显示的内容(no chart data available)

        Oil_month_bar = (ProgressBar)view.findViewById(R.id.Oil_month_bar);
        Oil_jidu_bar = (ProgressBar)view.findViewById(R.id.Oil_jidu_bar);
        Oil_year_bar = (ProgressBar)view.findViewById(R.id.Oil_year_bar);

        Calendar c = Calendar.getInstance();
        String  year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf(c.get(Calendar.MONTH)+1);
        String  day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        CurrentTime = year+"-"+month+"-"+day;
        fixedThreadPool.execute(MonthUseOil);//月总用油量
        //fixedThreadPool.execute(jiduUseOil);//季度总用油量
        fixedThreadPool.execute(yearUseOil);//季度总用油量

    }
    Runnable MonthUseOil = new Runnable() {
        @Override
        public void run() {
            try{
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Get_MonthOilTongJi";
                // EndPoint
                String endPoint =Path.get_ZanShibeidouPath();
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_MonthOilTongJi";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数日期
                /*String data = water_year.getText().toString()+"-"+water_Month.getText().toString()+"-"+water_day.getText().toString();
                rpc.addProperty("",data);*/
                rpc.addProperty("time",CurrentTime);
                Log.e("warn",CurrentTime);
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

                        Log.e("warn", soapProvince.getProperty("Month").toString() + ":");
                        sb.append(soapProvince.getProperty("Month").toString() + ",");

                        Log.e("warn", soapProvince.getProperty("Oil").toString() + ":");
                        if (i == count1 - 1) {
                            sb.append(soapProvince.getProperty("Oil").toString());
                        } else {
                            sb.append(soapProvince.getProperty("Oil").toString() + "|");
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
    private List<UserOilTongJi> month_lsit;
    private Handler hanlder =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if(i==0){
                //加载dialog小时
                Oil_jidu_bar.setVisibility(View.INVISIBLE);
                Oil_month_bar.setVisibility(View.INVISIBLE);
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "获取月季用油信息失败", Toast.LENGTH_SHORT).show();
                }
            }
            if(i==2){
                Oil_jidu_bar.setVisibility(View.INVISIBLE);
                Oil_month_bar.setVisibility(View.INVISIBLE);
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "无月季用油信息", Toast.LENGTH_SHORT).show();
                }
            }
            if(i==1){ //获取顶部数据
                String str = (String) msg.obj;
                Log.e("warn", "water:" + str);
                month_lsit = new ArrayList<>();
                String arr[] = str.split("\\|");
                for (int j = 0; j < arr.length; j++) {
                    UserOilTongJi oil = new UserOilTongJi();
                    String arr1[] = arr[j].split(",");
                    oil.setTime(arr1[0]);
                    oil.setOilNum(arr1[1]);
                    month_lsit.add(oil);
                }

                //设置季度数据
                JiDu_lsit = new ArrayList<>();//季度集合


                Oil_month_bar.setVisibility(View.INVISIBLE);
                setMethodUseOil();

                setJiDuData();
        }
        }
    };
    private void setMethodUseOil(){

        //monthHaoOil.setExtraLeftOffset(-30);;
        Float[] yl = new Float[month_lsit.size()];//数据用油量
        String[] appName = new String[month_lsit.size()];//x轴时间

        float start = 0f;
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        monthHaoOil.getXAxis().setAxisMinValue(start);

        List<Integer> list = new ArrayList<Integer>();//装载柱状图颜色


        for(int i =0;i<month_lsit.size();i++){
            list.add(ColorTemplate.SINGLE_COLORS[0]);
        }

        for (int i = 0; i < month_lsit.size(); i++) {

            yl[i] = Float.parseFloat(month_lsit.get(i).getOilNum());//油量值
            appName[i] = month_lsit.get(i).getTime();//x轴数值
            yVals1.add(new BarEntry(yl[i],i));
        }
        //monthHaoOil.setExtraOffsets(20,20,20,20);
        monthHaoOil.setDrawBarShadow(false);
        monthHaoOil.setDrawValueAboveBar(true);
        monthHaoOil.setDescription("");
        // 如果60多个条目显示在图表,数据不显示值
        monthHaoOil.setMaxVisibleValueCount(60);
        monthHaoOil.setPinchZoom(false);
        monthHaoOil.setDrawGridBackground(false);
        // 是否可以缩放
        monthHaoOil.setScaleEnabled(true);
        // 集双指缩放
        monthHaoOil.setPinchZoom(false);
        // 隐藏右边的坐标轴
        monthHaoOil.getAxisRight().setEnabled(false);
        //动画
        monthHaoOil.animateY(1000);

        //X轴
        XAxis xAxis = monthHaoOil.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(8);
        xAxis.setTextColor(Color.rgb(103,175,205));//设置x轴数据颜色
        xAxis.setAxisMinValue(-0.5f); //设置x轴坐标起始值为-0.5 防止其实条形图被切去一半
        //Y轴
        YAxis leftAxis = monthHaoOil.getAxisLeft();
        leftAxis.setTextColor(Color.rgb(103, 175, 205));//设置y轴数据颜色
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

        Legend mLegend =monthHaoOil.getLegend(); //设置比例图
        mLegend.setEnabled(false);//不绘制图例
          /* Legend l = monthHaoOil.getLegend().;//设置比例图
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setForm(Legend.LegendForm.CIRCLE);
            l.setFormSize(8f);
            l.setTextSize(8f);
            l.setXEntrySpace(4f);
            l.setCustom(myColors, lbls);*/

        BarDataSet set1;
        //判断图表中原来是否有数据
        if (monthHaoOil.getData() != null && monthHaoOil.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) monthHaoOil.getData().getDataSetByIndex(0);
            //set1 = new BarDataSet(yVals1, "");
            set1.setYVals(yVals1);
            monthHaoOil.getData().notifyDataChanged();
            monthHaoOil.notifyDataSetChanged();
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
            monthHaoOil.setData(data);// 设置数据
            monthHaoOil.setVisibleXRangeMaximum(10);//设置x坐标轴最多显示多少个数据，超出的部分需要滑动才能看到
            //monthHaoOil.setExtraLeftOffset(-30);;
        }
    }


/*    //季度总好油量
    Runnable jiduUseOil = new Runnable() {
        @Override
        public void run() {
            try{
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Get_QuarterOilTongJi";
                // EndPoint
                String endPoint =Path.get_ZanShibeidouPath();
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_QuarterOilTongJi";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数日期
                *//*String data = water_year.getText().toString()+"-"+water_Month.getText().toString()+"-"+water_day.getText().toString();
                rpc.addProperty("",data);*//*
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
                    JIduhanlder.sendMessage(msg);
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
                    Oil_jidu_bar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(), "暂无本季度用油信息数据", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(count1>0) {
                    StringBuffer sb = new StringBuffer();
                    for(int i=0;i<count1;i++) {
                        Log.e("warn", "-----------------------------");
                        SoapObject soapProvince = (SoapObject) object.getProperty(i);

                        Log.e("warn", soapProvince.getProperty("Quarter").toString() + ":");
                        sb.append(soapProvince.getProperty("Quarter").toString() + ",");

                        Log.e("warn", soapProvince.getProperty("Oil").toString() + ":");
                        if (i == count1 - 1) {
                            sb.append(soapProvince.getProperty("Oil").toString());
                        } else {
                            sb.append(soapProvince.getProperty("Oil").toString() + "|");
                        }
                    }
                    Message msg = Message.obtain();
                    msg.what=1;
                    msg.obj=sb.toString();
                    JIduhanlder.sendMessage(msg);
                }
            } catch (Exception e){
                Message msg = Message.obtain();
                msg.what=0;
                JIduhanlder.sendMessage(msg);
            }
        }
    };
    private List<UserOilTongJi> JiDu_lsit;
    private Handler JIduhanlder =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if(i==0){
                //加载dialog小时
                Oil_jidu_bar.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(), "获取本季度用油信息失败失败", Toast.LENGTH_SHORT).show();
            }
            if(i==1){ //获取顶部数据
                String str = (String) msg.obj;
                Log.e("warn", "water:" + str);
                JiDu_lsit = new ArrayList<>();
                String arr[] = str.split("\\|");
                for (int j = 0; j < arr.length; j++) {
                    UserOilTongJi oil = new UserOilTongJi();
                    String arr1[] = arr[j].split(",");
                    oil.setTime(arr1[0]);
                    oil.setOilNum(arr1[1]);
                    month_lsit.add(oil);
                }
                setJiDuUseOil();
                Oil_jidu_bar.setVisibility(View.INVISIBLE);
            }
        }
    };*/
    private void  setJiDuUseOil(){

        Float[] yl = new Float[JiDu_lsit.size()];//数据用油量
        String[] appName = new String[JiDu_lsit.size()];//x轴时间

        float start = 0f;
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        jiduHaoOil.getXAxis().setAxisMinValue(start);

        List<Integer> list = new ArrayList<Integer>();//装载柱状图颜色


        for(int i =0;i<JiDu_lsit.size();i++){
            list.add(ColorTemplate.SINGLE_COLORS[0]);
        }

        for (int i = 0; i < JiDu_lsit.size(); i++) {

            yl[i] = Float.parseFloat(JiDu_lsit.get(i).getOilNum());//雨量值
            appName[i] = JiDu_lsit.get(i).getTime();//x轴数值
            yVals1.add(new BarEntry(yl[i],i));
        }

        jiduHaoOil.setDrawBarShadow(false);
        jiduHaoOil.setDrawValueAboveBar(true);
        jiduHaoOil.setDescription("");
        // 如果60多个条目显示在图表,数据不显示值
        jiduHaoOil.setMaxVisibleValueCount(60);
        jiduHaoOil.setPinchZoom(false);
        jiduHaoOil.setDrawGridBackground(false);
        // 是否可以缩放
        jiduHaoOil.setScaleEnabled(true);
        // 集双指缩放
        jiduHaoOil.setPinchZoom(false);
        // 隐藏右边的坐标轴
        jiduHaoOil.getAxisRight().setEnabled(false);
        //动画
        jiduHaoOil.animateY(1000);

        //X轴
        XAxis xAxis = jiduHaoOil.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(8);
        //xAxis.setLabelRotationAngle(-30);//x轴数据旋转角度
        xAxis.setTextColor(Color.rgb(103,175,205));//设置x轴数据颜色
        xAxis.setAxisMinValue(-0.5f); //设置x轴坐标起始值为-0.5 防止其实条形图被切去一半
        //Y轴
        YAxis leftAxis = jiduHaoOil.getAxisLeft();
        leftAxis.setTextColor(Color.rgb(103, 175, 205));//设置y轴数据颜色
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)


        Legend mLegend =jiduHaoOil.getLegend(); //设置比例图
        mLegend.setEnabled(false);//不绘制图例
            /*Legend l = jiduHaoOil.getLegend();//设置比例图
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setForm(Legend.LegendForm.CIRCLE);
            l.setFormSize(8f);
            l.setTextSize(8f);
            l.setXEntrySpace(4f);
            l.setCustom(myColors, lbls);*/

        BarDataSet set1;
        //判断图表中原来是否有数据
        if (jiduHaoOil.getData() != null && jiduHaoOil.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) jiduHaoOil.getData().getDataSetByIndex(0);
            //set1 = new BarDataSet(yVals1, "");
            set1.setYVals(yVals1);
            jiduHaoOil.getData().notifyDataChanged();
            jiduHaoOil.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "");
            //set1.setColors(ColorTemplate.MATERIAL_COLORS);//设置柱状图的各种颜色，
            set1.setBarSpacePercent(80f);//设置柱间空白的宽度
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
            jiduHaoOil.setData(data);// 设置数据
            jiduHaoOil.setVisibleXRangeMaximum(10);//设置x坐标轴最多显示多少个数据，超出的部分需要滑动才能看到
        }
    }

    //季度总好油量
    Runnable yearUseOil = new Runnable() {
        @Override
        public void run() {
            try{
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Get_YearOilTongJi";
                // EndPoint
                String endPoint =Path.get_ZanShibeidouPath();
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_YearOilTongJi";
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
                    yearhanlder.sendMessage(msg);
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
                    yearhanlder.sendMessage(msg);
                    return;
                }
                if(count1>0) {
                    StringBuffer sb = new StringBuffer();
                    for(int i=0;i<count1;i++) {
                        Log.e("warn", "-----------------------------");
                        SoapObject soapProvince = (SoapObject) object.getProperty(i);

                        Log.e("warn", soapProvince.getProperty("Year").toString() + ":");
                        sb.append(soapProvince.getProperty("Year").toString() + ",");

                        Log.e("warn", soapProvince.getProperty("Oil").toString() + ":");
                        if (i == count1 - 1) {
                            sb.append(soapProvince.getProperty("Oil").toString());
                        } else {
                            sb.append(soapProvince.getProperty("Oil").toString() + "|");
                        }
                    }
                    Message msg = Message.obtain();
                    msg.what=1;
                    msg.obj=sb.toString();
                    yearhanlder.sendMessage(msg);
                }
            } catch (Exception e){
                Message msg = Message.obtain();
                msg.what=0;
                yearhanlder.sendMessage(msg);
            }
        }
    };
    private List<UserOilTongJi> year_lsit;
    private Handler yearhanlder =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if(i==0){
                //加载dialog小时
                Oil_year_bar.setVisibility(View.INVISIBLE);
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "获取本年度用油信息失败", Toast.LENGTH_SHORT).show();
                }
            }
            if(i==2){
                Oil_year_bar.setVisibility(View.INVISIBLE);
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "无本年度用油信息", Toast.LENGTH_SHORT).show();
                }
            }
            if(i==1){ //获取顶部数据
                String str = (String) msg.obj;
                Log.e("warn", "water:" + str);
                year_lsit = new ArrayList<>();
                String arr[] = str.split("\\|");
                for (int j =arr.length-1; j >=0; j--) {
                    UserOilTongJi oil = new UserOilTongJi();
                    String arr1[] = arr[j].split(",");
                    oil.setTime(arr1[0]);
                    oil.setOilNum(arr1[1]);
                    year_lsit.add(oil);
                }
                Oil_year_bar.setVisibility(View.INVISIBLE);
                setyearUseOil();
            }
        }
    };
    private void  setyearUseOil(){

        Float[] yl = new Float[year_lsit.size()];//数据用油量
        String[] appName = new String[year_lsit.size()];//x轴时间

        float start = 0f;
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        yearHaoOil.getXAxis().setAxisMinValue(start);

        List<Integer> list = new ArrayList<Integer>();//装载柱状图颜色


        for(int i =0;i<year_lsit.size();i++){
            list.add(ColorTemplate.SINGLE_COLORS[0]);
        }

        for (int i = 0; i<year_lsit.size(); i++) {

            yl[i] = Float.parseFloat(year_lsit.get(i).getOilNum());//雨量值
            appName[i] = year_lsit.get(i).getTime();//x轴数值
            yVals1.add(new BarEntry(yl[i],i));
        }

        yearHaoOil.setDrawBarShadow(false);
        yearHaoOil.setDrawValueAboveBar(true);
        yearHaoOil.setDescription("");
        // 如果60多个条目显示在图表,数据不显示值
        yearHaoOil.setMaxVisibleValueCount(60);
        yearHaoOil.setPinchZoom(false);
        yearHaoOil.setDrawGridBackground(false);
        // 是否可以缩放
        yearHaoOil.setScaleEnabled(true);
        // 集双指缩放
        yearHaoOil.setPinchZoom(false);
        // 隐藏右边的坐标轴
        yearHaoOil.getAxisRight().setEnabled(false);
        //动画
        yearHaoOil.animateY(1000);

        //X轴
        XAxis xAxis = yearHaoOil.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(8);
        xAxis.setTextColor(Color.rgb(103,175,205));//设置x轴数据颜色
        //.setAxisMinimum(-0.5f); //设置x轴坐标起始值为-0.5
        xAxis.setAxisMinValue(-0.5f); //设置x轴坐标起始值为-0.5 防止其实条形图被切去一半
        //xAxis.isAvoidFirstLastClippingEnabled();
        //xAxis.setAvoidFirstLastClipping(true);//设置x轴起点和终点label不超出屏幕
        //Y轴
        YAxis leftAxis = yearHaoOil.getAxisLeft();
        //leftAxis.setStartAtZero(false);
        leftAxis.setTextColor(Color.rgb(103, 175, 205));//设置y轴数据颜色
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

        Legend mLegend =yearHaoOil.getLegend(); //设置比例图
        mLegend.setEnabled(false);//不绘制图例
            /*Legend l = yearHaoOil.getLegend();//设置比例图
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setForm(Legend.LegendForm.CIRCLE);
            l.setFormSize(8f);
            l.setTextSize(8f);
            l.setXEntrySpace(4f);
            l.setCustom(myColors, lbls);*/
        //yearHaoOil.setExtraOffsets(20,0, 20,0);//此种方法可以一次设置上下左右偏移量。根据自己数据哪个地方显示不全，对应调用方法。
        //左上右下
        //yearHaoOil.setAvoidFirstLastClipping(true);//:如果设置为true，图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘。
        BarDataSet set1;
        //判断图表中原来是否有数据
        if (yearHaoOil.getData() != null && yearHaoOil.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) yearHaoOil.getData().getDataSetByIndex(0);
            //set1 = new BarDataSet(yVals1, "");
            set1.setYVals(yVals1);
            yearHaoOil.getData().notifyDataChanged();
            yearHaoOil.notifyDataSetChanged();
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
            yearHaoOil.setData(data);// 设置数据
            //yearHaoOil.setExtraOffsets(20,0, 20,0);//此种方法可以一次设置上下左右偏移量。根据自己数据哪个地方显示不全，对应调用方法。
            yearHaoOil.setVisibleXRangeMaximum(10);//设置x坐标轴最多显示多少个数据，超出的部分需要滑动才能看到
            //yearHaoOil.setExtraOffsets(20,0, 20,0);//此种方法可以一次设置上下左右偏移量。根据自己数据哪个地方显示不全，对应调用方法。
        }
    }
    private List<UserOilTongJi> JiDu_lsit;

    //季度数据
    private void setJiDuData(){
        JiDu_lsit = new ArrayList<>();
        UserOilTongJi uo = new UserOilTongJi();
        uo.setTime("一季度");
        float one = Float.valueOf(month_lsit.get(0).getOilNum())+ Float.valueOf(month_lsit.get(1).getOilNum())+ Float.valueOf(month_lsit.get(2).getOilNum());
        uo.setOilNum(one+"");
        JiDu_lsit.add(uo);
        UserOilTongJi uo1 = new UserOilTongJi();
        uo1.setTime("二季度");
        float two = Float.valueOf(month_lsit.get(3).getOilNum())+ Float.valueOf(month_lsit.get(4).getOilNum())+ Float.valueOf(month_lsit.get(5).getOilNum());
        uo1.setOilNum(two+"");
        JiDu_lsit.add(uo1);
        UserOilTongJi uo2 = new UserOilTongJi();
        uo2.setTime("三季度");
        float three = Float.valueOf(month_lsit.get(6).getOilNum())+ Float.valueOf(month_lsit.get(7).getOilNum())+ Float.valueOf(month_lsit.get(8).getOilNum());
        uo2.setOilNum(three+"");
        JiDu_lsit.add(uo2);
        UserOilTongJi uo3= new UserOilTongJi();
        uo3.setTime("四季度");
        float four = Float.valueOf(month_lsit.get(9).getOilNum())+ Float.valueOf(month_lsit.get(10).getOilNum())+ Float.valueOf(month_lsit.get(11).getOilNum());
        uo3.setOilNum(four+"");
        JiDu_lsit.add(uo3);
        Oil_jidu_bar.setVisibility(View.INVISIBLE);
        setJiDuUseOil();
    }


}
