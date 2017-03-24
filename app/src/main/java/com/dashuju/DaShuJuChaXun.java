package com.dashuju;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.benzhanzidonghua.CommonMethod;
import com.example.administrator.benzhanzidonghua.MyProgressDialog;
import com.example.administrator.benzhanzidonghua.Path;
import com.example.administrator.benzhanzidonghua.R;

import java.util.Calendar;

/**
 * Created by Administrator on 2016/12/22.
 * 大数据查询
 */

public class DaShuJuChaXun extends AppCompatActivity {
    private MyProgressDialog progressDialog;
    private String Start_year = "";
    private String Start_monthOfYear = "";
    private String Start_dayOfMonth = "";
    private String Start = "";
    private String end = "";
    //标题栏上空白位置的日期
    private TextView BJ_tv;
    private String val;
    private String sub_StartTime = "";
    private String sub_EndTime = "";
    private String[] arr;//泵站ID
    private String ID;
    private TextView tv_bzName;
    private TextView tv_startTime;
    private TextView tv_endTime;
    private ImageView iv_back;
    private ImageButton ib_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.dashujuchaxunlayout);
        init();
    }

    private void init() {
        Button BZDC_button=(Button)findViewById(R.id.BZDC_button);
        BZDC_button.setOnClickListener(new DaShuJuChaXunListener());
        //iv_back = (ImageView) findViewById(R.id.DJ_back);
        tv_startTime = (TextView) findViewById(R.id.DJ_StartTime);
        BJ_tv = (TextView) findViewById(R.id.Boxx);
        tv_endTime = (TextView) findViewById(R.id.DJ_EndTime);
        tv_bzName = (TextView) findViewById(R.id.DJ_Name);
        ib_search = (ImageButton) findViewById(R.id.DJ_search);
        //iv_back.setOnClickListener(new DaShuJuChaXunListener());
        tv_startTime.setOnClickListener(new DaShuJuChaXunListener());
        tv_endTime.setOnClickListener(new DaShuJuChaXunListener());
        tv_bzName.setOnClickListener(new DaShuJuChaXunListener());
        ib_search.setOnClickListener(new DaShuJuChaXunListener());
    }

    private class DaShuJuChaXunListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.BZDC_button:
                    finish();
                    break;
                case R.id.DJ_StartTime:
                    SelectStartTime(0);
                    break;
                case R.id.DJ_EndTime:
                    SelectStartTime(1);
                    break;
                case R.id.DJ_Name:
                    SelectBZName();
                    break;
                case R.id.DJ_search:
                    String str = tv_bzName.getText().toString().trim();
                    String startTime = tv_startTime.getText().toString().trim();
                    String endTime = tv_endTime.getText().toString().trim();
                    if (str.equals("全部")) {
                        if (startTime.equals("选择开始日期") || endTime.equals("选择结束日期")) {
                            Toast.makeText(getApplicationContext(), "请选择日期", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(DaShuJuChaXun.this, DaShuJUListActivity.class);
                            intent.putExtra("val", val);
                            intent.putExtra("sub_StartTime", sub_StartTime);
                            intent.putExtra("sub_EndTime", sub_EndTime);
                            startActivity(intent);
                        }
                    } else if (str.equals("请选择")) {
                        Toast.makeText(DaShuJuChaXun.this, "请选择泵站", Toast.LENGTH_SHORT).show();
                    } else {
                        if (startTime.equals("选择开始日期") || endTime.equals("选择结束日期")) {
                            Toast.makeText(getApplicationContext(), "请选择日期", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(DaShuJuChaXun.this, Simple_BZ.class);
                            intent.putExtra("ID", ID);
                            intent.putExtra("Name", tv_bzName.getText().toString().trim());
                            intent.putExtra("StartTime", sub_StartTime);
                            intent.putExtra("EndTime", sub_EndTime);
                            startActivity(intent);
                        }
                    }
                    break;
            }
        }
    }

    private void SelectBZName() {
        progressDialog = new MyProgressDialog(DaShuJuChaXun.this, true, "加载中..");

        Runnable networkGetBengZhanInfoNew = new Runnable() {
            @Override
            public void run() {
                String path = Path.get_WebServicesURL();
                String methodName = "AppGetBengZhanListResult";
                String SoapFileName = "assets/appgetbenzhanlist.xml";
                String soap = CommonMethod.ReadSoap(SoapFileName);
                try {
                    Log.e("warn", soap);
                    byte[] date = soap.getBytes();
                    String result = CommonMethod.Request(path, date, methodName);
                    Message msg = Message.obtain();
                    msg.obj = result;
                    handlerGetBengZhanListNew.sendMessage(msg);
                    Log.d("DEBUG", "获取泵站列表WebService结果：" + result.toString());
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.obj = "999999";
                    handlerGetBengZhanListNew.sendMessage(msg);
                }
            }
        };
        new Thread(networkGetBengZhanInfoNew).start();
    }

    public Handler handlerGetBengZhanListNew = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final String[] BZ_name;
            val = (String) msg.obj;
            if (val.toString().equals("999999")) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "获取泵站失败,网络或者服务器异常", Toast.LENGTH_SHORT).show();
                return;
            } else {
                progressDialog.dismiss();
                String[] objects = val.split("\\|");
                arr = new String[17];
                BZ_name = new String[18];
                for (int i = 0; i < objects.length - 10; i++) {

                    if (objects[i].length() > 0) {
                        String[] values = objects[i].split(",");
                        if (values.length > 1) {
                            //泵站名
                            BZ_name[i + 1] = values[1].toString();
                            arr[i] = values[0].toString();
                        }
                    }
                }
                BZ_name[0] = "全部";
                AlertDialog.Builder builder = new AlertDialog.Builder(DaShuJuChaXun.this);
                builder.setTitle("请选择");
                builder.setSingleChoiceItems(BZ_name, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String val = BZ_name[i];
                        tv_bzName.setText(val);
                        if (!val.equals("全部")) {
                            ID = arr[i - 1];
                        }else{
                            ID = arr[i];
                        }
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        }
    };

    //选择开始或结束时间
    private void SelectStartTime(final int state) {

        AlertDialog.Builder builder = new AlertDialog.Builder(DaShuJuChaXun.this);
        if (state == 0) {
            builder.setTitle("请选择开始时间");
        } else if (state == 1) {
            builder.setTitle("请选择结束时间");
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.baojingtime_layout, null);
        builder.setView(view);
        DatePicker dp = (DatePicker) view.findViewById(R.id.dp);
        Calendar c = Calendar.getInstance();
        int Now_year = c.get(Calendar.YEAR);
        int Now_monthOfYear = c.get(Calendar.MONTH);
        int Now_dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        //选择默认弹出的当前时间 及 datepicker未改变的时间
        Start_year = String.valueOf(Now_year);
        Start_monthOfYear = String.valueOf(Now_monthOfYear + 1);
        Start_dayOfMonth = String.valueOf(Now_dayOfMonth);
        //初始化年月日
        dp.init(Now_year, Now_monthOfYear, Now_dayOfMonth, new DatePicker.OnDateChangedListener() {
            //改变后的时间 时间改变后才执行这个方法
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Start_year = String.valueOf(year);
                Start_monthOfYear = String.valueOf(monthOfYear + 1);
                Start_dayOfMonth = String.valueOf(dayOfMonth);
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //当选择默认当前时间时Start_year Start_monthOfYear Start_dayOfMonth 都为null;

                if (state == 0) {
                    Start = Start_year + "年" + " " + Start_monthOfYear + "月" + Start_dayOfMonth + "日";
                    sub_StartTime = Start_year + "-" + Start_monthOfYear + "-" + Start_dayOfMonth;//提交到服务器上的时间
                    tv_startTime.setText(Start_year + "年" + " " + Start_monthOfYear + "月" + Start_dayOfMonth + "日");
                    tv_startTime.setTextSize(18);
                    SpannableStringBuilder spanBuilder = new SpannableStringBuilder(Start_year + "年" + " " + Start_monthOfYear + "月" + Start_dayOfMonth + "日");
                    spanBuilder.setSpan(new TextAppearanceSpan(null, 0, 30, null, null), 0, 5, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    tv_startTime.setText(spanBuilder);
                } else if (state == 1) {

                    end = Start_year + "年" + "" + Start_monthOfYear + "月" + Start_dayOfMonth + "日";

                    sub_EndTime = Start_year + "-" + Start_monthOfYear + "-" + Start_dayOfMonth;//提交到服务器上的时间
                    tv_endTime.setText(Start_year + "年" + " " + Start_monthOfYear + "月" + Start_dayOfMonth + "日");
                    tv_endTime.setTextSize(18);
                    SpannableStringBuilder spanBuilder = new SpannableStringBuilder(Start_year + "年" + " " + Start_monthOfYear + "月" + Start_dayOfMonth + "日");
                    spanBuilder.setSpan(new TextAppearanceSpan(null, 0, 30, null, null), 0, 5, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    tv_endTime.setText(spanBuilder);
                } else {
                    Toast.makeText(getApplicationContext(), "应用程序错误", Toast.LENGTH_SHORT).show();

                }
                //标题上的白框
                BJ_tv.setText(Start + "-" + end);
                //Toast.makeText(getApplicationContext(), Start + " " + "-" + end, Toast.LENGTH_SHORT).show();
                BJ_tv.setTextSize(12);
                /*SpannableStringBuilder spanBuilder1 = new SpannableStringBuilder(Start + " " + "-" + end);
                spanBuilder1.setSpan(new TextAppearanceSpan(null, 0, 20, null, null), 0, 5, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                BJ_tv.setText(spanBuilder1);*/
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
}
