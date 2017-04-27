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
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.com.vanpeng.Adapter.bengzhanNameAndId;
import com.com.vanpeng.Adapter.bengzhanTongjiGetHH;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
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
 */

public class BengZhanTongJIFragment extends Fragment {
    private View view;
    private TextView TJBZ_year;
    private TextView TJBZ_month;
    private TextView TJBZ_day;
    private TextView TJBZ_Name_Tv;
    private boolean isFirst = true;
    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(6);
    private ProgressBar Wuwater_sumBar;
    private ProgressBar Yuwater_sumBar;
    private ProgressBar water_sumBar;
    private int JY_hour;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflater = LayoutInflater.from(getActivity());
        view = inflater.inflate(R.layout.bengzhanfragmenttongji_layout, container, false);
        init();
        return view;
    }

    private LineChart TJ_chart;
    private TextView left_bottom;
    private TextView Right_bottom;
    private TextView up_Water;
    private LineChart YuShuiChart;
    private LineChart WuShuiChart;

    private void init() {
        Bundle bundle = getArguments();
        int sum_height = bundle.getInt("height_sum");
        //获取设置控件大小的对象
        LinearLayout time_top = (LinearLayout) view.findViewById(R.id.time_top);
        TJ_chart = (LineChart) view.findViewById(R.id.TJ_chart);//第一个图表
        TJ_chart.setNoDataText("");//更改图表无数据时，图表上显示的内容(no chart data available)
        LinearLayout two_chart = (LinearLayout) view.findViewById(R.id.two_chart);
        LinearLayout TJ_bottom = (LinearLayout) view.findViewById(R.id.TJ_bottom);
        left_bottom = (TextView) view.findViewById(R.id.bottom_tv1);
        Right_bottom = (TextView) view.findViewById(R.id.bottom_tv2);
        WindowManager wm = getActivity().getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight() - sum_height;

        /*LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) time_top.getLayoutParams(); //取控件textView当前的布局参数 linearParams.height = 20;// 控件的高强制设成20
        linearParams.height = height / 10;// 控件的高强制设成
        time_top.setLayoutParams(linearParams);

        RelativeLayout.LayoutParams linearParams1 = (RelativeLayout.LayoutParams) TJ_chart.getLayoutParams(); //取控件textView当前的布局参数 linearParams.height = 20;// 控件的高强制设成20
        linearParams.height = 4 * height / 10;// 控件的高强制设成
        TJ_chart.setLayoutParams(linearParams1);

        LinearLayout.LayoutParams linearParams2 = (LinearLayout.LayoutParams) two_chart.getLayoutParams(); //取控件textView当前的布局参数 linearParams.height = 20;// 控件的高强制设成20
        linearParams.height = 3 * height / 10;// 控件的高强制设成
        two_chart.setLayoutParams(linearParams2);

        LinearLayout.LayoutParams linearParams3 = (LinearLayout.LayoutParams) TJ_bottom.getLayoutParams(); //取控件textView当前的布局参数 linearParams.height = 20;// 控件的高强制设成20
        linearParams.height = 2 * height / 10;// 控件的高强制设成
        TJ_bottom.setLayoutParams(linearParams3);*/

        //三个加载bar

        Wuwater_sumBar = (ProgressBar)view.findViewById(R.id.Wuwater_sumBar) ;
        Yuwater_sumBar= (ProgressBar)view.findViewById(R.id.Yuwater_sumBar) ;
        water_sumBar= (ProgressBar)view.findViewById(R.id.water_sumBar) ;





        LinearLayout BZTJ_yeay_LL = (LinearLayout) view.findViewById(R.id.BZTJ_yeay_LL);//年 布局
        BZTJ_yeay_LL.setOnClickListener(new BengZhanTongJIFragmentListener());
        LinearLayout BZTJ_month_LL = (LinearLayout) view.findViewById(R.id.BZTJ_month_LL);//月 布局
        BZTJ_month_LL.setOnClickListener(new BengZhanTongJIFragmentListener());
        LinearLayout BZTJ_day_LL = (LinearLayout) view.findViewById(R.id.BZTJ_day_LL);//日 布局
        BZTJ_day_LL.setOnClickListener(new BengZhanTongJIFragmentListener());
        LinearLayout BZTJ_BZName_LL = (LinearLayout) view.findViewById(R.id.BZTJ_BZName_LL);//泵站名 布局
        BZTJ_BZName_LL.setOnClickListener(new BengZhanTongJIFragmentListener());
        //LinearLayout showMap = (LinearLayout) view.findViewById(R.id.aa);//显示地图布局
        //showMap.setOnClickListener(new BengZhanTongJIFragmentListener());

        up_Water = (TextView) view.findViewById(R.id.up_Water);

        YuShuiChart = (LineChart) view.findViewById(R.id.YuShuiChart);//雨水图表
        YuShuiChart.setNoDataText("");//更改图表无数据时，图表上显示的内容(no chart data available)
        WuShuiChart = (LineChart) view.findViewById(R.id.WuShuiChart);//污水图表
        WuShuiChart.setNoDataText("");//更改图表无数据时，图表上显示的内容(no chart data available)
        //日期
        TJBZ_year = (TextView) view.findViewById(R.id.TJBZ_year);
        TJBZ_month = (TextView) view.findViewById(R.id.TJBZ_Month);
        TJBZ_day = (TextView) view.findViewById(R.id.TJBZ_day);

        TJBZ_Name_Tv = (TextView) view.findViewById(R.id.TJBZ_Name_Tv);

        setTIME();//初始化时间
        //fixedThreadPool.execute(sum_water);
        fixedThreadPool.execute(networkTask);
        fixedThreadPool.execute(getYuShuiData);
        fixedThreadPool.execute(getWuShuiData);
    }

    private class BengZhanTongJIFragmentListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.BZTJ_yeay_LL:
                    selectTiem();
                    break;
                case R.id.BZTJ_month_LL:
                    selectTiem();
                    break;
                case R.id.BZTJ_day_LL:
                    selectTiem();
                    break;
                case R.id.BZTJ_BZName_LL:
                    isFirst=false;
                    if (arr_ALL!=null) {
                        setBengZhanpop();
                    }else{
                         if(getActivity()!=null) {
                             Toast.makeText(getActivity(), "获取泵站列表失败", Toast.LENGTH_SHORT).show();
                         }
                    }
                    break;
            }
        }
    }

    //初始化时间
    private void setTIME() {
        //自定义 选择日期和时间的选择器
        final Calendar calendar = Calendar.getInstance();

        //初始化各个控件的时间
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        JY_hour = calendar.get(Calendar.HOUR_OF_DAY);
        Log.e("warn",JY_hour+"小时");
        if (month.length() < 2) {
            month = "0" + month;
        }
        if (day.length() < 2) {
            day = "0" + day;
        }
        TJBZ_year.setText(year);
        TJBZ_month.setText(month);
        TJBZ_day.setText(day);
    }

    //选择年 月 日
    private String Year;
    private String MonthOfYear;
    private String DayOfMonth;

    private void selectTiem() {
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
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TJBZ_year.setText(Year);
                TJBZ_month.setText(MonthOfYear);
                TJBZ_day.setText(DayOfMonth);
                dialog.dismiss();
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

    //获取泵站列表
    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            try {
                Log.e("warn", "30");
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Get_BengZhanInfo_List";
                // EndPoint
                String endPoint = Path.get_ZanShibeidouPath();
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_BengZhanInfo_List";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数CarNum
                //rpc.addProperty("CarNum","");
                // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);

                AndroidHttpTransport ht = new AndroidHttpTransport(endPoint);
                ht.debug = true;
                Log.e("warn", "50");
                try {
                    // 调用WebService
                    ht.call(soapAction, envelope);
                } catch (Exception e) {
                    Message msg = Message.obtain();
                    msg.what = 0;
                    Liuliang_hanlder.sendMessage(msg);
                }

                SoapObject object;
                // 开始调用远程方法
                Log.e("warn", "60");

                object = (SoapObject) envelope.getResponse();
                Log.e("warn", "64");
                // 得到服务器传回的数据 返回的数据时集合 每一个count是一个及集合的对象
                int count1 = object.getPropertyCount();
                Log.e("warn", String.valueOf(count1));
                if (count1 == 0) {
                    Message msg = Message.obtain();
                    msg.what = 2;
                    Liuliang_hanlder.sendMessage(msg);
                    return;
                }
                if (count1 > 0) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < count1; i++) {
                        Log.e("warn", "-----------------------------");
                        SoapObject soapProvince = (SoapObject) object.getProperty(i);

                        Log.e("warn", soapProvince.getProperty("BengZhanName").toString() + ":");
                        sb.append(soapProvince.getProperty("BengZhanName").toString() + ",");

                        Log.e("warn", soapProvince.getProperty("BengZhanID").toString() + ":");
                        if (i == count1 - 1) {
                            sb.append(soapProvince.getProperty("BengZhanID").toString());
                        } else {
                            sb.append(soapProvince.getProperty("BengZhanID").toString() + "|");
                        }
                       /* Log.e("warn",soapProvince.getProperty("CARIMG").toString()+":");
                        sb.append(soapProvince.getProperty("CARIMG").toString()+",");
                        //lieBiao.setN_WD(soapProvince.getProperty("N_WD").toString());*/
                    }
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.obj = sb.toString();
                    Liuliang_hanlder.sendMessage(msg);
                }
            } catch (Exception e) {
                Message msg = Message.obtain();
                msg.what = 0;
                Liuliang_hanlder.sendMessage(msg);
            }
        }

    };
    //获取泵站状态数据等
    private Runnable BZ_state= new Runnable() {
        @Override
        public void run() {
            try{
                Log.e("warn","30");
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Get_ShuiShuiBengWorkAndNot";
                // EndPoint
                String endPoint = Path.get_ZanShibeidouPath();
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_ShuiShuiBengWorkAndNot";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数日期
                //String data = water_year.getText().toString()+"-"+water_Month.getText().toString()+"-"+water_day.getText().toString();
               if(isFirst) {
                   rpc.addProperty("bengID", String.valueOf(list.get(0).getID()));
               }else{
                   rpc.addProperty("bengID", String.valueOf(list.get(selectID).getID()));
               }
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
                    msg.what=4;
                    hanlder.sendMessage(msg);
                    return;
                }
                if(count1>0)
                {

                    Log.e("warn","-----------------------------");
                    SoapObject soapProvince = (SoapObject) envelope.bodyIn;

                    Log.e("warn",soapProvince.getProperty("Get_ShuiShuiBengWorkAndNotResult").toString()+":返回id");//dataset数据类型
                    String str = soapProvince.getProperty("Get_ShuiShuiBengWorkAndNotResult").toString();

                    Message msg = Message.obtain();
                    msg.what=3;
                    msg.obj=str;
                    hanlder.sendMessage(msg);
                }
            } catch (Exception e){
                Message msg = Message.obtain();
                msg.what=2;
                hanlder.sendMessage(msg);
            }
        }
    };
    private Handler hanlder = new Handler(){
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
            if(i==4){
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "无泵站工作状态数据", Toast.LENGTH_SHORT).show();
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
                TextView working = (TextView)view.findViewById(R.id.Working);
                working.setText(arr[1]);
                TextView noWork = (TextView)view.findViewById(R.id.noWork);
                noWork.setText(arr[2]);
            }
        }
    };
    private List<bengzhanNameAndId> list;
    private Handler Liuliang_hanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            Log.e("warn", "water:" + String.valueOf(i));
            if (i == 0) {//水流量
                //加载dialog小时 获取各个泵站水流量
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "获取泵站列表失败", Toast.LENGTH_SHORT).show();
                }
            }
            if(i==2){
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "无泵站列表数据", Toast.LENGTH_SHORT).show();
                }
            }
            if (i == 1) {//获取各个泵站水流量
                //progressDialog.dismiss();
                String str = (String) msg.obj;
                list = new ArrayList<>();
                Log.e("warn", "water:" + str);
                String arr[] = str.split("\\|");
                for (int j = 0; j < arr.length; j++) {
                    bengzhanNameAndId bz = new bengzhanNameAndId();
                    String arr1[] = arr[j].split(",");
                    bz.setID(arr1[1]);
                    bz.setName(arr1[0]);
                    list.add(bz);
                }
                arr_ALL = new String[list.size()];
                for (int k = 0; k < list.size(); k++) {
                    arr_ALL[k] = list.get(k).getName();
                }
                TJBZ_Name_Tv.setText(list.get(0).getName());
                if(isFirst) {
                    fixedThreadPool.execute(getBZHHdata);
                    fixedThreadPool.execute(BZ_state);
                }
            }
        }
    };
    private String arr_ALL[];//装泵站列表的数组

    //泵站列表
    private void setBengZhanpop() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("请选择");

        builder.setSingleChoiceItems(arr_ALL, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    TJBZ_Name_Tv.setText(arr_ALL[i]);
                    selectID = i;
                    dialogInterface.dismiss();
                    fixedThreadPool.execute(getBZHHdata);//查找
                    fixedThreadPool.execute(BZ_state);
                    TJ_chart.clear();
                    water_sumBar.setVisibility(View.VISIBLE);
            }
        });
        builder.show();
    }

    private MyProgressDialog ProgressDialog;
    private int selectID;
    //指定泵站每小时数据
    Runnable getBZHHdata = new Runnable() {

        @Override
        public void run() {
            try {
                Log.e("warn", "30");
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Get_OneBengZhanHH_List";
                // EndPoint
                String endPoint = Path.get_ZanShibeidouPath();
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_OneBengZhanHH_List";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数CarNum
                if(isFirst) {
                    rpc.addProperty("BengID", String.valueOf(list.get(0).getID()));
                }else{
                    rpc.addProperty("BengID", String.valueOf(list.get(selectID).getID()));
                }
                rpc.addProperty("time", TJBZ_year.getText().toString() + "-" + TJBZ_month.getText().toString() + "-" + TJBZ_day.getText().toString());
                // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);

                AndroidHttpTransport ht = new AndroidHttpTransport(endPoint);
                ht.debug = true;
                Log.e("warn", "50");
                try {
                    // 调用WebService
                    ht.call(soapAction, envelope);
                } catch (Exception e) {
                    Message msg = Message.obtain();
                    msg.what = 0;
                    HHandler.sendMessage(msg);
                }

                SoapObject object;
                // 开始调用远程方法
                Log.e("warn", "60");

                object = (SoapObject) envelope.getResponse();
                Log.e("warn", "64");
                // 得到服务器传回的数据 返回的数据时集合 每一个count是一个及集合的对象
                int count1 = object.getPropertyCount();
                Log.e("warn", String.valueOf(count1));
                if (count1 == 0) {
                    Message msg = Message.obtain();
                    msg.what = 2;
                    HHandler.sendMessage(msg);
                    return;
                }
                if (count1 > 0) {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < count1; i++) {


                        Log.e("warn", "-----------------------------");
                        SoapObject soapProvince = (SoapObject) object.getProperty(i);

                        Log.e("warn", soapProvince.getProperty("TYPE").toString() + ":");
                        sb.append(soapProvince.getProperty("TYPE").toString() + ",");

                        Log.e("warn", soapProvince.getProperty("HH").toString() + ":");

                        sb.append(soapProvince.getProperty("HH").toString() + ",");


                        Log.e("warn", soapProvince.getProperty("SumPaiShuiLiang").toString() + ":");
                        if (i == count1 - 1) {
                            sb.append(soapProvince.getProperty("SumPaiShuiLiang").toString());
                        } else {
                            sb.append(soapProvince.getProperty("SumPaiShuiLiang").toString() + "|");
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
                    HHandler.sendMessage(msg);
                }
            } catch (Exception e) {
                Message msg = Message.obtain();
                msg.what = 0;
                HHandler.sendMessage(msg);
            }
        }
    };
    List<bengzhanTongjiGetHH> HHlist = new ArrayList<>();
    private float Sum = 0;
    private Handler HHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if (i == 0) {
                water_sumBar.setVisibility(View.INVISIBLE);
                up_Water.setText("");
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "网络或服务器异常", Toast.LENGTH_SHORT).show();
                }
            }else if(i==2){
                water_sumBar.setVisibility(View.INVISIBLE);
                up_Water.setText("");
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "无泵站数据", Toast.LENGTH_SHORT).show();
                }
            } else if (i == 1) {
                Sum=0;
                String str = (String) msg.obj;
                HHlist = new ArrayList<>();
                Log.e("warn", "water:" + str);
                String arr[] = str.split("\\|");
                for (int j = 0; j < arr.length; j++) {
                    bengzhanTongjiGetHH bz = new bengzhanTongjiGetHH();
                    String arr1[] = arr[j].split(",");
                    bz.setType(arr1[0]);
                    bz.setName(arr1[1]);
                    bz.setYuliang(arr1[2]);
                    Sum = Sum + Float.valueOf(arr1[2]);
                    HHlist.add(bz);
                }

                if (HHlist.get(0).getType().equals("0")) {
                     if(String.valueOf(Sum).length()>=4){
                         left_bottom.setTextSize(12);
                         Right_bottom.setTextSize(12);
                     }
                    left_bottom.setText("当前泵站提升总量");
                    Right_bottom.setText("当前泵站");
                } else {
                    if(String.valueOf(Sum).length()>=4){//数据太长 设置字体小一点
                        left_bottom.setTextSize(12);
                        Right_bottom.setTextSize(12);
                    }
                    left_bottom.setText("当前泵站提升总量");
                    Right_bottom.setText("当前泵站");
                }
                up_Water.setText(String.valueOf(Sum));
                water_sumBar.setVisibility(View.INVISIBLE);
                setHHlineChart();
            }
        }
    };

    private void setHHlineChart() {
        //设置图表数据
        TJ_chart.setDescription("");
        TJ_chart.setDrawGridBackground(false);
        TJ_chart.setPinchZoom(false);
        TJ_chart.setTouchEnabled(true);// 设置是否可以触摸
        TJ_chart.setDragEnabled(true);// 是否可以拖拽
        TJ_chart.setScaleEnabled(true);// 是否可以缩放
        //Typeface mTf = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "OpenSans-Regular.ttf");
        XAxis xAxis = TJ_chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setTypeface(mTf);
        xAxis.setTextColor(Color.rgb(103, 175, 205));//设置x轴数据颜色
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisMinValue(-0.5f); //设置x轴坐标起始值为-0.5 防止其实条形图被切去一半
        //xAxis.setLabelRotationAngle(-90);
        TJ_chart.getAxisRight().setEnabled(false);
        YAxis leftAxis = TJ_chart.getAxisLeft();
        leftAxis.setDrawAxisLine(true);//显示y轴
        //leftAxis.setTypeface(mTf);
        //leftAxis.setTextColor(R.color.tj3);
        leftAxis.setTextColor(Color.rgb(103, 175, 205));//设置y轴数据颜色
        leftAxis.setLabelCount(4, false);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        Legend mLegend = TJ_chart.getLegend(); // 设置比例图标示，就是那个一组y的value的
        mLegend.setEnabled(false);
        // modify the legend ...
        //mLegend.setPosition(LegendPosition.LEFT_OF_CHART);
        /*mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(6f);// 字体
        mLegend.setTextColor(Color.WHITE);// 颜色*/

        TJ_chart.animateXY(2500, 2500);
//y轴数据
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        for (int i = 0; i < HHlist.size(); i++) {
            float val = Float.parseFloat(HHlist.get(i).getYuliang());//字符串转为浮点型
            Entry entry = new Entry(val, i);
            yVals1.add(entry);
        }
//x轴数据
        ArrayList<String> xValues = new ArrayList<>();
        for (int i = 0; i < HHlist.size(); i++) {
            // x轴显示的数据
            Log.e("warn", HHlist.get(i).getName());
            xValues.add(HHlist.get(i).getName());
        }

        LineDataSet dataSet1; /*= new LineDataSet(yVals1, "七星彩");//设置折线点数据
        dataSet1.setLineWidth(2.5f);//折线的宽
        dataSet1.setCircleSize(5.5f);//折线的圆点大小
        dataSet1.setHighLightColor(Color.rgb(244, 117, 117));*/
        //dataSet1.setDrawValues(true);
        //判断图表中原来是否有数据
        if (TJ_chart.getData() != null && TJ_chart.getData().getDataSetCount() > 0) {
            dataSet1 = (LineDataSet) TJ_chart.getData().getDataSetByIndex(0);
            dataSet1.setYVals(yVals1);//设置图表折线点数据
            TJ_chart.getData().notifyDataChanged();
            TJ_chart.notifyDataSetChanged();
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
            TJ_chart.setData(lineData);//设置折现数据
            //Toast.makeText(getActivity(), "如无数据，请点击屏幕", Toast.LENGTH_LONG).show();

            if(JY_hour==24||JY_hour==0){
                JY_hour = 0;
            }else {
                JY_hour = JY_hour-1;
            }
            TJ_chart.moveViewToX(JY_hour);//将视图移动到x轴上某点




            TJ_chart.setVisibleXRangeMaximum(8);//设置x坐标轴最多显示多少个数据，超出的部分需要滑动才能看到
        }
    }

    //获取每小时雨水数据
    Runnable getYuShuiData = new Runnable() {
        @Override
        public void run() {
            try {
                Log.e("warn", "30");
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
                rpc.addProperty("time", TJBZ_year.getText().toString() + "-" + TJBZ_month.getText().toString() + "-" + TJBZ_day.getText().toString());
                // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);

                AndroidHttpTransport ht = new AndroidHttpTransport(endPoint);
                ht.debug = true;
                Log.e("warn", "50");
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
                Log.e("warn", "60");

                object = (SoapObject) envelope.getResponse();
                Log.e("warn", "64");
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
    private Handler YuWater = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i  = msg.what;
            if(i==0){
                Yuwater_sumBar.setVisibility(View.INVISIBLE);
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "网络或服务器异常", Toast.LENGTH_SHORT).show();
                }
            }else if(i==2){
                Yuwater_sumBar.setVisibility(View.INVISIBLE);
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "无雨量数据", Toast.LENGTH_SHORT).show();
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
                }
                Yuwater_sumBar.setVisibility(View.INVISIBLE);
                setYushuiChart();
            }
        }
    };
    //设置每小时雨水数据图表
    private void setYushuiChart() {
        //设置图表数据
        YuShuiChart.setDescription("");
        YuShuiChart.setDrawGridBackground(false);
        YuShuiChart.setPinchZoom(false);
        YuShuiChart.setTouchEnabled(true);// 设置是否可以触摸
        YuShuiChart.setDragEnabled(true);// 是否可以拖拽
        YuShuiChart.setScaleEnabled(true);// 是否可以缩放
        //Typeface mTf = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "OpenSans-Regular.ttf");
        XAxis xAxis = YuShuiChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setTypeface(mTf);
        xAxis.setTextColor(Color.rgb(103, 175, 205));//设置x轴数据颜色
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisMinValue(-0.5f); //设置x轴坐标起始值为-0.5 防止其实条形图被切去一半
        //xAxis.setLabelRotationAngle(-90);
        YuShuiChart.getAxisRight().setEnabled(false);
        YAxis leftAxis = YuShuiChart.getAxisLeft();
        leftAxis.setDrawAxisLine(true);//显示y轴
        //leftAxis.setTypeface(mTf);
        //leftAxis.setTextColor(R.color.tj3);
        leftAxis.setTextColor(Color.rgb(103, 175, 205));//设置y轴数据颜色
        leftAxis.setLabelCount(4, false);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        Legend mLegend = YuShuiChart.getLegend(); // 设置比例图标示，就是那个一组y的value的
        mLegend.setEnabled(false);
        // modify the legend ...
        //mLegend.setPosition(LegendPosition.LEFT_OF_CHART);
        /*mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(6f);// 字体
        mLegend.setTextColor(Color.WHITE);// 颜色*/

        YuShuiChart.animateXY(2500, 2500);
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
        if (YuShuiChart.getData() != null && YuShuiChart.getData().getDataSetCount() > 0) {
            dataSet1 = (LineDataSet) YuShuiChart.getData().getDataSetByIndex(0);
            dataSet1.setYVals(yVals1);//设置图表折线点数据
            YuShuiChart.getData().notifyDataChanged();
            YuShuiChart.notifyDataSetChanged();
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
            YuShuiChart.setData(lineData);//设置折现数据
            //Toast.makeText(getActivity(), "如无数据，请点击屏幕", Toast.LENGTH_LONG).show();
            if(JY_hour==24||JY_hour==0){
                JY_hour = 0;
            }else {
                JY_hour = JY_hour-1;
            }
            YuShuiChart.moveViewToX(JY_hour);//将视图移动到x轴上某点
            YuShuiChart.setVisibleXRangeMaximum(5);//设置x坐标轴最多显示多少个数据，超出的部分需要滑动才能看到
        }
    }


    //获取每小时污水数据
    Runnable getWuShuiData = new Runnable() {
        @Override
        public void run() {
            try {
                Log.e("warn", "30");
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Get_WuBengZhanHH_List";
                // EndPoint
                String endPoint = Path.get_ZanShibeidouPath();
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_WuBengZhanHH_List";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数CarNum
                rpc.addProperty("time", TJBZ_year.getText().toString() + "-" + TJBZ_month.getText().toString() + "-" + TJBZ_day.getText().toString());
                // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);

                AndroidHttpTransport ht = new AndroidHttpTransport(endPoint);
                ht.debug = true;
                Log.e("warn", "50");
                try {
                    // 调用WebService
                    ht.call(soapAction, envelope);
                } catch (Exception e) {
                    Message msg = Message.obtain();
                    msg.what = 0;
                    WuWater.sendMessage(msg);
                }

                SoapObject object;
                // 开始调用远程方法
                Log.e("warn", "60");

                object = (SoapObject) envelope.getResponse();
                Log.e("warn", "64");
                // 得到服务器传回的数据 返回的数据时集合 每一个count是一个及集合的对象
                int count1 = object.getPropertyCount();
                Log.e("warn", String.valueOf(count1));
                if (count1 == 0) {
                    Message msg = Message.obtain();
                    msg.what = 2;
                    WuWater.sendMessage(msg);
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
                    WuWater.sendMessage(msg);
                }
            } catch (Exception e) {
                Message msg = Message.obtain();
                msg.what = 0;
                WuWater.sendMessage(msg);
            }
        }
    };
    //获取每小时雨水数据
    private List<TongJIYuWaterBeng> Wulist;
    private Handler WuWater = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if(i==0){
                Wuwater_sumBar.setVisibility(View.INVISIBLE);
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "网络或服务器异常", Toast.LENGTH_SHORT).show();
                }
            }else if(i==2){
                Wuwater_sumBar.setVisibility(View.INVISIBLE);
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "无污水数据", Toast.LENGTH_SHORT).show();
                }
            }
            else if(i==1) {
                String str = (String) msg.obj;
                Log.e("warn", "water:" + str);
                Wulist = new ArrayList<>();
                String arr[] = str.split("\\|");
                for (int j = 0; j < arr.length; j++) {
                    TongJIYuWaterBeng bz = new TongJIYuWaterBeng();
                    String arr1[] = arr[j].split(",");
                    bz.setNUM(arr1[0]);
                    bz.setTIME(arr1[1]);
                    Wulist.add(bz);
                }
                Wuwater_sumBar.setVisibility(View.INVISIBLE);
                setWushuiChart();
            }
        }
    };
    //设置每小时污水数据图表
    private void setWushuiChart() {
        //设置图表数据
        WuShuiChart.setDescription("");
        WuShuiChart.setDrawGridBackground(false);
        WuShuiChart.setPinchZoom(false);
        WuShuiChart.setTouchEnabled(true);// 设置是否可以触摸
        WuShuiChart.setDragEnabled(true);// 是否可以拖拽
        WuShuiChart.setScaleEnabled(true);// 是否可以缩放
        //Typeface mTf = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "OpenSans-Regular.ttf");
        XAxis xAxis = WuShuiChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setTypeface(mTf);
        xAxis.setTextColor(Color.rgb(103, 175, 205));//设置x轴数据颜色
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisMinValue(-0.5f); //设置x轴坐标起始值为-0.5 防止其实条形图被切去一半
        //xAxis.setLabelRotationAngle(-90);
        WuShuiChart.getAxisRight().setEnabled(false);
        YAxis leftAxis = WuShuiChart.getAxisLeft();
        leftAxis.setDrawAxisLine(true);//显示y轴
        //leftAxis.setTypeface(mTf);
        //leftAxis.setTextColor(R.color.tj3);
        leftAxis.setTextColor(Color.rgb(103, 175, 205));//设置y轴数据颜色
        leftAxis.setLabelCount(4, false);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        Legend mLegend = WuShuiChart.getLegend(); // 设置比例图标示，就是那个一组y的value的
        mLegend.setEnabled(false);
        // modify the legend ...
        //mLegend.setPosition(LegendPosition.LEFT_OF_CHART);
        /*mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(6f);// 字体
        mLegend.setTextColor(Color.WHITE);// 颜色*/
        WuShuiChart.animateXY(2500, 2500);
        //y轴数据
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        for (int i = 0; i < Wulist.size(); i++) {
            float val = Float.parseFloat(Wulist.get(i).getNUM());//字符串转为浮点型
            Entry entry = new Entry(val, i);
            yVals1.add(entry);
        }
        //x轴数据
        ArrayList<String> xValues = new ArrayList<>();
        for (int i = 0; i < Wulist.size(); i++) {
            // x轴显示的数据
            Log.e("warn", Wulist.get(i).getTIME());
            xValues.add(Wulist.get(i).getTIME());
        }

        LineDataSet dataSet1; /*= new LineDataSet(yVals1, "七星彩");//设置折线点数据
        dataSet1.setLineWidth(2.5f);//折线的宽
        dataSet1.setCircleSize(5.5f);//折线的圆点大小
        dataSet1.setHighLightColor(Color.rgb(244, 117, 117));*/
        //dataSet1.setDrawValues(true);
        //判断图表中原来是否有数据
        if (WuShuiChart.getData() != null && WuShuiChart.getData().getDataSetCount() > 0) {
            dataSet1 = (LineDataSet) WuShuiChart.getData().getDataSetByIndex(0);
            dataSet1.setYVals(yVals1);//设置图表折线点数据
            WuShuiChart.getData().notifyDataChanged();
            WuShuiChart.notifyDataSetChanged();
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
            WuShuiChart.setData(lineData);//设置折现数据
            //Toast.makeText(getActivity(), "如无数据，请点击屏幕", Toast.LENGTH_LONG).show();
            if(JY_hour==24||JY_hour==0){
                JY_hour = 0;
            }else {
                JY_hour = JY_hour-1;
            }
            WuShuiChart.moveViewToX(JY_hour);//将视图移动到x轴上某点
            WuShuiChart.setVisibleXRangeMaximum(5);//设置x坐标轴最多显示多少个数据，超出的部分需要滑动才能看到
        }
    }

}