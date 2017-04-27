package com.example.administrator.benzhanzidonghua;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.com.vanpeng.Adapter.BD_POPListviewAdapter;
import com.com.vanpeng.Adapter.BeiDouOilWarnJavaBeen;
import com.com.vanpeng.Adapter.YouLiangBaoJiangAdapter;
import com.dataandtime.data.DatePickerDialog;
import com.vanpeng.javabeen.BD_carPOPListView;
import com.vanpeng.javabeen.YouLiangBaoJiangTongJi;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2017/3/24.
 */

public class YouLiangBaoJingTongJi extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private ListView viewById;
    private MyProgressDialog progressDialog;
    private List<BeiDouOilWarnJavaBeen> youLiangBaoJiangTongJiList;
    private StringBuffer sb;
    private Button Oil_search;
    private Bundle savedState;
    private String array [];
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youliang_baojing_tongji);
        savedState=savedInstanceState;
        // CommonMethod.setStatuColor(YouLiangBaoJingTongJi.this, R.color.blue);
        viewById = (ListView) findViewById(R.id.lv_youLiangBaoJiang_Listview);
        Button Meau_Left = (Button) findViewById(R.id.OIL_back);
        Oil_search = (Button)findViewById(R.id.Oil_search);
        Oil_search.setOnClickListener(new YouLiangBaoJingListener());
        Meau_Left.setOnClickListener(new YouLiangBaoJingListener());
        array=getAllCarNum();
        //activity刚创建的时候弹出popwindow 500ms之后执行
        mHandler.postDelayed(mRunnable, 500);



    }
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        public void run() {
            // 弹出PopupWindow的具体代码
            RelativeLayout oil_top = (RelativeLayout)findViewById(R.id.oil_top);
            popWindowSearch(oil_top);
        }
    };
    //获取车牌号
    private String []  getAllCarNum(){
        String allCarNum = getIntent().getStringExtra("allCarNum");
        String arr [] ;
        String arr1 []=null;
        if(allCarNum!=null||!allCarNum.equals("")){
            arr =allCarNum.split(",");
            arr1 = new String[arr.length+1];
            arr1[0] = "全部";
            for(int i=0;i<arr.length;i++){
                arr1[i+1] = arr[i];
            }
        }

        return arr1;
    }
    Runnable RequestNet = new Runnable() {
        @Override
        public void run() {
            try {
                Log.e("warn", "30");
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称Get_OilAlarmInfo_List
                String methodName = "Get_OilAlarmInfo_List";
                // EndPoint
                String endPoint = Path.get_ZanShibeidouPath();
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_OilAlarmInfo_List";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数CarNum
                if(!array[index].equals("全部")){
                    rpc.addProperty("CARNUM", OilHistory_carNum.getText().toString());
                }
                rpc.addProperty("StartTime",Oil_Start_timeHour.getText().toString());
                rpc.addProperty("EndTime", Oil_End_timeHour.getText().toString());
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
                    hanlder.sendMessage(msg);
                }
                SoapObject object;
                object = (SoapObject) envelope.getResponse();
                // 得到服务器传回的数据 数据时dataset类型的
                int count1 = object.getPropertyCount();
                if(count1 ==0){
                    Message msg = Message.obtain();
                    msg.what = 2;
                    hanlder.sendMessage(msg);
                    return;
                }
                Log.e("warn", String.valueOf(count1));
                if (count1 > 0) {
                    sb = new StringBuffer();
                    for (int i = 0; i < count1; i++) {
                        SoapObject soapProvince = (SoapObject)object.getProperty(i);
                    /*Log.e("warn", soapProvince.getProperty("Get_OilAlarmInfo_ListResult").toString() + ":返回id");//dataset数据类型
                    String str = soapProvince.getProperty("Get_OilAlarmInfo_ListResult").toString();*/
                    Log.e("warn", soapProvince.getProperty("CARNUM").toString() + ":");
                    sb.append(soapProvince.getProperty("CARNUM").toString() + ",");
                    //lieBiao.setD_NUM(soapProvince.getProperty("D_NUM").toString());

                    Log.e("warn", soapProvince.getProperty("TIME").toString() + ":");
                    sb.append(soapProvince.getProperty("TIME").toString() + ",");
                    //lieBiao.setD_OFFNUM(soapProvince.getProperty("D_OFFNUM").toString());

                    Log.e("warn", soapProvince.getProperty("OILL").toString() + ":");
                    sb.append(soapProvince.getProperty("OILL").toString() + ",");
                    //lieBiao.setD_ONLINE(soapProvince.getProperty("D_ONLINE").toString());

                    Log.e("warn", soapProvince.getProperty("UPORDOWN").toString() + ":");
                    sb.append(soapProvince.getProperty("UPORDOWN").toString() + ",");
                    //lieBiao.setD_CARNUMBER(soapProvince.getProperty("D_CARNUMBER").toString());

                    Log.e("warn", soapProvince.getProperty("ID").toString() + ":");
                    sb.append(soapProvince.getProperty("ID").toString() + ",");

                    Log.e("warn", soapProvince.getProperty("NAME").toString() + ":");
                        if(i==count1-1){
                            sb.append(soapProvince.getProperty("NAME").toString());
                        }else{
                            sb.append(soapProvince.getProperty("NAME").toString()+"|");
                        }
                }
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.obj = sb.toString();
                    hanlder.sendMessage(msg);
                }
            } catch (Exception e) {
                Message msg = Message.obtain();
                msg.what = 0;
                hanlder.sendMessage(msg);
            }
        }
    };
    private Handler hanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if(i==0){
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"网络或服务器异常",Toast.LENGTH_SHORT).show();
            }else if(i==1){
                progressDialog.dismiss();
                String str = (String) msg.obj;
                Log.e("warn",str);
                String arr [] = str.split("\\|");
                youLiangBaoJiangTongJiList=new ArrayList<>();
                for(int j=arr.length-1;j>=0;j--){
                    BeiDouOilWarnJavaBeen warn= new BeiDouOilWarnJavaBeen();
                    String arr1 []=arr[j].split(",");
                    warn.setCarNum(arr1[0]);
                    warn.setTime(arr1[1]);
                    if(arr1[3].equals("上升")){
                        warn.setUpOrDown("↑"+arr1[2]);
                    }else{
                        warn.setUpOrDown("↓"+arr1[2]);
                    }
                    if(arr1[4].equals("anyType{}")){
                        warn.setNmae("");
                    }else{
                        warn.setNmae(arr1[4]);
                    }
                    if(arr1[5].equals("anyType{}")){
                        warn.setNmae("");
                    }else{
                        warn.setNmae(arr1[5]);
                    }
                    youLiangBaoJiangTongJiList.add(warn);
                }
                viewById.setAdapter(new YouLiangBaoJiangAdapter(YouLiangBaoJingTongJi.this,youLiangBaoJiangTongJiList));

            }else{
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"无数据",Toast.LENGTH_SHORT).show();
            }
        }
    };
    private PopupWindow popupWindow;
    private TextView OilHistory_carNum;
    private Button Oil_Start_timeHour;
    private Button  Oil_End_timeHour;
    private String start_Time;
    private String end_Time;
    private boolean StartOrEndTime=false;
    public static final String DATEPICKER_TAG = "datepicker";
    public static final String ENDTIMEPICKER_TAG = "Endtimepicker";
    private void popWindowSearch(RelativeLayout top){
        View addview = LayoutInflater.from(this).inflate(R.layout.oilhistorysearch_layout, null);
        OilHistory_carNum = (TextView) addview.findViewById(R.id.OilHistory_carNum);//选择车牌号
        Oil_Start_timeHour= (Button)addview.findViewById(R.id.Oil_Start_timeHour);//开始时间
        Oil_End_timeHour= (Button)addview.findViewById(R.id.Oil_End_TimeHour);//开始时间
        ImageButton Oil_chaxun =(ImageButton)addview.findViewById(R.id.Oil_chaxun);//查询
        OilHistory_carNum.setOnClickListener(new YouLiangBaoJingListener());
        /*Oil_Start_timeHour.setOnClickListener(new JiaOilJiLUListener() );
        Oil_End_timeHour.setOnClickListener(new JiaOilJiLUListener() );*/
        Oil_chaxun.setOnClickListener(new YouLiangBaoJingListener());




        popupWindow = new PopupWindow(addview, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);//2,3参数为宽高
        //改变屏幕透明度
       /* WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.alpha = 0.9f;
        getWindow().setAttributes(params);*/
        //ll.getBackground().setAlpha(50);
        popupWindow.setTouchable(true);//popupWindow可触摸
        popupWindow.setOutsideTouchable(true);//点击popupWindow以外区域消失
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                Log.i("mengdd", "onTouch : ");
                return false;
            }
        });
       /* //监听popwindow消失的事件
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                closePopwindow();
            }
        });*/
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.white));
        //popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置好参数之后再show
        // popupWindow.showAsDropDown(top,0, 0);//在view(top)控件正下方，以view为参照点第二个参数为popwindow距离view的横向距离，
        //第三个参数为y轴即popwindow距离view的纵向距离
        popupWindow.showAsDropDown(top,0,0);//在view(top)控件正下方，以view为参照点第二个参数为popwindow距离view的横向距离，

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
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(YouLiangBaoJingTongJi.this,
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
                dpd.setOnDateSetListener(YouLiangBaoJingTongJi.this);
            }
            DatePickerDialog dpd1 = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(ENDTIMEPICKER_TAG);
            if (dpd1 != null) {
                dpd1.setOnDateSetListener(YouLiangBaoJingTongJi.this);
            }
        }




    }


    private void closePopwindow() {

        if(popupWindow!=null){
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

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

    private class YouLiangBaoJingListener implements View.OnClickListener{
        RelativeLayout oil_top = (RelativeLayout)findViewById(R.id.oil_top);
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.OilHistory_carNum:
                                                setBuilderDialog();
                                                break;
                case R.id.Oil_chaxun:
                                        progressDialog = new MyProgressDialog(YouLiangBaoJingTongJi.this, false, "加载中...");
                                        new Thread(RequestNet).start();
                                        closePopwindow();
                                        break;
                case R.id.OIL_back:finish();break;
                case R.id.Oil_search:
                                        popWindowSearch(oil_top);
                                        break;
            }
        }
    }
    private int index=0;
    private void setBuilderDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(YouLiangBaoJingTongJi.this);
        builder.setTitle("请选择");
        builder.setSingleChoiceItems(array,-1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                OilHistory_carNum .setText(array[i]);
                index=i;
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
}
