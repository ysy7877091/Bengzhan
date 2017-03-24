package com.example.administrator.benzhanzidonghua;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vanpeng.javabeen.BeiDouCarLieBiaoBeen;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import java.util.Calendar;

/**
 * Created by Administrator on 2017/2/20 0020.
 * 车辆信息详情
 */

public class CarInformation extends AppCompatActivity {
    private MyProgressDialog progressDialog;
    private Bitmap car_ph=null;
    private Bitmap per_photo=null;
    private StringBuffer sb;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carinformation_layout);
        Button car_Back = (Button)findViewById(R.id.car_Back);
        car_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        progressDialog = new MyProgressDialog(CarInformation.this, false, "加载中...");
        new Thread( networkTask).start();

    }
    private void init(String [] arr){
        /*Button BD_meau = (Button)findViewById(R.id.BD_meau);
        BD_meau.setOnClickListener(new CarInformationListener());*/
        TextView car_BianHao = (TextView)findViewById(R.id.car_BianHao);//是否在线
        if(arr[5].equals("1")){arr[5]="不在线";}else{arr[5]="在线";}
        car_BianHao.setText(arr[5].trim());
        TextView car_zu=(TextView)findViewById(R.id.car_zu); //所属分组
        car_zu.setText(arr[2]);
        TextView car_name=(TextView)findViewById(R.id.car_name);//车主姓名
        car_name.setText(arr[3]);
        TextView telephone=(TextView)findViewById(R.id.telephone);//联系电话
        telephone.setText(arr[4]);
        TextView car_tyle=(TextView)findViewById(R.id.car_tyle);//车辆型号
        car_tyle.setText(arr[1]);
        TextView car_num=(TextView)findViewById(R.id.car_num);//车牌号
        car_num.setText(arr[0]);
        ImageView car_photo =(ImageView) findViewById(R.id.car_photo);//车辆照片
        if(arr[10]!=null||!arr[10].equals("")) {
            car_ph = stringtoBitmap(arr[10]);
            if (car_ph != null) {
                car_ph = zoomImg(car_ph, car_photo.getWidth(), car_photo.getHeight());
                car_photo.setImageBitmap(car_ph);
            }
        }
            ImageView person_photo = (ImageView) findViewById(R.id.person_photo);//人员照片

            if (!arr[11].equals("") || arr[11] != null) {
                per_photo = stringtoBitmap(arr[11]);
                if (per_photo != null) {
                    per_photo = zoomImg(per_photo, person_photo.getWidth(), person_photo.getHeight());
                    person_photo.setImageBitmap(per_photo);
                }
            }


    }
    //请求车辆信息
    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            try{
                Log.e("warn","30");
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Get_CarInfo";
                // EndPoint
                String endPoint = "http://beidoujieshou.sytxmap.com:5963/GPSService.asmx";
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_CarInfo";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数CarNum
                Log.e("warn",getIntent().getStringExtra("carNum").toString()+":提交");
                rpc.addProperty("carNum",getIntent().getStringExtra("carNum").toString());
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
                int count1 = object.getPropertyCount();
                Log.e("warn",String.valueOf(count1));
                if(count1>0)
                {
                    Log.e("warn","-----------------------------");
                    SoapObject soapProvince = (SoapObject) envelope.bodyIn;
                    Log.e("warn",soapProvince.getProperty("Get_CarInfoResult").toString()+":返回id");//dataset数据类型
                    String str = soapProvince.getProperty("Get_CarInfoResult").toString();
                    Message msg = Message.obtain();
                    msg.what=1;
                    msg.obj=str;
                    hanlder.sendMessage(msg);
                }
            } catch (Exception e){
                Message msg = Message.obtain();
                msg.what=0;
                hanlder.sendMessage(msg);
            }
        }
    };
    private Handler hanlder = new Handler(){
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
                int index = str.indexOf("{");
                int index1= str.length();
                String str2 = str.substring(index+1,index1-1);
                Log.e("warn",str2);
                String arr[] =str2.split(";");
                for(int j=0;j<arr.length;j++){
                    arr[j]=arr[j].substring(arr[j].indexOf("=")+1);
                    Log.e("warn",arr[j]);
                }
                init(arr);
            }else{
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"获取信息失败",Toast.LENGTH_SHORT).show();
            }
        }
    };
    public Bitmap stringtoBitmap(String string){
        //将base64字符串转换成Bitmap类型
        if(string==null||string.equals("anyType{}")){
            Toast.makeText(this, "照片不存在", Toast.LENGTH_SHORT).show();
            return null;
        }
        Bitmap bitmap=null;
        try {
            byte[] bitmapArray;
            bitmapArray= Base64.decode(string, Base64.DEFAULT);//将字符串转为字节数组
            bitmap= BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);//将字节数组转为图片
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    //设置图片的宽高
    public static Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth=0;
        float scaleHeight=0;
        if(width>newWidth){
            scaleWidth = ((float) newWidth) / width;
            scaleHeight=1;//无缩放
        }else if(height>newHeight){
            scaleHeight = ((float) newHeight) / height;
            scaleWidth=1;//无缩放
        }else if(width>newWidth&&height>newHeight){
            scaleWidth = ((float) newWidth) / width;
            scaleHeight = ((float) newHeight) / height;
        }else{
            return bm;
        }
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }
   /* Runnable YouLiangTask = new Runnable() {
        Calendar c = Calendar.getInstance();
        //取得系统日期:
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        String year1=String.valueOf(year);
        String month1=String.valueOf(month);
        String day1=String.valueOf(day);
        @Override
        public void run() {
            try{
                if(month1.length()==1){month1="0"+month1;}
                if(day1.length()==1){day1="0"+day1;}
                Log.e("warn","30");
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Get_DayOil_List";
                // EndPoint
                String endPoint = "http://100.0.26.50:805/GPSService.asmx";
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_DayOil_List";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数CarNum
                Log.e("warn",getIntent().getStringExtra("carNum").toString()+":提交");
                rpc.addProperty("carNum",getIntent().getStringExtra("carNum").toString());
                rpc.addProperty("time",year1+"-"+month1+"-"+day1);
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
                    Yhhanlder.sendMessage(msg);
                }

                SoapObject object;
                // 开始调用远程方法
                Log.e("warn","60");


                object = (SoapObject) envelope.getResponse();
                Log.e("warn","64");
                // 得到服务器传回的数据 数据时dataset类型的
                int count1 = object.getPropertyCount();
                Log.e("warn",String.valueOf(count1));
                if(count1>0)
                {  sb = new StringBuffer();
                    for (int i = 0; i < count1; i++) {
                        SoapObject soapProvince = (SoapObject) object.getProperty(i);
                        Log.e("warn", soapProvince.getProperty("CARNUM").toString() + ":");
                        sb.append(soapProvince.getProperty("CARNUM").toString() + ",");
                        //lieBiao.setD_NUM(soapProvince.getProperty("D_NUM").toString());

                        Log.e("warn", soapProvince.getProperty("TIME").toString() + ":");
                        sb.append(soapProvince.getProperty("TIME").toString() + ",");
                        //lieBiao.setD_OFFNUM(soapProvince.getProperty("D_OFFNUM").toString());

                        Log.e("warn", soapProvince.getProperty("OILL").toString() + ":");
                        if(i==count1-1){
                            sb.append(soapProvince.getProperty("OILL").toString());
                        }else {
                            sb.append(soapProvince.getProperty("OILL").toString() + "|");
                        }
                        //lieBiao.setD_ONLINE(soapProvince.getProperty("D_ONLINE").toString());
                        Message msg = Message.obtain();
                        msg.what = 1;
                        Yhhanlder.sendMessage(msg);
                    }
                }
            } catch (Exception e){
                Message msg = Message.obtain();
                msg.what=0;
                Yhhanlder.sendMessage(msg);
            }
        }
    };
    private Handler Yhhanlder = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int i = msg.what;
                if(i==0){
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"网络或服务器异常",Toast.LENGTH_SHORT).show();
                }else if(i==1){
                    String arr [] = sb.toString().split("\\|");
                    for(int j=0;j<arr.length;j++){
                        String arr1 [] = sb.toString().split(",");



                    }
                 }
              }
    };*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(car_ph!=null){
            car_ph.recycle();
        }
        if(per_photo!=null){
            per_photo.recycle();
        }
    }
}
