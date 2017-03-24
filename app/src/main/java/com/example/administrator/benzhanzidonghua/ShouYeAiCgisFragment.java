package com.example.administrator.benzhanzidonghua;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ant.liao.GifView;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISLayerInfo;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.TextSymbol;
import com.esri.core.tasks.identify.IdentifyParameters;
import com.esri.core.tasks.identify.IdentifyResult;
import com.esri.core.tasks.identify.IdentifyTask;
import com.vanpeng.javabeen.BD_GJChankan;
import com.vanpeng.javabeen.BengZhanClass;
import com.vanpeng.javabeen.RequestWebService;
import com.vanpeng.javabeen.ShuiBengClass;
import com.vanpeng.javabeen.VideoMonitoring;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2016/12/19.
 * 首页fragment 显示地图
 */

public class ShouYeAiCgisFragment extends Fragment {
    private View view;//布局view
    private int mLoadIcoChiCun = 140;
    private MapView mMapView;//地图
    private RelativeLayout rlLoadView;////gifview的父布局
    private GifView gifLoadGis;//gifview显示地图
    private String mCurrentBengZhanDaiMa = "";
    private boolean mIsIdentfy = false;
    private ArcGISDynamicMapServiceLayer layer;
    //private  static ArcGISTiledMapServiceLayer layer;
    private String serviceURL = MyApplicationData.get_WebServicesURL();
    private ImageView iv_QuanTu;//点击全图
    private ImageView iv_search;//
    private   String [] [] arr;
    //Manifest.permission.WRITE_EXTERNAL_STORAGE sd卡权限
    private ArcGISLayerInfo layerInforZJ = null;
    OnMapClikListener onMapClikListener;
    public ShouYeAiCgisFragment() {
    }

    private ImageView iv_TuCeng;
    private ImageView iv_BiaoZhu;
    private PopupWindow popupWindow;
    private boolean isQYcheckbox = false;
    private boolean isHQcheckbox = false;
    private boolean isPermission = false;
    private int sum = 0;
    private Button selectTime;
    private int Start_year = 0;
    private int Start_monthOfYear = 0;
    private int Start_dayOfMonth = 0;
    private String ID;
    private String end_time = "";
    //private String[][] arr;
    private MyProgressDialog progressDialog = null;
    private ImageView QingKongGuiJi;
    private Envelope el;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflater = LayoutInflater.from(getActivity());
        view = inflater.inflate(R.layout.shouyeaicgisfragment_layout, null);
        //ZhuCeReceiver();
        init();
        return view;
    }

    private void init() {
        selectTime = (Button) view.findViewById(R.id.selectTime);
        selectTime.setOnClickListener(new allListener());
        iv_TuCeng = (ImageView) view.findViewById(R.id.iv_TuCeng);//图层
        iv_TuCeng.setOnClickListener(new allListener());
        iv_TuCeng.setOnTouchListener(new allListener());
        iv_BiaoZhu = (ImageView) view.findViewById(R.id.iv_BiaoZhu);//标注
        iv_BiaoZhu.setOnClickListener(new allListener());
        iv_BiaoZhu.setOnTouchListener(new allListener());
        iv_QuanTu = (ImageView) view.findViewById(R.id.iv_QuanTu);
        iv_QuanTu.setOnClickListener(new allListener());
        iv_QuanTu.setOnTouchListener(new allListener());
        iv_search = (ImageView) view.findViewById(R.id.iv_search);
        iv_search.setOnClickListener(new allListener());
        QingKongGuiJi = (ImageView) view.findViewById(R.id.QingKongGuiJi);
        QingKongGuiJi.setOnClickListener(new allListener());
        QingKongGuiJi.setOnTouchListener(new allListener());

        //ImageView all=(ImageView)view.findViewById(R.id.All);
        mMapView = (MapView) view.findViewById(R.id.mapView);//地图控件
        //直接将服务器端发布的地图服务（MapService）作为图层
        //all.setOnClickListener(new allListener());
        //图层
        el = new Envelope(41511137.09 - 10000,4617857.567 - 10000,41511137.09 + 20000,4617857.567 + 10000);//这里有4个坐标点，看似是一个矩形的4个顶点。
        mMapView.setExtent(el);

        layer = new ArcGISDynamicMapServiceLayer(Path.get_MapUrl());
        layer.refresh();//刷新地图
        //添加地图

        mMapView.addLayer(layer);

        //mMapView.zoomout();
        mMapView.setOnSingleTapListener(mOnSingleTapListener);//单击地图上的泵站
        mMapView.setOnStatusChangedListener(new mMapViewChangListener());

        Button btn_clean = (Button) view.findViewById(R.id.btn_clean);//清空雨量图层
        rlLoadView = (RelativeLayout) view.findViewById(R.id.layoutLoadGISView);//gifview的父布局
        gifLoadGis = (GifView) view.findViewById(R.id.gifLoadGis); ////加载的动画
        WindowManager wm = getActivity().getWindowManager();
        //gifview控件刚开始加载的背景
        gifLoadGis.setGifImage(R.drawable.load);//加载的动画
        //getLayoutParams()方法 和 setLayoutParams()方法 重新设置控件布局
        //设置gifview的margin值
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) gifLoadGis.getLayoutParams();
        lp.setMargins((wm.getDefaultDisplay().getWidth() - mLoadIcoChiCun) / 2, (wm.getDefaultDisplay().getHeight() - mLoadIcoChiCun) / 2 - 200, (wm.getDefaultDisplay().getWidth() - mLoadIcoChiCun) / 2, (wm.getDefaultDisplay().getHeight() - mLoadIcoChiCun) / 2);
        gifLoadGis.setLayoutParams(lp);
        rlLoadView.setVisibility(View.VISIBLE);
        gifLoadGis.showAnimation();//加载的动画
        //chechVersion();//检查版本

        //主activity向fragment传入参数的回调  主activity传时间过来 搜索轨迹
        ((MainActivity)getActivity()).setOnclikListener(new MainActivity.OnMyclikListener() {
            @Override
            public void callBack(String carnum, String start_hour, String end_hour) {
                Toast.makeText(getActivity(),carnum+":"+start_hour+":"+end_hour, Toast.LENGTH_SHORT).show();
                if(graphicsLayer!=null){
                    graphicsLayer.removeAll();
                }
                carNum = carnum;
                StartTime = start_hour;
                EndTime=end_hour;
                progressDialog = new MyProgressDialog(getActivity(), false, "加载中..");
                new Thread(networkTask).start();
            }
        });


    }
    private View rootView;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //获取activity根视图,rootView设为全局变量
        rootView=activity.getWindow().getDecorView();
        onMapClikListener=(OnMapClikListener)getActivity();

    }

    OnSingleTapListener mOnSingleTapListener = new OnSingleTapListener() {
        @Override
        public void onSingleTap(float x, float y) {
            iv_search.setImageDrawable(getResources().getDrawable(R.mipmap.dianji));
            if (mIsIdentfy) {
                mIsIdentfy = false;
                mCurrentBengZhanDaiMa = "";

                rlLoadView.setVisibility(View.VISIBLE);
                gifLoadGis.showAnimation();

                IdentifyParameters params;
                params = new IdentifyParameters();//识别任务所需参数对象
                params.setTolerance(80);//设置容差
                params.setDPI(98);//设置地图的DPI
                params.setLayers(new int[]{0});//设置要识别的图层数组
                params.setLayerMode(IdentifyParameters.ALL_LAYERS);//设置识别模式

                final long serialVersionUID = 1L;
                if (!mMapView.isLoaded()) {
                    return;
                }
                //establish the identify parameters
                Point identifyPoint = mMapView.toMapPoint(x, y);//toMapPoint()方法实现获取地图上的点坐标信息
                //Toast.makeText(GISActivity.this, "X坐标:" + identifyPoint.getX() + "，Y坐标:" + identifyPoint.getY(), Toast.LENGTH_SHORT).show();
                params.setGeometry(identifyPoint);//设置识别位置
                params.setSpatialReference(mMapView.getSpatialReference());//设置坐标系
                params.setMapHeight(mMapView.getHeight());//设置地图像素高
                params.setMapWidth(mMapView.getWidth());//设置地图像素宽
                Envelope env = new Envelope();
                mMapView.getExtent().queryEnvelope(env);
                params.setMapExtent(env);//设置当前地图范围
                MyIdentifyTask mTask = new MyIdentifyTask(identifyPoint);
                mTask.execute(params);
            }
        }
    };



    //点击地图上的泵站跳转到泵站详情页
    private class MyIdentifyTask extends AsyncTask<IdentifyParameters, Void, IdentifyResult[]> {

        Handler handlerMapLoaded = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                String val = data.getString("value");

                if (val.equals("999999")) {
                    rlLoadView.setVisibility(View.INVISIBLE);
                    gifLoadGis.showCover();
                    Toast.makeText(getActivity(), "地图查询错误", Toast.LENGTH_SHORT).show();
                }
            }
        };

        IdentifyTask mIdentifyTask;
        Point mAnchor;

        Handler handlerGetBengZhanList = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                String val = data.getString("value");

                if (val.toString().equals("999999")) {
                    rlLoadView.setVisibility(View.INVISIBLE);
                    gifLoadGis.showCover();
                    Toast.makeText(getActivity(), "获取泵站失败,网络或者服务器异常", Toast.LENGTH_SHORT).show();
                } else {
                    List<BengZhanClass> listBengZhan = new ArrayList<BengZhanClass>();
                    String[] objects = val.split("\\|");
                    Log.d("DEBUG", "获取泵站列表WebService结果_回调函数：" + val);
                    for (int i = 0; i < objects.length; i++) {
                        Log.d("DEBUG", "WebService结果_回调函数objects[i]：" + objects[i].toString());
                        if (objects[i].length() > 0) {
                            String[] values = objects[i].split(",");
                            Log.d("DEBUG", "WebService结果_回调函数values.length：" + values.length);
                            if (values.length > 1) {
                                BengZhanClass bengzhanObject = new BengZhanClass();
                                bengzhanObject.setID(values[0].toString());
                                bengzhanObject.setName(values[1].toString());
                                bengzhanObject.setBeiZhu(values[3].toString());
                                bengzhanObject.setYeWei(Double.parseDouble(values[5].toString()));
                                bengzhanObject.setAnQi(Double.parseDouble(values[13].toString()));
                                bengzhanObject.setJiaWan(Double.parseDouble(values[7].toString()));
                                bengzhanObject.setLiuHuaQin(Double.parseDouble(values[11].toString()));
                                bengzhanObject.setYiYangHuaTan(Double.parseDouble(values[9].toString()));
                                List<ShuiBengClass> shuiBengList = new ArrayList<ShuiBengClass>();
                                if (values.length > 16) {
                                    String[] shuibengValues = values[16].split("\\[-\\]");
                                    for (int j = 0; j < shuibengValues.length; j++) {
                                        ShuiBengClass shuibengObject = new ShuiBengClass();
                                        String[] shuibengPar = shuibengValues[j].split("\\<-\\>");
                                        for (int z = 0; z < shuibengPar.length; z++) {
                                            Log.d("DEBUG", "shuibengPar[" + z + "]：" + shuibengPar[z]);
                                        }
                                        Log.d("DEBUG", "shuibengPar长度：" + shuibengPar.length);

                                        if (shuibengPar.length > 3) {
                                            shuibengObject.setShuiBengName(shuibengPar[1].toString());
                                            shuibengObject.setId(shuibengPar[0].toString());
                                            shuibengObject.setBengZhanID(shuibengPar[2].toString());
                                            shuibengObject.setBeiZhu(shuibengPar[3].toString());
                                            shuibengObject.setSortX(shuibengPar[4].toString());
                                        }
                                        if (shuibengPar.length > 5) {
                                            shuibengObject.setDianLiu(shuibengPar[5].toString());
                                            shuibengObject.setZhuangTai(shuibengPar[6].toString());
                                        }
                                        shuiBengList.add(shuibengObject);
                                    }
                                }
                                bengzhanObject.setShuiBengClassList(shuiBengList);

                                if (values.length > 17) {
                                    List<VideoMonitoring> videoMonitoringList = new ArrayList<VideoMonitoring>();
                                    String[] videoMonitoringValues = values[17].split("\\[-\\]");
                                    for (int j = 0; j < videoMonitoringValues.length; j++) {
                                        VideoMonitoring videoMonitoring = new VideoMonitoring();
                                        String[] video = videoMonitoringValues[j].split("\\<-\\>");
                                        videoMonitoring.setId(video[0]);
                                        videoMonitoring.setName(video[1]);
                                        videoMonitoring.setBengZhanID(video[2]);
                                        videoMonitoring.setIp(video[3]);
                                        videoMonitoring.setPort(video[4]);
                                        videoMonitoring.setLoginName(video[5]);
                                        videoMonitoring.setLoginPWD(video[6]);
                                        videoMonitoring.setChannelID(video[7]);
                                        videoMonitoring.setSortX(video[8]);
                                        videoMonitoringList.add(videoMonitoring);
                                    }
                                    bengzhanObject.setVideoMonitoringList(videoMonitoringList);
                                }
                                listBengZhan.add(bengzhanObject);
                            }
                        }
                    }

                    BengZhanClass bengzhanTmp = null;
                    for (int i = 0; i < listBengZhan.size(); i++) {
                        if (listBengZhan.get(i).getBeiZhu().equals(mCurrentBengZhanDaiMa)) {
                            bengzhanTmp = listBengZhan.get(i);
                        }
                    }

                    if (bengzhanTmp == null) {
                        Toast.makeText(getActivity(), "查询无结果", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    rlLoadView.setVisibility(View.INVISIBLE);
                    gifLoadGis.showCover();
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), BengZhanXiangQingActivity.class);
                    intent.putExtra("BengZhanClass", bengzhanTmp);
                    startActivity(intent);
                }

            }
        };
        Runnable networkGetBengZhanInfor = new Runnable() {
            @Override
            public void run() {

                String methodName = "AppGetBengZhanList";
                ArrayList<String> parameterNameList = new ArrayList<>();
                ArrayList<String> parameterValueList = new ArrayList<>();

                try {
                    String result = RequestWebService.ServiceRequest("http://tempuri.org/", methodName, serviceURL, parameterNameList, parameterValueList);
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("value", result);
                    msg.setData(data);
                    handlerGetBengZhanList.sendMessage(msg);
                    Log.d("DEBUG", "获取泵站列表WebService结果：" + result.toString());
                } catch (Exception e) {
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("value", "999999");
                    msg.setData(data);
                    handlerGetBengZhanList.sendMessage(msg);
                    //Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
                }
            }
        };

        MyIdentifyTask(Point anchorPoint) {
            mAnchor = anchorPoint;
        }

        @Override
        protected IdentifyResult[] doInBackground(IdentifyParameters... params) {
            IdentifyResult[] mResult = null;
            if (params != null && params.length > 0) {
                IdentifyParameters mParams = params[0];
                try {
                    mResult = mIdentifyTask.execute(mParams);//执行识别任务
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("value", "999999");
                    msg.setData(data);
                    handlerMapLoaded.sendMessage(msg);
                    e.printStackTrace();
                }
            }
            return mResult;
        }

        @Override
        protected void onPostExecute(IdentifyResult[] identifyResults) {
            if (identifyResults == null) {
                Toast.makeText(getActivity(), "查询结果为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (identifyResults.length == 0) {
                Toast.makeText(getActivity(), "点击查询结果为空。", Toast.LENGTH_SHORT).show();
            }


            for (int i = 0; i < identifyResults.length; i++) {
                Log.e("GISActivity地图服务", "图层名称：" + identifyResults[i].getLayerName() + "");
                Log.e("GISActivity地图服务", "图层名称：" + identifyResults[i].getAttributes().get("代码") + "");
                mCurrentBengZhanDaiMa = identifyResults[i].getAttributes().get("代码").toString();
                System.out.println("QWSA" + identifyResults.length);
            }

            //Toast.makeText(GISActivity.this,"查询到"+identifyResults.length+"个结果",Toast.LENGTH_SHORT).show();
            if (identifyResults.length > 0) {
                new Thread(networkGetBengZhanInfor).start();
            } else {
                rlLoadView.setVisibility(View.INVISIBLE);
                gifLoadGis.showCover();
                Toast.makeText(getActivity(), "请选择泵站", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onPreExecute() {
            mIdentifyTask = new IdentifyTask(Path.get_MapUrl());//
        }
    }

    private class mMapViewChangListener implements OnStatusChangedListener {//OnStatusChangedListener接口用于监听MapView或Layer（图层）状态变化的监听器

        @Override
        public void onStatusChanged(Object o, STATUS status) {
            if (status == STATUS.INITIALIZED) {
            } else if (status == STATUS.LAYER_LOADED) {
                if (layer != null) {
                    if (layer.getLayers() != null) {

                        layerInforZJ = layer.getLayers()[1];
                        for (int i = 0; i < layer.getLayers().length; i++) {
                            ArcGISLayerInfo layerInfor = layer.getLayers()[i];
                            if (layerInfor.getName().equals(Path.get_BengZhanZhuJi())) {
                                layerInfor.setVisible(true);
                            }
                            Log.e("GISActivity地图服务加载", "图层名称：" + layerInfor.getName() + "");
                        }
                    }
                }
                rlLoadView.setVisibility(View.INVISIBLE);
                //gifLoadGis.showCover();
                //Toast.makeText(GISActivity.this, "地图加载成功", Toast.LENGTH_SHORT).show();
            } else if (status == STATUS.INITIALIZATION_FAILED) {
                Toast.makeText(getActivity(), "地图加载失败", Toast.LENGTH_SHORT).show();
                rlLoadView.setVisibility(View.INVISIBLE);
                gifLoadGis.showCover();
            } else if (status == STATUS.LAYER_LOADING_FAILED) {
                Toast.makeText(getActivity(), "图层加载失败", Toast.LENGTH_SHORT).show();
                rlLoadView.setVisibility(View.INVISIBLE);
                gifLoadGis.showCover();
            }
        }
    }

    boolean isBengZhanZhuJi = true;
    boolean isYQ = true;
    boolean isHQ = true;

    private class allListener implements View.OnClickListener, View.OnTouchListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_search:
                    iv_search.setImageDrawable(getResources().getDrawable(R.mipmap.dianjiclick));
                    if (layer != null) {
                        mIsIdentfy = true;
                    }
                    break;
                case R.id.iv_QuanTu:
                    break;
                case R.id.iv_TuCeng:
                    break;
                case R.id.iv_BiaoZhu:
                    break;
                case R.id.selectTime:
                    DialogTime();
                    break;
                case R.id.QingKongGuiJi:
                    break;//清空轨迹

            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch(v.getId()) {
                case R.id.iv_QuanTu:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下
                        iv_QuanTu.setAlpha(10);
                        if (layer != null) {
                            mMapView.setExtent(el);//设置地图显示范围
                            //mMapView.setExtent(layer.getFullExtent());
                        }
                 }
                    if (event.getAction() == MotionEvent.ACTION_UP) {//松开
                        iv_QuanTu.setAlpha(255);
                    }
                    break;
                case R.id.iv_TuCeng: if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下
                                            iv_TuCeng.setAlpha(10);
                                            MeauPopWindows();
                                    }
                                    if (event.getAction() == MotionEvent.ACTION_UP) {//松开
                                        iv_TuCeng.setAlpha(255);
                                    }
                                    break;
                case R.id.iv_BiaoZhu:
                                if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下
                                        iv_BiaoZhu.setAlpha(10);
                                    layer.getLayers()[1].setVisible(isBengZhanZhuJi);
                                    isBengZhanZhuJi = !isBengZhanZhuJi;
                                    layer.refresh();
                                    if (!isBengZhanZhuJi) {
                                        iv_BiaoZhu.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.benzhangbiaozhuclick));
                                    } else {
                                        iv_BiaoZhu.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.benzhangbiaozhu));
                                    }
                                    }
                                if (event.getAction() == MotionEvent.ACTION_UP) {//松开
                                        iv_BiaoZhu.setAlpha(255);
                                    }
                                break;
                case R.id.QingKongGuiJi:
                                        if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下
                                            QingKongGuiJi.setAlpha(10);
                                            if(graphicsLayer!=null) {
                                                graphicsLayer.removeAll();
                                            }
                                                /*rootView.findViewById(R.id.BD_IV_GJsearch).setVisibility(View.GONE);
                                                rootView.findViewById(R.id.BD_GJsearch).setEnabled(false);*/
                                                onMapClikListener.onclik("Qing");
                                                selectTime.setVisibility(View.GONE);

                                        }
                                        if (event.getAction() == MotionEvent.ACTION_UP) {//松开
                                            QingKongGuiJi.setAlpha(255);
                                        }
                                            break;
            }
            return true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.unpause();
    }

    //检查更新
    private void updata() {
        UpdateManager um = new UpdateManager(getActivity());
        um.checkUpdate();
    }

    private View addview;


    private void MeauPopWindows() {
        //closePopwindow();
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        //title.getHeight();
        addview = LayoutInflater.from(getActivity()).inflate(R.layout.map_poplistview_layout, null);
        addinit(addview);
        popupWindow = new PopupWindow(addview,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);//2,3参数为宽高

        //改变屏幕透明度
        /*WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
        params.alpha = 0.5f;
        getActivity().getWindow().setAttributes(params);*/

        popupWindow.setWidth(width / 2);
        popupWindow.setAnimationStyle(R.style.popwindow_anim_style); // 设置动画
        popupWindow.setTouchable(true);//popupWindow可触摸
        popupWindow.setOutsideTouchable(true);
        //点击其他地方消失
        /*addview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                closePopwindow();
                return false;
            }
        });*/
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                Log.i("mengdd", "onTouch : ");
                //closePopwindow();
                return false;
            }
        });
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.color.white));
        //popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置好参数之后再show
        popupWindow.showAsDropDown(iv_BiaoZhu, width, 0);//在iv_TuCeng控件正下方，以iv_TuCeng为参照点第二个参数为popwindow距离view的横向距离，
        //第三个参数为y轴即popwindow距离view的纵向距离
    }

    /* private void closePopwindow() {
         if (popupWindow != null && popupWindow.isShowing()) {
             popupWindow.dismiss();
             popupWindow = null;
             WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
             params.alpha = 1f;
             getActivity().getWindow().setAttributes(params);
         }
     }*/
    private void addinit(View view) {
        //影像
        CheckBox YX_cb = (CheckBox) view.findViewById(R.id.YX_cb);//影像图复选框
        YX_cb.setClickable(false);
        ImageView YL_pop_iv = (ImageView) view.findViewById(R.id.YL_pop_iv);//影像图图片
        TextView YL_pop_tv = (TextView) view.findViewById(R.id.YL_pop_tv);//影像图内容
        //园区范围
        CheckBox YQ_cb = (CheckBox) view.findViewById(R.id.YQ_cb);//园区范围复选框
        YQ_cb.setOnCheckedChangeListener(new YQ_cbListener());
        ImageView YQ_pop_iv = (ImageView) view.findViewById(R.id.YQ_pop_iv);//园区范围图片
        TextView YQ_pop_tv = (TextView) view.findViewById(R.id.YQ_pop_tv);//园区范围内容
        if (isQYcheckbox) {
            sum = 1;
            YQ_cb.setChecked(true);//设置为选中 相当于又点击一次监听事件
            YQ_pop_iv.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.yingxiangtuclick));
            YQ_pop_tv.setTextColor(getActivity().getResources().getColor(R.color.blue));
        } else {
            YQ_pop_iv.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.yingxiangtu));
        }
        //企业红线
        CheckBox QH_cb = (CheckBox) view.findViewById(R.id.QH_cb);//企业红线复选框
        QH_cb.setOnCheckedChangeListener(new QH_cbListener());
        ImageView QH_pop_iv = (ImageView) view.findViewById(R.id.QH_pop_iv);//企业红线图片
        TextView QH_pop_tv = (TextView) view.findViewById(R.id.QH_pop_tv);//企业红线内容
        if (isHQcheckbox) {
            sum = 1;
            QH_cb.setChecked(true);//设置为选中 相当于又点击一次监听事件
            QH_pop_iv.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.qiyehongxianclick));
            QH_pop_tv.setTextColor(getActivity().getResources().getColor(R.color.blue));
        } else {
            QH_pop_iv.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.qiyehongxian));
        }
        //二维图
        CheckBox ET_cb = (CheckBox) view.findViewById(R.id.ET_cb);//二维图复选框
        ET_cb.setClickable(false);
        ImageView ET_pop_iv = (ImageView) view.findViewById(R.id.ET_pop_iv);//二维图图片
        TextView ET_pop_tv = (TextView) view.findViewById(R.id.ET_pop_tv);//二维图内容
        LinearLayout ET = (LinearLayout) view.findViewById(R.id.ET);
        ET.setOnClickListener(new allListener());

        CheckBox Water_cb = (CheckBox) view.findViewById(R.id.Water_cb);//雨水管线复选框
        Water_cb.setClickable(false);
        ImageView Water_pop_iv = (ImageView) view.findViewById(R.id.Water_pop_iv);//雨水管线图片
        TextView Water_pop_tv = (TextView) view.findViewById(R.id.Water_pop_tv);//雨水管线内容
        LinearLayout Water = (LinearLayout) view.findViewById(R.id.Water);
        Water.setOnClickListener(new allListener());

        CheckBox wWater_cb = (CheckBox) view.findViewById(R.id.wWater_cb);//污水管线复选框
        wWater_cb.setClickable(false);
        ImageView wWater_pop_iv = (ImageView) view.findViewById(R.id.wWater_pop_iv);//污水管线图片
        TextView wWater_pop_tv = (TextView) view.findViewById(R.id.wWater_pop_tv);//污水管线内容
        LinearLayout wWater = (LinearLayout) view.findViewById(R.id.wWater);
        wWater.setOnClickListener(new allListener());
    }

    //影像图
    private class YX_cbListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                Toast.makeText(getActivity(), "该功能尚未开启", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //园区范围
    private class YQ_cbListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (sum == 1) {
                sum = 0;
                return;
            }
            layer.getLayers()[20].setVisible(isYQ);
            isYQ = !isYQ;
            layer.refresh();//刷新地图
            isQYcheckbox = b;
            ImageView YQ_pop_iv = (ImageView) addview.findViewById(R.id.YQ_pop_iv);//园区范围图片
            TextView YQ_pop_tv = (TextView) addview.findViewById(R.id.YQ_pop_tv);//园区范围内容
            if (b == false) {
                YQ_pop_iv.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.yingxiangtu));
                YQ_pop_tv.setTextColor(getActivity().getResources().getColor(R.color.huise));
            } else {
                YQ_pop_iv.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.yingxiangtuclick));
                YQ_pop_tv.setTextColor(getActivity().getResources().getColor(R.color.blue));
            }
        }
    }

    //企业红线
    private boolean isHQ1 = true;

    private class QH_cbListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (sum == 1) {
                sum = 0;
                return;
            }
            layer.getLayers()[4].setVisible(isHQ);//true显示企业红线
            layer.getLayers()[5].setVisible(isHQ1);
            isHQ = !isHQ;
            isHQ1 = !isHQ1;
            layer.refresh();//刷新地图
            isHQcheckbox = b;
            ImageView QH_pop_iv = (ImageView) addview.findViewById(R.id.QH_pop_iv);//企业红线图片
            TextView QH_pop_tv = (TextView) addview.findViewById(R.id.QH_pop_tv);//企业红线内容
            if (b == false) {
                QH_pop_iv.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.qiyehongxian));
                QH_pop_tv.setTextColor(getActivity().getResources().getColor(R.color.huise));
            } else {
                QH_pop_iv.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.qiyehongxianclick));
                QH_pop_tv.setTextColor(getActivity().getResources().getColor(R.color.blue));
            }
        }
    }

    //二维图
    private class ET_cbListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                Toast.makeText(getActivity(), "该功能尚未开启", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //雨水管线
    private class Water_cbListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                Toast.makeText(getActivity(), "该功能尚未开启", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //污水管线
    private class wWater_cbListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                Toast.makeText(getActivity(), "该功能尚未开启", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //检查版本申请权限
    //在Fragment中申请权限，不要使用ActivityCompat.requestPermissions,
    // 直接使用Fragment的requestPermissions方法，否则会回调到Activity的 onRequestPermissionsResult
    private void chechVersion() {
        int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0;
        if (Build.VERSION.SDK_INT >= 23) {
            //检查手机是否有该权限
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {//当没有该权限时
                //弹出对话框申请该权限   数组里装的是要申请的权限 id = 0 索引0 申请权限在数组中的位置 返回的数组结果也在数组索引0中
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                return;
            } else {
                updata();
            }
        } else {
            updata();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //返回的数组 结果的位置就是申请该权限 申请的权限位置 即索引0
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//已授权
            Toast.makeText(getActivity(), "授权成功", Toast.LENGTH_SHORT).show();
            updata();
        } else {
            Toast.makeText(getActivity(), "您拒绝了该权限，可能会导致应用内部出现问题，请尽快授权", Toast.LENGTH_SHORT).show();
        }
    }

    private String start_hour;
    private String end_hour;
    private String carnum;
    /*public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.neter.broadcast.receiver.SendDownXMLBroadCast")) {
                //ShouYeLayoutMethod();
                ShiZhiTime();
                selectTime.setVisibility(View.VISIBLE);
               // String N_NUM = intent.getStringExtra("N_NUM");
                carnum = intent.getStringExtra("CARNUM");
                start_hour = intent.getStringExtra("start_hour");
                end_hour = intent.getStringExtra("end_hour");
                Log.e("warn","start_hour");
                Toast.makeText(getContext(),carnum+start_hour+end_hour,Toast.LENGTH_SHORT).show();
                Message msg = Message.obtain();
                msg.obj=carnum;
                //msg.obj = N_NUM;
                sendHandler.sendMessage(msg);//发送数据
            }
        }
    }*/

    private void ShiZhiTime() {
        Calendar c = Calendar.getInstance();
        int Now_year = c.get(Calendar.YEAR);
        int Now_monthOfYear = c.get(Calendar.MONTH);
        int Now_dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        Start_year = Now_year;
        Start_monthOfYear = Now_monthOfYear+1;
        Start_dayOfMonth = Now_dayOfMonth;
    }

    private MyProgressDialog progressDialog1 = null;

    private void DialogTime() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.baojingtime_layout, null);
        builder.setView(view);
        DatePicker dp = (DatePicker) view.findViewById(R.id.dp);
        dp.init(Start_year, Start_monthOfYear-1, Start_dayOfMonth, new DatePicker.OnDateChangedListener() {
            //改变后的时间 时间改变后才执行这个方法
            public void onDateChanged(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                Start_year = year;
                Start_monthOfYear = monthOfYear + 1;
                Start_dayOfMonth = dayOfMonth;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //arr = null;
                if(graphicsLayer!=null){
                    graphicsLayer.removeAll();
                }
                progressDialog1 = new MyProgressDialog(getActivity(), false, "加载中..");
                end_time = ZhuanHuanTime();
                new Thread(networkTask).start();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    //注册广播
   /* private void ZhuCeReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.neter.broadcast.receiver.SendDownXMLBroadCast");
        mReceiver = new MyReceiver();
        getActivity().registerReceiver(mReceiver, filter);
    }

    private MyReceiver mReceiver;*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        //getActivity().unregisterReceiver(mReceiver);//必须注销广播，否则有内存泄漏的风险！！！
    }

    //接受发送过来的数据ID（n_num），广播中不能进行耗时的操作，发送到主线程，在主线程中开一个子线程
    private Handler sendHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ID = (String) msg.obj;
            end_time = ZhuanHuanTime();//转换时间 计算加一天的结束时间
            if (end_time.equals("999999")) {
                Toast.makeText(getActivity(), "获取时间失败", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog = new MyProgressDialog(getActivity(), false, "加载中..");
                progressDialog.setCanceledOnTouchOutside(false);//设置点击屏幕不消失
                //轨迹图层
                if(graphicsLayer!=null){
                    graphicsLayer.removeAll();
                }
                new Thread(networkTask).start();
            }
        }
    };
    private String carNum;
    private String StartTime;
    private String EndTime;
    //获取指定ID(N_NUM)北斗轨迹坐标
    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            try {
                Log.e("warn", "30");
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Get_XYInfo_List";
                // EndPoint
                String endPoint = "http://beidoujieshou.sytxmap.com:5963/GPSService.asmx";
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_XYInfo_List";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数CarNum
                rpc.addProperty("carNum", carNum);
                rpc.addProperty("StartTime", StartTime);
                rpc.addProperty("EndTime", EndTime);
                // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);

                AndroidHttpTransport ht = new AndroidHttpTransport(endPoint);
                ht.debug = true;
                Log.e("warn", "50");
                try {
                    // 调用WebService
                    ht.call(soapAction, envelope);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                SoapObject object;
                // 开始调用远程方法
                Log.e("warn", "60");
                object = (SoapObject) envelope.getResponse();
                int count = object.getPropertyCount();
                Log.e("warn", "64");
                // 得到服务器传回的数据
                int count1 = object.getPropertyCount();
                Log.e("warn", String.valueOf(count));
                if (count1 == 0) {//无数据时
                    Message msg = Message.obtain();
                    msg.obj = "0";
                    HuaGuiJi.sendMessage(msg);
                    return;
                }
                StringBuffer sb = new StringBuffer();
                //Toast.makeText(MainActivity.this,String.valueOf(count), Toast.LENGTH_SHORT).show();
                if (count1 > 0) {
                    for (int i = 0; i < count1; i++) {
                        SoapObject soapProvince = (SoapObject) object.getProperty(i);
                        //Log.e("warn", "-----------------------------");
                        //arr[i][0]=soapProvince.getProperty("I_NUM").toString();
                        //Log.e("warn", soapProvince.getProperty("I_NUM").toString() + ":");
                        /*String x = soapProvince.getProperty("X").toString();
                        String y = soapProvince.getProperty("Y").toString();
                        markLocation(x,y);*/
                        sb.append(soapProvince.getProperty("CARNUM").toString() + ",");

                        //arr[i][1]=soapProvince.getProperty("I_WD").toString();
                        //Log.e("warn", soapProvince.getProperty("I_WD").toString() + ":");
                        Log.e("warn", soapProvince.getProperty("X").toString() + ":");
                        sb.append(soapProvince.getProperty("X").toString() + ",");

                        //arr[i][2]=soapProvince.getProperty("I_WDFH").toString();
                        //Log.e("warn", soapProvince.getProperty("I_WDFH").toString() + ":");
                        Log.e("warn", soapProvince.getProperty("Y").toString() + ":");
                        sb.append(soapProvince.getProperty("Y").toString() + ",");

                        if (i != count1) {
                            sb.append(soapProvince.getProperty("TIME").toString() + "|");
                        } else {
                            sb.append(soapProvince.getProperty("TIME").toString());
                        }
                    }
                    Message msg = Message.obtain();
                    msg.obj = sb.toString();
                    HuaGuiJi.sendMessage(msg);
                }
            } catch (Exception e) {
                Message msg = Message.obtain();
                msg.obj = "1";
                HuaGuiJi.sendMessage(msg);
            }
        }
    };

    //时间转换 将时间转为毫秒 再加一天的毫秒数 再转换为年月日，
    private String ZhuanHuanTime() {
        String month = String.valueOf(Start_monthOfYear);
        String day = String.valueOf(Start_dayOfMonth);
        String year = String.valueOf(Start_year);
        if (month.length() != 2) {
            month = "0" + month;
        }
        if (day.length() != 2) {
            day = "0" + day;
        }
        String startTime = year + month + day;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//时间格式yyyyMMdd
        String EndTime = "";
        try {
            long millionSeconds = sdf.parse(startTime).getTime();//将年月日转为毫秒
            long endTime = millionSeconds + 24 * 60 * 60 * 1000;//计算再加一天的毫秒数
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");//将毫秒转为年月日
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(endTime);//设置这个毫秒为1970年1月1日0时起的毫秒数
            EndTime = format.format(calendar.getTime());//将毫秒转为指定格式的年月日
        } catch (ParseException e) {
            return "999999";
        }
        return EndTime;
    }
    int a=0;
    private Handler HuaGuiJi = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = (String) msg.obj;
            if (result.equals("0")) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                } else {
                    progressDialog1.dismiss();
                    progressDialog1 = null;
                }
                Toast.makeText(getActivity(), "暂无轨迹数据", Toast.LENGTH_SHORT).show();
            } else if (result.equals("1")) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                } else {
                    progressDialog1.dismiss();
                    progressDialog1 = null;
                }
                Toast.makeText(getActivity(), "网络或服务器异常", Toast.LENGTH_SHORT).show();
            } else {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                } else {
                    progressDialog1.dismiss();
                    progressDialog1 = null;

                }
                arr = JieQuMethod(result);
                Log.e("warn", arr[0][0] + "|");
                if(graphicsLayer==null){
                    graphicsLayer = new GraphicsLayer();
                    mMapView.addLayer(graphicsLayer);
                }
                graphicsLayer.setVisible(true);
                markLocation(arr);
            }
        }
    };

    private String[][] JieQuMethod(String result) {
        String[] arr = result.split("\\|");
        String[][] arr1 = new String[arr.length][4];
        for (int i = 0; i < arr.length; i++) {
            String[] arr2 = arr[i].split(",");
            arr1[i][0] = arr2[0];
            arr1[i][1] = arr2[1];
            arr1[i][2] = arr2[2];
            arr1[i][3] = arr2[3];
        }
        return arr1;
    }
    //根据坐标点画轨迹
    private Graphic graphic2;
    private GraphicsLayer graphicsLayer;
    //private SimpleLineSymbol red=new SimpleLineSymbol(Color.RED, 2);
    private void markLocation(String arr[][]) {
        /*graphicsLayer = new GraphicsLayer();
        mMapView.addLayer(graphicsLayer);
        graphicsLayer.removeAll();*/

        //DrawGraphicTouchListener drawgraphictouchlistener=new DrawGraphicTouchListener(getActivity(),mMapView);
        Point wgspoint;
        Point mapPoint;
        Point startPoint = null;
        Point ptPrevious = null;
        Point wgspoint1;
        //此变量是用来保存线或者面的轨迹数据的
        Polyline poly = null;
        Polyline poly2 = null;
        android.graphics.Path linePath=null;
        for (int i = 0; i <arr.length; i++) {

            float locx =Float.valueOf(arr[i][1]);//获取坐标点
            float locy =Float.valueOf(arr[i][2]);

            Log.e("warn",String.valueOf(i));
            wgspoint = new Point(locx, locy);
           // wgspoint1 = new Point(locx1, locy1);
            mapPoint = (Point) GeometryEngine.project(wgspoint, SpatialReference.create(4326), mMapView.getSpatialReference());//当前
            //ptPrevious = (Point) GeometryEngine.project(wgspoint1, SpatialReference.create(4326), mMapView.getSpatialReference());//当前
            //mapPoint = mMapView.toMapPoint(locx,locy);
            //图层的创建
            //绘制点的样式 圆形
            //
            if(i==0) {//起点圆点图层 添加到graphicsLayer图层里
                Graphic graphicPoint = new Graphic(wgspoint, new SimpleMarkerSymbol(Color.GREEN, 15, SimpleMarkerSymbol.STYLE.CIRCLE));
                graphicsLayer.addGraphic(graphicPoint);
                //文字标注
                TextSymbol ts = new TextSymbol(10,arr[i][3],Color.BLUE);
                ts.setFontFamily("DroidSansFallback.ttf");//设置字体
                ts.setOffsetX(0);//偏移量
                ts.setOffsetY(-30);
                Graphic gText = new Graphic(wgspoint,ts);//创建图层 参数坐标 和文字标注对象
                graphicsLayer.addGraphic(gText);

            }else if(i==arr.length-1){//终点圆点图层 添加到graphicsLayer图层里
                Graphic graphicPoint = new Graphic(wgspoint, new SimpleMarkerSymbol(Color.BLACK, 15, SimpleMarkerSymbol.STYLE.CIRCLE));
                graphicsLayer.addGraphic(graphicPoint);
               /* poly1.lineTo(wgspoint);
                Graphic graphic =new Graphic(poly1, new SimpleLineSymbol(Color.RED, 2,SimpleLineSymbol.STYLE.SOLID));
                graphicsLayer.addGraphic(graphic);*/
                TextSymbol ts = new TextSymbol(10,arr[i][3],Color.BLUE);
                ts.setFontFamily("DroidSansFallback.ttf");
                ts.setOffsetX(0);//偏移量
                ts.setOffsetY(-30);
                Graphic gText = new Graphic(wgspoint,ts);//创建图层 参数坐标 和文字标注对象
                graphicsLayer.addGraphic(gText);
            }
            //画线
          if (startPoint == null) {
                poly = new Polyline();
                startPoint = mapPoint;
                poly.startPath(wgspoint);
                /*poly2 = new Polyline();
                poly2.startPath(wgspoint);
*/
          }
              /*if(i%30==0){
                  poly2.lineTo(wgspoint);
              }*/
              poly.lineTo(wgspoint);
        }
        //线图层 添加到graphicsLayer图层里
        //mMapView.addView(new MyCanvas(getActivity()));
        Graphic graphic =new Graphic(poly, new SimpleLineSymbol(Color.RED,2,SimpleLineSymbol.STYLE.SOLID));
        //Graphic graphic1 =new Graphic(poly2, new SimpleMarkerSymbol(Color.WHITE,5, SimpleMarkerSymbol.STYLE.TRIANGLE));
        graphicsLayer.addGraphic(graphic);
        //graphicsLayer.addGraphic(graphic1);
        //int k=arr.length/200;
       /*for(int j=1;j<=200;j++){
            if(j*k>arr.length-1||j*k+1>arr.length-1){break;}
                drawAL(Float.valueOf(arr[j*k][8]),Float.valueOf(arr[j*k][9]),Float.valueOf(arr[j*k+1][8]),Float.valueOf(arr[j*k+1][9]));
                if((x_3+"").equals("NaN")||(y_3+"").equals("NaN")||(x_4+"").equals("NaN")||(y_4+"").equals("NaN")){
                    //求不出相应坐标
                    continue;
                }else{
                    //Point point = new Point((float)x_3,(float)y_3);
                    Point point1 = new Point(Float.valueOf(arr[j*k][8]),Float.valueOf(arr[j*k][9]));
                    //Point point2 = new Point((float)x_4,(float)y_4);
                    //Polyline poly_1 = new Polyline();
                    //poly_1.startPath(point);
                    //poly_1.lineTo(point1);
                    //poly_1.lineTo(point2);
                    //Color color =new Color();

                    Graphic graphic2 =new Graphic(point1,new SimpleMarkerSymbol(Color.argb(255-j,0,0,255), 8, SimpleMarkerSymbol.STYLE.CIRCLE));
                    graphicsLayer.addGraphic(graphic2);
                    *//* Graphic graphic3 =new Graphic(point1,new SimpleMarkerSymbol(Color.GREEN, 10, SimpleMarkerSymbol.STYLE.CIRCLE));
                    graphicsLayer.addGraphic(graphic3);
                    Graphic graphic4 =new Graphic(point2,new SimpleMarkerSymbol(Color.GREEN, 10, SimpleMarkerSymbol.STYLE.CIRCLE));
                    graphicsLayer.addGraphic(graphic4);*//*
                }
        }*/
      /*for(int j=1;j<=200;j++){
            if(j*k>arr.length-1||j*k+1>arr.length-1){break;}
            drawAL(Float.valueOf(arr[j*k][8]),Float.valueOf(arr[j*k][9]),Float.valueOf(arr[j*k+1][8]),Float.valueOf(arr[j*k+1][9]));
            if((x_3+"").equals("NaN")||(y_3+"").equals("NaN")||(x_4+"").equals("NaN")||(y_4+"").equals("NaN")){
                //求不出相应坐标
                continue;
            }else{
                Point point = new Point((float)x_3,(float)y_3);
                Point point1 = new Point(Float.valueOf(arr[j*k+1][8]),Float.valueOf(arr[j*k+1][9]));
                Point point2 = new Point((float)x_4,(float)y_4);
                Polyline poly_1 = new Polyline();
                poly_1.startPath(point);
                poly_1.lineTo(point1);
                poly_1.lineTo(point2);
                Graphic graphic2 =new Graphic(poly_1,new SimpleLineSymbol(Color.RED,5,SimpleLineSymbol.STYLE.SOLID));
                graphicsLayer.addGraphic(graphic2);
                *//* Graphic graphic3 =new Graphic(point1,new SimpleMarkerSymbol(Color.GREEN, 10, SimpleMarkerSymbol.STYLE.CIRCLE));
                   graphicsLayer.addGraphic(graphic3);
                   Graphic graphic4 =new Graphic(point2,new SimpleMarkerSymbol(Color.GREEN, 10, SimpleMarkerSymbol.STYLE.CIRCLE));
                   graphicsLayer.addGraphic(graphic4);*//*
            }
        }*/
    }


    double x_3 = 0; // (x3,y3)是第一端点
    double y_3 = 0;
    double x_4 = 0; // (x4,y4)是第二端点
    double y_4 = 0;
    public void drawAL(float sx, float sy, float ex, float ey)
    {
        double H = 8; // 箭头高度
        double L = 3.5; // 底边的一半
       /* double H = 6; // 箭头高度
        double L = 2.5; // 底边的一半*/
        /*double H = 10; // 箭头高度
        double L = 7; // 底边的一半*/
        /*int x3 = 0;
        int y3 = 0;
        int x4 = 0;
        int y4 = 0;*/
        double awrad = Math.atan(L / H); // 箭头角度
        double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度
        double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
        double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
         x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点
         y_3 = ey - arrXY_1[1];
        Log.e("warn",x_3+":"+y_3+"");
        x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点
        y_4 = ey - arrXY_2[1];
        Log.e("warn",x_4+":"+y_4+"");
        Double X3 = new Double(x_3);
        int x3 = X3.intValue();
        Double Y3 = new Double(y_3);
        int y3 = Y3.intValue();
        Double X4 = new Double(x_4);
        int x4 = X4.intValue();
        Double Y4 = new Double(y_4);
        int y4 = Y4.intValue();
        // 画线
      /*  myCanvas.drawLine(sx, sy, ex, ey,myPaint);
        // 画箭头s
        android.graphics.Path triangle = new android.graphics.Path();
        triangle.moveTo(ex, ey);
        triangle.lineTo(x3, y3);
        triangle.lineTo(x4, y4);
        triangle.close();
        myCanvas.drawPath(triangle,myPaint);*/
    }
    // 计算
    public double[] rotateVec(float px, float py, double ang, boolean isChLen, double newLen)
    {
        double mathstr[] = new double[2];
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度
        double vx = px * Math.cos(ang) - py * Math.sin(ang);
        double vy = px * Math.sin(ang) + py * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
            mathstr[0] = vx;
            mathstr[1] = vy;
        }
        return mathstr;
    }
    interface OnMapClikListener {
        public void onclik(String isSelect);
    }
}