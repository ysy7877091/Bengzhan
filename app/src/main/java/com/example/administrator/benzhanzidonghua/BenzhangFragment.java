package com.example.administrator.benzhanzidonghua;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.com.vanpeng.Adapter.BZ_Pop_Listview;
import com.com.vanpeng.Adapter.BenZhanFragmentAdapter;
import com.vanpeng.javabeen.BengZhanClass;
import com.vanpeng.javabeen.ShuiBengClass;
import com.vanpeng.javabeen.VideoMonitoring;

import java.util.ArrayList;
import java.util.List;

import static com.example.administrator.benzhanzidonghua.R.id.JiShuiDian;

/**
 * Created by Administrator on 2016/12/19.
 */

public class BenzhangFragment extends Fragment{
    private View view;
    private ListView bListView;
    private LinearLayout zuo_layout;
    private LinearLayout center;
    private List<BengZhanClass> listBengZhan;
    private MyProgressDialog progressDialog;
    private Button BZ_pop;
    private PopupWindow popupWindow;
    private TextView bz_text;
    private List<BengZhanClass> list = new ArrayList<BengZhanClass>();//存储 用于刷新
    private List<BengZhanClass> list1 = new ArrayList<BengZhanClass>();//存储 给popwindow
    private BenZhanFragmentAdapter bzf;
    //private BengZhanClass bengzhanObject;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflater=LayoutInflater.from(getActivity());
        view = inflater.inflate(R.layout.benzhan_layout,container,false);
        init();
        return view;
    }
    private void init(){
        bz_text = (TextView)view.findViewById(R.id.bz_text);
        bListView=(ListView)view.findViewById(R.id.bListView);
        bListView.setOnItemClickListener(new ListViewListener());
        BZ_pop =(Button)view.findViewById(R.id.BZ_pop);
        BZ_pop.setOnClickListener(new POP_Listener());
        BengZhanClass bengzhanObject = new BengZhanClass();
        bengzhanObject.setName("全部");
        list1.add(bengzhanObject);
        progressDialog = new MyProgressDialog(getActivity(),false,"加载中...");
        new Thread(networkGetBengZhanInfor).start();
    }

    Runnable networkGetBengZhanInfor = new Runnable() {
        @Override
        public void run() {
            String path = Path.get_WebServicesURL();//webservice url
            String methodName = "AppGetBengZhanListResult";//返回的方法名称
            String SoapFileName = "assets/appgetbenzhanlist.xml";//soap协议文件名称
            String soap = CommonMethod.ReadSoap(SoapFileName);//读取soap协议
            try {
                Log.e("warn",soap);
                byte [] date=soap.getBytes();//soap协议转为字符数组
                String result=CommonMethod.Request(path,date,methodName);
                Message msg = Message.obtain();
                msg.obj=result;
                handlerGetBengZhanList.sendMessage(msg);
                Log.d("DEBUG", "获取泵站列表WebService结果：" + result.toString());
            } catch (Exception e) {
                Message msg = Message.obtain();
                msg.obj="999999";
                handlerGetBengZhanList.sendMessage(msg);
                //Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
            }
        }
    };
    Handler handlerGetBengZhanList = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String val=(String)msg.obj;
            Log.e("warn",val);
            if (val.toString().equals("999999")) {
                Toast.makeText(getActivity(), "获取泵站列表网络或者服务器异常", Toast.LENGTH_SHORT).show();
            } else {
                listBengZhan = new ArrayList<BengZhanClass>();
                String[] objects = val.split("\\|");
                for (int i = 0; i < objects.length-10; i++) {
                    if (objects[i].length() > 0) {
                        String[] values = objects[i].split(",");
                        if (values.length > 1) {
                            BengZhanClass bengzhanObject = new BengZhanClass();
                            bengzhanObject.setID(values[0].toString());
                            bengzhanObject.setName(values[1].toString());
                            bengzhanObject.setYeWei(Double.parseDouble(values[5].toString()));
                            bengzhanObject.setAnQi(Double.parseDouble(values[13].toString()));
                            bengzhanObject.setJiaWan(Double.parseDouble(values[7].toString()));
                            bengzhanObject.setLiuHuaQin(Double.parseDouble(values[11].toString()));
                            bengzhanObject.setYiYangHuaTan(Double.parseDouble(values[9].toString()));
                            List<ShuiBengClass> shuiBengList = new ArrayList<>();
                            if (values.length > 16) {//水泵信息
                                String[] shuibengValues = values[16].split("\\[-\\]");
                                for (int j = 0; j < shuibengValues.length; j++) {
                                    ShuiBengClass shuibengObject = new ShuiBengClass();
                                    shuibengObject.setShuizhanName(values[1].toString());
                                    String[] shuibengPar = shuibengValues[j].split("\\<-\\>");
                                    if (shuibengPar.length > 3) {
                                        shuibengObject.setShuiBengName(shuibengPar[1].toString());//水泵名称
                                        shuibengObject.setId(shuibengPar[0].toString());//水泵id
                                        shuibengObject.setBengZhanID(shuibengPar[2].toString());//水泵站id
                                        shuibengObject.setBeiZhu(shuibengPar[3].toString());//水泵备注
                                        shuibengObject.setSortX(shuibengPar[4].toString());//值
                                    }
                                    if (shuibengPar.length > 5) {
                                        shuibengObject.setDianLiu(shuibengPar[5].toString());//电流
                                        shuibengObject.setZhuangTai(shuibengPar[6].toString());//状态
                                    }
                                    shuiBengList.add(shuibengObject);
                                }
                            }
                            bengzhanObject.setShuiBengClassList(shuiBengList);
                            if (values.length > 17) {//监控视频信息
                                List<VideoMonitoring> videoMonitoringList = new ArrayList<>();
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
                            progressDialog.dismiss();
                            listBengZhan.add(bengzhanObject);
                            list.add(bengzhanObject);
                            list1.add(bengzhanObject);
                            //list = listBengZhan;//保存
                            bzf=new BenZhanFragmentAdapter(getActivity(),listBengZhan);
                            bListView.setAdapter(bzf);
                        }
                    }
                }
            }
        }
    };
    private class ListViewListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(getActivity(),BengZhanXiangQingActivity.class);
            intent.putExtra("BengZhanClass",listBengZhan.get(i));//传递当前泵站对象
            startActivity(intent);
        }
    }
    //倒三角的点击事件
    private class POP_Listener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            MeauPopWindows();
        }
    }
    //标题上的菜单popwindow
    private void MeauPopWindows(){
        RelativeLayout bz_rl=(RelativeLayout)view.findViewById(R.id.bz_rl);
        //RelativeLayout rllll=(RelativeLayout)getActivity().findViewById(R.id.rllll);//找另一个界面的空间ID
        WindowManager wm = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
        int width=wm.getDefaultDisplay().getWidth();
        int height=wm.getDefaultDisplay().getHeight();
        //title.getHeight();
        View addview = LayoutInflater.from(getActivity()).inflate(R.layout.bz_pop_layout, null);
        addinit(addview);
        popupWindow = new PopupWindow(addview,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);//2,3参数为宽高
        //popupWindow.showAtLocation(view,Gravity.BOTTOM,0,0);
        //popupWindow.showAsDropDown(view,0,10);
        popupWindow.setHeight(height-bz_rl.getHeight()-200);
        popupWindow.setWidth(width/2);
        popupWindow.setTouchable(true);//popupWindow可触摸
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                // TODO Auto-generated method stub
                Log.i("mengdd", "onTouch : ");
                return false;
            }
        });
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.color.white));
        //popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置好参数之后再show
        popupWindow.showAsDropDown(BZ_pop,width,0);//在view控件正下方，以view为参照点第二个参数为popwindow距离view的横向距离，
        //第三个参数为y轴即popwindow距离view的纵向距离
    }
    private void closePopwindow() {
        popupWindow.dismiss();
        if(popupWindow!=null){ popupWindow = null;}
    }
    //popwindow布局中对象
    private void addinit(View view){
        ListView bz_pop_listview = (ListView) view.findViewById(R.id.bz_pop_listview);
        bz_pop_listview.setAdapter(new BZ_Pop_Listview(list1,getActivity()));
        bz_pop_listview.setOnItemClickListener(new BZ_pop_ListviewListener());
    }

    private class BZ_pop_ListviewListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            bz_text.setText(list1.get(i).getName());
            //popupWindow.dismiss();
            closePopwindow();
            if(list1.get(i).getName().equals("全部")){
                listBengZhan.clear();
                //将list集合值赋给listbenzhan中，不能直接用等于号
                for(BengZhanClass ji:list){
                    listBengZhan.add(ji);
                }

                bzf.notifyDataSetChanged();//获取选择条目的数据 list用来保存所有数据 以便listBengZhan清空时数据依然能保留下来
            }else{
                listBengZhan.clear();//每次选择不同泵站清除listBengZhan，确保刷新listview只出现想要的条目
                BengZhanClass bengzhanObject = new BengZhanClass();//获取选择条目的数据 list1用来保存所有数据 以便listBengZhan清空时数据依然能保留下来
                bengzhanObject.setID(list1.get(i).getID());
                bengzhanObject.setName(list1.get(i).getName());
                bengzhanObject.setYeWei(list1.get(i).getYeWei());
                bengzhanObject.setAnQi(list1.get(i).getAnQi());
                bengzhanObject.setJiaWan(list1.get(i).getJiaWan());
                bengzhanObject.setLiuHuaQin(list1.get(i).getLiuHuaQin());
                bengzhanObject.setYiYangHuaTan(list1.get(i).getYiYangHuaTan());
                List<ShuiBengClass> shuiBengList = new ArrayList<>();
                if(list1.get(i).getShuiBengClassList()!=null){
                    shuiBengList = list1.get(i).getShuiBengClassList();
                }
                bengzhanObject.setShuiBengClassList(shuiBengList);

                List<VideoMonitoring> videoMonitoringList = new ArrayList<>();
                if(list1.get(i).getVideoMonitoringList()!=null){
                    videoMonitoringList = list1.get(i).getVideoMonitoringList();
                }
                bengzhanObject.setVideoMonitoringList(videoMonitoringList);
                listBengZhan.add(bengzhanObject);
                bzf.notifyDataSetChanged();
            }


        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            //fragment被隐藏
        }else{
            init();//显示出来
        }

    }
}
