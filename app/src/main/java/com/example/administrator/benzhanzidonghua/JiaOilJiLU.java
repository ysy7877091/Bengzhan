package com.example.administrator.benzhanzidonghua;

import android.app.Fragment;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.com.vanpeng.Adapter.AddOilHistoryAdapter;
import com.dataandtime.data.DatePickerDialog;
import com.vanpeng.javabeen.AddOilHistoryBeen;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2017/4/1.
 * 加油记录
 */

public class JiaOilJiLU extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private ListView lv_jiaYouJiLu;
    private Bundle savedState;
    private boolean StartOrEndTime=false;
    private MyProgressDialog progressDialog;
    private String power=null;
    private String SiJiPERID=null;
    private String sijiCARnum=null;
    private RelativeLayout top;
    private PopupWindow popupwindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.jiayoujilu_layout);
        savedState=savedInstanceState;
        init();
        mHandler.postDelayed(mRunnable, 500);
    }
    private void init(){
        Intent intent = getIntent();
        String str2 =intent.getStringExtra("personInformation");
        Log.e("warn",str2+"：加油");
        String arr[] =str2.split(";");
        Log.e("warn",String.valueOf(arr.length));
        for(int j=0;j<arr.length;j++){
            arr[j]=arr[j].substring(arr[j].indexOf("=")+1);
            Log.e("warn",arr[j]);
        }
        if(str2.contains("Power")){
            if(!arr[7].equals("anyType{}")){
                power = arr[7];
            }
        }
        if(str2.contains("CARNUM")){
            if(!arr[9].equals("anyType{}")){
                sijiCARnum = arr[9];
            }
        }
        if(str2.contains("PERID")){
            if(!arr[8].equals("anyType{}")){
                SiJiPERID = arr[8];
            }
        }
        Button OilMeau_left = (Button)findViewById(R.id.OilMeau_left);
        OilMeau_left.setOnClickListener(new JiaOilJiLUListener());
        Button OilMeau_Right = (Button)findViewById(R.id.OilMeau_Right);
        OilMeau_Right.setOnClickListener(new JiaOilJiLUListener());
        lv_jiaYouJiLu= (ListView)findViewById(R.id.lv_jiaYouJiLu);
        TextView Oil_search_TV = (TextView)findViewById(R.id.Oil_search_TV);//搜索框
        Oil_search_TV.setOnClickListener(new JiaOilJiLUListener());
        ImageView Oil_search_IV = (ImageView)findViewById(R.id.Oil_search_IV);//搜索图片
        //Oil_search_IV.setOnClickListener(new JiaOilJiLUListener());
        top = (RelativeLayout) findViewById(R.id.addOil_top);

        /*progressDialog = new MyProgressDialog(JiaOilJiLU.this, false, "加载中...");
        if(power.equals("1")){//司机
            new Thread(SiJi).start();
        }else {//领导或者普通用户
            new Thread(add_OilHistory).start();
        }*/
        list =new ArrayList<>();
        aa = new AddOilHistoryAdapter(JiaOilJiLU.this, list);
        lv_jiaYouJiLu.setAdapter(aa);
        //BD_TimepopWindow();
    }
    //activity刚加载的时候不能弹出popwindow  可以用如下方法解决 activity加载完成之后
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        public void run() {
            // 弹出PopupWindow的具体代码
            BD_TimepopWindow();
        }
    };

    //选择时间后的结果
    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        String monthString  = String.valueOf(month+1);
        String dayString = String.valueOf(day);
        if(monthString.length()==1){
            monthString="0"+monthString;
        }
        if(dayString.length()==1){
            dayString = "0"+dayString;
        }
        if(StartOrEndTime){//结束时间
            //Toast.makeText(MainActivity.this, "new date:" + year + "-" + monthString + "-" + dayString, Toast.LENGTH_LONG).show();
            Oil_End_timeHour.setText(year + "-" + monthString + "-" + dayString);
        }else{
            Oil_Start_timeHour.setText(year + "-" + monthString + "-" + dayString);
        }
    }

    private class JiaOilJiLUListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.OilMeau_left:finish();break;
                case R.id.OilMeau_Right:
                                            Intent intent = new Intent(JiaOilJiLU.this,AddOilHistoryWrite.class);
                                            intent.putExtra("ALLcarNum",getIntent().getStringExtra("ALLcarNum"));
                                            intent.putExtra("personInformation",getIntent().getStringExtra("personInformation"));
                                            startActivityForResult(intent,0);
                                            break;
                case R.id.Oil_search_TV:
                                            BD_TimepopWindow();
                                            break;
                //case R.id.Oil_search_IV:break;
                case R.id.OilHistory_carNum:
                                            getcarNum();//获取所有车牌号
                                            break;
                case R.id.Oil_chaxun: closePopwindow();
                                        String carNum = OilHistory_carNum.getText().toString();
                                        if(carNum.equals("全部")){

                                            progressDialog = new MyProgressDialog(JiaOilJiLU.this, false, "加载中...");
                                                if(power.equals("1")){//司机
                                                    new Thread(SiJi).start();
                                                }else{//领导或者普通用户
                                                    new Thread(add_OilHistory).start();
                                                }
                                                    /*list.clear();
                                                    for(AddOilHistoryBeen all:Alllist){
                                                        list.add(all);
                                                    }
                                                    aa.notifyDataSetChanged();*/

                                        }else{
                                            getSerachData();
                                        }
                                        break;

            }
        }
    }

  Runnable add_OilHistory  = new Runnable() {
      StringBuffer sb;
      @Override
      public void run() {
          try {



              Log.e("warn", "30");
              // 命名空间
              String nameSpace = "http://tempuri.org/";
              // 调用的方法名称Get_OilAlarmInfo_List
              String methodName = "Get_CheckJYRecord";
              // EndPoint
              String endPoint = Path.get_ZanShibeidouPath();
              // SOAP Action
              String soapAction = "http://tempuri.org/Get_CheckJYRecord";
              // 指定WebService的命名空间和调用的方法名
              SoapObject rpc = new SoapObject(nameSpace, methodName);
              //设置需调用WebService接口需要传入的参数CarNum
              //rpc.addProperty("CARNUM",OilHistory_carNum.getText().toString());
              rpc.addProperty("startTime",Oil_Start_timeHour.getText().toString());
              rpc.addProperty("endTime",Oil_End_timeHour.getText().toString());

              // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
              SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
              envelope.dotNet = true;
              envelope.setOutputSoapObject(rpc);

              AndroidHttpTransport ht = new AndroidHttpTransport(endPoint);
              ht.debug = true;
              try {
                  // 调用WebService
                  ht.call(soapAction, envelope);
              } catch (Exception e) {
                  Message msg = Message.obtain();
                  msg.what = 0;
                  handler.sendMessage(msg);
              }
              SoapObject object;
              object = (SoapObject) envelope.getResponse();
              // 得到服务器传回的数据 数据时dataset类型的
              int count1 = object.getPropertyCount();
              if(count1 ==0){
                  progressDialog.dismiss();
                  Message msg = Message.obtain();
                  msg.what = 2;
                  handler.sendMessage(msg);
                  return;
              }
              Log.e("warn", String.valueOf(count1));
              if (count1 > 0) {
                  sb = new StringBuffer();
                  for (int i = 0; i < count1; i++) {
                      SoapObject soapProvince = (SoapObject)object.getProperty(i);
                    /*Log.e("warn", soapProvince.getProperty("Get_OilAlarmInfo_ListResult").toString() + ":返回id");//dataset数据类型
                    String str = soapProvince.getProperty("Get_OilAlarmInfo_ListResult").toString();*/
                      if(soapProvince.getProperty("NAME").toString().equals("anyType{}")){
                          sb.append(" " + ",");
                      }else {
                          sb.append(soapProvince.getProperty("NAME").toString() + ",");
                      }
                      //lieBiao.setD_NUM(soapProvince.getProperty("D_NUM").toString());
                      if(soapProvince.getProperty("CARNUM").toString().equals("anyType{}")){
                            sb.append(" " + ",");
                      }else{
                            sb.append(soapProvince.getProperty("CARNUM").toString() + ",");
                      }
                      //lieBiao.setD_OFFNUM(soapProvince.getProperty("D_OFFNUM").toString());
                      if(soapProvince.getProperty("JYTime").toString().equals("anyType{}")){
                          sb.append(" "+ ",");
                      }else {
                          sb.append(soapProvince.getProperty("JYTime").toString() + ",");
                      }
                      //lieBiao.setD_ONLINE(soapProvince.getProperty("D_ONLINE").toString());
                      if(soapProvince.getProperty("JYLiang").toString().equals("anyType{}")){
                          sb.append("" + ",");
                      } else {
                          sb.append(soapProvince.getProperty("JYLiang").toString() + ",");
                      }
                      //lieBiao.setD_CARNUMBER(soapProvince.getProperty("D_CARNUMBER").toString());
                      if( soapProvince.getProperty("JYJinE").toString().equals("anyType{}")){
                          sb.append(" " + ",");
                      }else{
                          sb.append(soapProvince.getProperty("JYJinE").toString() + ",");
                      }
                      if(i==count1-1){
                          if(soapProvince.toString().contains("PERIMG")&&!soapProvince.getProperty("PERIMG").toString().equals("anyType{}")) {
                              sb.append(soapProvince.getProperty("PERIMG").toString());
                          }else{
                              sb.append("kong");
                          }
                      }else{
                          if(soapProvince.toString().contains("PERIMG")&&!soapProvince.getProperty("PERIMG").toString().equals("anyType{}")){
                              sb.append(soapProvince.getProperty("PERIMG").toString()+"|");
                          }else {
                              sb.append("kong" + "|");
                          }
                      }
                  }
                  Message msg = Message.obtain();
                  msg.what = 1;
                  msg.obj = sb.toString();
                  handler.sendMessage(msg);
              }
          } catch (Exception e) {
              Message msg = Message.obtain();
              msg.what = 0;
              handler.sendMessage(msg);
          }
      }
  };
    private List<AddOilHistoryBeen> list=null;
    private List<AddOilHistoryBeen> Alllist;
    private AddOilHistoryAdapter aa;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if(list!=null){
                list.clear();
            }else{
                list =new ArrayList<>();
            }
            Alllist=new ArrayList<>();
            if(i==1){
                String str=(String)msg.obj;
                Log.e("warn",str);
                String arr [] = str.split("\\|");
                for(int j=0;j<arr.length;j++){
                    AddOilHistoryBeen been = new AddOilHistoryBeen();
                    String arr1 [] = arr[j].split(",");
                    been.setCarNum(arr1[1]);
                    been.setName(arr1[0]);
                    been.setTime(arr1[2]);
                    been.setOilNum(arr1[3]);
                    been.setMoney(arr1[4]);
                    been.setImage(arr1[5]);
                    list.add(been);
                    Alllist.add(been);
                }
               /* if(isfirst) {
                    //aa = new AddOilHistoryAdapter(JiaOilJiLU.this, list);
                    lv_jiaYouJiLu.setAdapter(aa);
                }else{*/
                    if(aa!=null) {
                        aa.notifyDataSetChanged();
                    }
                //}
                progressDialog.dismiss();
            }else if(i==2){
                progressDialog.dismiss();
                Toast.makeText(JiaOilJiLU.this, "无加油记录", Toast.LENGTH_SHORT).show();
            }else{
                progressDialog.dismiss();
                Toast.makeText(JiaOilJiLU.this, "网络或服务器异常", Toast.LENGTH_SHORT).show();
            }
        }
    };
    public static final String DATEPICKER_TAG = "datepicker";
    public static final String ENDTIMEPICKER_TAG = "Endtimepicker";
    private Button Oil_Start_timeHour;
    private  Button Oil_End_timeHour;
    private TextView OilHistory_carNum;
    private String start_Time= "";
    private String end_Time= "";
    private View addview;
    private void BD_TimepopWindow(){
        //RelativeLayout top = (RelativeLayout) findViewById(R.id.addOil_top);
        addview = LayoutInflater.from(JiaOilJiLU.this).inflate(R.layout.oilhistorysearch_layout, null);
        OilHistory_carNum = (TextView) addview.findViewById(R.id.OilHistory_carNum);//选择车牌号
        Oil_Start_timeHour= (Button)addview.findViewById(R.id.Oil_Start_timeHour);//开始时间
        Oil_End_timeHour= (Button)addview.findViewById(R.id.Oil_End_TimeHour);//开始时间
        ImageButton Oil_chaxun =(ImageButton)addview.findViewById(R.id.Oil_chaxun);//查询
        if(power.equals("1")){
            OilHistory_carNum.setText(sijiCARnum);
        }
        OilHistory_carNum.setOnClickListener(new JiaOilJiLUListener());
        /*Oil_Start_timeHour.setOnClickListener(new JiaOilJiLUListener() );
        Oil_End_timeHour.setOnClickListener(new JiaOilJiLUListener() );*/
        Oil_chaxun.setOnClickListener(new JiaOilJiLUListener() );

        //BD_addinit(addview);
        popupwindow = new PopupWindow(addview, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);//2,3参数为宽高
        popupwindow.setTouchable(true);//popupWindow可触摸
        popupwindow.setOutsideTouchable(true);//点击popupWindow以外区域消失
        popupwindow.setFocusable(true);
        popupwindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                Log.i("mengdd", "onTouch : ");
                return false;
            }
        });
       /* //设置popwindow消失的监听事件
        popupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isGJorCancel=!isGJorCancel;
                Meau.setText("轨迹");
            }
        });*/
        popupwindow.setBackgroundDrawable(getResources().getDrawable(R.color.white));
        //popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置好参数之后再show
        popupwindow.showAsDropDown(top,0,0);//在view(top)控件正下方，以view为参照点第二个参数为popwindow距离view的横向距离，
        //第三个参数为y轴即popwindow距离view的纵向距离

        //自定义 选择日期和时间的选择器
        final Calendar calendar = Calendar.getInstance();

        //初始化各个控件的时间
        String year  = String.valueOf(calendar.get(Calendar.YEAR));
        String month  = String.valueOf(calendar.get(Calendar.MONTH)+1);
        String day  = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        if(month.length()<2){
            month = "0"+month;
        }
        if(day.length()<2){
            day = "0"+day;
        }

        Oil_Start_timeHour.setText(year+"-"+month+"-"+day);
        Oil_End_timeHour.setText(year+"-"+month+"-"+day);
        start_Time = year+"-"+month+"-"+day;
        end_Time = year+"-"+month+"-"+day;
        //初始化日期时间
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(JiaOilJiLU.this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                true);

        Oil_Start_timeHour.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                StartOrEndTime = false;//点击开始时间是false
                datePickerDialog.setVibrate(true);//是否抖动，推荐true
                datePickerDialog.setYearRange(1985, 2028);//设置年份区间
                datePickerDialog.setCloseOnSingleTapDay(false);//选择后是否消失，推荐false
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);//展示dialog，传一个tag参数
            }
        });
        Oil_End_timeHour.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                StartOrEndTime = true;//点击结束时间是true
                datePickerDialog.setVibrate(true);//是否抖动，推荐true
                datePickerDialog.setYearRange(1985, 2028);//设置年份区间
                datePickerDialog.setCloseOnSingleTapDay(false);//选择后是否消失，推荐false
                datePickerDialog.show(getSupportFragmentManager(), ENDTIMEPICKER_TAG);//展示dialog，传一个tag参数
            }
        });

        //保存状态
        if (savedState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(JiaOilJiLU.this);
            }
            DatePickerDialog dpd1 = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(ENDTIMEPICKER_TAG);
            if (dpd1 != null) {
                dpd1.setOnDateSetListener(JiaOilJiLU.this);
            }
        }

    }
    private void getcarNum(){
        String ALLCARNUM=getIntent().getStringExtra("ALLcarNum");
        if(ALLCARNUM==null){
            //权限1直接进入此页面的
           /* getCarNum();
                if(allcarnum!=null){
                    CarPai(allcarnum);
                }else{
                    Toast.makeText(this, "获取车辆列表失败", Toast.LENGTH_SHORT).show();
                }*/
            sijiSearchCarNum();
        }else {
            CarPai(ALLCARNUM);
        }
    }
    private String [] arr_ALL;
    private void CarPai(String ALLCARNUM){
        arr_ALL= FENJ_ALLcarNum(ALLCARNUM);//获取所有车牌的集合
        AlertDialog.Builder builder = new AlertDialog.Builder(JiaOilJiLU.this);
        builder.setTitle("请选择");
        builder.setSingleChoiceItems(arr_ALL,0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                OilHistory_carNum .setText(arr_ALL[i]);
                dialogInterface.dismiss();
                if(arr_ALL[i].equals("全部")){

                    if(!power.equals("1")){

                    }else{
                        Oil_End_timeHour.setEnabled(false);
                        Oil_End_timeHour.setTextColor(getResources().getColor(R.color.huise));
                        Oil_End_timeHour.setText("");
                        Oil_Start_timeHour.setEnabled(false);
                        Oil_Start_timeHour.setText("");
                        Oil_Start_timeHour.setTextColor(getResources().getColor(R.color.huise));
                    }


                }else{
                    if(!power.equals("1")){

                    }else{
                        Oil_End_timeHour.setEnabled(true);
                        Oil_Start_timeHour.setEnabled(true);
                        Oil_End_timeHour.setTextColor(getResources().getColor(R.color.z001));
                        Oil_Start_timeHour.setTextColor(getResources().getColor(R.color.z001));
                        Oil_End_timeHour.setText(end_Time);
                        Oil_Start_timeHour.setText(start_Time);
                    }
                }
            }
        });
        builder.show();
    }
    private String [] FENJ_ALLcarNum(String ALLCARNUM){
        Log.e("warn",ALLCARNUM);
        //List<String> ALL_list =new ArrayList<String>();
        String arr [] = ALLCARNUM.split(",");
        String arr1 [] = new String[arr.length+1];
        arr1[0] = "全部";
        for(int i=0;i<arr.length;i++){
            arr1[i+1] = arr[i];
        }
        return  arr1;
    }
    private MyProgressDialog progressdialog;
    //查询部分加油记录
    private void getSerachData(){
        progressdialog = new MyProgressDialog(JiaOilJiLU.this, false, "加载中...");
        new Thread(){
            @Override
            public void run() {
                super.run();

                try {

                    Log.e("warn", "30");
                    // 命名空间
                    String nameSpace = "http://tempuri.org/";
                    // 调用的方法名称Get_OilAlarmInfo_List
                    String methodName = "Get_CheckJYRecord";
                    // EndPoint
                    String endPoint = Path.get_ZanShibeidouPath();
                    // SOAP Action
                    String soapAction = "http://tempuri.org/Get_CheckJYRecord";
                    // 指定WebService的命名空间和调用的方法名
                    SoapObject rpc = new SoapObject(nameSpace, methodName);
                    //设置需调用WebService接口需要传入的参数CarNum
                    rpc.addProperty("CARNUM",OilHistory_carNum.getText().toString());
                    rpc.addProperty("startTime",Oil_Start_timeHour.getText().toString());
                    rpc.addProperty("endTime",Oil_End_timeHour.getText().toString());
                    // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(rpc);

                    AndroidHttpTransport ht = new AndroidHttpTransport(endPoint);
                    ht.debug = true;
                    try {
                        // 调用WebService
                        ht.call(soapAction, envelope);
                    } catch (Exception e) {
                        Message msg = Message.obtain();
                        msg.what = 0;
                        SearchHandler.sendMessage(msg);
                    }
                    SoapObject object;
                    object = (SoapObject) envelope.getResponse();
                    // 得到服务器传回的数据 数据时dataset类型的
                    int count1 = object.getPropertyCount();
                    Log.e("warn", String.valueOf(count1));
                    Log.e("warn","561");
                    if(count1==0){
                        progressdialog.dismiss();
                        Message msg = Message.obtain();
                        msg.what =2;
                        SearchHandler.sendMessage(msg);
                        return;
                    }
                    StringBuffer sb;
                    Log.e("warn", String.valueOf(count1));
                    if (count1 > 0) {
                        sb = new StringBuffer();
                        for (int i = 0; i < count1; i++) {
                            SoapObject soapProvince = (SoapObject)object.getProperty(i);
                    /*Log.e("warn", soapProvince.getProperty("Get_OilAlarmInfo_ListResult").toString() + ":返回id");//dataset数据类型
                    String str = soapProvince.getProperty("Get_OilAlarmInfo_ListResult").toString();*/


                            if(soapProvince.getProperty("NAME").toString().equals("anyType{}")){
                                sb.append(" " + ",");
                            }else {
                                sb.append(soapProvince.getProperty("NAME").toString() + ",");
                            }
                            //lieBiao.setD_NUM(soapProvince.getProperty("D_NUM").toString());
                            if(soapProvince.getProperty("CARNUM").toString().equals("anyType{}")){
                                sb.append(" " + ",");
                            }else{
                                sb.append(soapProvince.getProperty("CARNUM").toString() + ",");
                            }
                            //lieBiao.setD_OFFNUM(soapProvince.getProperty("D_OFFNUM").toString());
                            if(soapProvince.getProperty("JYTime").toString().equals("anyType{}")){
                                sb.append(" "+ ",");
                            }else {
                                sb.append(soapProvince.getProperty("JYTime").toString() + ",");
                            }
                            //lieBiao.setD_ONLINE(soapProvince.getProperty("D_ONLINE").toString());
                            if(soapProvince.getProperty("JYLiang").toString().equals("anyType{}")){
                                sb.append("" + ",");
                            } else {
                                sb.append(soapProvince.getProperty("JYLiang").toString() + ",");
                            }
                            //lieBiao.setD_CARNUMBER(soapProvince.getProperty("D_CARNUMBER").toString());
                            if( soapProvince.getProperty("JYJinE").toString().equals("anyType{}")){
                                sb.append(" " + ",");
                            }else{
                                sb.append(soapProvince.getProperty("JYJinE").toString() + ",");
                            }

                            if(i==count1-1){
                                if(soapProvince.toString().contains("PERIMG")&&!soapProvince.getProperty("PERIMG").toString().equals("anyType{}")) {
                                    sb.append(soapProvince.getProperty("PERIMG").toString());
                                }else{
                                    sb.append("kong");
                                }
                            }else{
                                if(soapProvince.toString().contains("PERIMG")&&!soapProvince.getProperty("PERIMG").toString().equals("anyType{}")){
                                    sb.append(soapProvince.getProperty("PERIMG").toString()+"|");
                                }else {
                                    sb.append("kong" + "|");
                                }
                            }
                        }
                        Message msg = Message.obtain();
                        msg.what = 1;
                        msg.obj = sb.toString();
                        SearchHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    Message msg = Message.obtain();
                    msg.what = 0;
                    SearchHandler.sendMessage(msg);
                }


            }
        }.start();
    }
    private Handler SearchHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            Log.e("warn",String.valueOf(i));
            if(i==1){
                if(list!=null){
                    list.clear();
                }else{
                    list= new ArrayList<>();
                }
                progressdialog.dismiss();
                String str=(String)msg.obj;
                Log.e("warn",str);
                String arr [] = str.split("\\|");
                for(int j=0;j<arr.length;j++){
                    AddOilHistoryBeen been = new AddOilHistoryBeen();
                    String arr1 [] = arr[j].split(",");
                    been.setCarNum(arr1[1]);
                    been.setName(arr1[0]);
                    been.setTime(arr1[2]);
                    been.setOilNum(arr1[3]);
                    been.setMoney(arr1[4]);
                    been.setImage(arr1[5]);
                    list.add(been);
                }
                if(aa!=null) {
                            aa.notifyDataSetChanged();
                        }
            }else if(i==2){
                progressdialog.dismiss();
                Toast.makeText(JiaOilJiLU.this, "无加油记录", Toast.LENGTH_SHORT).show();
            }else{
                progressdialog.dismiss();
                Toast.makeText(JiaOilJiLU.this, "网络或服务器异常", Toast.LENGTH_SHORT).show();
            }
        }
    };
    private void closePopwindow() {
        if(popupwindow!=null){
            popupwindow.dismiss();
            popupwindow = null;}
    }

private MyProgressDialog progressDialog1;
    private void getCarNum(){
        progressDialog1 = new MyProgressDialog(JiaOilJiLU.this,false,"");
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {  Log.e("warn", "30");
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称Get_OilAlarmInfo_List
                String methodName = "Get_CarNum_List";
                // EndPoint
                String endPoint = Path.get_ZanShibeidouPath();
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_CarNum_List";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数CarNum
                // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);

                AndroidHttpTransport ht = new AndroidHttpTransport(endPoint);
                ht.debug = true;
                try {
                    // 调用WebService
                    ht.call(soapAction, envelope);
                } catch (Exception e) {
                    Message msg = Message.obtain();
                    msg.what = 0;
                    carHandler.sendMessage(msg);
                }
                SoapObject object;
                object = (SoapObject) envelope.getResponse();
                // 得到服务器传回的数据 数据时dataset类型的
                int count1 = object.getPropertyCount();
                if(count1 ==0){
                    progressDialog1.dismiss();
                    Message msg = Message.obtain();
                    msg.what = 2;
                    carHandler.sendMessage(msg);
                    return;
                }
                StringBuffer sb;
                Log.e("warn", String.valueOf(count1));
                if (count1 > 0) {
                    sb = new StringBuffer();
                    for (int i = 0; i < count1; i++) {
                        SoapObject soapProvince = (SoapObject)object.getProperty(i);

                        Log.e("warn", soapProvince.getProperty("CARNUM").toString() + ":");
                        if(i==count1-1){
                            if(soapProvince.getProperty("CARNUM").toString().equals("anyType{}")){
                                sb.append("");
                            } else {
                                sb.append(soapProvince.getProperty("CARNUM").toString());
                            }
                        }else{
                            if(soapProvince.getProperty("CARNUM").toString().equals("anyType{}")){
                                sb.append(""+",");
                            }else {
                                sb.append(soapProvince.getProperty("CARNUM").toString() + ",");
                            }
                        }
                    }
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.obj = sb.toString();
                    carHandler.sendMessage(msg);
                }
            } catch (Exception e) {
                Message msg = Message.obtain();
                msg.what = 0;
                carHandler.sendMessage(msg);
            }
            }
        }.start();
    }
    private String allcarnum=null;
    private Handler carHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if(i==2){
                progressDialog1.dismiss();
                Toast.makeText(JiaOilJiLU.this, "无数据", Toast.LENGTH_SHORT).show();
            }else if(i==1){
                progressDialog1.dismiss();
                allcarnum = (String)msg.obj;
            }else{
                progressDialog1.dismiss();
                Toast.makeText(JiaOilJiLU.this, "网络或服务器异常", Toast.LENGTH_SHORT).show();
            }
        }
    };
    Runnable SiJi = new Runnable() {
        @Override
        public void run() {
            try {
                Log.e("warn", "30");
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称Get_OilAlarmInfo_List
                String methodName = "Get_OnePerJYRecord";
                // EndPoint
                String endPoint = Path.get_ZanShibeidouPath();
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_OnePerJYRecord";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数CarNum
                if(SiJiPERID!=null){
                    rpc.addProperty("PerID",SiJiPERID);
                }else{
                    Toast.makeText(JiaOilJiLU.this, "无效权限，无法获取历史加油信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);

                AndroidHttpTransport ht = new AndroidHttpTransport(endPoint);
                ht.debug = true;
                try {
                    // 调用WebService
                    ht.call(soapAction, envelope);
                } catch (Exception e) {
                    Message msg = Message.obtain();
                    msg.what = 0;
                    SiJiHandler.sendMessage(msg);
                }
                SoapObject object;
                object = (SoapObject) envelope.getResponse();
                // 得到服务器传回的数据 数据时dataset类型的
                int count1 = object.getPropertyCount();
                if(count1==0){
                    progressDialog.dismiss();
                    Message msg = Message.obtain();
                    msg.what = 2;
                    SiJiHandler.sendMessage(msg);
                    return;
                }
                StringBuffer sb;
                Log.e("warn", String.valueOf(count1));
                if (count1 > 0) {
                    sb = new StringBuffer();
                    for (int i = 0; i < count1; i++) {
                        SoapObject soapProvince = (SoapObject)object.getProperty(i);
                        Log.e("warn",soapProvince.toString());//判断是否存在图片
                    /*Log.e("warn", soapProvince.getProperty("Get_OilAlarmInfo_ListResult").toString() + ":返回id");//dataset数据类型
                    String str = soapProvince.getProperty("Get_OilAlarmInfo_ListResult").toString();*/

                        if(soapProvince.getProperty("NAME").toString().equals("anyType{}")){
                            sb.append(" " + ",");
                        }else {
                            sb.append(soapProvince.getProperty("NAME").toString() + ",");
                        }
                        //lieBiao.setD_NUM(soapProvince.getProperty("D_NUM").toString());
                        if(soapProvince.getProperty("CARNUM").toString().equals("anyType{}")){
                            sb.append(" " + ",");
                        }else{
                            sb.append(soapProvince.getProperty("CARNUM").toString() + ",");
                        }
                        //lieBiao.setD_OFFNUM(soapProvince.getProperty("D_OFFNUM").toString());
                        if(soapProvince.getProperty("JYTime").toString().equals("anyType{}")){
                            sb.append(" "+ ",");
                        }else {
                            sb.append(soapProvince.getProperty("JYTime").toString() + ",");
                        }
                        //lieBiao.setD_ONLINE(soapProvince.getProperty("D_ONLINE").toString());
                        if(soapProvince.getProperty("JYLiang").toString().equals("anyType{}")){
                            sb.append("" + ",");
                        } else {
                            sb.append(soapProvince.getProperty("JYLiang").toString() + ",");
                        }
                        //lieBiao.setD_CARNUMBER(soapProvince.getProperty("D_CARNUMBER").toString());
                        if( soapProvince.getProperty("JYJinE").toString().equals("anyType{}")){
                            sb.append(" " + ",");
                        }else{
                            sb.append(soapProvince.getProperty("JYJinE").toString() + ",");
                        }

                        if(i==count1-1){
                            if(soapProvince.toString().contains("PERIMG")&&!soapProvince.getProperty("PERIMG").toString().equals("anyType{}")) {
                                sb.append(soapProvince.getProperty("PERIMG").toString());
                            }else{
                                sb.append("kong");
                            }
                        }else{
                            if(soapProvince.toString().contains("PERIMG")&&!soapProvince.getProperty("PERIMG").toString().equals("anyType{}")){
                                sb.append(soapProvince.getProperty("PERIMG").toString()+"|");
                            }else {
                                sb.append("kong" + "|");
                            }
                        }
                    }
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.obj = sb.toString();
                    SiJiHandler .sendMessage(msg);
                }
            } catch (Exception e) {
                Message msg = Message.obtain();
                msg.what = 0;
                SiJiHandler .sendMessage(msg);
            }
        }
    };
    private Handler SiJiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int i = msg.what;
            Log.e("warn",String.valueOf(i));
            if(i==1){
                progressDialog.dismiss();
                if(list==null){
                    list = new ArrayList<>();

                }else {
                    list.clear();
                }
                String str=(String)msg.obj;
                Log.e("warn",str);
                String arr [] = str.split("\\|");
                for(int j=0;j<arr.length;j++){
                    AddOilHistoryBeen been = new AddOilHistoryBeen();
                    String arr1 [] = arr[j].split(",");
                    been.setCarNum(arr1[1]);
                    been.setName(arr1[0]);
                    been.setTime(arr1[2]);
                    been.setOilNum(arr1[3]);
                    been.setMoney(arr1[4]);
                    been.setImage(arr1[5]);
                    list.add(been);
                }
                    if(aa!=null) {
                        aa.notifyDataSetChanged();
                    }

            }else if(i==2){
                progressDialog.dismiss();
                Toast.makeText(JiaOilJiLU.this, "无加油记录", Toast.LENGTH_SHORT).show();
            }else{
                progressDialog.dismiss();
                Toast.makeText(JiaOilJiLU.this, "网络或服务器异常", Toast.LENGTH_SHORT).show();
            }
        }
    };
    RelativeLayout rl;
    RelativeLayout time_rl;
    //权限喂司机时的选择车牌号
    private void sijiSearchCarNum(){

        if(addview!=null) {
            rl = (RelativeLayout) addview.findViewById(R.id.rl_ll);
            time_rl = (RelativeLayout) addview.findViewById(R.id.time_rl);
        }
        final String arr [] = new String[2];
        if(sijiCARnum!=null){
            arr[0] = "全部";
            arr[1]=sijiCARnum;
        }else{
            Toast.makeText(JiaOilJiLU.this, "无效的权限，无法获取选择条件", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(JiaOilJiLU.this);
        builder.setTitle("请选择");
        builder.setSingleChoiceItems(arr,-1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                OilHistory_carNum .setText(arr[i]);
                dialogInterface.dismiss();
                if(arr[i].equals("全部")){
                    rl.setVisibility(View.GONE);
                    time_rl.setVisibility(View.GONE);
                }else{
                    rl.setVisibility(View.VISIBLE);
                    time_rl.setVisibility(View.VISIBLE);
                }
            }
        });
        builder.show();
    }
}
