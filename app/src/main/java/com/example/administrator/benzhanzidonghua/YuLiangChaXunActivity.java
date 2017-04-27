package com.example.administrator.benzhanzidonghua;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.vanpeng.javabeen.StringTemplate;
import com.vanpeng.javabeen.YuliangChaXunbeen;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/1/6.
 * 历史雨量查询activity
 */

public class YuLiangChaXunActivity extends AppCompatActivity {
    private BarChart barChart;
    private MyProgressDialog progressDialog;
    private MyProgressDialog progressDialog1;
    private String CurrentTime;
    private String StringNewTime;
    private String agoTime24;
    private List<YuliangChaXunbeen> list_data = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yuliangchaxunactivity_layout);
        init();
    }
    private void init(){
        Button LSCX_button = (Button)findViewById(R.id.LSCX_button);
        LSCX_button.setOnClickListener(new YuLiangChaXunListener());
        barChart=(BarChart)findViewById(R.id.HBchart1);
        Intent intent =getIntent();
        String ID = intent.getStringExtra("ID");
        Log.e("warn","泵站ID："+ID);
        String panduan = intent.getStringExtra("panduan");







        if(panduan.equals("00")){//只通过ID获取数据 即24小时之内数据
            RequestDataMethod(ID);
        }else{
            String StartTime=intent.getStringExtra("StartTime");
            String EndTime=intent.getStringExtra("EndTime");
            RequestMethod(ID,StartTime,EndTime);
        }
    }
    private class YuLiangChaXunListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.LSCX_button:finish();break;
            }
        }
    }
    //只通过ID获取数据 即24小时之内数据
    private void RequestDataMethod(final String id){

        Date date = new Date();
        long newTime=date.getTime();
        Date datenewTime = new Date(newTime);

        long Time24 = newTime-24*60*60*1000;
        Date dateTime24  = new Date(Time24);


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        StringNewTime = formatter.format(datenewTime);;
        Log.e("warn",StringNewTime);

        agoTime24 = formatter.format(dateTime24 );
        Log.e("warn",agoTime24);


        progressDialog = new MyProgressDialog(YuLiangChaXunActivity.this,false,"数据加载中");
        if(id.equals("")||id==null){
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "查找的条件不足", Toast.LENGTH_SHORT).show();
            return;
        }else{
            new Thread(){
                @Override
                public void run() {
                    super.run();

                    try{
                        // 命名空间
                        String nameSpace = "http://tempuri.org/";
                        // 调用的方法名称
                        String methodName = "Get_CheckRainFallHistory_List";
                        // EndPoint
                        String endPoint =Path.get_ZanShibeidouPath();
                        // SOAP Action
                        String soapAction = "http://tempuri.org/Get_CheckRainFallHistory_List";
                        // 指定WebService的命名空间和调用的方法名
                        SoapObject rpc = new SoapObject(nameSpace, methodName);
                        //设置需调用WebService接口需要传入的参数日期
                /*String data = water_year.getText().toString()+"-"+water_Month.getText().toString()+"-"+water_day.getText().toString();
                rpc.addProperty("",data);*/
                        rpc.addProperty("id",id);
                        rpc.addProperty("startTime",agoTime24);
                        rpc.addProperty("endTime",StringNewTime);
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
                            Hour24handler.sendMessage(msg);
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
                            Toast.makeText(YuLiangChaXunActivity.this, "暂无数据", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(count1>0) {
                            StringBuffer sb = new StringBuffer();
                            for(int i=0;i<count1;i++) {
                                Log.e("warn", "-----------------------------");
                                SoapObject soapProvince = (SoapObject) object.getProperty(i);
                                YuliangChaXunbeen yl = new YuliangChaXunbeen();
                                if(soapProvince.getProperty("ValueX").toString().equals("0.00")||soapProvince.getProperty("ValueX").toString().equals("0.0")||soapProvince.getProperty("ValueX").toString().equals("0")) {
                                }else{
                                    Log.e("warn", soapProvince.getProperty("TIME").toString() + ":");
                                    yl.setTIME(soapProvince.getProperty("TIME").toString());
                                    Log.e("warn", soapProvince.getProperty("ValueX").toString() + ":");
                                    yl.setValueX(soapProvince.getProperty("ValueX").toString());
                                    list_data.add(yl);
                                }
                            }
                            Message msg = Message.obtain();
                            msg.what=1;
                            msg.obj=sb.toString();
                            Hour24handler.sendMessage(msg);
                        }
                    } catch (Exception e){
                        Message msg = Message.obtain();
                        msg.what=0;
                        Hour24handler.sendMessage(msg);
                    }

                }
            }.start();
        }

    }
    Handler Hour24handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String val=(String) msg.obj;
            //Log.e("warn",val);
            if(val==null){
                Toast.makeText(getApplicationContext(),"无历史信息",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }else {
                progressDialog.dismiss();


                Float[] yl = new Float[list_data.size()];//雨量
                String[] Time = new String[list_data.size()];//时间
                List<Integer> list = new ArrayList<Integer>();//装载柱状图颜色
                ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();//柱形图数值

               int len = StringTemplate.YLStringTemplate.length+1;//数据长度

                int[] myColors = new int[len];//取颜色长度
                String[] lbls = new String[len];//取比量值长度

                for (int i = 0; i < len; i++) {//示例数据
                    if(i==len-1){
                        lbls[i] = "警戒线";//示例颜色描述说明
                        myColors[i] =Color.rgb(255,33,33);//示例颜色
                    }else{
                        lbls[i] = StringTemplate.YLStringTemplate[i];//示例颜色描述说明
                        myColors[i] = ColorTemplate.YL_Simaple[i];//示例颜色
                    }

                }

                for (int i = 0; i < list_data.size(); i++) {
                    if (list_data.size() > 0) {

                                Time[i] = list_data.get(i).getTIME();
                                yVals1.add(new BarEntry(Float.parseFloat(list_data.get(i).getValueX()), i));//Float.parseFloat(arr[1]
                                ColorMethod(Float.parseFloat(list_data.get(i).getValueX()),list);//
                    }
                }
                if(list_data.size()==0){//无雨量信息 结束此方法
                    Toast.makeText(YuLiangChaXunActivity.this, "无雨量信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                barChart.setDrawBarShadow(false);
                barChart.setDrawValueAboveBar(true);
                barChart.setDescription("");
                barChart.setMaxVisibleValueCount(60);
                barChart.setPinchZoom(false);
                barChart.setDrawGridBackground(false);
                // 是否可以缩放
                barChart.setScaleEnabled(true);
                // 集双指缩放
                barChart.setPinchZoom(false);
                // 隐藏右边的坐标轴
                barChart.getAxisRight().setEnabled(false);
                //动画
                barChart.animateY(2500);
                barChart.animateX(2500);
                //X轴
                XAxis xAxis = barChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                //xAxis.setTypeface(mTfLight);
                xAxis.setDrawGridLines(false);
                xAxis.setSpaceBetweenLabels(1);
                xAxis.setDrawLabels(true);//是否显示X轴数值
                //xAxis.setLabelRotationAngle(-90);

                YAxis leftAxis = barChart.getAxisLeft();
                //leftAxis.setTypeface(mTfLight);
                leftAxis.setLabelCount(5, false);//设置y轴数据个数
                leftAxis.setAxisMaxValue(100f);
                //leftAxis.setLabelCount(8, false);
                //leftAxis.setValueFormatter(custom);
                leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                leftAxis.setSpaceTop(15f);
                leftAxis.setAxisMinValue(0f);


                //可以设置一条警戒线，如下：
                LimitLine ll = new LimitLine(60, "");//第一个参数为警戒线在坐标轴的位置，第二个参数为警戒线描述
                ll.setLineColor(Color.rgb(255,33,33));
                ll.setLineWidth(1f);
                ll.setTextColor(Color.GRAY);
                ll.setTextSize(12f);
                // .. and more styling options
                leftAxis.addLimitLine(ll);



                Legend l = barChart.getLegend();//设置比例图
                l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
                l.setForm(Legend.LegendForm.CIRCLE);
                l.setFormSize(8f);
                l.setTextSize(8f);
                l.setXEntrySpace(4f);
                l.setCustom(myColors, lbls);


                BarDataSet set1;
                //判断图表中原来是否有数据
                if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
                    set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
                    //set1 = new BarDataSet(yVals1,"");
                    set1.setYVals(yVals1);//设置图表柱形图数值
                    barChart.getData().notifyDataChanged();
                    barChart.notifyDataSetChanged();

                } else {
                    set1 = new BarDataSet(yVals1, "雨量监测");
                    set1.setColors(list);//设置柱状图的各种颜色，上面已设置ColorTemplate.MATERIAL_COLORS的值
                    set1.setBarSpacePercent(30f);//设置柱间空白的宽度
                    //set1.setBarSpacePercent(10);
                    // set custom labels and colors
                    //设置柱状图颜色，第一个color.rgb为第一个柱状图颜色。以此类推
                    //set1.setColors(new int[]{Color.rgb(255,241,226),Color.rgb(155,241,226)});
                    ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                    dataSets.add(set1);

                    //BarData data = new BarData(dataSets);
                    BarData data = new BarData(Time, dataSets);
                    data.setValueTextSize(7f);
                    data.setDrawValues(true);
                    data.setValueTextColor(Color.BLACK);
                    //data.setValueFormatter(new CustomerValueFormatter());
                    //data.setValueTypeface(mTfLight);
                    //data.setBarWidth(0.8f);
                    //mChart.setExtraOffsets(0,0,0,200);//此种方法可以一次设置上下左右偏移量。根据自己数据哪个地方显示不全，对应调用方法。
                    barChart.setData(data);// 设置数据
                }
            }
        }
    };




    //自由时间查询
    private void RequestMethod(final String id,final String StartTime,final String EndTime) {
        progressDialog = new MyProgressDialog(YuLiangChaXunActivity.this,false,"数据加载中");
        if(id.equals("")||id==null||StartTime.equals("")||EndTime.equals("")){
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "查找的条件不足", Toast.LENGTH_SHORT).show();
            return;
        } else{
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try{
                        // 命名空间
                        String nameSpace = "http://tempuri.org/";
                        // 调用的方法名称
                        String methodName = "Get_CheckRainFallHistory_List";
                        // EndPoint
                        String endPoint =Path.get_ZanShibeidouPath();
                        // SOAP Action
                        String soapAction = "http://tempuri.org/Get_CheckRainFallHistory_List";
                        // 指定WebService的命名空间和调用的方法名
                        SoapObject rpc = new SoapObject(nameSpace, methodName);
                        //设置需调用WebService接口需要传入的参数日期
                /*String data = water_year.getText().toString()+"-"+water_Month.getText().toString()+"-"+water_day.getText().toString();
                rpc.addProperty("",data);*/
                        rpc.addProperty("id",id);
                        rpc.addProperty("startTime",StartTime);
                        rpc.addProperty("endTime",EndTime);
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
                            Hour24handler.sendMessage(msg);
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
                            progressDialog.dismiss();
                            Toast.makeText(YuLiangChaXunActivity.this, "暂无数据", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(count1>0) {
                            StringBuffer sb = new StringBuffer();
                            for(int i=0;i<count1;i++) {
                                Log.e("warn", "-----------------------------");
                                SoapObject soapProvince = (SoapObject) object.getProperty(i);
                                YuliangChaXunbeen yl = new YuliangChaXunbeen();
                                if(soapProvince.getProperty("ValueX").toString().equals("0.00")||soapProvince.getProperty("ValueX").toString().equals("0.0")||soapProvince.getProperty("ValueX").toString().equals("0")) {
                                }else{
                                    Log.e("warn", soapProvince.getProperty("TIME").toString() + ":");
                                    yl.setTIME(soapProvince.getProperty("TIME").toString());
                                    Log.e("warn", soapProvince.getProperty("ValueX").toString() + ":");
                                    yl.setValueX(soapProvince.getProperty("ValueX").toString());
                                    list_data.add(yl);
                                }
                            }
                            Message msg = Message.obtain();
                            msg.what=1;
                            msg.obj=sb.toString();
                            Hour24handler.sendMessage(msg);
                        }
                    } catch (Exception e){
                        Message msg = Message.obtain();
                        msg.what=0;
                        Hour24handler.sendMessage(msg);
                    }
                }
            }.start();
        }


    }
    private void ColorMethod(float y,List<Integer> list){
        if (y > 300) {
            list.add(getResources().getColor(R.color.yl17));
        } else if (y >= 200) {
            list.add(getResources().getColor(R.color.yl16));
        } else if (y >= 150) {
            list.add(getResources().getColor(R.color.yl15));
        } else if (y >= 130) {
            list.add(getResources().getColor(R.color.yl14));
        } else if (y >= 110) {
            list.add(getResources().getColor(R.color.yl13));
        } else if (y >= 90) {
            list.add(getResources().getColor(R.color.yl12));
        } else if (y >= 70) {
            list.add(getResources().getColor(R.color.yl11));
        } else if (y >= 50) {
            list.add(getResources().getColor(R.color.yl10));
        } else if (y >= 40) {
            list.add(getResources().getColor(R.color.yl09));
        } else if (y >= 30) {
            list.add(getResources().getColor(R.color.yl08));
        } else if (y >= 20) {
            list.add(getResources().getColor(R.color.yl07));
        } else if (y >= 15) {
            list.add(getResources().getColor(R.color.yl06));
        } else if (y >= 10) {
            list.add(getResources().getColor(R.color.yl05));
        } else if (y >= 6) {
            list.add(getResources().getColor(R.color.yl04));
        } else if (y >= 2) {
            list.add(getResources().getColor(R.color.yl03));
        } else if (y >= 1) {
            list.add(getResources().getColor(R.color.yl02));
        }else if(y>=0){
            list.add(getResources().getColor(R.color.yl01));
        }
    }
}
