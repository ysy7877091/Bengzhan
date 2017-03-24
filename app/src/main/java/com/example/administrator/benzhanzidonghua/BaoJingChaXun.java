package com.example.administrator.benzhanzidonghua;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dataandtime.data.DatePickerDialog;
import com.dataandtime.time.RadialPickerLayout;
import com.dataandtime.time.TimePickerDialog;

import java.util.Calendar;

/**
 * Created by Administrator on 2016/12/22.
 * 报警查询activity
 */

public class BaoJingChaXun extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private MyProgressDialog progressDialog;
    private TextView BJ_BengZhanName;
    //选择开始或结束日期 年月日
    private String Start_year="";
    private String Start_monthOfYear="";
    private String Start_dayOfMonth="";
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.baojingchaxun_layout);
        savedState = savedInstanceState;
        CommonMethod cm = new CommonMethod();
        cm.setStatuColor(this,R.color.blue);
        init();
    }
    private void init(){
        BJ_tv=(TextView) findViewById(R.id.BJ_tv);
        //BJ_tv.setText(Start+" "+"-"+end);
        //返回
        Button BJM_button = (Button)findViewById(R.id.BJM_button);
        BJM_button.setOnClickListener(new BaoJingChaXunListener());
        /*ImageView BJ_back = (ImageView) findViewById(BJ_back);
        BJ_back.setOnClickListener(new BaoJingChaXunListener());*/
        //选择开始时间
        tv_startTime = (TextView)findViewById(R.id.StartTime);
        //tv_startTime.setOnClickListener(new BaoJingChaXunListener());
        //选择结束时间
        tv_endTime = (TextView)findViewById(R.id.EndTime);
        //tv_endTime.setOnClickListener(new BaoJingChaXunListener());
        //搜索按钮
        ImageButton ib_serachButton=(ImageButton)findViewById(R.id.BaoJing_search);
        ib_serachButton.setOnClickListener(new BaoJingChaXunListener());
        //选择本站名称
        BJ_BengZhanName=(TextView) findViewById(R.id.BJ_BengZhanName);
        BJ_BengZhanName.setOnClickListener(new BaoJingChaXunListener());
        SelectStartTime();//自定义时间选择
    }
    //自定义选择日期事件
    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        String hourOfDayString  = String.valueOf(year);
        String minuteString = String.valueOf(month);
        String dayString = String.valueOf(day);
        if(minuteString.length()==1){
            minuteString="0"+minuteString;
        }
        if(dayString.length()==1){
            hourOfDayString = "0"+hourOfDayString;
        }
        //Toast.makeText(this, "new time:" +hourOfDayString+"-"+ minuteString+ "-" +dayString, Toast.LENGTH_LONG).show();
            if(startOrend==true){//选择开始时间
                tv_startTime.setText(hourOfDayString+"-"+ minuteString+ "-" +dayString);
                sub_StartTime=hourOfDayString+"-"+ minuteString+ "-" +dayString;
            }else{//选择结束时间
                tv_endTime.setText(hourOfDayString+"-"+ minuteString+ "-" +dayString);
                sub_EndTime=hourOfDayString+"-"+ minuteString+ "-" +dayString;
            }
        BJ_tv.setText(sub_StartTime+" "+"至"+" "+sub_EndTime);
    }

    private class BaoJingChaXunListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.BJM_button:
                                    finish();
                                    break;
                /*case R.id.StartTime:
                                    SelectStartTime(0);//0代表开始日期
                                    break;
                case R.id.EndTime:
                                    SelectStartTime(1);//1代表结束日期
                                    break;*/
                case R.id.BaoJing_search:
                                            String str = BJ_BengZhanName.getText().toString().trim();
                                            String startTime =tv_startTime.getText().toString().trim();
                                            String endTime = tv_endTime.getText().toString().trim();
                                            if(str.equals("全部")){
                                                if(startTime.equals("选择开始日期")||endTime.equals("选择结束日期")){
                                                    Toast.makeText(getApplicationContext(),"请选择日期",Toast.LENGTH_SHORT).show();
                                                }else{
                                                    Intent intent = new Intent(BaoJingChaXun.this,BaoJingListActivity.class);
                                                    intent.putExtra("val",val);
                                                    intent.putExtra("sub_StartTime",sub_StartTime);
                                                    intent.putExtra("sub_EndTime",sub_EndTime);
                                                    startActivity(intent);
                                                }
                                            }else if(str.equals("请选择")){
                                                Toast.makeText(BaoJingChaXun.this, "请选择泵站", Toast.LENGTH_SHORT).show();
                                            }else{
                                                if(startTime.equals("选择开始日期")||endTime.equals("选择结束日期")){
                                                    Toast.makeText(getApplicationContext(),"请选择日期",Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Intent intent = new Intent(BaoJingChaXun.this, BaoJingMeauActivity.class);
                                                    intent.putExtra("ID",ID);
                                                    intent.putExtra("Name",BJ_BengZhanName.getText().toString().trim());
                                                    intent.putExtra("StartTime",sub_StartTime);
                                                    intent.putExtra("EndTime",sub_EndTime);
                                                    startActivity(intent);
                                                }
                                            }
                                            break;
                case R.id.BJ_BengZhanName:
                                            SelectBZName();
                                            break;
            }
        }
    }
    private void SelectBZName(){
        progressDialog = new MyProgressDialog(BaoJingChaXun.this, true, "加载中..");

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
    public Handler handlerGetBengZhanListNew = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final String [] BZ_name;
            val = (String) msg.obj;
            if (val.toString().equals("999999")) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "获取泵站失败,网络或者服务器异常", Toast.LENGTH_SHORT).show();
                return;
            }else {
                progressDialog.dismiss();
                String[] objects = val.split("\\|");
                arr= new String [objects.length-10];
                BZ_name = new String [objects.length-10+1];
                for (int i = 0; i < objects.length-10; i++) {
                    //BZ_name[i]=
                    if (objects[i].length() > 0) {
                        String[] values = objects[i].split(",");
                        if (values.length > 1) {
                            //泵站名
                            BZ_name[i+1]= values[1].toString();
                            arr[i]=values[0].toString();
                        }
                        }
                }
                BZ_name[0]="全部";
                AlertDialog.Builder builder = new AlertDialog.Builder(BaoJingChaXun.this);
                builder.setTitle("请选择");
                builder.setSingleChoiceItems(BZ_name, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String val = BZ_name[i];
                        BJ_BengZhanName.setText(val);
                        if(!val.equals("全部")) {//判断是否选的是全部泵站
                            ID = arr[i-1];
                        }
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }

        }
    };
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
                End_datePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);//展示dialog，传一个tag参数
            }
        });
        if (savedState != null) {
            DatePickerDialog start = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (start != null) {
                start.setOnDateSetListener(this);
            }
            DatePickerDialog end = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(TIMEPICKER_TAG);
            if (end!= null) {
                end.setOnDateSetListener(this);
            }
            //选择开始时间事件
        }
           /* AlertDialog.Builder builder = new AlertDialog.Builder(BaoJingChaXun.this);
            if(state==0) {
                builder.setTitle("请选择开始时间");
            }else if(state==1){
                builder.setTitle("请选择结束时间");
            }
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.baojingtime_layout, null);
            builder.setView(view);
            DatePicker dp = (DatePicker) view.findViewById(R.id.dp);*/
            /*Calendar c = Calendar.getInstance();
            int Now_year = c.get(Calendar.YEAR);
            int Now_monthOfYear = c.get(Calendar.MONTH);
            int Now_dayOfMonth= c.get(Calendar.DAY_OF_MONTH);*/



           /* //选择默认弹出的当前时间 及 datepicker未改变的时间
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
            });*/
        /*builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
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
                BJ_tv.setTextSize(15);
                SpannableStringBuilder spanBuilder1 = new SpannableStringBuilder(Start+" "+"-"+end);
                //style 为0 即是正常的，还有Typeface.BOLD(粗体) Typeface.ITALIC(斜体)等
                //size  为0 即采用原始的正常的 size大小
                spanBuilder1.setSpan(new TextAppearanceSpan(null, 0,20,null,null), 0, 5, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                BJ_tv.setText(spanBuilder1);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
*/
    }

}
