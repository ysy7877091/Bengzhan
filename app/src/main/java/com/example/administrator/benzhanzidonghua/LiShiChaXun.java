package com.example.administrator.benzhanzidonghua;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dataandtime.data.DatePickerDialog;
import com.vanpeng.javabeen.JiangYuFragmentBeen;
import com.vanpeng.javabeen.PublicInterface;
import com.vanpeng.javabeen.RequestWebService;
import com.vanpeng.javabeen.YuLiangClass;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2016/12/22.
 * 历史查询 雨量 气体 水位
 */

public class LiShiChaXun extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, PublicInterface {
    private MyProgressDialog progressDialog;
    private MyProgressDialog progressDialog1;
    private TextView BJ_BengZhanName;
    //选择开始或结束日期 年月日
    private String year="";
    private String month="";
    private String day="";
    //用于设置标题栏上空白位置的日期
    private String Start="";
    private String end="";
    //标题栏上空白位置的日期
    private TextView BJ_tv;

    private TextView tv_startTime;//开始时间
    private TextView tv_endTime;//结束时间
    private String val;

    private String sub_StartTime="";
    private String sub_EndTime="";

    private String [] arr;//泵站ID
    private String ID;
    private Bundle savedState;

    private RelativeLayout YL_fanwei;//雨量时间设置
    private TextView YL_Name;//雨量站点时间
    private String serviceURL = Path.get_WebServicesYLURL();
    private String bool="";
    private List<JiangYuFragmentBeen> BZ_nameList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedState=savedInstanceState;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.dashujuchaxun_layout);
        CommonMethod.setStatuColor(this,R.color.blue);
        init();
    }
    private void init(){
        Button LS_button = (Button)findViewById(R.id.LS_button);
        LS_button.setOnClickListener(new LiShiChaXunListener());
        TextView LS_change = (TextView)findViewById(R.id.DATA_title_change);
        LS_change.setText("历史记录查询");
        ImageView iv_back=(ImageView)findViewById(R.id.DSJ_back);
        iv_back.setOnClickListener(new LiShiChaXunListener());
        //标题栏上的白框
        BJ_tv=(TextView)findViewById(R.id.Box);
        //泵站名称
        BJ_BengZhanName=(TextView)findViewById(R.id.DSJ_Name);
        BJ_BengZhanName.setOnClickListener(new LiShiChaXunListener());
        //选择开始时间
        tv_startTime=(TextView)findViewById(R.id.DSJ_StartTime);
        //tv_startTime.setOnClickListener(new LiShiChaXunListener());
        //选择结束时间
        tv_endTime=(TextView)findViewById(R.id.DSJ_EndTime);
        //tv_endTime.setOnClickListener(new LiShiChaXunListener());
        //搜索按钮
        ImageButton LS_search = (ImageButton)findViewById(R.id.DSJ_search);
        LS_search.setOnClickListener(new LiShiChaXunListener());

        //Calendar c = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        year = String.valueOf(calendar.get(Calendar.YEAR));
        month = String.valueOf(calendar.get(Calendar.MONTH)+1);
        day =  String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        if(month.length()==1){
          month = "0"+month;
        }
        if(day.length()==1){
            day= "0"+day;
        }

        getIntentData();
        SelectStartTime();
    }
   /* //设置当前时间
    private void setNowTime(){
        Calendar c = Calendar.getInstance();
        int Now_year = c.get(Calendar.YEAR);
        int Now_monthOfYear = c.get(Calendar.MONTH)+1;
        int Now_dayOfMonth= c.get(Calendar.DAY_OF_MONTH);
        SpannableStringBuilder spanBuilder = new SpannableStringBuilder(Now_year + "年" + " " + Now_monthOfYear + "月" + Now_dayOfMonth + "日");
        //style 为0 即是正常的，还有Typeface.BOLD(粗体) Typeface.ITALIC(斜体)等
        //size  为0 即采用原始的正常的 size大小
        spanBuilder.setSpan(new TextAppearanceSpan(null, 0,30,null,null), 0, 5, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        tv_startTime.setText(spanBuilder);
        tv_endTime.setText(spanBuilder);
    }*/
    //获取传递过来的信息
    private void getIntentData(){
        Intent intent = getIntent();
        if(intent!=null) {
            bool = intent.getStringExtra("bool");
            if (bool.equals("0")||bool.equals("00")) {
                YL_Name = (TextView) findViewById(R.id.YL_Name);
                YL_Name.setOnClickListener(new LiShiChaXunListener());
                TextView mingcheng = (TextView)findViewById(R.id.mingcheng);
                mingcheng.setText("选择雨量区域");
                if(bool.equals("00")) {
                    TextView DATA_title_change = (TextView) findViewById(R.id.DATA_title_change);
                    DATA_title_change.setText("雨量大数据");
                }
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        String hourOfDayString  = String.valueOf(year);
        String minuteString = String.valueOf(month+1);
        String dayString = String.valueOf(day);
        if(minuteString.length()==1){
            minuteString="0"+minuteString;
        }
        if(dayString.length()==1){
            dayString = "0"+dayString;
        }
        //Toast.makeText(this, "new time:" +hourOfDayString+"-"+ minuteString+ "-" +dayString, Toast.LENGTH_LONG).show();
        if(startOrend==true){//选择开始时间
            tv_startTime.setText(hourOfDayString+"-"+ minuteString+ "-" +dayString);
            sub_StartTime=hourOfDayString+"-"+ minuteString+ "-" +dayString;
        }else{//选择结束时间
            tv_endTime.setText(hourOfDayString+"-"+ minuteString+ "-" +dayString);
            sub_EndTime=hourOfDayString+"-"+ minuteString+ "-" +dayString;
        }
        BJ_tv.setText(tv_startTime.getText().toString()+" "+"至"+" "+tv_endTime.getText().toString());
    }
    //获取雨量区域名称和ID成功回调
    @Override
    public void onGetDataSuccess(List<JiangYuFragmentBeen> list) {
        this.BZ_nameList = list;
        Message msg = Message.obtain();
        msg.obj="1";
        handlerGetYuLiangList.sendMessage(msg);
    }
    //获取雨量区域名称和ID失败回调
    @Override
    public void onGetDataError(String errmessage) {
        Message msg = Message.obtain();
        msg.obj=errmessage;
        handlerGetYuLiangList.sendMessage(msg);
    }
    //获取雨量区域名称和ID无数据回调
    @Override
    public void onEmptyData(String Emptymessage) {
        Message msg = Message.obtain();
        msg.obj=Emptymessage;
        handlerGetYuLiangList.sendMessage(msg);
    }

    private class LiShiChaXunListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.DSJ_back:

                    break;
               /* case R.id.DSJ_StartTime:  SelectStartTime(0);//0代表开始日期
                                            break;
                case R.id.DSJ_EndTime:SelectStartTime(1);//1代表结束日期
                                        break;*/
                case R.id.DSJ_Name:    if(bool.equals("0")){
                                                                    if(arr_ID!=null&&arr_Name!=null&&arr_ID.length>1&&arr_Name.length>1){
                                                                        waterDialog();
                                                                    }else{
                                                                        SelectYLName();//雨量查询选择站点名称
                                                                    }
                                                                    //SelectYLName();//雨量查询选择站点名称
                                                            } else{
                                                                    //SelectBZName();//选择泵站名
                                                                if(BZ_name!=null&&BZ_name.length>1){
                                                                    setDialog();
                                                                }else{
                                                                    SelectBZName();//选择泵站名
                                                                }
                                                        }
                                                break;
                case R.id.YL_Name://雨量查询选择时间24小时
                                    select24Hour();
                                    break;
                case R.id.DSJ_search: if(bool.equals("0")||bool.equals("00")){//雨量查询
                                                    //String value=YL_Name.getText().toString().trim();
                                                    /*if(value.equals("24小时")){
                                                        String str = BJ_BengZhanName.getText().toString().trim();
                                                        if(str.equals("请选择")){
                                                            Toast.makeText(LiShiChaXun.this, "请选择雨量监控地区", Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            Intent intent = new Intent(LiShiChaXun.this,YuLiangChaXunActivity.class);
                                                            intent.putExtra("ID",ID);
                                                            intent.putExtra("panduan","00");//00代表查询24小时时间内数据
                                                            startActivity(intent);
                                                        }
                                                    }else{*/
                                                        String startTime = tv_startTime.getText().toString().trim();
                                                        String endTime = tv_endTime.getText().toString().trim();
                                                        String val = BJ_BengZhanName.getText().toString().trim();
                                                        if(!val.equals("请选择")) {
                                                            if(!startTime.equals("选择开始日期")&&!endTime.equals("选择结束日期")) {
                                                                Intent intent1 = new Intent(LiShiChaXun.this, YuLiangChaXunActivity.class);
                                                                intent1.putExtra("ID", ID);
                                                                intent1.putExtra("StartTime",tv_startTime.getText().toString());
                                                                intent1.putExtra("EndTime", tv_endTime.getText().toString());
                                                                intent1.putExtra("panduan","11");//11代表查询自己设置的时间内数据
                                                                startActivity(intent1);
                                                            }else{
                                                                Toast.makeText(LiShiChaXun.this, "请选择完整日期", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }else{
                                                            Toast.makeText(LiShiChaXun.this, "请选择雨量监控地区", Toast.LENGTH_SHORT).show();
                                                        }

                                            }else {//气体和水位的查询
                                                String str = BJ_BengZhanName.getText().toString().trim();
                                                String startTime = tv_startTime.getText().toString().trim();
                                                String endTime = tv_endTime.getText().toString().trim();
                                                if (str.equals("请选择")) {
                                                    Toast.makeText(LiShiChaXun.this, "请选择泵站", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    if (startTime.equals("选择开始日期") || endTime.equals("选择结束日期")) {
                                                        Toast.makeText(getApplicationContext(), "请选择日期", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Intent intent = new Intent(LiShiChaXun.this, LiShiMeau.class);
                                                        intent.putExtra("ID", ID);
                                                        intent.putExtra("Name", BJ_BengZhanName.getText().toString().trim());
                                                        intent.putExtra("StartTime", tv_startTime.getText().toString());
                                                        intent.putExtra("EndTime",tv_endTime.getText().toString());
                                                        startActivity(intent);
                                                    }
                                                }
                                            }
                                        break;
                case R.id.LS_button:
                                        Intent intentOK = new Intent();
                                        setResult(2,intentOK);
                                        finish();
                                        break;

            }
        }
    }
    OnlsclikListener myListener;
    //定义点击返回时回调接口 view1向mainactivity传数据时，在view1中定义
    interface OnlsclikListener {
        public void callBack1() ;
    }

    //定义供fragment调用的函数
    public void setOnclikListener(OnlsclikListener Listener) {
        this.myListener =Listener;
    }

   /* //选择开始或结束时间
    private void SelectStartTime(final int state) {

        AlertDialog.Builder builder = new AlertDialog.Builder(LiShiChaXun.this);
        if(state==0) {
            builder.setTitle("请选择开始时间");
        }else if(state==1){
            builder.setTitle("请选择结束时间");
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.baojingtime_layout, null);
        builder.setView(view);
        DatePicker dp = (DatePicker) view.findViewById(R.id.dp);
        Calendar c = Calendar.getInstance();
        int Now_year = c.get(Calendar.YEAR);
        int Now_monthOfYear = c.get(Calendar.MONTH);
        int Now_dayOfMonth= c.get(Calendar.DAY_OF_MONTH);
        //选择默认弹出的当前时间 及 datepicker未改变的时间
        Start_year=String.valueOf(Now_year);
        Start_monthOfYear=String.valueOf(Now_monthOfYear+1);
        Start_dayOfMonth =String.valueOf(Now_dayOfMonth);
        //初始化年月日
        dp.init(Now_year ,Now_monthOfYear,Now_dayOfMonth, new DatePicker.OnDateChangedListener() {
            //改变后的时间 时间改变后才执行这个方法
            public void onDateChanged(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                Start_year = String.valueOf(year);
                Start_monthOfYear = String.valueOf(monthOfYear + 1);
                Start_dayOfMonth = String.valueOf(dayOfMonth);
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //当选择默认当前时间时Start_year Start_monthOfYear Start_dayOfMonth 都为null;
                String year;
                String monthOfYear;
                String dayOfMonth;

                if(state==0) {//开始时间
                    Start=Start_year + "年" + " " + Start_monthOfYear + "月" + Start_dayOfMonth + "日";
                    sub_StartTime=Start_year+"-"+Start_monthOfYear+"-"+Start_dayOfMonth;//提交到服务器上的时间
                    tv_startTime.setText(Start_year + "年" + " " + Start_monthOfYear + "月" + Start_dayOfMonth + "日");
                    tv_startTime.setTextSize(18);
                    SpannableStringBuilder spanBuilder = new SpannableStringBuilder(Start_year + "年" + " " + Start_monthOfYear + "月" + Start_dayOfMonth + "日");
                    //style 为0 即是正常的，还有Typeface.BOLD(粗体) Typeface.ITALIC(斜体)等
                    //size  为0 即采用原始的正常的 size大小
                    spanBuilder.setSpan(new TextAppearanceSpan(null, 0,30,null,null), 0, 5, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    tv_startTime.setText(spanBuilder);

                }else if(state==1){//选择结束时间
                    if(Start.contains(Start_year)&&Start!="") {//判断开始时间和结束时间是否在同一年
                        end = Start_monthOfYear + "月" + Start_dayOfMonth + "日";
                    }else{
                        end = Start_year+"年"+""+Start_monthOfYear + "月" + Start_dayOfMonth + "日";
                    }
                    sub_EndTime=Start_year+"-"+Start_monthOfYear+"-"+Start_dayOfMonth;//提交到服务器上的时间
                    tv_endTime.setText(Start_year + "年" + " " + Start_monthOfYear + "月" + Start_dayOfMonth + "日");
                    tv_endTime.setTextSize(18);
                    SpannableStringBuilder spanBuilder = new SpannableStringBuilder(Start_year + "年" + " " + Start_monthOfYear + "月" + Start_dayOfMonth + "日");
                    //style 为0 即是正常的，还有Typeface.BOLD(粗体) Typeface.ITALIC(斜体)等
                    //size  为0 即采用原始的正常的 size大小
                    spanBuilder.setSpan(new TextAppearanceSpan(null, 0,30,null,null), 0, 5, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    tv_endTime.setText(spanBuilder);
                }else{
                    Toast.makeText(getApplicationContext(),"应用程序错误",Toast.LENGTH_SHORT).show();;
                }
                //标题上的白框
                BJ_tv.setText(Start+" "+"-"+end);
                //Toast.makeText(getApplicationContext(),Start+" "+"-"+end,Toast.LENGTH_SHORT).show();
                BJ_tv.setTextSize(12);

                *//*SpannableStringBuilder spanBuilder1 = new SpannableStringBuilder(Start+" "+"-"+end);
                //style 为0 即是正常的，还有Typeface.BOLD(粗体) Typeface.ITALIC(斜体)等
                //size  为0 即采用原始的正常的 size大小
                spanBuilder1.setSpan(new TextAppearanceSpan(null, 0,20,null,null), 0, 5, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                BJ_tv.setText(spanBuilder1);*//*
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();


    }*/
   //选择开始或结束时间
    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "Starttimepicker";
    private boolean startOrend=true;//用于判断点击了开始时间还是结束时间按钮 开始true结束false
   private void SelectStartTime() {

       final Calendar calendar = Calendar.getInstance();
       //初始化日期时间

       final DatePickerDialog Start_datePickerDialog = DatePickerDialog.newInstance(this,
               calendar.get(Calendar.YEAR),
               calendar.get(Calendar.MONTH),
               calendar.get(Calendar.DAY_OF_MONTH),
               true);
       final DatePickerDialog End_datePickerDialog = DatePickerDialog.newInstance(this,
               calendar.get(Calendar.YEAR),
               calendar.get(Calendar.MONTH),
               calendar.get(Calendar.DAY_OF_MONTH),
               true);

       tv_startTime.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View v) {
               startOrend=true;
               Start_datePickerDialog.setVibrate(true);//是否抖动，推荐true
               Start_datePickerDialog.setYearRange(1985, 2028);//设置年份区间
               Start_datePickerDialog.setCloseOnSingleTapDay(false);//选择后是否消失，推荐false
               Start_datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);//展示dialog，传一个tag参数
           }
       });
       tv_endTime.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startOrend=false;
               End_datePickerDialog.setVibrate(true);//是否抖动，推荐true
               End_datePickerDialog.setYearRange(1985, 2028);//设置年份区间
               End_datePickerDialog.setCloseOnSingleTapDay(false);//选择后是否消失，推荐false
               End_datePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG );//展示dialog，传一个tag参数
           }
       });
       if (savedState != null) {
           DatePickerDialog start = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
           if (start != null) {
               start.setOnDateSetListener(this);
           }
           DatePickerDialog end = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(TIMEPICKER_TAG);
           if (end != null) {
               end.setOnDateSetListener(this);
           }
       }
   }
    private void SelectBZName(){
        progressDialog = new MyProgressDialog(LiShiChaXun.this, true, "加载中..");

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
    private String [] BZ_name;
    public Handler handlerGetBengZhanListNew = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            val = (String) msg.obj;
            Log.e("warn",val);
            if (val.toString().equals("999999")) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "获取泵站失败,网络或者服务器异常", Toast.LENGTH_SHORT).show();
                return;
            }else {
                progressDialog.dismiss();
                String[] objects = val.split("\\|");
                arr= new String [objects.length-10];
                BZ_name = new String [objects.length-10];
                for (int i = 0; i < objects.length-10; i++) {
                    //BZ_name[i]=
                    if (objects[i].length() > 0) {
                        String[] values = objects[i].split(",");
                        if (values.length > 1) {
                            //泵站名
                            Log.e("warn",values[1].toString());
                            BZ_name[i]= values[1].toString();
                            arr[i]=values[0].toString();
                        }
                    }
                }
                setDialog();
                /*AlertDialog.Builder builder = new AlertDialog.Builder(LiShiChaXun.this);
                builder.setTitle("请选择");
                builder.setSingleChoiceItems(BZ_name, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String val = BZ_name[i];
                        BJ_BengZhanName.setText(val);
                        ID=arr[i];
                        dialogInterface.dismiss();
                    }
                });
                builder.show();*/
            }

        }
    };
    private void setDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LiShiChaXun.this);
        builder.setTitle("请选择");
        builder.setSingleChoiceItems(BZ_name, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String val = BZ_name[i];
                BJ_BengZhanName.setText(val);
                ID=arr[i];
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void SelectYLName() {
        progressDialog1=new MyProgressDialog(LiShiChaXun.this, true, "加载中..");
        new YuliangQuyu().getShopsData(this);
    }

    private String arr_Name [];//装载雨量站点名称
    private  String arr_ID [];//装载雨量站点ID
    private void closeDialog(){
        if( progressDialog1!=null){
            progressDialog1.dismiss();
        }
    }
    Handler handlerGetYuLiangList = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String val = (String) msg.obj;

            if (val.toString().equals("1")) {
                closeDialog();
                arr_Name=new String [BZ_nameList.size()];//装载雨量站点名称
                arr_ID=new String [BZ_nameList.size()];//装载雨量站点ID
                for (int i = 0; i < BZ_nameList.size(); i++) {
                    if (BZ_nameList.size() > 0) {
                        if (BZ_nameList.size() > 1) {
                            arr_Name[i]=BZ_nameList.get(i).getNAME();
                            arr_ID [i]=BZ_nameList.get(i).getID();
                        }
                    }
                }
                waterDialog();
            }else{
                closeDialog();
                Toast.makeText(getApplicationContext(), val, Toast.LENGTH_SHORT).show();
            }

        }
    };
    private void waterDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LiShiChaXun.this);
        builder.setTitle("请选择");
        builder.setSingleChoiceItems(arr_Name, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String val = arr_Name[i];
                BJ_BengZhanName.setText(val);
                ID=arr_ID[i];
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
    //雨量查询选择时间24小时
    private void select24Hour(){
        final String [] arr_Time = {"24小时","无限制"};
        AlertDialog.Builder builder = new AlertDialog.Builder(LiShiChaXun.this);
        builder.setTitle("请选择");
        builder.setSingleChoiceItems(arr_Time, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String val = arr_Time[i];
                YL_Name.setText(val);
                dialogInterface.dismiss();
                if(val.equals("24小时")){
                    tv_startTime.setEnabled(false);
                    tv_endTime.setEnabled(false);
                    tv_startTime.setText(year+"-"+month+"-"+day);
                    tv_endTime.setText(year+"-"+month+"-"+day);
                    BJ_tv.setText(year+"-"+month+"-"+day+" "+"至"+" "+year+"-"+month+"-"+day);
                }else{
                    tv_startTime.setEnabled(true);
                    tv_endTime.setEnabled(true);
                }
            }
        });
        builder.show();
    }
}
