package com.example.administrator.benzhanzidonghua;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.vanpeng.javabeen.StringTemplate;

import java.util.ArrayList;
import java.util.List;

import static android.os.Build.ID;

/**
 * Created by Administrator on 2017/1/6.
 * 历史雨量查询activity
 */

public class YuLiangChaXunActivity extends AppCompatActivity {
    private BarChart barChart;
    private MyProgressDialog progressDialog;
    private MyProgressDialog progressDialog1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.yuliangchaxunactivity_layout);
        init();
    }
    private void init(){
        Button LSCX_button = (Button)findViewById(R.id.LSCX_button);
        LSCX_button.setOnClickListener(new YuLiangChaXunListener());
        barChart=(BarChart)findViewById(R.id.HBchart1);
        Intent intent =getIntent();
        String ID = intent.getStringExtra("ID");
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
    private void RequestDataMethod(String id){
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
                    String methodName = "Get24HourJiangYuLiangTongJiResult";//方法名
                    String path="http://ysservices.sytxmap.com/JCDLXXGXPT.asmx";//请求地址
                    String SoapFileName = "assets/yuliang24lishichaxun.xml";//读取的soap协议xml文件名称
                    String soap = CommonMethod.ReadSoap(SoapFileName);
                    soap=soap.replaceAll("string1",ID);
                    Log.e("warn",soap);
                    byte [] date=soap.getBytes();//soap协议转为字符数组
                    String result=CommonMethod.Request(path,date,methodName);
                    Message msg = Message.obtain();
                    msg.obj=result;
                    Hour24handler.sendMessage(msg);
                }
            }.start();
        }

    }
    Handler Hour24handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String val=(String) msg.obj;
            Log.e("warn",val);
            if(val.equals("")){
                Toast.makeText(getApplicationContext(),"无历史信息",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }else {
                progressDialog.dismiss();
                int k=0;

                String[] objects = val.split("\\|");
                //计算时间数组的长度
                for(int a=0;a<objects.length;a++) {
                    String[] array = objects[a].split(",");
                    if (array[1].equals("0")) {
                        continue;
                    }else{
                        ++k;
                    }
                }
                Log.e("warn",String.valueOf(k)+":122");
                Float[] yl = new Float[objects.length];//雨量
                String[] Time = new String[k];//时间
                List<Integer> list = new ArrayList<Integer>();//装载柱状图颜色
                ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();//柱形图数值

                int len = StringTemplate.YLStringTemplate.length;//数据长度

                int[] myColors = new int[len];//取颜色长度
                String[] lbls = new String[len];//取比量值长度

                for (int i = 0; i < len; i++) {//示例数据
                    lbls[i] = StringTemplate.YLStringTemplate[i];//示例颜色描述说明
                    myColors[i] = ColorTemplate.YL_Simaple[i];//示例颜色
                }
                int j=0;
                int b=0;
                for (int i = 0; i < objects.length; i++) {
                    if (objects[i].length() > 0) {
                        String[] arr = objects[i].split(",");
                        if (arr.length > 1) {
                            if(arr[1].equals("0")){ j++;continue;}//如果雨量为0，不显示
                            else{
                                arr[0]=arr[0].substring(0,16);
                                Time[b] = arr[0];
                                yVals1.add(new BarEntry(Float.parseFloat(arr[1]), b));//Float.parseFloat(arr[1]
                                ColorMethod(Float.parseFloat(arr[1]),list);//
                                b++;
                            }
                        }
                    }
                }
                if(j==objects.length){//无雨量信息 结束此方法
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
                xAxis.setLabelRotationAngle(-90);
                YAxis leftAxis = barChart.getAxisLeft();
                //leftAxis.setTypeface(mTfLight);
                leftAxis.setLabelCount(4, false);//设置y轴数据个数
                //leftAxis.setLabelCount(8, false);
                //leftAxis.setValueFormatter(custom);
                leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                leftAxis.setSpaceTop(15f);
                leftAxis.setAxisMinValue(0f);

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
                    String methodName = "GetHisJiangYuLiangSearchResult";//方法名
                    String path="http://ysservices.sytxmap.com/JCDLXXGXPT.asmx";//请求地址
                    String SoapFileName = "assets/yulianglishichaxun.xml";//读取的soap协议xml文件名称
                    String soap = CommonMethod.ReadSoap(SoapFileName);
                    soap=soap.replaceAll("string1",StartTime);
                    soap=soap.replaceAll("string2",EndTime);
                    soap=soap.replaceAll("string3",id);
                    Log.e("warn",soap);
                    byte [] date=soap.getBytes();//soap协议转为字符数组
                    String result=CommonMethod.Request(path,date,methodName);
                    Message msg = Message.obtain();
                    msg.obj=result;
                    Hour24handler.sendMessage(msg);
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
