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

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

/**
 * Created by Administrator on 2017/3/6 0006.
 */

public class BeiDouPersonInformation extends AppCompatActivity {
    private StringBuffer sb;
    private MyProgressDialog progressDialog;
    private ImageView photo;
    private Bitmap bitmap=null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_info);
        Button person_Back = (Button)findViewById(R.id.person_Back);
        person_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        progressDialog = new MyProgressDialog(BeiDouPersonInformation.this, false, "加载中...");
        new Thread( networkTask).start();
    }
    private void init(String arr []){
        photo =(ImageView)findViewById(R.id.imageView);//人员照片
            bitmap = stringtoBitmap(arr[11]);
            if (bitmap != null) {
                bitmap = zoomImg(bitmap, photo.getWidth(), photo.getHeight());//设置图片大小
                photo.setImageBitmap(bitmap);
            }else{
                Toast.makeText(this, "人员图片无", Toast.LENGTH_SHORT).show();
            }
        TextView person_Name = (TextView)findViewById(R.id.tv_person_Name);//人员姓名
        person_Name.setText(arr[1]);
        TextView person_sex = (TextView)findViewById(R.id.tv_person_sex);//人员性别
        person_sex.setText(arr[2]);
        TextView person_Number =(TextView)findViewById(R.id.tv_person_Number);//人员电话
        person_Number.setText(arr[4]);
        TextView person_FenZu =(TextView)findViewById(R.id.tv_person_FenZu);//所属分zu
        person_FenZu.setText(arr[3]);
        TextView JiaShiZhengBianHao =(TextView)findViewById(R.id.tv_person_JiaShiZhengBianHao);//驾驶证编号
        JiaShiZhengBianHao.setText(arr[5]);
        TextView person_JiaLing =(TextView)findViewById(R.id.tv_person_JiaLing);//驾龄
        person_JiaLing.setText(arr[8]);
        TextView NianJianQiXian =(TextView)findViewById(R.id.tv_person_NianJianQiXian);//年检期限
        NianJianQiXian.setText(arr[7]);
        TextView person_SuoShuDanWei =(TextView)findViewById(R.id.tv_person_SuoShuDanWei);//所属单位
        person_SuoShuDanWei.setText(arr[6]);
        TextView tv_JiaShiZhengJiBie =(TextView)findViewById(R.id.tv_JiaShiZhengJiBie);//驾驶证级别
        tv_JiaShiZhengJiBie.setText(arr[9]);
    }
    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            try{
                Log.e("warn","30");
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Get_PersonInfo";
                // EndPoint
                String endPoint = "http://beidoujieshou.sytxmap.com:5963/GPSService.asmx";
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_PersonInfo";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数CarNum
                Log.e("warn",getIntent().getStringExtra("perID").toString()+":提交id");
                rpc.addProperty("id",getIntent().getStringExtra("perID").toString());
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
                    sb = new StringBuffer();

                        Log.e("warn","-----------------------------");
                        SoapObject soapProvince = (SoapObject) envelope.bodyIn;

                        Log.e("warn",soapProvince.getProperty("Get_PersonInfoResult").toString()+":返回id");//dataset数据类型
                        String str = soapProvince.getProperty("Get_PersonInfoResult").toString();

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
        //将字符串转换成Bitmap类型
        if(string==null||string.equals("")||string.equals("anyType{}")){
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bitmap!=null){
            bitmap.recycle();
        }
    }
}
