package com.example.administrator.benzhanzidonghua;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dataandtime.data.DatePickerDialog;
import com.dataandtime.time.RadialPickerLayout;
import com.dataandtime.time.TimePickerDialog;
import com.example.administrator.benzhanzidonghua.ZhuYeFragment.OnMyClikListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.vanpeng.javabeen.BeiDouCarLieBiaoBeen;

import java.util.Calendar;
import static com.example.administrator.benzhanzidonghua.ShouYeAiCgisFragment.OnMapClikListener;

import static com.example.administrator.benzhanzidonghua.R.id.JiShuiDian;
/**
 * 首页 地图
 */
public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener,OnMyClikListener,OnMapClikListener{
    private TextView title_change;//标题
    private Button Meau;//标题栏上的菜单
    private LinearLayout replace;//被fragment替换的布局
    private ImageView All;//返回全景图
    private ImageView BenZhan;//泵站
    private ImageView ShuiWei;//水位
    private ImageView YuLiang;//雨量
    private ImageView JianKong;//监控
    private TextView BengZhanText;//泵站文字
    private TextView ShuiWeiText;//水位文字
    private TextView YuLiangText;//雨量文字
    private TextView JianKongText;//监控文字
    private ImageView ShouYe;//首页
    private TextView ShouYeText;//首页文字
    private Fragment AiCgisFragment;
    private Fragment ShuiWeiFragment;
    private Fragment YuLiangFragment;
    private Fragment BenzhangFragment;
    private Fragment JianKongFragment;
    private MyProgressDialog progressDialog;
    private String value = "";//泵站列表详情
    private PopupWindow popupWindow;
    private View addview;//popwindow布局
    private Button BD_GJsearch;
    private ImageView BD_IV_GJsearch;
    private String CARNUM;
    private Bundle savedState;
    //private LinearLayout ZhuYeLL;
    private ImageView ZhuYeIV;
    private TextView ZhuYeText;
    private ZhuYeFragment mZhuYeFragment;
    private Button Main_back;
    private ImageView Main_Iv;
    private boolean ss=false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    //定义点击查询时fragment的回调接口 view1向view2传数据时，在view1中定义
    OnMyclikListener myListener;
   //自定义日期选择获得的年月日
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
        Toast.makeText(MainActivity.this, "new date:" + year + "-" + monthString + "-" + dayString, Toast.LENGTH_LONG).show();
        BD_dateTime.setText(year + "-" + monthString + "-" + dayString);
    }
    //自定义时间选择获得的小时
    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        String hourOfDayString  = String.valueOf(hourOfDay);
        String minuteString = String.valueOf(minute);
        if(minuteString.length()==1){
            minuteString="0"+minuteString;
        }
        if(hourOfDayString.length()==1){
            hourOfDayString = "0"+hourOfDayString;
        }
        Toast.makeText(MainActivity.this, "new time:" + hourOfDayString+ "-" + minuteString, Toast.LENGTH_LONG).show();
            if(startOrend){//点击了开始时间按钮
                Start_timeHour.setText(hourOfDayString+ ":" + minuteString);
            }else{
                End_TimeHour.setText(hourOfDayString+ ":" + minuteString);
            }
    }
    //主页向主activity传参回调
    @Override
    public void onclik(int num) {
            switch (num){
                case 0://泵站列表
                        BengZhanLayoutMethod();
                        showView();
                        break;
                case 1: //报警
                        Intent bjIntetn = new Intent(MainActivity.this, BaoJingChaXun.class);
                        startActivity(bjIntetn);
                        break;
                case 2://监控
                        JianKongLayoutMethod();
                        showView();
                        break;
                case 3://地图
                        ShouYeLayoutMethod();
                        showView();
                        break;
                case 4://历史
                        Intent LSIntetn = new Intent(MainActivity.this, LiShiChaXun.class);
                        LSIntetn.putExtra("bool", "1");
                        startActivity(LSIntetn);
                        break;
                case 5://北斗定位
                        Intent intent = new Intent(MainActivity.this,BeiDouCarLieBiao.class);
                        startActivity(intent);
                        break;
                case 6://水位监测
                        ShuiWeiLayoutMethod();
                        showView();
                        break;
                case 7://雨量监测
                        YuLiangLayoutMethod();
                        showView();
                        break;
                case 8:
                        Intent JSD_intent = new Intent(MainActivity.this, LiShiChaXun.class);
                        JSD_intent.putExtra("bool", "0");//0用于区分是雨量查询还是水位和气体查询
                        startActivity(JSD_intent);
                        showView();
                        break;
                case 9: finish();
                        break;
            }
    }
    //清空轨迹回调
    @Override
    public void onclik(String isSelect) {
        ss=false;
        Main_back.setEnabled(true);
        Main_Iv.setImageDrawable(getResources().getDrawable(R.mipmap.fanhui));
        Main_Iv.setVisibility(View.VISIBLE);
    }

    //定义点击查询时fragment的回调接口 view1向view2传数据时，在view1中定义
    interface OnMyclikListener {
        public void callBack(String carnum,String start_hour,String end_hour) ;
    }

    //定义供fragment调用的函数
    public void setOnclikListener(OnMyclikListener onMyListener) {
        this.myListener = onMyListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedState = savedInstanceState;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        CommonMethod.setStatuColor(MainActivity.this, R.color.blue);
        //setWindowStatusBarColor(this,R.color.blue);
        ZhuCeReceiver();
        init();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    //左上角显示返回主页按钮
    private void showView(){
        Main_back.setEnabled(true);
        Main_Iv.setVisibility(View.VISIBLE);
    }
    //控件
    private void init() {
        //title_change=(TextView)findViewById(R.id.title_change);
        //title_change.setText("首页");
        Meau = (Button) findViewById(R.id.bt_rightMeau);
        Meau.setOnClickListener(new MainActivityListener());
        /*ShouYe = (ImageView) findViewById(R.id.ShouYe);
        ShouYeText = (TextView) findViewById(R.id.ShouYeText);
        BenZhan = (ImageView) findViewById(R.id.BenZhan);
        BengZhanText = (TextView) findViewById(R.id.BengZhanText);
        ShuiWei = (ImageView) findViewById(R.id.ShuiWei);
        ShuiWeiText = (TextView) findViewById(R.id.ShuiWeiText);
        YuLiang = (ImageView) findViewById(R.id.YuLiang);
        YuLiangText = (TextView) findViewById(R.id.YuLiangText);
        JianKong = (ImageView) findViewById(R.id.JianKong);
        JianKongText = (TextView) findViewById(R.id.JianKongText);*/
        /*BD_GJsearch = (Button)findViewById(R.id.BD_GJsearch);//轨迹时间选择按钮
        BD_GJsearch.setOnClickListener(new MainActivityListener());
        BD_GJsearch.setEnabled(false);//不可点击
        BD_IV_GJsearch =(ImageView)findViewById(R.id.BD_IV_GJsearch); //轨迹时间选择图片*/
        Main_Iv = (ImageView)findViewById(R.id.Main_Iv);// 返回图片
        Main_back = (Button)findViewById(R.id.Main_back);//返回
        Main_back.setOnClickListener(new MainActivityListener());
        Main_back.setEnabled(true);//可点击
        /*LinearLayout ZhuYeLL = (LinearLayout)findViewById(R.id.ZhuYeLL); //主页布局
        ZhuYeLL.setOnClickListener(new MainActivityListener());
        ZhuYeIV = (ImageView)findViewById(R.id.ZhuYeIV);//主页图标
        ZhuYeText = (TextView)findViewById(R.id.ZhuYeText);//主页文字
*/
        /*LinearLayout ShouYeLayout = (LinearLayout) findViewById(R.id.ShouYeLayout);
        ShouYeLayout.setOnClickListener(new MainActivityListener());
        LinearLayout BengZhanLayout = (LinearLayout) findViewById(R.id.BengZhanLayout);
        BengZhanLayout.setOnClickListener(new MainActivityListener());
        LinearLayout ShuiWeiLayout = (LinearLayout) findViewById(R.id.ShuiWeiLayout);
        ShuiWeiLayout.setOnClickListener(new MainActivityListener());
        LinearLayout YuLiangLayout = (LinearLayout) findViewById(R.id.YuLiangLayout);
        YuLiangLayout.setOnClickListener(new MainActivityListener());
        LinearLayout JianKongLayout = (LinearLayout) findViewById(R.id.JianKongLayout);
        JianKongLayout.setOnClickListener(new MainActivityListener());*/
        //All=(ImageView)findViewById(R.id.All);
        //底部菜单的五个fragment
        AiCgisFragment = new ShouYeAiCgisFragment();
        ShuiWeiFragment = new ShuiWeiFragment();
        YuLiangFragment = new YuLiangFragment();
        BenzhangFragment = new BenzhangFragment();
        JianKongFragment = new JianKongFragment();
        mZhuYeFragment  = new ZhuYeFragment();
        ZhuYeLayoutMethod();//进入应用默认的页面主页
        //switchFragment(ShuiWeiFragment,YuLiangFragment,BenzhangFragment,JianKongFragment,ShouYeAiCgisFragment);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    //控件的点击事件
    private class MainActivityListener implements View.OnClickListener {

        private String start_hour;
        private String end_hour;
        private String date;

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_rightMeau:
                    MeauPopWindows();//标题菜单展开的popwindow
                    break;
                /*case R.id.ShouYeLayout:
                    ShouYeLayoutMethod();
                    break;
                case R.id.BengZhanLayout:
                    BengZhanLayoutMethod();
                    break;
                case R.id.ShuiWeiLayout:
                    ShuiWeiLayoutMethod();
                    break;
                case R.id.YuLiangLayout:
                    YuLiangLayoutMethod();
                    break;
                case R.id.JianKongLayout:
                    JianKongLayoutMethod();
                    break;*/
               /* case R.id.ChaXun:
                                        POP_CX();
                                        popupWindow.dismiss();
                                        Intent click_intent = new Intent();
                                        click_intent.setAction("com.example.broadcastreceiver.action.CLICK");
                                        sendBroadcast(click_intent);
                                        break;

                case R.id.QuanTu:  POP_All();
                                    popupWindow.dismiss();
                                    Intent intent = new Intent();
                                    intent.setAction("com.example.broadcastreceiver.action.QUANTU");
                                    //ShouYeAiCgisFragment sy = new ShouYeAiCgisFragment();
                                    sendBroadcast(intent);
                                    break;*/
                case R.id.BengZhanLieBiao:
                    BZList();
                    closePopwindow();//点击popwindow条目时popwindow窗体消失
                    //popupWindow.dismiss();
                    BengZhanLayoutMethod();
                    break;
                case R.id.ShiPin:
                    POP_ShiPin();
                    closePopwindow();//点击popwindow条目时popwindow窗体消失
                    //popupWindow.dismiss();
                    JianKongLayoutMethod();
                    break;
                case R.id.ShuiWeiJianCe:
                    POP_ShuiWei();
                    closePopwindow();//点击popwindow条目时popwindow窗体消失
                    //popupWindow.dismiss();
                    //popupWindow.dismiss();
                    ShuiWeiLayoutMethod();
                    break;
                case R.id.YuLiangJianCe:
                    POP_YuLiang();
                    closePopwindow();//点击popwindow条目时popwindow窗体消失
                    //popupWindow.dismiss();
                    YuLiangLayoutMethod();

                    break;
                case JiShuiDian://雨量历史记录查询
                    POP_JiShuiDian();

                    Intent JSD_intent = new Intent(MainActivity.this, LiShiChaXun.class);
                    JSD_intent.putExtra("bool", "0");//0用于区分是雨量查询还是水位和气体查询
                    startActivity(JSD_intent);
                    closePopwindow();
                    //popupWindow.dismiss();
                    break;
                case R.id.LiShiJiLu: //跳转到历史查询页面
                    POP_LiShiJiLu();
                    closePopwindow();//点击popwindow条目时popwindow窗体消失
                    //popupWindow.dismiss();
                    Intent LSIntetn = new Intent(MainActivity.this, LiShiChaXun.class);
                    LSIntetn.putExtra("bool", "1");
                    startActivity(LSIntetn);
                    break;
                case R.id.BaoJing://跳转到报警activity
                    POP_BaoJing();
                    closePopwindow();//点击popwindow条目时popwindow窗体消失
                    //popupWindow.dismiss();
                    Intent bjIntetn = new Intent(MainActivity.this, BaoJingChaXun.class);
                    startActivity(bjIntetn);
                    break;
                /*case R.id.DaShuJu: //跳转到大数据activity
                                    POP_DaShuJu();
                                    popupWindow.dismiss();//点击popwindow条目时popwindow窗体消失
                                    Intent DSJIntetn =new Intent(MainActivity.this,DaShuJuChaXun.class);
                                    startActivity(DSJIntetn);
                                    break;
                case R.id.SheZhi://雨量大数据分析
                                    Intent YLData_intent  =new Intent(MainActivity.this,LiShiChaXun.class);
                                    YLData_intent.putExtra("bool","00");//0用于区分是雨量查询还是水位和气体查询
                                    startActivity(YLData_intent);
                                    popupWindow.dismiss();
                                    break;*/
                case R.id.TuiChu:
                    POP_TuiChu();
                    finish();
                    break;//退出当前应用
                case R.id.BeiDou: Intent intent = new Intent(MainActivity.this,BeiDouCarLieBiao.class);
                                    startActivity(intent);
                                    closePopwindow();
                                    //popupWindow.dismiss();
                                    break;
                /*case R.id.BD_GJsearch:
                    Toast.makeText(MainActivity.this, "1111111111111", Toast.LENGTH_SHORT).show();
                                    BD_TimepopWindow();
                                    break;*/
               /* case R.id.BD_dateTime:selectTime();break;
                case R.id.Start_timeHour:selectStartHour();break;
                case R.id.End_TimeHour:selectEndHour();break;*/
                case R.id.bd_chaxun:  //查看行车轨迹查看按钮 以下是提交参数 自定义回调方法
                                        //CARNUM

                             date = BD_dateTime.getText().toString();
                             start_hour = date +" "+Start_timeHour.getText().toString()+":"+"00";
                             end_hour = date +" "+End_TimeHour.getText().toString()+":"+"00";
                                        //BD_sendDate();
                            //传入地图fragment的回调
                             myListener.callBack(CARNUM,start_hour,end_hour);
                                        break;
                /*case R.id.ZhuYeLL:
                                    break;*/
                case R.id.Main_back:
                                if(ss==false) {
                                            ZhuYeLayoutMethod();//进入应用主页
                                }else {
                                    BD_TimepopWindow();
                                }
                                      break;
            }
        }

        private void BD_sendDate() {
            Intent send_Inent = new Intent();
            send_Inent.setAction("com.neter.broadcast.receiver.SendDownXMLBroadCast");
            send_Inent.putExtra("start_hour",start_hour);
            send_Inent.putExtra("end_hour",end_hour);
            sendBroadcast(send_Inent);
        }
    }
    private Button BD_dateTime;
    private Button Start_timeHour;
    private Button End_TimeHour;
    //搜索北斗轨迹选择条件的popwindow 和 自定义选择时间控件 查看行车轨迹
    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "Starttimepicker";
    public static final String ENDTIMEPICKER_TAG = "Endtimepicker";
    private boolean startOrend=true;//用于判断点击了开始时间还是结束时间按钮 开始true结束false
    private void BD_TimepopWindow(){
        RelativeLayout top = (RelativeLayout) findViewById(R.id.topMain);
        View addview = LayoutInflater.from(this).inflate(R.layout.bd_gitime_layout, null);
        ImageButton bd_chaxun = (ImageButton)addview.findViewById(R.id.bd_chaxun);
        bd_chaxun.setOnClickListener(new MainActivityListener());
        BD_dateTime = (Button)addview .findViewById(R.id.BD_dateTime);//选择日期
        Start_timeHour =(Button)addview .findViewById(R.id.Start_timeHour) ;
        End_TimeHour = (Button)addview .findViewById(R.id.End_TimeHour);
        EditText BD_GJcarPaiHao = (EditText)addview.findViewById(R.id.BD_GJcarPaiHao);
        BD_GJcarPaiHao.setText(CARNUM);
        //BD_addinit(addview);
        PopupWindow popupWindow = new PopupWindow(addview,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);//2,3参数为宽高
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
        popupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.color.white));
        //popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置好参数之后再show
        popupWindow.showAsDropDown(top,0,0);//在view(top)控件正下方，以view为参照点第二个参数为popwindow距离view的横向距离，
        //第三个参数为y轴即popwindow距离view的纵向距离



        //自定义 选择日期和时间的选择器
        final Calendar calendar = Calendar.getInstance();
        //初始化日期时间
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                true);
        //初始化开始 小时时间
        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this,
                calendar.get(Calendar.HOUR_OF_DAY) ,
                0,
                false, true);//最后两个参数，是否是24小时制，是否抖动。推荐false，false
        //初始化结束 小时时间
        final TimePickerDialog EndtimePickerDialog = TimePickerDialog.newInstance(this,
                calendar.get(Calendar.HOUR_OF_DAY) ,
                0,
                false, true);//最后两个参数，是否是24小时制，是否抖动。推荐false，false
        //日期
        BD_dateTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                datePickerDialog.setVibrate(true);//是否抖动，推荐true
                datePickerDialog.setYearRange(1985, 2028);//设置年份区间
                datePickerDialog.setCloseOnSingleTapDay(false);//选择后是否消失，推荐false
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);//展示dialog，传一个tag参数
            }
        });
        //开始时间
        Start_timeHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOrend = true;//代表点击了开始时间按钮
                timePickerDialog.setVibrate(true);//是否抖动
                timePickerDialog.setCloseOnSingleTapMinute(true);//选择后是否消失，推荐false true不可以选择分钟 false可以选择分钟
                timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);//展示dialog，传一个tag参数
            }
        });
        //结束时间
        End_TimeHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOrend = false;//代表点击了结束时间按钮
                EndtimePickerDialog.setVibrate(true);//是否抖动
                EndtimePickerDialog.setCloseOnSingleTapMinute(true);//选择后是否消失，推荐false true不可以选择分钟 false可以选择分钟
                EndtimePickerDialog.show(getSupportFragmentManager(),ENDTIMEPICKER_TAG );//展示dialog，传一个tag参数
            }
        });

        //保存状态
        if (savedState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }
            //选择开始时间事件
            TimePickerDialog tpd = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag(TIMEPICKER_TAG);
            if (tpd != null) {
                tpd.setOnTimeSetListener(this);
            }
            //选择结束时间的事件
            TimePickerDialog end_tpd = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag(ENDTIMEPICKER_TAG);
            if (end_tpd != null) {
                tpd.setOnTimeSetListener(this);
            }
        }

    }
    /*private Button BD_dateTime;
    private Button Start_timeHour;
    private Button End_TimeHour;
    private void  BD_addinit(View view){
        EditText BD_GJcarPaiHao = (EditText)view.findViewById(R.id.BD_GJcarPaiHao);
        BD_GJcarPaiHao.setText(CARNUM);
        BD_dateTime = (Button)view.findViewById(R.id.BD_dateTime);//选择日期
        Start_timeHour =(Button)view.findViewById(R.id.Start_timeHour) ;
        End_TimeHour = (Button)view.findViewById(R.id.End_TimeHour);
        ImageButton bd_chaxun = (ImageButton)view.findViewById(R.id.bd_chaxun);
        bd_chaxun.setOnClickListener(new MainActivityListener());
    }*/
    //查看行车轨迹 选择日期 年月日
    /*private void selectTime(){//选择日期
        android.app.AlertDialog.Builder builder2 = new android.app.AlertDialog.Builder(MainActivity.this);
        builder2.setTitle("请选择日期");
        LayoutInflater inflater2 = LayoutInflater.from(this);
        View view2 = inflater2.inflate(R.layout.datepicker_layout, null);
        builder2.setView(view2);
        DatePicker dp = (DatePicker) view2.findViewById(R.id.dp);
        //当前时间
        Calendar c = Calendar.getInstance();
        int Now_year = c.get(Calendar.YEAR);
        int Now_monthOfYear = c.get(Calendar.MONTH);
        int Now_dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        //判断时间长度
        String m1= String.valueOf(Now_monthOfYear + 1);
        String d1 = String.valueOf(Now_dayOfMonth );
        if(m1.length()==1){m1="0"+m1;}
        if(d1.length()==1){d1="0"+d1;}
        Start_year = String.valueOf(Now_year);
        Start_monthOfYear = m1;
        Start_dayOfMonth = d1;
        //初始化年月日
        dp.init(Now_year, Now_monthOfYear, Now_dayOfMonth, new DatePicker.OnDateChangedListener() {
            //改变后的时间 时间改变后才执行这个方法
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String y = String.valueOf(year);
                String m= String.valueOf(monthOfYear + 1);
                String d = String.valueOf(dayOfMonth);
                if(m.length()==1){m="0"+m;}
                if(d.length()==1){d="0"+d;}
                Start_year = y;
                Start_monthOfYear = m;
                Start_dayOfMonth = d;
            }
        });

        builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //提交到服务器上的时间
                BD_dateTime.setText(Start_year + "-" + Start_monthOfYear + "-" + Start_dayOfMonth);
            }
        });
        builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder2.show();
    }*/

  /*  private void selectTime(){//选择日期
        final Calendar calendar = Calendar.getInstance();
        //初始化 小时时间
        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(MainActivity.this,
                calendar.get(Calendar.HOUR_OF_DAY) ,
                0,
                false, true);//最后两个参数，是否是24小时制，是否抖动。推荐false，false

        timePickerDialog.setVibrate(true);//是否抖动
        timePickerDialog.setCloseOnSingleTapMinute(true);//选择后是否消失，推荐false true不可以选择分钟 false可以选择分钟
        timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);//展示dialog，传一个tag参数


         }*/
    //查看行车轨迹 选择开始的小时时间
/*    private String start_str="";
    private void selectStartHour(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setTitle("请选择开始时间");
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.select_time, null);
        builder.setView(view);

        NumberPicker hourPicker = (NumberPicker) view.findViewById(R.id.hourpicker);
        hourPicker.setMinValue(01);//设置最小值
        hourPicker.setMaxValue(24);//设置最大值
        hourPicker.setValue(0);
        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.e("warn","xin"+newVal+"");
                String str = String.valueOf(newVal);
                if(str.length()==1){
                    str="0"+str;
                }
                start_str=str;//获取选择的小时时间

            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(start_str.equals("")||start_str==null){
                            start_str="24";
                        }
                        Start_timeHour.setText(start_str);
                        dialog.dismiss();
                    }
                }
        );
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
    //查看行车轨迹 选择结束的小时时间
    private String end_str="";
    private void selectEndHour(){
        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(MainActivity.this);
        builder1.setTitle("请选择结束时间");
        LayoutInflater inflater1 = LayoutInflater.from(this);
        View view1 = inflater1.inflate(R.layout.select_time, null);
        builder1.setView(view1);

        NumberPicker hourPicker = (NumberPicker) view1.findViewById(R.id.hourpicker);
        hourPicker.setMinValue(01);
        hourPicker.setMaxValue(24);
        hourPicker.setValue(0);
        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                String str = String.valueOf(newVal);
                if(str.length()==1){
                    str="0"+str;
                }
                end_str=str;
            }
        });
        builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(end_str.equals("")||end_str==null){
                            end_str="24";
                        }
                        End_TimeHour.setText(end_str);
                        dialog.dismiss();
                    }
                }
        );
        builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder1.show();
    }*/
    //标题上的菜单popwindow
    private void MeauPopWindows() {

        //closePopwindow();//当popupWindow！=null再点击消失时屏幕恢复亮度
        //LinearLayout ll = (LinearLayout)findViewById(R.id.replace);
        RelativeLayout top = (RelativeLayout) findViewById(R.id.topMain);
        //RelativeLayout rllll = (RelativeLayout) findViewById(R.id.rllll);
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        //title.getHeight();
        addview = LayoutInflater.from(this).inflate(R.layout.mainactivity_pop_window, null);
        addinit(addview);
        popupWindow = new PopupWindow(addview,ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);//2,3参数为宽高
        //改变屏幕透明度
       /* WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.alpha = 0.9f;
        getWindow().setAttributes(params);*/
        //ll.getBackground().setAlpha(50);

        popupWindow.setHeight(height - top.getHeight()- 55);
        popupWindow.setWidth(width / 2);
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
        popupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.color.white));
        //popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置好参数之后再show
        popupWindow.showAsDropDown(top, width, 0);//在view(top)控件正下方，以view为参照点第二个参数为popwindow距离view的横向距离，
        //第三个参数为y轴即popwindow距离view的纵向距离
}
  /*  private AlertDialog dialog=null;
  //标题上的菜单popwindow
    private boolean ifDialog=false;//dialog关闭状态
  private void MeauPopWindows() {
      if(dialog!=null){
          closePopwindow();
         ifDialog=false;
          return;
      }else{
          ifDialog=true;
      }
      //closePopwindow();//当popupWindow！=null再点击消失时屏幕恢复亮度
      RelativeLayout top = (RelativeLayout) findViewById(R.id.topMain);
      int topHeight = top.getHeight();
      RelativeLayout rllll = (RelativeLayout) findViewById(R.id.rllll);
      WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
      int width = wm.getDefaultDisplay().getWidth();
      int height = wm.getDefaultDisplay().getHeight();
      //title.getHeight();
      addview = LayoutInflater.from(this).inflate(R.layout.mainactivity_pop_window, null);
      addinit(addview,height-topHeight-80);
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      dialog = builder.setView(addview).create();
      Window win = dialog.getWindow();
      win.setGravity(Gravity.RIGHT | Gravity.TOP);//设置窗体右上角
      WindowManager.LayoutParams params = win.getAttributes();
      params.x =-100;//设置x坐标
      params.y =topHeight-30;//设置y坐标*//**//**//**//*
      win.setAttributes(params);
      builder.setCancelable(true);
      dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
      dialog.show();
      dialog.getWindow().setLayout(width*2/3, height-topHeight-80);//设置dialog窗口大小

  }*/
   private void closePopwindow() {
            popupWindow.dismiss();
        if(popupWindow!=null){ popupWindow = null;}
    }
 /*  private void closePopwindow() {
       dialog.dismiss();
       if(dialog!=null){ dialog = null;}
   }*/
    //popwindow布局中对象
    private void addinit(View view) {
        /*LinearLayout ChaXun=(LinearLayout)view.findViewById(R.id.ChaXun);
        ChaXun.setOnClickListener(new MainActivityListener());
        LinearLayout QuanTu=(LinearLayout)view.findViewById(R.id.QuanTu);
        QuanTu.setOnClickListener(new MainActivityListener());*/
        LinearLayout BengZhanLieBiao = (LinearLayout) view.findViewById(R.id.BengZhanLieBiao);
        BengZhanLieBiao.setOnClickListener(new MainActivityListener());
        //setViewDaXiao(BengZhanLieBiao,height);

        LinearLayout ShiPin = (LinearLayout) view.findViewById(R.id.ShiPin);
        ShiPin.setOnClickListener(new MainActivityListener());
        //setViewDaXiao(ShiPin,height);

        LinearLayout ShuiWeiJianCe = (LinearLayout) view.findViewById(R.id.ShuiWeiJianCe);
        ShuiWeiJianCe.setOnClickListener(new MainActivityListener());
        //setViewDaXiao(ShuiWeiJianCe,height);

        LinearLayout YuLiangJianCe = (LinearLayout) view.findViewById(R.id.YuLiangJianCe);
        YuLiangJianCe.setOnClickListener(new MainActivityListener());
        //setViewDaXiao(YuLiangJianCe,height);

        LinearLayout YuLaingJiLu = (LinearLayout) view.findViewById(JiShuiDian);
        YuLaingJiLu.setOnClickListener(new MainActivityListener());
        //setViewDaXiao(YuLaingJiLu,height);

        LinearLayout LiShiJiLu = (LinearLayout) view.findViewById(R.id.LiShiJiLu);
        LiShiJiLu.setOnClickListener(new MainActivityListener());
        //setViewDaXiao(LiShiJiLu,height);

        LinearLayout BaoJing = (LinearLayout) view.findViewById(R.id.BaoJing);
        BaoJing.setOnClickListener(new MainActivityListener());
        //setViewDaXiao(BaoJing,height);
        /*LinearLayout DaShuJu=(LinearLayout)view.findViewById(R.id.DaShuJu);
        DaShuJu.setOnClickListener(new MainActivityListener());
        LinearLayout YuLiangFenXi=(LinearLayout)view.findViewById(R.id.SheZhi);
        YuLiangFenXi.setOnClickListener(new MainActivityListener());*/
        LinearLayout TuiChu = (LinearLayout) view.findViewById(R.id.TuiChu);
        TuiChu.setOnClickListener(new MainActivityListener());
        //setViewDaXiao(TuiChu ,height);
        LinearLayout BeiDou = (LinearLayout) view.findViewById(R.id.BeiDou);
        BeiDou.setOnClickListener(new MainActivityListener());
        //setViewDaXiao(BeiDou,height);
    }

    //地图菜单的点击事件
    private void ShouYeLayoutMethod() {
        /*BD_IV_GJsearch.setVisibility(View.VISIBLE);
        BD_GJsearch.setEnabled(true);//可点击*/
        /*ShouYe.setImageDrawable(getResources().getDrawable(R.mipmap.shouyeclick));
        ShouYeText.setTextColor(getResources().getColor(R.color.blue));
        BenZhan.setImageDrawable(getResources().getDrawable(R.mipmap.benzhan));
        BengZhanText.setTextColor(getResources().getColor(R.color.huise));
        ShuiWei.setImageDrawable(getResources().getDrawable(R.mipmap.waterwei));
        ShuiWeiText.setTextColor(getResources().getColor(R.color.huise));
        YuLiang.setImageDrawable(getResources().getDrawable(R.mipmap.yuliang));
        YuLiangText.setTextColor(getResources().getColor(R.color.huise));
        JianKong.setImageDrawable(getResources().getDrawable(R.mipmap.jiankong));
        JianKongText.setTextColor(getResources().getColor(R.color.huise));*/
        //title_change.setText("首页");
        switchFragment(mZhuYeFragment,ShuiWeiFragment, YuLiangFragment, BenzhangFragment, JianKongFragment, AiCgisFragment,"AiCgisFragment");
    }
   /* //首页菜单的点击事件
    private void ZhuYeLayoutMethod() {
        ZhuYeIV.setImageDrawable(null);
        ZhuYeText.setTextColor(getResources().getColor(R.color.blue));
        ShouYe.setImageDrawable(getResources().getDrawable(R.mipmap.firstpage));
        ShouYeText.setTextColor(getResources().getColor(R.color.huise));
        BenZhan.setImageDrawable(getResources().getDrawable(R.mipmap.benzhan));
        BengZhanText.setTextColor(getResources().getColor(R.color.huise));
        ShuiWei.setImageDrawable(getResources().getDrawable(R.mipmap.waterwei));
        ShuiWeiText.setTextColor(getResources().getColor(R.color.huise));
        YuLiang.setImageDrawable(getResources().getDrawable(R.mipmap.yuliang));
        YuLiangText.setTextColor(getResources().getColor(R.color.huise));
        JianKong.setImageDrawable(getResources().getDrawable(R.mipmap.jiankong));
        JianKongText.setTextColor(getResources().getColor(R.color.huise));
        //title_change.setText("首页");
        switchFragment(ShuiWeiFragment, YuLiangFragment, BenzhangFragment, JianKongFragment, AiCgisFragment,mZhuYeFragment,"AiCgisFragment");
    }*/
   //首页菜单的点击事件
   private void ZhuYeLayoutMethod() {
       //title_change.setText("首页");
       switchFragment(ShuiWeiFragment, YuLiangFragment, BenzhangFragment, JianKongFragment, AiCgisFragment,mZhuYeFragment,"AiCgisFragment");
   }
    //泵站点击事件
    private void BengZhanLayoutMethod() {
        //title_change.setText("泵站列表");
        /*ShouYe.setImageDrawable(getResources().getDrawable(R.mipmap.firstpage));
        ShouYeText.setTextColor(getResources().getColor(R.color.huise));
        BenZhan.setImageDrawable(getResources().getDrawable(R.mipmap.benzhanclick));
        BengZhanText.setTextColor(getResources().getColor(R.color.blue));
        ShuiWei.setImageDrawable(getResources().getDrawable(R.mipmap.waterwei));
        ShuiWeiText.setTextColor(getResources().getColor(R.color.huise));
        YuLiang.setImageDrawable(getResources().getDrawable(R.mipmap.yuliang));
        YuLiangText.setTextColor(getResources().getColor(R.color.huise));
        JianKong.setImageDrawable(getResources().getDrawable(R.mipmap.jiankong));
        JianKongText.setTextColor(getResources().getColor(R.color.huise));*/
        switchFragment(mZhuYeFragment,ShuiWeiFragment, YuLiangFragment, AiCgisFragment, JianKongFragment, BenzhangFragment,"BenzhangFragment");
    }

    //水位点击事件
    private void ShuiWeiLayoutMethod() {
        //title_change.setText("水位");
        /*ShouYe.setImageDrawable(getResources().getDrawable(R.mipmap.firstpage));
        ShouYeText.setTextColor(getResources().getColor(R.color.huise));
        BenZhan.setImageDrawable(getResources().getDrawable(R.mipmap.benzhan));
        BengZhanText.setTextColor(getResources().getColor(R.color.huise));
        ShuiWei.setImageDrawable(getResources().getDrawable(R.mipmap.shuiweimeauclick));
        ShuiWeiText.setTextColor(getResources().getColor(R.color.blue));
        YuLiang.setImageDrawable(getResources().getDrawable(R.mipmap.yuliang));
        YuLiangText.setTextColor(getResources().getColor(R.color.huise));
        JianKong.setImageDrawable(getResources().getDrawable(R.mipmap.jiankong));
        JianKongText.setTextColor(getResources().getColor(R.color.huise));*/
        ShuiWeiGetDate();
    }

    //雨量点击事件
    private void YuLiangLayoutMethod() {
        //title_change.setText("雨量");
        /*ShouYe.setImageDrawable(getResources().getDrawable(R.mipmap.firstpage));
        ShouYeText.setTextColor(getResources().getColor(R.color.huise));
        BenZhan.setImageDrawable(getResources().getDrawable(R.mipmap.benzhan));
        BengZhanText.setTextColor(getResources().getColor(R.color.huise));
        ShuiWei.setImageDrawable(getResources().getDrawable(R.mipmap.waterwei));
        ShuiWeiText.setTextColor(getResources().getColor(R.color.huise));
        YuLiang.setImageDrawable(getResources().getDrawable(R.mipmap.yuliangmeauclick));
        YuLiangText.setTextColor(getResources().getColor(R.color.blue));
        JianKong.setImageDrawable(getResources().getDrawable(R.mipmap.jiankong));
        JianKongText.setTextColor(getResources().getColor(R.color.huise));*/
        switchFragment(mZhuYeFragment,ShuiWeiFragment, BenzhangFragment, AiCgisFragment, JianKongFragment, YuLiangFragment,"YuLiangFragment");
    }

    //监控点击事件
    private void JianKongLayoutMethod() {
        //title_change.setText("监控列表");
       /* ShouYe.setImageDrawable(getResources().getDrawable(R.mipmap.firstpage));
        ShouYeText.setTextColor(getResources().getColor(R.color.huise));
        BenZhan.setImageDrawable(getResources().getDrawable(R.mipmap.benzhan));
        BengZhanText.setTextColor(getResources().getColor(R.color.huise));
        ShuiWei.setImageDrawable(getResources().getDrawable(R.mipmap.waterwei));
        ShuiWeiText.setTextColor(getResources().getColor(R.color.huise));
        YuLiang.setImageDrawable(getResources().getDrawable(R.mipmap.yuliang));
        YuLiangText.setTextColor(getResources().getColor(R.color.huise));
        JianKong.setImageDrawable(getResources().getDrawable(R.mipmap.jiankongmeauclick));
        JianKongText.setTextColor(getResources().getColor(R.color.blue));*/
        switchFragment(mZhuYeFragment,ShuiWeiFragment, YuLiangFragment, AiCgisFragment, BenzhangFragment, JianKongFragment,"JianKongFragment");
    }

    //popwindow泵站列表点击事件
    private void BZList() {
        //泵站
        ImageView BZ_iv = (ImageView) addview.findViewById(R.id.BengZhanLieBiaoImage);
        BZ_iv.setImageDrawable(getResources().getDrawable(R.mipmap.benzhanliebiaoclick));
        TextView BZ_tv = (TextView) addview.findViewById(R.id.BengZhanLieBiaoTuText);
        BZ_tv.setTextColor(getResources().getColor(R.color.yl03));
    }


    private void POP_ShiPin() {
        ImageView CX_iv = (ImageView) addview.findViewById(R.id.ShiPinImage);
        CX_iv.setImageDrawable(getResources().getDrawable(R.mipmap.quantuclick));
        TextView CX_tv = (TextView) addview.findViewById(R.id.ShiPinText);
        CX_tv.setTextColor(getResources().getColor(R.color.yl03));
    }

    //水位监测
    private void POP_ShuiWei() {
        ImageView CX_iv = (ImageView) addview.findViewById(R.id.ShuiWeiJianCeImage);
        CX_iv.setImageDrawable(getResources().getDrawable(R.mipmap.shuiweimeauclick));
        TextView CX_tv = (TextView) addview.findViewById(R.id.ShuiWeiJianCeText);
        CX_tv.setTextColor(getResources().getColor(R.color.yl03));
    }

    //雨量监测
    private void POP_YuLiang() {
        ImageView CX_iv = (ImageView) addview.findViewById(R.id.YuLiangJianCeImage);
        CX_iv.setImageDrawable(getResources().getDrawable(R.mipmap.yuliangmeauclick));
        TextView CX_tv = (TextView) addview.findViewById(R.id.YuLiangJianCeText);
        CX_tv.setTextColor(getResources().getColor(R.color.yl03));
    }

    //雨量历史记录
    private void POP_JiShuiDian() {
        ImageView CX_iv = (ImageView) addview.findViewById(R.id.JiShuiDianImage);
        CX_iv.setImageDrawable(getResources().getDrawable(R.mipmap.yulianglishijiluclick));
        TextView CX_tv = (TextView) addview.findViewById(R.id.YuLiangJiLu);
        CX_tv.setTextColor(getResources().getColor(R.color.yl03));
    }

    //历史记录
    private void POP_LiShiJiLu() {
        ImageView CX_iv = (ImageView) addview.findViewById(R.id.LiShiJiLuImage);
        CX_iv.setImageDrawable(getResources().getDrawable(R.mipmap.lishijiluclick));
        TextView CX_tv = (TextView) addview.findViewById(R.id.LiShiJiLuText);
        CX_tv.setTextColor(getResources().getColor(R.color.yl03));
    }

    //报警
    private void POP_BaoJing() {
        ImageView CX_iv = (ImageView) addview.findViewById(R.id.BaoJingImage);
        CX_iv.setImageDrawable(getResources().getDrawable(R.mipmap.baojingclick));
        TextView CX_tv = (TextView) addview.findViewById(R.id.BaoJingText);
        CX_tv.setTextColor(getResources().getColor(R.color.yl03));
    }


    private void POP_TuiChu() {
        ImageView CX_iv = (ImageView) addview.findViewById(R.id.TuiChuImage);
        CX_iv.setImageDrawable(getResources().getDrawable(R.mipmap.tuichuclick));
        TextView CX_tv = (TextView) addview.findViewById(R.id.TuiChuText);
        CX_tv.setTextColor(getResources().getColor(R.color.yl03));
    }


    public void switchFragment(Fragment from, Fragment from1, Fragment from2, Fragment from3,Fragment from4, Fragment to,String tag) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        //设置切换的效果
        if (!to.isAdded()) {    // 先判断是否被add过
            if (from.isAdded()) {
                transaction.hide(from);
            } //隐藏当前的fragment
            if (from1.isAdded()) {
                transaction.hide(from1);
            } //隐藏当前的fragment
            if (from2.isAdded()) {
                transaction.hide(from2);
            } //隐藏当前的fragment
            if (from3.isAdded()) {
                transaction.hide(from3);
            }
            if (from4.isAdded()) {
                transaction.hide(from4);
            } //隐藏当前的fragment
            transaction.add(R.id.replace, to,tag).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            if (from.isAdded()) {
                transaction.hide(from);
            } //隐藏当前的fragment
            if (from1.isAdded()) {
                transaction.hide(from1);
            } //隐藏当前的fragment
            if (from2.isAdded()) {
                transaction.hide(from2);
            } //隐藏当前的fragment
            if (from3.isAdded()) {
                transaction.hide(from3);
            }
            if (from4.isAdded()) {
                transaction.hide(from4);
            }//隐藏当前的fragment
            transaction.show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
        }
    }

    //水位请求数据方法
    private void ShuiWeiGetDate() {
        progressDialog = new MyProgressDialog(MainActivity.this, true, "加载中..");

        Runnable networkGetBengZhanInfoNew = new Runnable() {
            @Override
            public void run() {
                String path = Path.get_WebServicesURL();//webservice url
                String methodName = "AppGetBengZhanListResult";//返回的方法名称
                String SoapFileName = "assets/appgetbenzhanlist.xml";//soap协议文件名称
                String soap = CommonMethod.ReadSoap(SoapFileName);//读取soap协议
                try {
                    Log.e("warn", soap);
                    byte[] date = soap.getBytes();//soap协议转为字符数组
                    String result = CommonMethod.Request(path, date, methodName);
                    Message msg = Message.obtain();
                    msg.obj = result;
                    handlerGetBengZhanListNew.sendMessage(msg);
                    Log.d("DEBUG", "获取泵站列表WebService结果：" + result.toString());
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.obj = "999999";
                    handlerGetBengZhanListNew.sendMessage(msg);
                    //Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
                }
            }
        };
        new Thread(networkGetBengZhanInfoNew).start();
    }

    //获取水位数据
    public Handler handlerGetBengZhanListNew = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String val = (String) msg.obj;
            //String val = data.getString("value");
            Log.e("warn", "泵站" + val);
            if (val.toString().equals("999999")) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "获取泵站失败,网络或者服务器异常", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.dismiss();
                value = val;
                switchFragment(mZhuYeFragment,BenzhangFragment, YuLiangFragment, AiCgisFragment, JianKongFragment, ShuiWeiFragment,"ShuiWeiFragment");
                //Toast.makeText(MainActivity.this,val, Toast.LENGTH_SHORT).show();
            }
        }
    };

    //传给水位fragment的数据
    public String getBenZhanData() {
        return value;
    }

    //监听后退
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //moveTaskToBack(true);退出当前应用，但不销毁activity
            if(popupWindow!=null){
                closePopwindow();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("是否退出应用");
            //builder.setTitle("是否退出应用");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
            //return true;
        }
        return super.onKeyDown(keyCode, event);
    }
 //判断是否查询行车轨迹
    private void CarGuiJi(){
        Intent intent =getIntent();
        BeiDouCarLieBiaoBeen bdcb=(BeiDouCarLieBiaoBeen)intent.getSerializableExtra("beidouInformation");
        if(bdcb==null){
            return;
        }else {
            ShouYeLayoutMethod();
        }
    }
    private MyReceiver mReceiver;
    private void ZhuCeReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.neter.broadcast.receiver.SendDownXMLBroadCast");
        mReceiver = new MyReceiver();
        registerReceiver(mReceiver, filter);
    }
    private void BD_GJReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.neter.broadcast.receiver.bdguiji");
        mReceiver = new MyReceiver();
        registerReceiver(mReceiver, filter);
    }
    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.neter.broadcast.receiver.SendDownXMLBroadCast")){
                CARNUM=intent.getStringExtra("CARNUM");
                Main_back.setEnabled(true);
                Main_Iv.setVisibility(View.VISIBLE);
                Main_Iv.setImageDrawable(getResources().getDrawable(R.mipmap.bd_search));
                ss=true;
                ShouYeLayoutMethod();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);//必须注销广播，否则有内存泄漏的风险！！！
    }
    private void setViewDaXiao(View view,int height){
        LinearLayout.LayoutParams lay =(LinearLayout.LayoutParams)view.getLayoutParams();
        lay.height=height/9;
        view.setLayoutParams(lay);
    }
}
