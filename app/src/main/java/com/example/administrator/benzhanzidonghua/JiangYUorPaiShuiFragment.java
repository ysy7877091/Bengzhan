package com.example.administrator.benzhanzidonghua;

import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.vanpeng.javabeen.JiangYUorPaiShuiFragmentJavaBeen;
import com.vanpeng.javabeen.TongJIYuWaterBeng;

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
 *
 */

public class JiangYUorPaiShuiFragment extends Fragment {
    private View view;
    private TextView water_year;
    private TextView water_Month;
    private TextView water_day;
    private LineChart JY_chart;
    private LineChart paiShuiChart;
    private BarChart yeWeiChart;
    private MyProgressDialog progressDialog;
    private boolean isPaiShuiComplete = false;
    private boolean isJiShuiComplete = false;
    private ProgressBar jiangYuChart;
    private ProgressBar PaiShuiBar;
    private ProgressBar YeWeiBar;
    private int JY_hour;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflater=LayoutInflater.from(getActivity());
        view=inflater.inflate(R.layout.jiangyuorpaishuifragment_layout,container,false);
        init();
        return view;
    }
   /* private LinearLayout water_year_ll;
    private LinearLayout water_Month_ll;
    private  LinearLayout water_day_ll*/

    private void init(){
        TextView f_tv = (TextView)view.findViewById(R.id.f_tv);//今日平均总降雨量
        JY_chart = (LineChart)view.findViewById(R.id.jiangYuChart) ;
        JY_chart.setNoDataText("");//更改图表无数据时，图表上显示的内容(no chart data available)
        paiShuiChart= (LineChart)view.findViewById(R.id.paiShuiChart) ;
        paiShuiChart.setNoDataText("");//更改图表无数据时，图表上显示的内容(no chart data available)
        yeWeiChart = (BarChart)view.findViewById(R.id.yeWeiChart);
        yeWeiChart.setNoDataText("");//更改图表无数据时，图表上显示的内容(no chart data available)
        LinearLayout water_year_ll = (LinearLayout)view.findViewById(R.id.water_year_ll);
        water_year_ll.setOnClickListener(new JiangYUorPaiShuiFragmentListener());
        LinearLayout water_Month_ll = (LinearLayout)view.findViewById(R.id.water_Month_ll);
        water_Month_ll.setOnClickListener(new JiangYUorPaiShuiFragmentListener());
        LinearLayout water_day_ll = (LinearLayout)view.findViewById(R.id.water_day_ll);
        water_day_ll.setOnClickListener(new JiangYUorPaiShuiFragmentListener());
        //三个加载进度条
        jiangYuChart = (ProgressBar)view.findViewById(R.id.JiangYuBar) ;
        PaiShuiBar= (ProgressBar)view.findViewById(R.id. PaiShuiBar) ;
        YeWeiBar = (ProgressBar)view.findViewById(R.id.YeWeiBar) ;

         water_year= (TextView)view.findViewById(R.id.water_year);//
         water_Month= (TextView)view.findViewById(R.id.water_Month);//
         water_day= (TextView)view.findViewById(R.id.water_day);//
        setTIME();
        fixedThreadPool.execute(JWater);
        fixedThreadPool.execute(workState);
        fixedThreadPool.execute(PaiShui);
        //new Thread(PaiShui).start();
        JiShui();
    }



    private class JiangYUorPaiShuiFragmentListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.water_day_ll:
                                        selectTiem();
                                        break;
                case R.id.water_Month_ll:
                                            selectTiem();
                                            break;
                case R.id.water_year_ll:
                                            selectTiem();
                                            break;
            }
        }
    }
    //初始化时间
    private void setTIME(){
        //自定义 选择日期和时间的选择器
        final Calendar calendar = Calendar.getInstance();

        //初始化各个控件的时间
        String year  = String.valueOf(calendar.get(Calendar.YEAR));
        String month  = String.valueOf(calendar.get(Calendar.MONTH)+1);
        String day  = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        JY_hour = calendar.get(Calendar.HOUR_OF_DAY);
        Log.e("warn",JY_hour+"小时");
        if(month.length()<2){
            month = "0"+month;
        }
        if(day.length()<2){
            day = "0"+day;
        }
        water_year.setText(year);
        water_Month.setText(month);
        water_day.setText(day);
    }

    //选择年 月 日
    private String Year;
    private String MonthOfYear;
    private String DayOfMonth;

    private  void selectTiem(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.baojingtime_layout, null);
        builder.setView(view);
        DatePicker dp = (DatePicker) view.findViewById(R.id.dp);
        Calendar c = Calendar.getInstance();
        int Now_year = c.get(Calendar.YEAR);
        int Now_monthOfYear = c.get(Calendar.MONTH);
        int Now_dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        //选择默认弹出的当前时间 及 datepicker未改变的时间
        Year = String.valueOf(Now_year);
        MonthOfYear = String.valueOf(Now_monthOfYear + 1);
        DayOfMonth = String.valueOf(Now_dayOfMonth);
        //初始化年月日
        dp.init(Now_year, Now_monthOfYear, Now_dayOfMonth, new DatePicker.OnDateChangedListener() {
            //改变后的时间 时间改变后才执行这个方法
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Year = String.valueOf(year);
                MonthOfYear = String.valueOf(monthOfYear + 1);
                DayOfMonth = String.valueOf(dayOfMonth);
                if(MonthOfYear.length()==2){
                    MonthOfYear ="0"+ MonthOfYear;
                }
                if(DayOfMonth.length()==1){
                    DayOfMonth="0"+DayOfMonth;
                }
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                water_year.setText(Year);
                water_Month.setText(MonthOfYear);
                water_day.setText(DayOfMonth);
                Sum=0;
                dialog.dismiss();
                //清空图表
                JY_chart.clear();
                paiShuiChart.clear();

                fixedThreadPool.execute(JWater);
                fixedThreadPool.execute(PaiShui);

                PaiShuiBar.setVisibility(View.VISIBLE);
                jiangYuChart.setVisibility(View.VISIBLE);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(4);

    //降雨量数据等
    Runnable  JWater = new Runnable() {
        @Override
        public void run() {
            try{
                Log.e("warn","30");
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Get_EveryHourSumRain_List";
                // EndPoint
                String endPoint = Path.get_ZanShibeidouPath();
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_EveryHourSumRain_List";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数日期
                String data = water_year.getText().toString()+"-"+water_Month.getText().toString()+"-"+water_day.getText().toString();
                Log.e("warn",data);
                rpc.addProperty("time",data);
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
                StringBuffer sb=null;
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
                     sb = new StringBuffer();
                     for(int i=0;i<count1;i++) {
                         Log.e("warn", "-----------------------------");
                         SoapObject soapProvince = (SoapObject)object.getProperty(i);


                         Log.e("warn", soapProvince.getProperty("HH").toString() + ":");//dataset数据类型
                         String HH = soapProvince.getProperty("HH").toString();
                         sb.append(HH+",");//小时

                         Log.e("warn", soapProvince.getProperty("ValueX").toString() + ":");//dataset数据类型
                         String ValueX = soapProvince.getProperty("ValueX").toString();
                         if(i==count1-1) {
                             sb.append(ValueX );//降雨量
                         }else{
                             sb.append(ValueX+"|");//降雨量
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
    private List<JiangYUorPaiShuiFragmentJavaBeen> jy_list=null;
    private Handler hanlder = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if(i==0){
                //加载dialog小时 降雨量
                jiangYuChart.setVisibility(View.INVISIBLE);
                TextView f_tv =(TextView)view.findViewById(R.id.f_tv);
                f_tv.setText("");
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "获取降雨量数据失败", Toast.LENGTH_SHORT).show();
                }
            }
            if(i==1){ //获取降雨量数据
                float Sum=0;
                String str = (String)msg.obj;
                jy_list = new ArrayList<>();
                Log.e("warn",str);
                String JY_arr [] = str.split("\\|");
                for (int j= 0;j<JY_arr.length;j++){
                    JiangYUorPaiShuiFragmentJavaBeen jy = new JiangYUorPaiShuiFragmentJavaBeen();
                    String JY_arr1 [] = JY_arr[j].split(",");
                    if (JY_arr1[0].length()==1){
                        JY_arr1[0] = "0"+JY_arr1[0];
                    }
                    jy.setNum(JY_arr1[1]);
                    jy.setTime(JY_arr1[0]);

                    Sum = Sum + Float.valueOf(JY_arr1[1]);

                    jy_list.add(jy);
                }
                TextView f_tv =(TextView)view.findViewById(R.id.f_tv);
                f_tv.setText(String.valueOf(Sum));
                jiangYuChart.setVisibility(View.INVISIBLE);
                setJYChart(jy_list);
            }
            if(i==2){
                //加载dialog小时 降雨量
                jiangYuChart.setVisibility(View.INVISIBLE);
                TextView f_tv =(TextView)view.findViewById(R.id.f_tv);
                f_tv.setText("");
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "无数据", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    //获取泵站状态数据等
    private Runnable workState = new Runnable() {
        @Override
        public void run() {
            try{
                Log.e("warn","30");
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Get_BengZhanWorkStat";
                // EndPoint
                String endPoint = Path.get_ZanShibeidouPath();
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_BengZhanWorkStat";
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
                    msg.what=2;
                    hanlder1.sendMessage(msg);
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
                    msg.what=4;
                    hanlder1.sendMessage(msg);
                    return;
                }
                if(count1>0)
                {

                    Log.e("warn","-----------------------------");
                    SoapObject soapProvince = (SoapObject) envelope.bodyIn;

                    Log.e("warn",soapProvince.getProperty("Get_BengZhanWorkStatResult").toString()+":返回id");//dataset数据类型
                    String str = soapProvince.getProperty("Get_BengZhanWorkStatResult").toString();

                    Message msg = Message.obtain();
                    msg.what=3;
                    msg.obj=str;
                    hanlder1.sendMessage(msg);
                }
            } catch (Exception e){
                Message msg = Message.obtain();
                msg.what=2;
                hanlder1.sendMessage(msg);
            }
        }
    };
    private Handler hanlder1 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            Log.e("warn",String.valueOf(i));
            if(i==2){
                //加载dialog消失 获取泵站工作状态
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "获取泵站工作状态失败", Toast.LENGTH_SHORT).show();
                }
            }
            if(i==3){
                //获取泵站工作状态
                String bz_str  = (String)msg.obj;
                Log.e("warn",bz_str);
                int index = bz_str.indexOf("{");
                int index1= bz_str.length();
                String str2 = bz_str.substring(index+1,index1-1);
                Log.e("warn",str2);
                String arr[] =str2.split(";");
                for(int j=0;j<arr.length;j++){
                    arr[j]=arr[j].substring(arr[j].indexOf("=")+1);
                    Log.e("warn",arr[j]);
                }
                TextView working = (TextView)view.findViewById(R.id.work);
                if(arr[0].length()==1){
                    arr[0] = "0"+arr[0];
                }
                working.setText(arr[0]);
                TextView noWork = (TextView)view.findViewById(R.id.offwork);
                if(arr[1].length()==1){
                    arr[1] = "0"+arr[1];
                }
                noWork.setText(arr[1]);
            }
            if(i==4){
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "无工作状态数据", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    //降雨量图标
    private void setJYChart(List<JiangYUorPaiShuiFragmentJavaBeen> list){
        //设置图表数据
            JY_chart.setDescription("");
            JY_chart.setDrawGridBackground(false);
            JY_chart.setPinchZoom(false);
            JY_chart.setTouchEnabled(true);// 设置是否可以触摸
            JY_chart.setDragEnabled(true);// 是否可以拖拽
            JY_chart.setScaleEnabled(true);// 是否可以缩放
            //Typeface mTf = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "OpenSans-Regular.ttf");
            XAxis xAxis = JY_chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            //xAxis.setTypeface(mTf);
            xAxis.setTextColor(Color.rgb(103,175,205));//设置x轴数据颜色
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(true);
            //xAxis.setLabelRotationAngle(-90);
            JY_chart.getAxisRight().setEnabled(false);
            YAxis leftAxis =JY_chart.getAxisLeft();
            leftAxis.setDrawAxisLine(true);//显示y轴
            //leftAxis.setTypeface(mTf);
            //leftAxis.setTextColor(R.color.tj3);
            leftAxis.setTextColor(Color.rgb(103,175,205));//设置y轴数据颜色
            leftAxis.setLabelCount(4, false);
            leftAxis.setSpaceTop(15f);
            leftAxis.setAxisMinValue(0f);
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

            Legend mLegend = JY_chart.getLegend(); // 设置比例图标示，就是那个一组y的value的
            mLegend.setEnabled(false);
            // modify the legend ...
            //mLegend.setPosition(LegendPosition.LEFT_OF_CHART);
        /*mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(6f);// 字体
        mLegend.setTextColor(Color.WHITE);// 颜色*/

            JY_chart.animateXY(2500,2500);
//y轴数据
            ArrayList<Entry> yVals1 = new ArrayList<Entry>();
            for (int i = 0; i < list.size(); i++) {
                float val = Float.parseFloat(list.get(i).getNum());//字符串转为浮点型
                Entry entry = new Entry(val, i);
                yVals1.add(entry);
            }
//x轴数据
            ArrayList<String> xValues = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                // x轴显示的数据
                Log.e("warn",list.get(i).getTime());
                xValues.add(list.get(i).getTime());
            }

            LineDataSet dataSet1; /*= new LineDataSet(yVals1, "七星彩");//设置折线点数据
        dataSet1.setLineWidth(2.5f);//折线的宽
        dataSet1.setCircleSize(5.5f);//折线的圆点大小
        dataSet1.setHighLightColor(Color.rgb(244, 117, 117));*/
            //dataSet1.setDrawValues(true);
            //判断图表中原来是否有数据
            if(JY_chart.getData()!=null&&JY_chart.getData().getDataSetCount()>0){
                dataSet1 =(LineDataSet)JY_chart.getData().getDataSetByIndex(0);
                dataSet1.setYVals(yVals1);//设置图表折线点数据
                JY_chart.getData().notifyDataChanged();
                JY_chart.notifyDataSetChanged();
            }else {
                dataSet1= new LineDataSet(yVals1, "水位");//设置数据
                dataSet1.setLineWidth(2.5f);//折线的宽
                dataSet1.setCircleSize(2.5f);//折线的圆点大小

                //设置圆点颜色
                dataSet1.setCircleColorHole(Color.rgb(245,110,78));
                dataSet1.setCircleColor(Color.rgb(245,110,78));

                //设置折线颜色
                dataSet1.setColor(Color.rgb(255,255,255));
                //dataSet1.setHighLightColor(Color.rgb(244, 117, 117));
                dataSet1.setValueTextColor(Color.rgb(255,255,255));//设置数据颜色


                ArrayList<ILineDataSet> dataSets = new ArrayList<>();//装载折线点数值
                dataSets.add(dataSet1);
                LineData lineData = new LineData(xValues, dataSets);//装载x轴数值与折线点数值
                JY_chart.setData(lineData);//设置折现数据
                if(JY_hour==24||JY_hour==0){
                    JY_hour = 0;
                }else {
                    JY_hour = JY_hour-1;
                }
                JY_chart.moveViewToX(JY_hour);//将视图移动到x轴上某点
                JY_chart.setVisibleXRangeMaximum(8);//设置x坐标轴最多显示多少个数据，超出的部分需要滑动才能看到
                //Toast.makeText(getActivity(), "如无数据，请点击屏幕", Toast.LENGTH_LONG).show();

        }
    }
    //泵站排水
    Runnable PaiShui = new Runnable() {
        @Override
        public void run() {
            try {
                Log.e("warn", "30"+"排水");
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Get_YuBengZhanHH_List";
                // EndPoint
                String endPoint = Path.get_ZanShibeidouPath();
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_YuBengZhanHH_List";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数CarNum
                String data = water_year.getText().toString()+"-"+water_Month.getText().toString()+"-"+water_day.getText().toString();
                rpc.addProperty("time", data);
                // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);

                AndroidHttpTransport ht = new AndroidHttpTransport(endPoint);
                ht.debug = true;
                Log.e("warn", "50"+"排水");
                try {
                    // 调用WebService
                    ht.call(soapAction, envelope);
                } catch (Exception e) {
                    Message msg = Message.obtain();
                    msg.what = 0;
                    YuWater.sendMessage(msg);
                }

                SoapObject object;
                // 开始调用远程方法
                Log.e("warn", "60"+"排水");

                object = (SoapObject) envelope.getResponse();
                Log.e("warn", "64"+"排水");
                // 得到服务器传回的数据 返回的数据时集合 每一个count是一个及集合的对象
                int count1 = object.getPropertyCount();
                Log.e("warn", String.valueOf(count1));
                if (count1 == 0) {
                    Message msg = Message.obtain();
                    msg.what = 2;
                    YuWater.sendMessage(msg);
                    return;
                }
                if (count1 > 0) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < count1; i++) {


                        Log.e("warn", "-----------------------------");
                        SoapObject soapProvince = (SoapObject) object.getProperty(i);

                        Log.e("warn", soapProvince.getProperty("SumPaiShuiLiang").toString() + ":");
                        sb.append(soapProvince.getProperty("SumPaiShuiLiang").toString() + ",");

                        Log.e("warn", soapProvince.getProperty("HH").toString() + ":");
                        if (i == count1 - 1) {
                            sb.append(soapProvince.getProperty("HH").toString());
                        } else {
                            sb.append(soapProvince.getProperty("HH").toString() + "|");
                        }
                       /* Log.e("warn",soapProvince.getProperty("CARIMG").toString()+":");
                        sb.append(soapProvince.getProperty("CARIMG").toString()+",");
                        //lieBiao.setN_WD(soapProvince.getProperty("N_WD").toString());*/

                       /* SoapObject soapProvince = (SoapObject) envelope.bodyIn;
                        Log.e("warn",soapProvince.getProperty("Get_OneBengZhanHH_ListResult").toString()+":返回id");//dataset数据类型
                        sb.append(soapProvince.getProperty("Get_OneBengZhanHH_ListResult").toString());*/
                    }
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.obj = sb.toString();
                    YuWater.sendMessage(msg);
                }
            } catch (Exception e) {
                Message msg = Message.obtain();
                msg.what = 0;
                YuWater.sendMessage(msg);
            }
        }
    };
    //获取每小时雨水数据
    private List<TongJIYuWaterBeng> Yulist;
    private float Sum=0;
    private Handler YuWater = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i  = msg.what;
            if(i==0){
                PaiShuiBar.setVisibility(View.INVISIBLE);
                TextView w_tv= (TextView)view.findViewById(R.id.w_tv);//今日雨水泵站提升总量
                w_tv.setText("");
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "获取排水数据失败", Toast.LENGTH_SHORT).show();
                }
            }else if(i==1) {
                String str = (String) msg.obj;
                Log.e("warn", "water:" + str);
                Yulist = new ArrayList<>();
                String arr[] = str.split("\\|");
                for (int j = 0; j < arr.length; j++) {
                    TongJIYuWaterBeng bz = new TongJIYuWaterBeng();
                    String arr1[] = arr[j].split(",");
                    bz.setNUM(arr1[0]);
                    bz.setTIME(arr1[1]);
                    Yulist.add(bz);
                    Sum = Sum +Float.valueOf(arr1[0]);
                }
                TextView w_tv= (TextView)view.findViewById(R.id.w_tv);//今日雨水泵站提升总量
                w_tv.setText(String.valueOf(Sum));
                PaiShuiBar.setVisibility(View.INVISIBLE);
                setYushuiChart();
            }else if(i==2){
                PaiShuiBar.setVisibility(View.INVISIBLE);
                TextView w_tv= (TextView)view.findViewById(R.id.w_tv);//今日雨水泵站提升总量
                w_tv.setText("");
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "无排水数据", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    //设置每小时雨水数据图表
    private void setYushuiChart() {
        //设置图表数据
        paiShuiChart.setDescription("");
         paiShuiChart.setDrawGridBackground(false);
         paiShuiChart.setPinchZoom(false);
         paiShuiChart.setTouchEnabled(true);// 设置是否可以触摸
         paiShuiChart.setDragEnabled(true);// 是否可以拖拽
         paiShuiChart.setScaleEnabled(true);// 是否可以缩放
        //Typeface mTf = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "OpenSans-Regular.ttf");
        XAxis xAxis =  paiShuiChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setTypeface(mTf);
        xAxis.setTextColor(Color.rgb(103, 175, 205));//设置x轴数据颜色
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);

        //xAxis.setSpaceBetweenLabels(100); // 设置数据之间的间距
        //设置视口
        //paiShuiChart.centerViewPort(10, 50);
        //paiShuiChart.setScaleMinima(1f, 1f);
        //xAxis.setLabelRotationAngle(-90);
         paiShuiChart.getAxisRight().setEnabled(false);
        YAxis leftAxis =  paiShuiChart.getAxisLeft();
        leftAxis.setDrawAxisLine(true);//显示y轴
        //leftAxis.setTypeface(mTf);
        //leftAxis.setTextColor(R.color.tj3);
        leftAxis.setLabelCount(4, false);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTextColor(Color.rgb(103, 175, 205));//设置y轴数据颜色
        Legend mLegend =  paiShuiChart.getLegend(); // 设置比例图标示，就是那个一组y的value的
        mLegend.setEnabled(false);
        // modify the legend ...
        //mLegend.setPosition(LegendPosition.LEFT_OF_CHART);
        /*mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(6f);// 字体
        mLegend.setTextColor(Color.WHITE);// 颜色*/
         paiShuiChart.animateXY(2500, 2500);
//y轴数据
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        for (int i = 0; i < Yulist.size(); i++) {
            float val = Float.parseFloat(Yulist.get(i).getNUM());//字符串转为浮点型
            Entry entry = new Entry(val, i);
            yVals1.add(entry);
        }
//x轴数据
        ArrayList<String> xValues = new ArrayList<>();
        for (int i = 0; i < Yulist.size(); i++) {
            // x轴显示的数据
            Log.e("warn", Yulist.get(i).getTIME());
            xValues.add(Yulist.get(i).getTIME());
        }

        LineDataSet dataSet1; /*= new LineDataSet(yVals1, "七星彩");//设置折线点数据
        dataSet1.setLineWidth(2.5f);//折线的宽
        dataSet1.setCircleSize(5.5f);//折线的圆点大小
        dataSet1.setHighLightColor(Color.rgb(244, 117, 117));*/
        //dataSet1.setDrawValues(true);
        //判断图表中原来是否有数据
        if ( paiShuiChart.getData() != null &&  paiShuiChart.getData().getDataSetCount() > 0) {
            dataSet1 = (LineDataSet)  paiShuiChart.getData().getDataSetByIndex(0);
            dataSet1.setYVals(yVals1);//设置图表折线点数据
             paiShuiChart.getData().notifyDataChanged();
             paiShuiChart.notifyDataSetChanged();
        } else {
            dataSet1 = new LineDataSet(yVals1, "水位");//设置数据
            dataSet1.setLineWidth(2.5f);//折线的宽
            dataSet1.setCircleSize(2.5f);//折线的圆点大小

            //设置圆点颜色
            dataSet1.setCircleColorHole(Color.rgb(245, 110, 78));
            dataSet1.setCircleColor(Color.rgb(245, 110, 78));

            //设置折线颜色
            dataSet1.setColor(Color.rgb(255, 255, 255));
            //dataSet1.setHighLightColor(Color.rgb(244, 117, 117));
            dataSet1.setValueTextColor(Color.rgb(255, 255, 255));//设置数据颜色


            ArrayList<ILineDataSet> dataSets = new ArrayList<>();//装载折线点数值
            dataSets.add(dataSet1);
            LineData lineData = new LineData(xValues, dataSets);//装载x轴数值与折线点数值
             paiShuiChart.setData(lineData);//设置折现数据
           //data.getXValCount()每次返回的总是全部x坐标轴上总数量，
            // 将坐标移动到最新
            // 此代码将刷新图表的绘图
            // //mChart.moveViewToX(data.getXValCount() - 5);
            if(JY_hour==24||JY_hour==0){
                JY_hour = 0;
            }else {
                JY_hour = JY_hour-1;
            }
            paiShuiChart.moveViewToX(JY_hour);//将视图移动到x轴上某点
            //Toast.makeText(getActivity(), "如无数据，请点击屏幕", Toast.LENGTH_LONG).show();
            paiShuiChart.setNoDataText("");
            paiShuiChart.setVisibleXRangeMaximum(8);//设置x坐标轴最多显示多少个数据，超出的部分需要滑动才能看到
        }
    }
    private void JiShui() {


            YeWeiBar.setVisibility(View.INVISIBLE);

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
            String arr [ ] ={"洪庆中学","三环桥","北方重工"};
            for (int i = 0; i < 3; i++) {//取数据
                lbls[i] = arr [i]  ;
                myColors[i] = ColorTemplate.MATERIAL_COLORS[i];
            }
            //String arr1 [] = {"01","02","03"};
            for (int i = 0; i < 3; i++) {

                        yl[i] = Float.parseFloat("0");//雨量值
                        appName[i] = arr[i].toString();//区域名

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
            xAxis.setAxisMinValue(-0.5f); //设置x轴坐标起始值为-0.5 防止其实条形图被切去一半
            //Y轴
            YAxis leftAxis = yeWeiChart.getAxisLeft();
            leftAxis.setTextColor(Color.rgb(103, 175, 205));//设置y轴数据颜色
            leftAxis.setLabelCount(8, false);
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            leftAxis.setSpaceTop(15f);
            leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

            Legend mLegend = yeWeiChart.getLegend(); //设置比例图
            mLegend.setEnabled(false);//不绘制图例

            /*l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setForm(Legend.LegendForm.CIRCLE);
            l.setFormSize(8f);
            l.setTextSize(8f);
            l.setXEntrySpace(4f);
            l.setCustom(myColors, lbls);*/

                BarDataSet set1;
                //判断图表中原来是否有数据
                if (yeWeiChart.getData() != null && yeWeiChart.getData().getDataSetCount() > 0) {
                    set1 = (BarDataSet) yeWeiChart.getData().getDataSetByIndex(0);
                    //set1 = new BarDataSet(yVals1, "");
                    set1.setYVals(yVals1);
                    yeWeiChart.getData().notifyDataChanged();
                    yeWeiChart.notifyDataSetChanged();
                } else {
                    set1 = new BarDataSet(yVals1, "");
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
                    //mChart.setExtraOffsets(float left, float top, float right, float bottom);//此种方法可以一次设置上下左右偏移量。根据自己数据哪个地方显示不全，对应调用方法。
                    //mChart.setExtraOffsets(0,0,0,200);//此种方法可以一次设置上下左右偏移量。根据自己数据哪个地方显示不全，对应调用方法。
                    yeWeiChart.setData(data);// 设置数据
        }
    }

}
