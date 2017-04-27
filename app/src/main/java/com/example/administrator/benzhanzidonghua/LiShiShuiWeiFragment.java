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
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.vanpeng.javabeen.ShuiWeiBaoJing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/5.
 * 历史雨量fragment
 * 注意：图表最好用相对布局
 */

public class LiShiShuiWeiFragment extends Fragment {
    private HorizontalBarChart  barChart;
    private MyProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflater=LayoutInflater.from(getActivity());
        View view=inflater.inflate(R.layout.jishuidianfragment_layout,container,false);
        barChart=(HorizontalBarChart) view.findViewById(R.id.lineChart);
        getBundleData();
        return view;
    }

    private void getBundleData(){
        Bundle bundle=getArguments();//获取传送过来的bundle
        String ID=bundle.getString("ID");
        String StartTime=bundle.getString("StartTime");
        String EndTime = bundle.getString("EndTime");
        RequestData(ID,StartTime,EndTime);
    }
   /* //设置图表数据
    private void setTuBiao(List<ShuiWeiBaoJing> list){
        lineChart.setDescription("");
        lineChart.setDrawGridBackground(false);
        lineChart.setPinchZoom(false);
        lineChart.setTouchEnabled(true);// 设置是否可以触摸
        lineChart.setDragEnabled(true);// 是否可以拖拽
        lineChart.setScaleEnabled(true);// 是否可以缩放
        //Typeface mTf = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "OpenSans-Regular.ttf");
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setLabelRotationAngle(-90);
        lineChart.getAxisRight().setEnabled(false);
        YAxis leftAxis =lineChart.getAxisLeft();
        leftAxis.setDrawAxisLine(true);//显示y轴
        //leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(4, false);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        Legend mLegend = lineChart.getLegend(); // 设置比例图标示，就是那个一组y的value的
        mLegend.setEnabled(false);
        // modify the legend ...
        //mLegend.setPosition(LegendPosition.LEFT_OF_CHART);
        *//*mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(6f);// 字体
        mLegend.setTextColor(Color.WHITE);// 颜色*//*

        lineChart.animateXY(2500,2500);
//y轴数据
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        for (int i = 0; i < list.size(); i++) {
            float val = Float.parseFloat(list.get(i).getShuiWei());//字符串转为浮点型
            Entry entry = new Entry(val, i);
            yVals1.add(entry);
        }
//x轴数据
        ArrayList<String> xValues = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            // x轴显示的数据
            xValues.add(list.get(i).getShiJian());
        }

       LineDataSet dataSet1; *//*= new LineDataSet(yVals1, "七星彩");//设置折线点数据
        dataSet1.setLineWidth(2.5f);//折线的宽
        dataSet1.setCircleSize(5.5f);//折线的圆点大小
        dataSet1.setHighLightColor(Color.rgb(244, 117, 117));*//*
        //dataSet1.setDrawValues(true);
        //判断图表中原来是否有数据
        if(lineChart.getData()!=null&&lineChart.getData().getDataSetCount()>0){
            dataSet1 =(LineDataSet)lineChart.getData().getDataSetByIndex(0);
            dataSet1.setYVals(yVals1);//设置图表折线点数据
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        }else {
            dataSet1= new LineDataSet(yVals1, "水位");//设置数据
            dataSet1.setLineWidth(2.5f);//折线的宽
            dataSet1.setCircleSize(2.5f);//折线的圆点大小
            dataSet1.setHighLightColor(Color.rgb(244, 117, 117));

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();//装载折线点数值
            dataSets.add(dataSet1);
            LineData lineData = new LineData(xValues, dataSets);//装载x轴数值与折线点数值
            lineChart.setData(lineData);//设置折现数据
            //Toast.makeText(getActivity(), "如无数据，请点击屏幕", Toast.LENGTH_LONG).show();
        }
    }*/
    //请求数据
    private void RequestData(final String ID,final String StartTime,final String EndTime ){
        progressDialog = new MyProgressDialog(getActivity(),false,"数据加载中");
        if(ID.equals("")||EndTime.equals("")||StartTime.equals("")){
           progressDialog.dismiss();
            Toast.makeText(getActivity(), "查找的条件不足", Toast.LENGTH_SHORT).show();
            return;
        }else {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        String methodName = "GetWarningInfoResult";//方法名
                        String path = Path.get_WebServicesURL();//请求地址
                        String SoapFileName = "assets/shuiweilishijilu.xml";//读取的soap协议xml文件名称
                        String soap = CommonMethod.ReadSoap(SoapFileName);
                        soap = soap.replaceAll("string1", ID);
                        soap = soap.replaceAll("string2", StartTime);
                        soap = soap.replaceAll("string3", EndTime);
                        Log.e("warn", soap);
                        byte[] date = soap.getBytes();//soap协议转为字符数组
                        String result = CommonMethod.Request(path, date, methodName);
                        Message msg = Message.obtain();
                        msg.obj = result;
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
    //接受请求回来的数据
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String val=(String) msg.obj;
            Log.e("warn",val);
            if(val.equals("")){
                Toast.makeText(getActivity(),"无历史记录信息",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }else if(val.equals("999999")){
                Toast.makeText(getActivity(),"网络异常",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }else{
                List<ShuiWeiBaoJing> list=Spl(val);
                setTuBiao(list);
                progressDialog.dismiss();
            }
        }
    };
    //分隔传送过来的字符串信息
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
    private void setTuBiao(List<ShuiWeiBaoJing> list){

            Float[] yl = new Float[list.size()];//雨量
            String[] appName = new String[list.size()];//时间

            float start = 0f;
            ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
            barChart.getXAxis().setAxisMinValue(start);


            for (int i = 0; i < list.size(); i++) {
                if (list.size() > 0) {
                        yl[i] = Float.parseFloat(list.get(i).getShuiWei());//雨量值

                        appName[i] = list.get(i).getShiJian();//时间

                        yVals1.add(new BarEntry(yl[i]/100,i));

                }
            }


            barChart.setDrawBarShadow(false);
            barChart.setDrawValueAboveBar(true);
            barChart.setDescription("");
            //barChart.setMaxVisibleValueCount(20);//显示多少数据 其余滑动才能看见
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
            //xAxis.setGranularity(1f); // only intervals of 1 day
            //xAxis.setLabelCount(objects.length,true);//label长度=返回数据长度
            //xAxis.setValueFormatter(xAxisFormatter);//X轴数据
            xAxis.setTextSize(8);
            xAxis.setAxisMinValue(-0.5f); //设置x轴坐标起始值为-0.5 防止其实条形图被切去一半

            //AxisValueFormatter custom = new MyAxisValueFormatterYL();
            //Y轴
            YAxis leftAxis = barChart.getAxisLeft();
            //leftAxis.setTypeface(mTfLight);
            leftAxis.setLabelCount(4, false);//设置y轴数据个数
            //leftAxis.setLabelCount(8, false);
            //leftAxis.setValueFormatter(custom);
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            leftAxis.setSpaceTop(15f);
            leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
            leftAxis.setAxisMaxValue(10f); // this replaces setStartAtZero(true)
            leftAxis.setLabelCount(5, false);//设置y轴数据个数
            Legend mLegend = barChart.getLegend(); //设置比例图
            mLegend.setEnabled(false);//不绘制图例

            //可以设置一条警戒线，如下：
            LimitLine ll = new LimitLine(6, "");//第一个参数为警戒线在坐标轴的位置，第二个参数为警戒线描述
            ll.setLineColor(Color.rgb(255,33,33));
            ll.setLineWidth(1f);
            ll.setTextColor(Color.GRAY);
            ll.setTextSize(12f);
            // .. and more styling options
            leftAxis.addLimitLine(ll);


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
                set1.setBarSpacePercent(30f);//设置柱间空白的宽度
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
                data.setValueTextColor(Color.BLACK);
                //data.setValueFormatter(new CustomerValueFormatter());
                //data.setValueTypeface(mTfLight);
                //data.setBarWidth(0.8f);
                //mChart.setExtraOffsets(0,0,0,200);//此种方法可以一次设置上下左右偏移量。根据自己数据哪个地方显示不全，对应调用方法。
                barChart.setData(data);// 设置数据
                barChart.setVisibleYRangeMaximum(100,YAxis.AxisDependency.LEFT);//水平的设置x轴数据可见个数
            }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(progressDialog!=null){
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            progressDialog=null;
        }
    }
}
