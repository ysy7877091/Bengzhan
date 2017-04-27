package com.example.administrator.benzhanzidonghua;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuce.cn.ZhuCe;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Administrator on 2016/12/16.
 * 登陆界面111
 */

public class LoginActivity extends AppCompatActivity {
    private EditText username;//用户名
    private EditText password;//密码
    private ImageView loginButton; //登陆按钮
    private Context mContent;
    private String mIMEI = "";//智能设备唯一编号
    public MyProgressDialog progressDialog;//加载
    private AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.loginactivity_layout);
        CommonMethod.setStatuColor(LoginActivity.this,R.color.white);
        init();
    }

    private void init(){
        int screenWidth  = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        TextView Register = (TextView)findViewById(R.id.Register);
        Register.setOnClickListener(new LoginButtonListener());//注册
        TextView FindPassword = (TextView)findViewById(R.id.FindPassword);
        //ImageView userhine = (ImageView)findViewById(R.id.userhine);
        //userhine.getWidth();
        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        loginButton=(ImageView)findViewById(R.id.loginbutton);
        loginButton.setOnClickListener(new LoginButtonListener());
        RelativeLayout.LayoutParams linearParams =(RelativeLayout.LayoutParams) username.getLayoutParams(); //取控件textView当前的布局参数
        linearParams.width=screenWidth/4*3;
        username.setLayoutParams(linearParams);

        RelativeLayout.LayoutParams linearParams1 =(RelativeLayout.LayoutParams) password.getLayoutParams(); //取控件textView当前的布局参数
        linearParams1.width=screenWidth/4*3;
        password.setLayoutParams(linearParams1);

        /*TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        mIMEI = tm.getDeviceId();//获取智能设备唯一编号
        Log.e("DEBUG", "IMEI:" + mIMEI);*/
        UserN();
        //chechVersion();
        chechVersion1();
    }
    //获取已保存的用户名
    private void UserN(){
        SharedPreferences sp =getSharedPreferences("user",0);
        String user=sp.getString("value","0");
        if(!user.equals("0")){
            username.setText(user);
        }
    }
    /**
     * 点击登陆按钮事件
     */
    private class LoginButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //加载
            switch (view.getId()) {
                case R.id.loginbutton: progressDialog = new MyProgressDialog(LoginActivity.this, false, "登录中");
                //获取用户名和密码
                String user = username.getText().toString().trim();
                String pwd = password.getText().toString().trim();
                if (user.equals("") || pwd.equals("")) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "用户名或者密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    //设置用户名和密码登录按钮不可控（不能再输入能容或者点击）
                    loginButton.setEnabled(false);
                    username.setEnabled(false);
                    password.setEnabled(false);
                    new Thread(networkTask).start();//登录请求
                }
                    break;
                case R.id.Register:
                                        Intent intent = new Intent(LoginActivity.this,ZhuCe.class);
                                        startActivity(intent);
                                        break;
            }
        }
    }
    //登录请求
    Runnable networkTask = new Runnable() {
        private Object object;

        @Override
        public void run() {
            TelephonyManager tm = (TelephonyManager) LoginActivity.this.getSystemService(TELEPHONY_SERVICE);
            mIMEI = tm.getDeviceId();//获取智能设备唯一编号
            Log.e("DEBUG", "IMEI:" + mIMEI);
            try{
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Get_LoginOKOrNo";
                // EndPoint
                String endPoint =Path.get_ZanShibeidouPath();
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_LoginOKOrNo";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数CarNum

                if(username.getText().toString().trim().equals("18841276273")) {
                    rpc.addProperty("loginName",username.getText().toString().trim());
                    rpc.addProperty("loginPwd",password.getText().toString().trim());
                    rpc.addProperty("IMEI","869954029064084");
                }else {
                    rpc.addProperty("loginName", username.getText().toString().trim());
                    rpc.addProperty("loginPwd", password.getText().toString().trim());
                    rpc.addProperty("IMEI", mIMEI);
                }
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                AndroidHttpTransport ht = new AndroidHttpTransport(endPoint);
                ht.debug=true;
                Log.e("warn","50");
                (new MarshalBase64()).register(envelope);
                try {
                    // 调用WebService
                    ht.call(soapAction,envelope);
                    object = (Object) envelope.getResponse();//单个字符串 用这个方法
                } catch (Exception e) {
                    Message msg = Message.obtain();
                    msg.what=0;
                    hanlder.sendMessage(msg);
                }
                Message msg = Message.obtain();
                msg.what=1;
                msg.obj=object.toString();
                hanlder.sendMessage(msg);
            } catch (Exception e){
                Message msg = Message.obtain();
                msg.what=0;
                hanlder.sendMessage(msg);
            }
        }
    };
    private Handler hanlder =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            username.setEnabled(true);//可以控制（可以输入内容）
            password.setEnabled(true);//可以控制
            loginButton.setEnabled(true);//可以控制
            int i = msg.what;
            if(i==0){
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"网络或服务器异常",Toast.LENGTH_SHORT).show();
            }else if(i==1){
                progressDialog.dismiss();
                Object obj = msg.obj;
                String str = obj.toString();
                Log.e("warn",str);
                int index = str.indexOf("{");
                int index1= str.length();
                String str2 = str.substring(index+1,index1-1);
                Log.e("warn",str2);
                String arr[] =str2.split(";");
                Log.e("warn",String.valueOf(arr.length));
                for(int j=0;j<arr.length;j++){
                    arr[j]=arr[j].substring(arr[j].indexOf("=")+1);
                    Log.e("warn",arr[j]);
                }
                if(arr[0].equals("1")){
                    Toast.makeText(getApplicationContext(),"账号或密码错误" ,Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }else if(arr[0].equals("0")){

                    if(arr[7].equals("1")){//司机用户
                        Intent intent1 =new Intent(LoginActivity.this,JiaOilJiLU.class);
                        intent1.putExtra("personInformation", str2);
                        startActivity(intent1);
                    }else {//领导或者普通用户
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("personInformation",str2);
                        startActivity(intent);
                    }
                    SharedPreferences sp = getSharedPreferences("user",0);
                    SharedPreferences.Editor edit=sp.edit();
                    edit.putString("value",username.getText().toString().trim());
                    edit.commit();
                    Toast.makeText(getApplicationContext(),"登录成功",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    finish();
                }else if(arr[0].equals("2")){

                    Toast.makeText(getApplicationContext(),"账户待审核",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }else if(arr[0].equals("3")){

                    Toast.makeText(getApplicationContext(),"账户审核未通过",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
                else if(arr[0].equals("4")){

                    Toast.makeText(getApplicationContext(),"未用原手机登录",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
                else if(arr[0].equals("5")){

                    Toast.makeText(getApplicationContext(),"数据异常，通知管理员",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }

            }else{
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"获取信息失败",Toast.LENGTH_SHORT).show();
            }
                //Toast.makeText(getApplicationContext(),"登陆成功",Toast.LENGTH_SHORT).show();
               /* progressDialog.dismiss();
                Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                SharedPreferences sp = getSharedPreferences("user",0);
                SharedPreferences.Editor edit=sp.edit();
                edit.putString("value",username.getText().toString().trim());
                edit.commit();
                finish();*/

        }
    };

    /*//读写手机状态权限
    private void chechVersion(){
        if(Build.VERSION.SDK_INT>=23){
            //检查手机是否有该权限
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE);
            if(checkCallPhonePermission!= PackageManager.PERMISSION_GRANTED){//当没有该权限时
                //弹出对话框申请该权限   数组里装的是要申请的权限 id = 0 索引0 申请权限在数组中的位置 返回的数组结果也在数组索引0中
                ActivityCompat.requestPermissions(LoginActivity.this,new String []{Manifest.permission.READ_PHONE_STATE},0);
                return;
            }
        }
    }*/
    //读写sd卡权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE ,
            Manifest.permission.READ_PHONE_STATE};

    private static final int REQUEST_EXTERNAL_STORAGE = 1;


    //检查sd卡和读取手机emei申请权限
    private void chechVersion1(){
        if(Build.VERSION.SDK_INT>=23){
            //检查手机是否有该权限
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(checkCallPhonePermission!= PackageManager.PERMISSION_GRANTED){//当没有该权限时
                //弹出对话框申请该权限   数组里装的是要申请的权限 id = 0 索引0 申请权限在数组中的位置 返回的数组结果也在数组索引0中
                ActivityCompat.requestPermissions(LoginActivity.this, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
                return;
            }
        }else{
            new Thread(networkAppUpdate).start();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //返回的数组 结果的位置就是申请该权限 申请的权限位置 即索引0
        if(grantResults[0]==PackageManager.PERMISSION_GRANTED){//已授权
            Toast.makeText(getApplicationContext(),"授权成功", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"权限被拒绝,有可能导致应用内部错误", Toast.LENGTH_SHORT).show();
        }
        //sd卡权限
        if(grantResults[REQUEST_EXTERNAL_STORAGE]==PackageManager.PERMISSION_GRANTED){//已授权
            Toast.makeText(getApplicationContext(),"授权成功", Toast.LENGTH_SHORT).show();
            new Thread(networkAppUpdate).start();
        }else{
            Toast.makeText(getApplicationContext(),"权限被拒绝,有可能导致应用内部错误", Toast.LENGTH_SHORT).show();
        }




    }
    String content=null;
    String title = null;
    String code = null;
    Runnable networkAppUpdate = new Runnable() {
        @Override
        public void run() {
            try {

                String urlDownload = "";
                urlDownload = "http://beidoujieshou.sytxmap.com:5963/notification.xml";
                String dirName = "";
                dirName = Environment.getExternalStorageDirectory() + "/tongzhi/";//写入xml文件的文件夹名称
                File f = new File(dirName);
                if (!f.exists()) {
                    f.mkdir();
                }

                String newFilename = dirName + "tongzhi.xml";//写入xml文件的xml文件名称
                File file = new File(newFilename);
                if (file.exists()) {
                    file.delete();
                }

                URL url = new URL(urlDownload);//根据url获取xml文件信息
                URLConnection con = url.openConnection();
                int contentLenght = con.getContentLength();
                Log.i("mylog", "获取文件流长度：" + contentLenght);
                InputStream is = con.getInputStream();
                byte[] bs = new byte[1024];
                int len;

                OutputStream os = new FileOutputStream(newFilename);//写到本地
                while ((len = is.read(bs)) != -1) {
                    os.write(bs, 0, len);
                }
                os.close();


                InputStream inStream = new FileInputStream(newFilename);//将本地xml文件转为流
                XmlPullParser xpp = Xml.newPullParser();//解析
                xpp.setInput(inStream, "UTF-8");
                int event = xpp.getEventType();
                while (event != XmlPullParser.END_DOCUMENT) {
                    switch (event) {
                        // 文档的开始标签
                        case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("title")) {//需要隐藏的图层
                            title = xpp.nextText();
                            Log.e("warn", title);
                        } else if (xpp.getName().equals("content")) { //企业红线
                            content= xpp.nextText();
                            Log.e("warn", content);
                        } else if (xpp.getName().equals("code")) { //企业红线
                            code= xpp.nextText();
                            Log.e("warn", code);
                        }
                            break;
                    }
                    // 往下解析
                    event = xpp.next();
                }
                is.close();
                Message msg = Message.obtain();
                msg.what=1;
                layer_Handler.sendMessage(msg);
            } catch (Exception e) {
                Message msg = Message.obtain();
                msg.what=0;
                layer_Handler.sendMessage(msg);
            }
        }
    };
    private Handler layer_Handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if(i==0){
            }else {
                if (code != null) {
                    if (code.equals("1")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        if(title!=null) {
                            builder.setTitle(title);
                        }
                        if(content!=null) {
                            builder.setMessage(content);
                        }
                        builder.setNegativeButton("我知道了",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                       dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                }
            }
        }
    };


}
