package com.example.administrator.benzhanzidonghua;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

        TextView cancel = (TextView)findViewById(R.id.cancel);
        cancel.setOnClickListener(new LoginButtonListener());
        chechVersion();
        TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        mIMEI = tm.getDeviceId();//获取智能设备唯一编号
        Log.d("DEBUG", "IMEI:" + mIMEI);
        UserN();
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
                case R.id.cancel:finish();break;
            }
        }
    }
    //登录请求
    Runnable networkTask = new Runnable() {
        @Override
        public void run() {

            String methodName = "AppLoginResult";//登陆方法名
            String path=Path.get_WebServicesURL();//登陆请求地址

            String SoapFileName = "assets/login.xml";//读取的soap协议xml文件名称
            String soap = CommonMethod.ReadSoap(SoapFileName);
            soap=soap.replaceAll("string1",username.getText().toString().trim());
            soap=soap.replaceAll("string2",password.getText().toString().trim());
            soap=soap.replaceAll("string3",mIMEI);
            //Log.e("warn",username.getText().toString().trim()+":"+password.getText().toString().trim());
            Log.e("warn",soap);
            byte [] date=soap.getBytes();//soap协议转为字符数组
            String result=CommonMethod.Request(path,date,methodName);
            Message msg = Message.obtain();
            msg.obj=result;
            hanlder.sendMessage(msg);
        }
    };
    private Handler hanlder =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            username.setEnabled(true);//可以控制（可以输入内容）
            password.setEnabled(true);//可以控制
            loginButton.setEnabled(true);//可以控制
            String val=(String) msg.obj;
            if (val.toString().equals("999999")) {
                Toast.makeText(getApplicationContext(), "登录失败网络或者服务器异常", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            } else if (val.toString().equals("0")) {
                Toast.makeText(getApplicationContext(), "用户名不存在", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            } else if (val.toString().equals("-1")) {
                Toast.makeText(getApplicationContext(), "此电话未经授权登录", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            } else if (val.toString().equals("-2")) {
                Toast.makeText(getApplicationContext(), "密码不正确", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            } else if (val.toString().equals("-3")) {
                Toast.makeText(getApplicationContext(), "此用户未授权登录", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            } else if (val.toString().equals(("1"))) {
                //Toast.makeText(getApplicationContext(),"登陆成功",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                SharedPreferences sp = getSharedPreferences("user",0);
                SharedPreferences.Editor edit=sp.edit();
                edit.putString("value",username.getText().toString().trim());
                edit.commit();
                finish();
            }
        }
    };
    //检查版本申请权限
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
    }
}
