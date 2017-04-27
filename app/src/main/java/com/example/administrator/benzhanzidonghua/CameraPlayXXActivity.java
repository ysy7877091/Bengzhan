package com.example.administrator.benzhanzidonghua;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.company.PlaySDK.IPlaySDK;
import com.dh.DpsdkCore.Enc_Channel_Info_Ex_t;
import com.dh.DpsdkCore.Get_Dep_Info_t;
import com.dh.DpsdkCore.Get_RealStream_Info_t;
import com.dh.DpsdkCore.IDpsdkCore;
import com.dh.DpsdkCore.Login_Info_t;
import com.dh.DpsdkCore.Ptz_Direct_Info_t;
import com.dh.DpsdkCore.Return_Value_Info_t;
import com.dh.DpsdkCore.fDPSDKStatusCallback;
import com.dh.DpsdkCore.fMediaDataCallback;
import com.vanpeng.javabeen.VideoMonitoring;

import java.util.List;

/**
 * 泵站显示视频 dpsdk_ptz_direct_e  com.dh.DpsdkCore.Ptz_Direct_Info_t IDpsdkCore
 */
public class CameraPlayXXActivity extends AppCompatActivity {
    //Return_Value_Info_t jar包里的
    static Return_Value_Info_t m_ReValue = new Return_Value_Info_t();
    static int m_nLastError = 0;
    //String m_serverIp = "223.100.253.208";
    //String m_serverPort = "9000";
    //String m_serverUserName = "cs";
    //String m_serverPassword = "admin";
    //String m_channelId = "1000084$1$0$0";

    String m_serverIp = "";
    String m_serverPort =  "";
    String m_serverUserName = "";
    String m_serverPassword ="";
    String m_channelId ="";

    private String isfirstLogin;

    private int m_pDLLHandle = 0;
    private byte[] m_szCameraId = null;
    private int m_nPort = 0;
    SurfaceView m_svPlayer = null;
    private int m_nSeq = 0;
    private int mTimeOut = 30 * 1000;
    static long m_loginHandle = 0;
    private Button SP_UP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        isScreenChange();
        VideoMonitoring VideoMonitoring =(VideoMonitoring)getIntent().getSerializableExtra("VideoMonitoring");
        if(VideoMonitoring==null){
            Toast.makeText(getApplicationContext(),"此路视频不存在",Toast.LENGTH_SHORT).show();
            return;
        }
        /*SP_UP =(Button)findViewById(R.id.SP_UP);
        SP_UP.setOnClickListener(new CameraPlayXXActivityListener());*/
        //返回
        Button SP_button = (Button)findViewById(R.id.SP_button);
        SP_button.setOnClickListener(new CameraPlayXXActivityListener());
        /*Ptz_Direct_Info_t p  =new Ptz_Direct_Info_t();
        p.bStop*/
        //Intent mIntent=getIntent();
        m_serverIp = VideoMonitoring.getIp();
        Log.e("warn","IP:"+m_serverIp);
        m_serverPort =  VideoMonitoring.getPort();
        Log.e("warn","port:"+m_serverPort);
        m_serverUserName =  VideoMonitoring.getLoginName();
        m_serverPassword =VideoMonitoring.getLoginPWD();
        m_channelId =VideoMonitoring.getChannelID();


        Log.d("onCreate:", m_nLastError + "");
        int nType = 1;
        m_nLastError = IDpsdkCore.DPSDK_Create(nType, m_ReValue);

        IDpsdkCore.DPSDK_SetDPSDKStatusCallback(m_ReValue.nReturnValue, new fDPSDKStatusCallback() {

            @Override
            public void invoke(int nPDLLHandle, int nStatus) {
                Log.v("fDPSDKStatusCallback", "nStatus = " + nStatus);
            }
        });
        Log.d("DpsdkCreate:", m_nLastError + "");
        //视图，用于游戏画面，或者摄像头预览、视频播放
        //m_svPlayer = (SurfaceView) findViewById(R.id.surfaceView);
        new LoginTask().execute();

        new Thread(new ThreadShow()).start();

    }

    class LoginTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... arg0) {               //在此处处理UI会导致异常
            if (m_loginHandle != 0) {
                IDpsdkCore.DPSDK_Logout(m_ReValue.nReturnValue, 30000);
                m_loginHandle = 0;
            }
            //申请视频  与控制台控制相似
            Login_Info_t loginInfo = new Login_Info_t();
            Integer error = Integer.valueOf(0);
            loginInfo.szIp = m_serverIp.getBytes();
            String strPort = m_serverPort.trim();
            loginInfo.nPort = Integer.parseInt(strPort);
            loginInfo.szUsername = m_serverUserName.getBytes();
            loginInfo.szPassword = m_serverPassword.getBytes();
            loginInfo.nProtocol = 2;
            //saveLoginInfo();
            int nRet = IDpsdkCore.DPSDK_Login(m_ReValue.nReturnValue, loginInfo, 30000);
            return nRet;
        }

        @Override
        protected void onPostExecute(Integer result) {

            super.onPostExecute(result);
            //mProgressDialog.dismiss();
            if (result == 0) {
                Log.d("DpsdkLogin success:", result + "");
                IDpsdkCore.DPSDK_SetCompressType(m_ReValue.nReturnValue, 0);
                m_loginHandle = 1;

                //登录成功播放视频

                m_pDLLHandle = m_ReValue.nReturnValue;
                ;

                m_szCameraId = m_channelId.getBytes();
                //etCam.setText(getIntent().getStringExtra("channelName"));
                //int nRet;
                m_nPort = IPlaySDK.PLAYGetFreePort();
                SurfaceHolder holder = m_svPlayer.getHolder();
                holder.addCallback(new Callback() {
                    public void surfaceCreated(SurfaceHolder holder) {
                        Log.d("xss", "surfaceCreated");
                        IPlaySDK.InitSurface(m_nPort, m_svPlayer);
                    }

                    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                               int height) {
                        Log.d("xss", "surfaceChanged");
                    }

                    public void surfaceDestroyed(SurfaceHolder holder) {
                        Log.d("xss", "surfaceDestroyed");
                    }
                });

                final fMediaDataCallback fm = new fMediaDataCallback() {

                    @Override
                    public void invoke(int nPDLLHandle, int nSeq, int nMediaType,
                                       byte[] szNodeId, int nParamVal, byte[] szData, int nDataLen) {
                        Log.d("xss", "fMediaDataCallback回调");
                        int ret = IPlaySDK.PLAYInputData(m_nPort, szData, nDataLen);
                        if (ret == 1) {
                            Log.e("xss", "playing success=" + nSeq + " package size=" + nDataLen);
                        } else {
                            Log.e("xss", "playing failed=" + nSeq + " package size=" + nDataLen);
                        }
                    }
                };

                if (!StartRealPlay()) {
                    Log.e("xss", "StartRealPlay failed!");
                    //Toast.makeText(getApplicationContext(), "Open video failed!", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    Return_Value_Info_t retVal = new Return_Value_Info_t();
                    Log.e("AAAAAAAAAA", "1");
                    Get_RealStream_Info_t getRealStreamInfo = new Get_RealStream_Info_t();
                    //m_szCameraId = etCam.getText().toString().getBytes();
                    Log.e("AAAAAAAAAA", "2");
                    System.arraycopy(m_szCameraId, 0, getRealStreamInfo.szCameraId, 0, m_szCameraId.length);
                    Log.e("AAAAAAAAAA", "3");
                    //getRealStreamInfo.szCameraId = "1000096$1$0$0".getBytes();
                    getRealStreamInfo.nMediaType = 1;
                    getRealStreamInfo.nRight = 0;
                    getRealStreamInfo.nStreamType = 1;
                    getRealStreamInfo.nTransType = 1;
                    Log.e("AAAAAAAAAA", "4");
                    Enc_Channel_Info_Ex_t ChannelInfo = new Enc_Channel_Info_Ex_t();
                    Log.e("AAAAAAAAAA", "5");
                    IDpsdkCore.DPSDK_GetChannelInfoById(m_pDLLHandle, m_szCameraId, ChannelInfo);
                    Log.e("AAAAAAAAAA", "6");
                    int ret = IDpsdkCore.DPSDK_GetRealStream(m_pDLLHandle, retVal, getRealStreamInfo, fm, mTimeOut);
                    Log.e("AAAAAAAAAA", "7,ret:" + ret);
                    if (ret == 0) {

                        m_nSeq = retVal.nReturnValue;
                        //Log.e("xss DPSDK_GetRealStream success!", ret + "");



                        Toast.makeText(getApplicationContext(), "打开视频成功!", Toast.LENGTH_SHORT).show();
                    } else {
                        StopRealPlay();
                        //Log.e("xss DPSDK_GetRealStream failed!", ret + "");
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
                Log.d("DpsdkLogin failed:", result + "");
                //Toast.makeText(getApplicationContext(), "login failed" + result, Toast.LENGTH_SHORT).show();
                m_loginHandle = 0;

                if(isState){
                    return;//代表退出了activity
                }

                if(result==3)
                {
                    if(isState){
                        return;//代表退出了activity
                    }
                    AlertDialog.Builder builder=new AlertDialog.Builder(CameraPlayXXActivity.this);
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
                    if(isState){
                        return;//代表退出了activity
                    }
                    AlertDialog.Builder builder=new AlertDialog.Builder(CameraPlayXXActivity.this);
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
                {
                    if(isState){
                        return;//代表退出了activity
                    }
                    AlertDialog.Builder builder=new AlertDialog.Builder(CameraPlayXXActivity.this);
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
                //jumpToContentListActivity();
            }
        }

    }

    public void StopRealPlay() {
        try {
            //mWakeLock.release();
            IPlaySDK.PLAYStopSoundShare(m_nPort);
            IPlaySDK.PLAYStop(m_nPort);
            IPlaySDK.PLAYCloseStream(m_nPort);

            //IDpsdkCore. DPSDK_Logout(m_ReValue.nReturnValue, mTimeOut);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean StartRealPlay() {
        if (m_svPlayer == null)
            return false;

        Log.d("BBBBBBBBB开始", "BBBBB");
        Get_Dep_Info_t get_dep_info_t=new Get_Dep_Info_t(100,100);
        IDpsdkCore.DPSDK_GetDGroupInfo(m_ReValue.nReturnValue,get_dep_info_t);



        boolean bOpenRet = IPlaySDK.PLAYOpenStream(m_nPort, null, 0, 1500 * 1024) == 0 ? false : true;
        if (bOpenRet) {
            boolean bPlayRet = IPlaySDK.PLAYPlay(m_nPort, m_svPlayer) == 0 ? false : true;
            Log.i("StartRealPlay", "StartRealPlay1");
            if (bPlayRet) {
                boolean bSuccess = IPlaySDK.PLAYPlaySoundShare(m_nPort) == 0 ? false : true;
                Log.i("StartRealPlay", "StartRealPlay2");
                Log.i("StartRealPlay", "StartRealPlay2,是否成功:" + bSuccess);
                if (!bSuccess) {
                    IPlaySDK.PLAYStop(m_nPort);
                    IPlaySDK.PLAYCloseStream(m_nPort);
                    Log.i("StartRealPlay", "StartRealPlay3");
                    return false;
                }
            } else {
                IPlaySDK.PLAYCloseStream(m_nPort);
                Log.i("StartRealPlay", "StartRealPlay4");
                return false;
            }
        } else {
            Log.i("StartRealPlay", "StartRealPlay5");
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
    private boolean isState = false;//true activity销毁了 FALSE activity未销毁
    @Override
    protected void onDestroy() {
        isState=true;
        super.onDestroy();
    }

    private void displayVideo()
    {
        int ret = IDpsdkCore.DPSDK_CloseRealStreamBySeq(m_pDLLHandle, m_nSeq, mTimeOut);
        if(ret == 0){
            Log.e("xss","DPSDK_CloseRealStreamByCameraId success!");
            //Toast.makeText(getApplicationContext(), "Close video success!", Toast.LENGTH_SHORT).show();
        }else{
            Log.e("xss","DPSDK_CloseRealStreamByCameraId failed! ret = " + ret);
            //Toast.makeText(getApplicationContext(), "Close video failed!", Toast.LENGTH_SHORT).show();
        }

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

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                /*
                BACKGROUND=400 EMPTY=500 FOREGROUND=100
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                 */
                Log.i(context.getPackageName(), "此appimportace ="
                        + appProcess.importance
                        + ",context.getClass().getName()="
                        + context.getClass().getName());
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i(context.getPackageName(), "处于后台"
                            + appProcess.processName);
                    return true;
                } else {
                    Log.i(context.getPackageName(), "处于前台"
                            + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    // 线程类
    class ThreadShow implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                    System.out.println("send...");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("thread error...");
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
                System.out.println("receive....");
            }
        };
    };
//onConfigurationChanged事件并不是只有屏幕方向改变才可以触发，
// 其他的一些系统设置改变也可以触发，比如打开或者隐藏键盘。
   /* @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isScreenChange();
    }*/

    public boolean isScreenChange() {

        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation ; //获取屏幕方向
        /*DisplayMetrics metric  = new DisplayMetrics();
        float dy  = metric.density;*/
        if(ori == mConfiguration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.layout_camera_hplay);
            Display display = getWindowManager().getDefaultDisplay();
            int height = display.getHeight();
            int width = display.getWidth();
            m_svPlayer = (SurfaceView) findViewById(R.id.surfaceView);
            RelativeLayout.LayoutParams lay = (RelativeLayout.LayoutParams)m_svPlayer.getLayoutParams();
            lay.height=height;
            lay.width = width*2/3-50;
            //Toast.makeText(this, String.valueOf(width*2/3-100), Toast.LENGTH_SHORT).show();
            m_svPlayer.setLayoutParams(lay);
//横屏
        }else if(ori == mConfiguration.ORIENTATION_PORTRAIT){
//竖屏
            setContentView(R.layout.layout_camera_play);
            //获取手机屏幕大小
            Display display = getWindowManager().getDefaultDisplay();
            int height = display.getHeight();
            int width = display.getWidth();
            //视图，用于游戏画面，或者摄像头预览、视频播放
            m_svPlayer = (SurfaceView) findViewById(R.id.surfaceView);
            RelativeLayout.LayoutParams lay = ( RelativeLayout.LayoutParams)m_svPlayer.getLayoutParams();
            lay.height=height*1/2;
            lay.width = width;
            m_svPlayer.setLayoutParams(lay);
        }
        return false;
    }
    private class CameraPlayXXActivityListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                /*case R.id.SP_UP:
                    Ptz_Direct_Info_t Direct = new  Ptz_Direct_Info_t();
                    Direct.nDirect=1;
                    Direct.szCameraId  = m_channelId.getBytes();
                    Direct.bStop =false;
                    Direct.nStep =1;
                    int i = IDpsdkCore.DPSDK_PtzDirection(m_ReValue.nReturnValue,Direct,30000);
                    Log.e("warn",String.valueOf(i));
                    short j=1;
                    int o = IPlaySDK.PLAYSetPlayDirection(Integer.parseInt(m_serverPort.trim()),j);
                    Log.e("warn",String.valueOf(o));
                    break;*/
                case R.id.SP_button:finish();break;
            }
        }
    }

}
