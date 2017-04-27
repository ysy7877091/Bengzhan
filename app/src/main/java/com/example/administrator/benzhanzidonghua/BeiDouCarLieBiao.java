package com.example.administrator.benzhanzidonghua;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.com.vanpeng.Adapter.BDListViewAdapter;
import com.com.vanpeng.Adapter.BD_POPListviewAdapter;
import com.sousuo.CharacterParser;
import com.sousuo.ClearEditText;
import com.vanpeng.javabeen.BD_carPOPListView;
import com.vanpeng.javabeen.BeiDouCarLieBiaoBeen;
import com.vanpeng.javabeen.ListItemClickHelp;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/6 0006.
 * 北斗定位车辆列表
 */

public class BeiDouCarLieBiao extends AppCompatActivity implements ListItemClickHelp,AdapterView.OnItemClickListener {
    private List<BeiDouCarLieBiaoBeen> list=null;
    private MyProgressDialog progressDialog;
    private ListView BDListView;
    private StringBuffer sb;
    private  PopupWindow popupWindow;
    private ClearEditText BD_EditText;
    private Button tv_caidan;
    private List<BeiDouCarLieBiaoBeen> list1=null;
    private Bundle savedInstance;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.beidoucheliangliebiao_layout);
        savedInstance=savedInstanceState;
        CommonMethod.setStatuColor(BeiDouCarLieBiao.this, R.color.blue);
        init();
    }
    private void init(){

        Button bt_BDButton = (Button)findViewById(R.id.bt_BDButton);//返回
        bt_BDButton.setOnClickListener(new BeiDouCarLieBiaoListener());
        tv_caidan = (Button) findViewById(R.id.tv_caidan);
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        // BD_CarMeau = (ImageButton)findViewById(R.id.BD_CarMeau);//菜单
       // Button BD_CarMeau = (Button) findViewById(R.id.BD_CarMeau);
        //BD_CarMeau.setOnClickListener(new BeiDouCarLieBiaoListener());

        tv_caidan.setOnClickListener(new BeiDouCarLieBiaoListener());
        //搜索
        BD_EditText =(ClearEditText)findViewById(R.id.BD_EditText);//搜索框
        //监听 输入框内容变化
        BD_EditText.addTextChangedListener(new BD_EditTextListener());
        ImageButton BD_CarSearch =(ImageButton)findViewById(R.id.BD_CarSearch);//搜索按钮
        BD_CarSearch.setOnClickListener(new BeiDouCarLieBiaoListener());
       // Button BD_serach =(Button)findViewById(R.id.BD_serach);
       // BD_serach.setOnClickListener(new BeiDouCarLieBiaoListener());
        BDListView = (ListView)findViewById(R.id.BDListView);
        progressDialog = new MyProgressDialog(BeiDouCarLieBiao.this, false, "加载中...");
        //BDListView.setOnItemClickListener(new BDListViewListener());
        list = new ArrayList<BeiDouCarLieBiaoBeen>();
        list1 = new ArrayList<BeiDouCarLieBiaoBeen>();
        new Thread(networkTask).start();
    }
//listview条目里不同按钮点击事件回调
    @Override
    public void onClick(View item, View widget, int position, int which) {
            switch (which){
                case R.id.car_in:
                                        Intent guji_intent = new Intent();
                                        guji_intent.setAction("com.neter.broadcast.receiver.SendDownXMLBroadCast");//发出自定义广播
                                        guji_intent.putExtra("CARNUM",list.get(position).getCARNUM().trim());
                                        guji_intent.putExtra("ALLcarNum",getAllCarNum());
                                        sendBroadcast(guji_intent);
                                        finish();
                                    break;
                case R.id.centerID:
                                        Intent intent = new Intent(BeiDouCarLieBiao.this,CarInformation.class);
                                        intent.putExtra("carNum",list.get(position).getCARNUM());
                                        intent.putExtra("PERID",list.get(position).getPERID());
                                        intent.putExtra("allCarNum",getAllCarNum());
                                            if (getIntent().getStringExtra("personInformation") == null) {

                                            } else {
                                                intent.putExtra("personInformation", getIntent().getStringExtra("personInformation"));
                                            }

                                        startActivity(intent);
                                    /* Intent PER_intent = new Intent(BeiDouCarLieBiao.this,BeiDouPersonInformation.class);
                                     PER_intent.putExtra("perID",list.get(position).getPERID());
                                     startActivity(PER_intent);*/
                                    break;
            }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 1:
                    closePopwindow();
                    Intent intent = new Intent(BeiDouCarLieBiao.this,YouLiangBaoJingTongJi.class);
                    intent.putExtra("allCarNum",getAllCarNum());
                    startActivity(intent);
                    break;
            case 2:
                    closePopwindow();
                    Intent Oil_intent = new Intent(BeiDouCarLieBiao.this,LiChengYouHaoTongJi.class);
                    startActivity(Oil_intent);
                    break;
            /*case 3:
                    closePopwindow();
                    Intent Work_intent = new Intent(BeiDouCarLieBiao.this,WorkWarnLog.class);
                    startActivity(Work_intent);
                    break;*/
            case 3:
                     closePopwindow();
                     Intent intent2 = new Intent(BeiDouCarLieBiao.this,JiaOilJiLU.class);

                        if (getIntent().getStringExtra("personInformation") == null) {
                        } else {
                            intent2.putExtra("personInformation", getIntent().getStringExtra("personInformation"));
                        }
                        intent2.putExtra("ALLcarNum", getAllCarNum());

                     startActivity(intent2);

                    //判断权限
                    /*String str [] = getIntent().getStringExtra("personInformation").split(",");
                    if(str [6].equals("0")){
                        Intent intent3 = new Intent(BeiDouCarLieBiao.this,AddOilHistoryWrite.class);
                        intent3.putExtra("personInformation",getIntent().getStringExtra("personInformation"));
                        intent3.putExtra("ALLcarNum",getAllCarNum());
                        startActivity(intent3);
                    }else{
                        Intent intent2 = new Intent(BeiDouCarLieBiao.this,JiaOilJiLU.class);
                        intent2.putExtra("personInformation",getIntent().getStringExtra("personInformation"));
                        intent2.putExtra("ALLcarNum",getAllCarNum());
                        startActivity(intent2);
                    }*/
                    break;
            case 4: finish();break;
        }
    }

    private class BeiDouCarLieBiaoListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_BDButton:
                    setBroadcastReceiver();
                    finish();
                    break;

               /* case R.id.BD_serach:  Intent intent = new Intent(BeiDouCarLieBiao.this,BeiDouSouSuo.class);
                                        startActivityForResult(intent,0);
                                        break;*/
                /*case R.id.BD_CarMeau:
                    Toast.makeText(BeiDouCarLieBiao.this,"1111",Toast.LENGTH_SHORT).show();
                    BD_popWindow();
                    break;*/
                case R.id.tv_caidan:
                    //Toast.makeText(BeiDouCarLieBiao.this,"1111",Toast.LENGTH_SHORT).show();
                    BD_popWindow();
                    break;
                case R.id.BD_CarSearch:
                    //selectCar(BD_EditText.getText().toString().trim());
                    break;
                case R.id.meau_x:
                    closePopwindow();break;
            }
        }
    }
    private void BD_popWindow(){

        RelativeLayout top = (RelativeLayout) findViewById(R.id.rl_top);
        //RelativeLayout rllll = (RelativeLayout) findViewById(R.id.rllll);
        /*WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();*/
        //title.getHeight();
        View addview = LayoutInflater.from(this).inflate(R.layout.bdcarpopwindow_layout, null);
        addinit(addview);
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
        popupWindow.showAtLocation(top, Gravity.TOP,0,0);
    }
    private void closePopwindow() {

        if(popupWindow!=null){
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
    private void addinit(View view){
        List<BD_carPOPListView> list = new ArrayList<>();
        int [] iv = {R.mipmap.youliangbaobiao,R.mipmap.licheng,R.mipmap.jiayoujilu,R.mipmap.tuichubeidou};
        String [] tv={"油量报警统计","里程油耗统计","加油记录","退出北斗"};
        for (int i=0;i<iv.length;i++){
            BD_carPOPListView bd =new BD_carPOPListView();
            bd.setImageview(iv[i]);
            bd.setText(tv[i]);
            list.add(bd);
        }
        ListView listview=(ListView)view.findViewById(R.id.BD_carListView);
        View headerView = LayoutInflater.from(this).inflate(R.layout.listviewhead,null);
        Button meau_x = (Button)headerView.findViewById(R.id.meau_x);
        meau_x.setOnClickListener(new BeiDouCarLieBiaoListener());
        listview.setAdapter(new BD_POPListviewAdapter(BeiDouCarLieBiao.this,list));
        listview.addHeaderView(headerView);
        listview.setOnItemClickListener(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String value = data.getStringExtra("value");
        switch (resultCode){
            /*case 1:selectCar(value);
                break;*/
            case 2:break;
            case 3:break;
            default:
                Toast.makeText(this, "选择失败，请重新选择", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /*private class BDListViewListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(list!=null) {
                //MainActivity.instance.finish();
                Intent intent = new Intent();
                intent.setAction("com.neter.broadcast.receiver.SendDownXMLBroadCast");//发出自定义广播
                intent.putExtra("N_NUM",list.get(position).getCARNUM());
                sendBroadcast(intent);
                finish();
            }
        }
    }*/

    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            try{
                Log.e("warn","30");
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Get_CarNoPic_List";
                // EndPoint
                String endPoint =Path.get_ZanShibeidouPath();
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_CarNoPic_List";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数CarNum
                //rpc.addProperty("CarNum","");
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
                // 得到服务器传回的数据 返回的数据时集合 每一个count是一个及集合的对象
                int count1 = object.getPropertyCount();
                Log.e("warn",String.valueOf(count1));
                if(count1==0){
                    progressDialog.dismiss();
                    Message msg = Message.obtain();
                    msg.what=2;
                    hanlder.sendMessage(msg);
                    return;
                }
                if(count1>0)
                {
                    sb = new StringBuffer();
                    for (int i = 0; i < count1; i++) {
                        Log.e("warn","-----------------------------");
                        BeiDouCarLieBiaoBeen lieBiao = new BeiDouCarLieBiaoBeen();
                        SoapObject soapProvince = (SoapObject)object.getProperty(i);

                        Log.e("warn",soapProvince.getProperty("NUM").toString()+":");
                        sb.append(soapProvince.getProperty("NUM").toString()+",");

                        Log.e("warn",soapProvince.getProperty("CARNUM").toString()+":");
                        sb.append(soapProvince.getProperty("CARNUM").toString()+",");
                        //lieBiao.setD_NUM(soapProvince.getProperty("D_NUM").toString());

                        Log.e("warn",soapProvince.getProperty("CARTYPE").toString()+":");
                        sb.append(soapProvince.getProperty("CARTYPE").toString()+",");
                        //lieBiao.setD_OFFNUM(soapProvince.getProperty("D_OFFNUM").toString());

                        Log.e("warn",soapProvince.getProperty("PERID").toString()+":");
                        sb.append(soapProvince.getProperty("PERID").toString()+",");
                        //lieBiao.setD_ONLINE(soapProvince.getProperty("D_ONLINE").toString());

                        Log.e("warn",soapProvince.getProperty("NAME").toString()+":");
                        sb.append(soapProvince.getProperty("NAME").toString()+",");
                        //lieBiao.setD_CARNUMBER(soapProvince.getProperty("D_CARNUMBER").toString());

                        Log.e("warn",soapProvince.getProperty("TELNUMBER").toString()+":");
                        sb.append(soapProvince.getProperty("TELNUMBER").toString()+",");
                        //lieBiao.setN_NUM(soapProvince.getProperty("N_NUM").toString());;
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
                       /* Log.e("warn",soapProvince.getProperty("CARIMG").toString()+":");
                        sb.append(soapProvince.getProperty("CARIMG").toString()+",");
                        //lieBiao.setN_WD(soapProvince.getProperty("N_WD").toString());*/
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
    //旧
   /* Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            try{
                Log.e("warn","30");
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Get_DEVICE_NOWINFO_List";
                // EndPoint
                String endPoint = "http://103.215.28.26:2003/GPSService.asmx";
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_DEVICE_NOWINFO_List";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数CarNum
                rpc.addProperty("CarNum","");
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
                // 得到服务器传回的数据
                int count1 = object.getPropertyCount();
                Log.e("warn",String.valueOf(count1));
                if(count1>0)
                {
                    sb = new StringBuffer();
                    for (int i = 0; i < count1; i++) {
                        Log.e("warn","-----------------------------");
                        BeiDouCarLieBiaoBeen lieBiao = new BeiDouCarLieBiaoBeen();
                        SoapObject soapProvince = (SoapObject)object.getProperty(i);

                        Log.e("warn",soapProvince.getProperty("D_NUM").toString()+":");
                        sb.append(soapProvince.getProperty("D_NUM").toString()+",");
                        //lieBiao.setD_NUM(soapProvince.getProperty("D_NUM").toString());

                        Log.e("warn",soapProvince.getProperty("D_OFFNUM").toString()+":");
                        sb.append(soapProvince.getProperty("D_OFFNUM").toString()+",");
                        //lieBiao.setD_OFFNUM(soapProvince.getProperty("D_OFFNUM").toString());

                        Log.e("warn",soapProvince.getProperty("D_ONLINE").toString()+":");
                        sb.append(soapProvince.getProperty("D_ONLINE").toString()+",");
                        //lieBiao.setD_ONLINE(soapProvince.getProperty("D_ONLINE").toString());

                        Log.e("warn",soapProvince.getProperty("D_CARNUMBER").toString()+":");
                        sb.append(soapProvince.getProperty("D_CARNUMBER").toString()+",");
                        //lieBiao.setD_CARNUMBER(soapProvince.getProperty("D_CARNUMBER").toString());

                        Log.e("warn",soapProvince.getProperty("N_NUM").toString()+":");
                        sb.append(soapProvince.getProperty("N_NUM").toString()+",");
                        //lieBiao.setN_NUM(soapProvince.getProperty("N_NUM").toString());;

                        Log.e("warn",soapProvince.getProperty("N_WD").toString()+":");
                        sb.append(soapProvince.getProperty("N_WD").toString()+",");
                        //lieBiao.setN_WD(soapProvince.getProperty("N_WD").toString());

                        Log.e("warn",soapProvince.getProperty("N_WDFH").toString()+":");
                        sb.append(soapProvince.getProperty("N_WDFH").toString()+",");
                        //lieBiao.setN_WDFH(soapProvince.getProperty("N_WDFH").toString());

                        Log.e("warn",soapProvince.getProperty("N_JD").toString()+":");
                        sb.append(soapProvince.getProperty("N_JD").toString()+",");
                        //lieBiao.setN_JD(soapProvince.getProperty("N_JD").toString());

                        Log.e("warn",soapProvince.getProperty("N_JDFH").toString()+":");
                        sb.append(soapProvince.getProperty("N_JDFH").toString()+",");
                        //lieBiao.setN_JDFH(soapProvince.getProperty("N_JDFH").toString());

                        Log.e("warn",soapProvince.getProperty("N_DWZT").toString()+":");
                        sb.append(soapProvince.getProperty("N_DWZT").toString()+",");
                        //lieBiao.setN_DWZT(soapProvince.getProperty("N_DWZT").toString());

                        Log.e("warn",soapProvince.getProperty("N_YW").toString()+":");
                        sb.append(soapProvince.getProperty("N_YW").toString()+",");
                       // lieBiao.setN_YW(soapProvince.getProperty("N_YW").toString());

                        Log.e("warn",soapProvince.getProperty("N_TIME").toString()+":");
                        sb.append(soapProvince.getProperty("N_TIME").toString()+",");
                        //lieBiao.setN_TIME(soapProvince.getProperty("N_TIME").toString());


                        Log.e("warn",soapProvince.getProperty("X").toString()+":");
                        sb.append(soapProvince.getProperty("X")+",");
                       // lieBiao.setX((Double) soapProvince.getProperty("X"));

                        Log.e("warn",soapProvince.getProperty("Y").toString()+":");
                        sb.append(soapProvince.getProperty("Y")+",");
                        //lieBiao.setY((Double) soapProvince.getProperty("Y"));

                        Log.e("warn",soapProvince.getProperty("D_LENGHT").toString()+":");
                        sb.append(soapProvince.getProperty("D_LENGHT").toString()+",");
                        //lieBiao.setD_LENGHT(soapProvince.getProperty("D_LENGHT").toString());

                        Log.e("warn",soapProvince.getProperty("D_WIDTH").toString()+":");
                        sb.append(soapProvince.getProperty("D_WIDTH").toString()+",");
                        //lieBiao.setD_WIDTH(soapProvince.getProperty("D_WIDTH").toString());

                        Log.e("warn",soapProvince.getProperty("D_HEIGHT").toString()+":");
                        if(i==count1-1) {
                            sb.append(soapProvince.getProperty("D_HEIGHT").toString());
                        }else{
                            sb.append(soapProvince.getProperty("D_HEIGHT").toString()+"|");
                        }
                        //lieBiao.setD_HEIGHT(soapProvince.getProperty("D_HEIGHT").toString());
                        //listItem.put("img", soapProvince.getProperty("DJXX_NSRSBH").toString());
                        //list.add(lieBiao);
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
    };*/
    private BDListViewAdapter bdl;
    private android.os.Handler hanlder =new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String [] auto_arr;
            int i = msg.what;
            if(i==2){
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"无车辆信息",Toast.LENGTH_SHORT).show();
            }else if(i==1){
                progressDialog.dismiss();
                Log.e("warn",sb.toString());
                String arr [] = sb.toString().split("\\|");
                auto_arr = new String [arr.length];
                for(int j=0;j<arr.length;j++){
                    BeiDouCarLieBiaoBeen lieBiao = new BeiDouCarLieBiaoBeen();
                    String arr1 []=arr[j].split(",");
                    lieBiao.setNUM(arr1[0]);


                    lieBiao.setCARNUM(arr1[1]);
                    filledData(lieBiao);


                    lieBiao.setCARTYPE(arr1[2]);
                    lieBiao.setPERID(arr1[3]);
                    auto_arr[j]=arr1[1];
                    if(arr1[4].equals("anyType{}")){
                        lieBiao.setNAME("");
                    }else{
                        lieBiao.setNAME(arr1[4]);
                    }
                    if(arr1[5].equals("anyType{}")){
                        lieBiao.setTELNUMBER("");
                    }else{
                        lieBiao.setTELNUMBER(arr1[5]);
                    }
                    lieBiao.setONLINE(arr1[6]);
                    /*lieBiao.setD_NUM(arr1[0]);
                    lieBiao.setD_OFFNUM(arr1[1]);
                    lieBiao.setD_ONLINE(arr1[2]);
                    lieBiao.setD_CARNUMBER(arr1[3]);
                    lieBiao.setN_NUM(arr1[4]);
                    lieBiao.setN_WD(arr1[5]);
                    lieBiao.setN_WDFH(arr1[6]);
                    lieBiao.setN_JD(arr1[7]);
                    lieBiao.setN_JDFH(arr1[8]);
                    lieBiao.setN_DWZT(arr1[9]);
                    lieBiao.setN_YW(arr1[10]);
                    lieBiao.setN_TIME(arr1[11].substring(5,16));
                    lieBiao.setX(arr1[12]);
                    lieBiao.setY(arr1[13]);
                    lieBiao.setD_LENGHT(arr1[14]);
                    lieBiao.setD_WIDTH(arr1[15]);
                    lieBiao.setD_HEIGHT(arr1[16]);*/

                    list.add(lieBiao);
                    //list1.add(lieBiao);
                }

                bdl=new BDListViewAdapter(BeiDouCarLieBiao.this,list,BeiDouCarLieBiao.this);
                BDListView.setAdapter(bdl);


            }else{
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"网络或服务器异常",Toast.LENGTH_SHORT).show();
            }
        }
    };









    private void selectCar(String value){
        int i =0;
        int j=0;
        for(BeiDouCarLieBiaoBeen lt:list){
            j++;
           if(value.equals(lt.getCARNUM().toString().trim())){
               i=i+1;
               break;
           }
        }
        if(i==0){
            Toast.makeText(this, "你输入的车牌号不存在", Toast.LENGTH_SHORT).show();
        }else{
            BeiDouCarLieBiaoBeen lieBiao=list.get(j-1);
            list.clear();
            list.add(lieBiao);
            bdl.notifyDataSetChanged();//刷新listview列表
        }
    }
    private Handler imageHandle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private String getAllCarNum(){
        String str="";
        for(int i=0;i<list.size();i++){
            if(i==0){
                str = str +list.get(i).getCARNUM().toString();
            }else {
                str = str +","+list.get(i).getCARNUM().toString();
            }
        }
        return str;
    }
    private void setBroadcastReceiver(){
        Intent fanhui_intent = new Intent();
        fanhui_intent.setAction("com.neter.broadcast.receiver.fanhuizhuye");//发出自定义广播
        sendBroadcast(fanhui_intent);
    }
    //搜索
    private void filledData( BeiDouCarLieBiaoBeen sortModel){
        //汉字转换成拼音
        String pinyin = characterParser.getSelling(sortModel.getCARNUM());

        String sortString = pinyin.substring(0, 1).toUpperCase();

        // 正则表达式，判断首字母是否是英文字母
        if(sortString.matches("[A-Z]")){
            sortModel.setSortLetters(sortString.toUpperCase());
        }else{
            sortModel.setSortLetters("#");
        }

    }
    //监听输入框内容变化
    class BD_EditTextListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //s 是变化的内容 start输入内容数量 before删除内容数量 count
                Log.e("warn",s.toString());
                filterData(s.toString());
        }
        @Override
        public void afterTextChanged(Editable s) {
           /* Log.e("warn",s.toString()+"改变后");
            if(s.toString().equals("")){
                if(list.size()>1){//代表没选择 list没清空过
                }else{//被清空过 只有一条内容
                    list.clear();
                    for(BeiDouCarLieBiaoBeen l:list1){
                        list.add(l);
                    }
                    if(bdl!=null){
                        bdl.notifyDataSetChanged();
                    }

                }
            }*/
        }
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr
     */
    private void filterData(String filterStr){
        List<BeiDouCarLieBiaoBeen> filterDateList = new ArrayList<BeiDouCarLieBiaoBeen>();

        if(TextUtils.isEmpty(filterStr)){
            filterDateList =list;
        }else{
            filterDateList.clear();
            for(BeiDouCarLieBiaoBeen sortModel : list){
                String name = sortModel.getCARNUM();  //输入的内容转为大写
                if(name.indexOf(filterStr.toString().toUpperCase()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
                    filterDateList.add(sortModel);
                }
            }
        }

        // // 根据a-z进行排序��
        //Collections.sort(filterDateList, pinyinComparator);
        bdl.updateListView(filterDateList);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setBroadcastReceiver();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
