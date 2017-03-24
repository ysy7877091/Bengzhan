package com.example.administrator.benzhanzidonghua;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Xml;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.vanpeng.javabeen.SystemBarTintManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/12/16.
 * 相同的方法
 */

public class CommonMethod {
    public static String ReadSoap(String fileName){
      InputStream ios =MainActivity.class.getClassLoader().getResourceAsStream(fileName);
      byte[] b = new byte[1024];
      StringBuffer sb = new StringBuffer();
      int len = 0;
      try {
          while ((len = ios.read(b)) != -1) {
              String str = new String(b, 0, len, "UTF-8");
              sb.append(str);
          }
      } catch (Exception e) {
          e.printStackTrace();
      }
      System.out.println(sb.toString());
      return sb.toString();
  }
    public static String Request(String path, byte[] date,String MethodName){
        String msg=null;
        try {//参数一请求地址参数二soap数组参数三方法名
            HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            conn.setRequestProperty("Content-Length", String.valueOf(date.length));
            conn.getOutputStream().write(date);
            Log.e("warn", String.valueOf(conn.getResponseCode()));
            if (conn.getResponseCode() == 200) {
                msg= CommonMethod.parseXml(conn.getInputStream(),MethodName);
            }
        }catch (Exception e){
            return "999999";
        }
         if(msg==null){
             return "999999";
         }
        return msg;
    }
    public static String parseXml(InputStream is,String name) throws XmlPullParserException,
            IOException {

        XmlPullParser xpp = Xml.newPullParser();
        xpp.setInput(is, "UTF-8");
        int event = xpp.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                // 文档的开始标签
                case XmlPullParser.START_TAG:
                    if (xpp.getName().equals(name)) {
                        return xpp.nextText();
                    }
                    break;
            }
            // 往下解析
            event = xpp.next();
        }
        return null;
    }


    // 我们知道IOS上的应用，
// 状态栏的颜色总能与应用标题栏颜色保持一致，用户体验很不错，那安卓是否可以呢？
// 若是在安卓4.4之前，答案是否定的，但在4.4之后，谷歌允许开发者自定义状态栏背景颜色啦，
// 这是个不错的体验！若你手机上安装有最新版的qq，并且你的安卓SDK版本是4.4及以上
    public static void setStatuColor(Activity activity,int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true,activity);
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(color);//通知栏所需颜色
        }
    }
    // @TargetApi(19)
    private static void setTranslucentStatus(boolean on,Activity activity) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
    private Context context;
    //检查版本申请权限
    public void chechVersion(Context context,Activity activity,String permission){
        if(Build.VERSION.SDK_INT>=23){
            //检查手机是否有该权限
            this.context = activity;
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(context, permission);
            if(checkCallPhonePermission!= PackageManager.PERMISSION_GRANTED){//当没有该权限时
                //弹出对话框申请该权限   数组里装的是要申请的权限 id = 0 索引0 申请权限在数组中的位置 返回的数组结果也在数组索引0中
                ActivityCompat.requestPermissions(activity,new String []{permission},0);
                return;
            }
        }
    }
    public void onRequestResult(int[] grantResults,String str) {
        //返回的数组 结果的位置就是申请该权限 申请的权限位置 即索引0
        if(grantResults[0]==PackageManager.PERMISSION_GRANTED){//已授权
            Toast.makeText(context,"授权成功", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        }
    }
}
