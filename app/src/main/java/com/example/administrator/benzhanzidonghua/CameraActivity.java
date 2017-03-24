package com.example.administrator.benzhanzidonghua;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.company.PlaySDK.IPlaySDK;
import com.dh.DpsdkCore.Enc_Channel_Info_Ex_t;
import com.dh.DpsdkCore.Get_Dep_Info_t;
import com.dh.DpsdkCore.Get_RealStream_Info_t;
import com.dh.DpsdkCore.IDpsdkCore;
import com.dh.DpsdkCore.Login_Info_t;
import com.dh.DpsdkCore.Return_Value_Info_t;
import com.dh.DpsdkCore.fDPSDKStatusCallback;
import com.dh.DpsdkCore.fMediaDataCallback;

import java.util.List;

/**
 * Created by wypqwer on 2016/12/21.
 */

public class CameraActivity extends Activity {

    static Return_Value_Info_t m_ReValue = new Return_Value_Info_t();
    static int m_nLastError ;

    private ImageView iv_fanHui;
    String m_serverIp = "";
    String m_serverPort = "";
    String m_serverUserName = "";
    String m_serverPassword = "";
    String m_channelId = "";
    private int m_pDLLHandle = 0;
    private byte[] m_szCameraId = null;
    private int m_nPort = 0;
    SurfaceView m_svPlayer = null;
    private int m_nSeq = 0;
    private int mTimeOut = 30 * 1000;
    static long m_loginHandle = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.camera_layout);

        iv_fanHui = (ImageView) findViewById(R.id.iv_fanHui);
        iv_fanHui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent mIntent = getIntent();
        m_serverIp = mIntent.getStringExtra("IP");
        m_serverPort = mIntent.getStringExtra("Port");
        m_serverUserName = mIntent.getStringExtra("LoginName");
        m_serverPassword = mIntent.getStringExtra("LoginPWD");
        m_channelId = mIntent.getStringExtra("ChannelId");

        int nType = 1;
        m_nLastError = IDpsdkCore.DPSDK_Create(nType, m_ReValue);
        IDpsdkCore.DPSDK_SetDPSDKStatusCallback(m_ReValue.nReturnValue, new fDPSDKStatusCallback() {
            @Override
            public void invoke(int nPDLLHandle, int nStatus) {
            }
        });

        m_svPlayer = (SurfaceView) findViewById(R.id.surfaceView);

        new LoginTask().execute();
        new Thread(new ThreadShow()).start();
    }

    class LoginTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            if (m_loginHandle != 0) {
                IDpsdkCore.DPSDK_Logout(m_ReValue.nReturnValue, 30000);
                m_loginHandle = 0;
            }
            Login_Info_t loginInfo = new Login_Info_t();
            loginInfo.szIp = m_serverIp.getBytes();
            String strPort = m_serverPort.trim();
            loginInfo.nPort = Integer.parseInt(strPort);
            loginInfo.szUsername = m_serverUserName.getBytes();
            loginInfo.szPassword = m_serverPassword.getBytes();
            loginInfo.nProtocol = 2;
            int nRet = IDpsdkCore.DPSDK_Login(m_ReValue.nReturnValue, loginInfo, 30000);

            return nRet;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == 0) {
                IDpsdkCore.DPSDK_SetCompressType(m_ReValue.nReturnValue, 0);
                m_loginHandle = 1;

                //登录成功播放视频
                m_pDLLHandle = m_ReValue.nReturnValue;
                m_szCameraId = m_channelId.getBytes();
                m_nPort = IPlaySDK.PLAYGetFreePort();
                SurfaceHolder holder = m_svPlayer.getHolder();
                holder.addCallback(new SurfaceHolder.Callback() {
                    public void surfaceCreated(SurfaceHolder holder) {
                        IPlaySDK.InitSurface(m_nPort, m_svPlayer);
                    }
                    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
                    public void surfaceDestroyed(SurfaceHolder holder) {}
                });

                final fMediaDataCallback fm = new fMediaDataCallback() {
                    @Override
                    public void invoke(int nPDLLHandle, int nSeq, int nMediaType,
                                       byte[] szNodeId, int nParamVal, byte[] szData, int nDataLen) {
                    }
                };
                if (!StartRealPlay()) {
                    return;
                }
                try {
                    Return_Value_Info_t retVal = new Return_Value_Info_t();
                    Get_RealStream_Info_t getRealStreamInfo = new Get_RealStream_Info_t();
                    System.arraycopy(m_szCameraId, 0, getRealStreamInfo.szCameraId, 0, m_szCameraId.length);
                    getRealStreamInfo.nMediaType = 1;
                    getRealStreamInfo.nRight = 0;
                    getRealStreamInfo.nStreamType = 1;
                    getRealStreamInfo.nTransType = 1;
                    Enc_Channel_Info_Ex_t ChannelInfo = new Enc_Channel_Info_Ex_t();
                    IDpsdkCore.DPSDK_GetChannelInfoById(m_pDLLHandle, m_szCameraId, ChannelInfo);
                    int ret = IDpsdkCore.DPSDK_GetRealStream(m_pDLLHandle, retVal, getRealStreamInfo, fm, mTimeOut);
                    if (ret == 0) {
                        m_nSeq = retVal.nReturnValue;
                        Toast.makeText(getApplicationContext(), "打开视频成功!", Toast.LENGTH_SHORT).show();
                    } else {
                        StopRealPlay();
                        Toast.makeText(getApplicationContext(), "打开视频失败!", Toast.LENGTH_SHORT).show();
                    }
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("value", "");
                    msg.setData(data);
                    handlerPlay.sendMessage(msg);
                } catch (Exception e) {
                    Log.e("xss", e.toString());
                }
            } else {
                m_loginHandle = 0;
                if(result==3)
                {
                    AlertDialog.Builder builder=new AlertDialog.Builder(CameraActivity.this);
                    builder.setMessage("摄像头登录失败("+result+")");
                    builder.setTitle("提示");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            displayVideo();
                            finish();
                        }
                    });
                    builder.create().show();
                }
                else if(result==4)
                {
                    AlertDialog.Builder builder=new AlertDialog.Builder(CameraActivity.this);
                    builder.setMessage("摄像头登录已经超过最大连接数("+result+")");
                    builder.setTitle("提示");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            displayVideo();
                            finish();
                        }
                    });
                    builder.create().show();
                }
                else
                { AlertDialog.Builder builder=new AlertDialog.Builder(CameraActivity.this);
                    builder.setMessage("摄像头登录失败("+result+")");
                    builder.setTitle("提示");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            displayVideo();
                            finish();
                        }
                    });
                    builder.create().show();
                }
            }
        }
    }
    public void StopRealPlay() {
        try {
            IPlaySDK.PLAYStopSoundShare(m_nPort);
            IPlaySDK.PLAYStop(m_nPort);
            IPlaySDK.PLAYCloseStream(m_nPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean StartRealPlay() {
        if (m_svPlayer == null)
            return false;

        Get_Dep_Info_t get_dep_info_t=new Get_Dep_Info_t(100,100);
        IDpsdkCore.DPSDK_GetDGroupInfo(m_ReValue.nReturnValue,get_dep_info_t);

        boolean bOpenRet = IPlaySDK.PLAYOpenStream(m_nPort, null, 0, 1500 * 1024) == 0 ? false : true;
        if (bOpenRet) {
            boolean bPlayRet = IPlaySDK.PLAYPlay(m_nPort, m_svPlayer) == 0 ? false : true;
            if (bPlayRet) {
                boolean bSuccess = IPlaySDK.PLAYPlaySoundShare(m_nPort) == 0 ? false : true;
                if (!bSuccess) {
                    IPlaySDK.PLAYStop(m_nPort);
                    IPlaySDK.PLAYCloseStream(m_nPort);
                    return false;
                }
            } else {
                IPlaySDK.PLAYCloseStream(m_nPort);
                return false;
            }
        } else {
            return false;
        }
        return true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            displayVideo();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private void displayVideo()
    {
        StopRealPlay();
        IDpsdkCore.DPSDK_Logout(m_pDLLHandle,mTimeOut);
    }
    Handler handlerPlay = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            TextView textView=(TextView)findViewById(R.id.txt等待视频);
            textView.setVisibility(View.INVISIBLE);
        }
    };

    public static boolean isBackground(Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }
    class ThreadShow implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    // handler类接收数据
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if(isBackground(getApplicationContext()))
                {
                    //后台运行则关闭视频播放画面
                    displayVideo();
                    finish();
                    System.out.println("后台运行");
                }
                else{
                    System.out.println("前台运行");
                }
            }
        }
    };
}