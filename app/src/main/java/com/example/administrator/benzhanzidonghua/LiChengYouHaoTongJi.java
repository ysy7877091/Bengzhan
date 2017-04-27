package com.example.administrator.benzhanzidonghua;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
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

import com.com.vanpeng.Adapter.BDListViewAdapter;
import com.dashuju.DaShuJUListActivity;
import com.dashuju.DaShuJuChaXun;
import com.dashuju.Simple_BZ;
import com.vanpeng.javabeen.BeiDouCarLieBiaoBeen;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2017/3/24.
 *
 */

public class LiChengYouHaoTongJi extends AppCompatActivity implements View.OnClickListener {

    private List<BeiDouCarLieBiaoBeen> list=null;
    private MyProgressDialog progressDialog;
    private String Start_year = "";
    private String Start_monthOfYear = "";
    private String Start_dayOfMonth = "";
    private String Start = "";
    private String end = "";
    private StringBuffer sb;
    //标题栏上空白位置的日期
    private TextView LiChengYouHao_tv;
    private String sub_StartTime = "";
    private String sub_EndTime = "";
    private TextView tv_LiChengCarNumber;
    private TextView tv_startTime;
    private TextView tv_endTime;
    private ImageButton ib_search;
    private ImageView liChengYouHao_search;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.licheng_youhao_tongji);
        init();
    }

    private void init() {
        Button LiChengYouHao_back = (Button) findViewById(R.id.LiChengYouHao_back);
        LiChengYouHao_back.setOnClickListener(this);
        tv_startTime = (TextView) findViewById(R.id.LiChengYouHao_StartTime);
        LiChengYouHao_tv = (TextView) findViewById(R.id.LiChengYouHao_tv);
        tv_endTime = (TextView) findViewById(R.id.LiChengYouHao_EndTime);
        tv_LiChengCarNumber = (TextView) findViewById(R.id.tv_LiChengCarNumber);
        ib_search = (ImageButton) findViewById(R.id.LiChengYouHao_search);
        //liChengYouHao_search = (ImageView) findViewById(R.id.LiChengYouHao_Search);
        tv_startTime.setOnClickListener(this);
        tv_endTime.setOnClickListener(this);
        tv_LiChengCarNumber.setOnClickListener(this);
        ib_search.setOnClickListener(this);
        //liChengYouHao_search.setOnClickListener(this);
    }

    private void SelectStartTime(final int state) {

        AlertDialog.Builder builder = new AlertDialog.Builder(LiChengYouHaoTongJi.this);
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
                LiChengYouHao_tv.setText(Start + "-" + end);
                LiChengYouHao_tv.setTextSize(12);
              // SpannableStringBuilder spanBuilder1 = new SpannableStringBuilder(Start + " " + "-" + end);
               // spanBuilder1.setSpan(new TextAppearanceSpan(null, 0, 20, null, null), 0, 5, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
               // LiChengYouHao_tv.setText(spanBuilder1);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.LiChengYouHao_back:
                finish();
                break;
            case R.id.LiChengYouHao_StartTime:
                SelectStartTime(0);
                break;
           /* case R.id.LiChengYouHao_Search:
                String str1 = tv_LiChengCarNumber.getText().toString().trim();
                String startTime1 = tv_startTime.getText().toString().trim();
                String endTime1 = tv_endTime.getText().toString().trim();

                if (str1.equals("请选择")) {
                    Toast.makeText(LiChengYouHaoTongJi.this, "请选择车牌号", Toast.LENGTH_SHORT).show();
                } else {
                    if (startTime1.equals("选择开始日期") || endTime1.equals("选择结束日期")) {
                        Toast.makeText(getApplicationContext(), "请选择日期", Toast.LENGTH_SHORT).show();
                    } else {

                        Intent intent = new Intent(LiChengYouHaoTongJi.this, lichengyouhao_onclick.class);

                        intent.putExtra("Name", tv_LiChengCarNumber.getText().toString().trim());
                        intent.putExtra("StartTime", sub_StartTime);
                        intent.putExtra("EndTime", sub_EndTime);

                        startActivity(intent);
                    }
                }
                break;*/
            case R.id.LiChengYouHao_EndTime:
                SelectStartTime(1);
                break;
            case R.id.LiChengYouHao_search:

                String str = tv_LiChengCarNumber.getText().toString().trim();
                String startTime = tv_startTime.getText().toString().trim();
                String endTime = tv_endTime.getText().toString().trim();

                if (str.equals("请选择")) {
                    Toast.makeText(LiChengYouHaoTongJi.this, "请选择车牌号", Toast.LENGTH_SHORT).show();
                } else {
                    if (startTime.equals("选择开始日期") || endTime.equals("选择结束日期")) {
                        Toast.makeText(getApplicationContext(), "请选择日期", Toast.LENGTH_SHORT).show();
                    } else {

                        Intent intent = new Intent(LiChengYouHaoTongJi.this, lichengyouhao_onclick.class);

                        intent.putExtra("Name", tv_LiChengCarNumber.getText().toString().trim());
                        intent.putExtra("StartTime", sub_StartTime);
                        intent.putExtra("EndTime", sub_EndTime);

                        startActivity(intent);
                    }
                }
                break;
            case R.id.tv_LiChengCarNumber:
                SelectCarNumber();
                break;
        }
    }
    private void SelectCarNumber() {
        progressDialog = new MyProgressDialog(LiChengYouHaoTongJi.this, true, "加载中..");

        Runnable RequestLiCheng = new Runnable() {
            public void run() {
                try{
                    Log.e("warn","30");
                    // 命名空间
                    String nameSpace = "http://tempuri.org/";
                    // 调用的方法名称
                    String methodName = "Get_CarNoPic_List";
                    // EndPoint
                    String endPoint = Path.get_ZanShibeidouPath();
                    // SOAP Action
                    String soapAction = "http://tempuri.org/Get_CarNoPic_List";
                    // 指定WebService的命名空间和调用的方法名
                    SoapObject rpc = new SoapObject(nameSpace, methodName);
                    //设置需调用WebService接口需要传入的参数CarNum
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(rpc);

                    AndroidHttpTransport ht = new AndroidHttpTransport(endPoint);
                    ht.debug=true;
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
                    object = (SoapObject) envelope.getResponse();
                    // 得到服务器传回的数据 返回的数据时集合 每一个count是一个及集合的对象
                    int count1 = object.getPropertyCount();
                    if(count1>0) {
                        sb = new StringBuffer();
                        for (int i = 0; i < count1; i++) {

                            SoapObject soapProvince = (SoapObject)object.getProperty(i);

                            sb.append(soapProvince.getProperty("CARNUM").toString()+",");
                            sb.append(soapProvince.getProperty("CARTYPE").toString()+",");
                            sb.append(soapProvince.getProperty("PERID").toString()+",");
                            sb.append(soapProvince.getProperty("NAME").toString()+",");
                            sb.append(soapProvince.getProperty("TELNUMBER").toString()+",");

                            if(i==count1-1) {
                                if(soapProvince.getProperty("ONLINE").toString().equals("0")) {
                                    sb.append("在线");
                                }else {
                                    sb.append("不在线");
                                }
                            }else{
                                if(soapProvince.getProperty("ONLINE").toString().equals("0")) {
                                    sb.append("在线"+"|");
                                }else {
                                    sb.append("不在线"+"|");
                                }
                            }
                        }
                        Message msg = Message.obtain();
                        msg.what=1;
                        hanlder.sendMessage(msg);
                    }
                } catch (Exception e){
                    Message msg = Message.obtain();
                    msg.what=0;
                    hanlder.sendMessage(msg);
                }
            }
        };
        new Thread(RequestLiCheng).start();
    }

    private android.os.Handler hanlder =new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final String[] BZ_name;
            int i = msg.what;
            if (i == 0) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "网络或服务器异常", Toast.LENGTH_SHORT).show();
            } else if (i == 1) {
                progressDialog.dismiss();
                Log.e("warn", sb.toString());
                String arr[] = sb.toString().split("\\|");
                BZ_name = new String[arr.length];
                for (int j = 0; j < arr.length; j++) {
                    String arr1[] = arr[j].split(",");
                    BZ_name[j]= arr1[0].toString();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(LiChengYouHaoTongJi.this);
                builder.setTitle("请选择");
                builder.setSingleChoiceItems(BZ_name, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String val = BZ_name[i];
                        tv_LiChengCarNumber.setText(val);
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        }
    };
}
