package com.example.administrator.benzhanzidonghua;


import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import jcifs.dcerpc.msrpc.MsrpcGetMembersInAlias;

/**
 * Created by Administrator on 2016-1-17.
 */
public class UpdateManager {

    String verCode = "";//版本号
    String verName = "";
    String verUrl = "";
    //* 下载中 *//*
    private static final int DOWNLOAD = 1;
    //* 下载结束 *//*
    private static final int DOWNLOAD_FINISH = 2;
    //* 保存解析的XML信息 *//*
    HashMap<String, String> mHashMap;
    //* 下载保存路径 *//*
    private String mSavePath;
    //* 记录进度条数量 *//*
    private int progress;
    //* 是否取消更新 *//*
    private boolean cancelUpdate = false;

    private Context mContext;
    //* 更新进度条 *//*
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;
    private TextView updateProgress;
    //下载的状态
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 正在下载
                case DOWNLOAD:
                    // 设置进度条位置
                    mProgress.setProgress(progress);
                    updateProgress.setText(progress+"%");
                    break;
                case DOWNLOAD_FINISH:
                    // 安装文件
                    installApk();
                    break;
                default:
                    break;
            }
        }

        ;
    };


    //构造函数，传参数
    public UpdateManager(Context context) {
        this.mContext = context;
    }

    //检测软件更新
    public void checkUpdate() {
        new Thread(networkAppUpdate).start();
    }

    Runnable networkAppUpdate = new Runnable() {
        @Override
        public void run() {
        try{
            // 获取当前软件版本
            int versionCode = getVersionCode(mContext);

            //二〇一六年六月二十二日 11:08:53

            String urlDownload="";
            urlDownload=MyApplicationData.get_AppUpdateUrl();
            String dirName="";
            dirName=Environment.getExternalStorageDirectory()+"/PSZX_Update/";//写入xml文件的文件夹名称
            File f=new File(dirName);
            if(!f.exists())
            {
                f.mkdir();
            }

            String newFilename=dirName+"version.xml";//写入xml文件的xml文件名称
            File file=new File(newFilename);
            if(file.exists())
            {
                file.delete();
            }

                URL url=new URL(MyApplicationData.get_AppUpdateUrl());//根据url获取xml文件信息
                URLConnection con=url.openConnection();
                int contentLenght=con.getContentLength();
                Log.i("mylog", "获取文件流长度："+contentLenght);
                InputStream is=con.getInputStream();
                byte[] bs=new byte[1024];
                int len;

                OutputStream os=new FileOutputStream(newFilename);//写到本地
                while ((len=is.read(bs))!=-1)
                {
                    os.write(bs,0,len);
                }
                os.close();


                InputStream inStream =new  FileInputStream(newFilename);//将本地xml文件转为流
                XmlPullParser xpp = Xml.newPullParser();//解析
                xpp.setInput(inStream, "UTF-8");
                int event = xpp.getEventType();
                while (event != XmlPullParser.END_DOCUMENT) {
                    switch (event) {
                        // 文档的开始标签
                        case XmlPullParser.START_TAG:
                            if (xpp.getName().equals("versionCode")) {
                                verCode = xpp.nextText();
                            } else if (xpp.getName().equals("versionName")) {
                                verName = xpp.nextText();
                            } else if (xpp.getName().equals("versionUrl")) {
                                verUrl = xpp.nextText();
                            }
                            break;
                    }
                    // 往下解析
                    event = xpp.next();
                }
                Log.e("warn",verCode+","+verName+","+verUrl);
                is.close();
                int verCodeInt = Integer.parseInt(verCode);//检测到的版本
                if (verCodeInt > versionCode) {
                    Message msg = Message.obtain();
                    msg.obj = "1";//有更新版本
                    handlerAppUpdate.sendMessage(msg);
                } else {
                    Message msg = Message.obtain();
                    msg.obj = "0";
                    handlerAppUpdate.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        };
        Handler handlerAppUpdate = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String val = (String) msg.obj;
                if (val.equals("1")) {
                    Log.i("mylog", "版本检查更新：开始更新");
                    // 显示提示对话框
                    showNoticeDialog();
                } else {
                    Log.i("mylog", "版本检查更新：已经是最新版本");
                    Toast.makeText(mContext, "版本检查更新：已经是最新版本", Toast.LENGTH_LONG).show();
                }
            }
        };
        //获取当前版本
        private int getVersionCode(Context context) {
            int versionCode = 0;
            try {
                // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
                versionCode = context.getPackageManager().getPackageInfo("com.example.administrator.benzhanzidonghua", 0).versionCode;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            return versionCode;
        }

        //显示更新对话框
        private void showNoticeDialog() {
            // 构造对话框
            Builder builder = new Builder(mContext);
            builder.setTitle("软件更新");
            builder.setMessage("检测到新版本，立即更新吗?");
            // 更新
            builder.setPositiveButton("更新", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    // 显示下载对话框
                    showDownloadDialog();
                }
            });
            // 稍后更新
            builder.setNegativeButton("取消", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            Dialog noticeDialog = builder.create();
            noticeDialog.show();
        }

        //显示软件下载对话框
        private void showDownloadDialog() {
            // 构造软件下载对话框
            Builder builder = new Builder(mContext);
            builder.setTitle("正在更新");
            builder.setCancelable(false);///点击对话框以外区域不关闭对话框
            // 给下载对话框增加进度条
            final LayoutInflater inflater = LayoutInflater.from(mContext);
            View v = inflater.inflate(R.layout.softupdate_progress, null);
            mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
            updateProgress = (TextView)v.findViewById(R.id.updateProgress);
            builder.setView(v);
            // 取消更新
            builder.setNegativeButton("取消", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    // 设置取消状态 即取消读取文件
                    cancelUpdate = true;
                }
            });
            mDownloadDialog = builder.create();
            mDownloadDialog.show();
            // 下载文件
            downloadApk();
        }

        //下载apk文件
        private void downloadApk() {
            // 启动新线程下载软件
            new downloadApkThread().start();
        }

        //下载文件线程
        class downloadApkThread extends Thread {
            @Override
            public void run() {
                try {
                    // 判断SD卡是否存在，并且是否具有读写权限
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        // 获得存储卡的路径 apk存储文件夹
                        String sdpath = Environment.getExternalStorageDirectory() + "/";
                        mSavePath = sdpath + "download";
                        if (verUrl.equals("")) {
                            Toast.makeText(mContext, "下载链接失效", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        URL url = new URL(verUrl);
                        // 创建连接
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.connect();
                        // 获取文件大小
                        int length = conn.getContentLength();
                        // 创建输入流
                        InputStream is = conn.getInputStream();
                        File file = new File(mSavePath);
                        // 判断文件目录是否存在
                        if (!file.exists()) {
                            //不存在，生成目录
                            file.mkdir();
                        }
                        File apkFile = new File(mSavePath, verName);//生成apk文件的名称
                        FileOutputStream fos = new FileOutputStream(apkFile);
                        int count = 0;
                        // 缓存
                        byte buf[] = new byte[1024];
                        // 写入到文件中
                        do { //设置进度条进度
                            int numread = is.read(buf);
                            count += numread;
                            // 计算进度条位置
                            progress = (int) (((float) count / length) * 100);     //读取流 下载过程
                            // 更新进度
                            mHandler.sendEmptyMessage(DOWNLOAD);
                            if (numread <= 0) {
                                // 下载完成
                                mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                                break;
                            }
                            // 写入文件
                            fos.write(buf, 0, numread);
                        } while (!cancelUpdate);// 点击取消就停止下载.
                        fos.close();
                        is.close();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 取消下载对话框显示
                mDownloadDialog.dismiss();
            }
        }
    };

        //安装APK文件
        private void installApk() {
            File apkfile = new File(mSavePath, verName);
            if (!apkfile.exists()) {
                return;
            }
            // 通过Intent安装APK文件
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
            mContext.startActivity(i);
        }

}