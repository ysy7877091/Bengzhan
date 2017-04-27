package com.example.administrator.benzhanzidonghua;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viewpagerlunbo.RollPagerView;
import com.viewpagerlunbo.adapter.StaticPagerAdapter;

/**
 * Created by Administrator on 2017/3/23.
 */

public class ZhuYeFragment extends Fragment {
    OnMyClikListener myListener;
    private LinearLayout ZhuYe_BZ;
    private LinearLayout ZhuYe_BJ;
    private LinearLayout ZhuYe_Map;
    private LinearLayout ZhuYe_JK;
    private LinearLayout ZhuYe_LS;
    private LinearLayout ZhuYe_DW;
    private LinearLayout ZhuYe_SWJC;
    private LinearLayout ZhuYe_YLJC;
    private LinearLayout ZhuYe_YLLS;
    private LinearLayout ZhuYe_TuiChu;
    private String power=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflater=LayoutInflater.from(getActivity());
        View view=inflater.inflate(R.layout.zhuyefragment_layout,container,false);
        init(view);
        return view;
    }
    private void init(View view){
        RollPagerView mRollPagerView = (RollPagerView)view.findViewById(R.id.mRollPagerView);//图片轮播
        rollPagerViewSet(mRollPagerView);

        WindowManager wm = getActivity().getWindowManager();

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();


        LinearLayout left = (LinearLayout)view.findViewById(R.id.left);
        LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) left.getLayoutParams(); //取控件textView当前的布局参数 linearParams.height = 20;// 控件的高强制设成20
        linearParams.width = width/3;// 控件的宽强制设成
        left.setLayoutParams(linearParams); //使设置好的布局参数应用到控件

        LinearLayout center = (LinearLayout)view.findViewById(R.id.center);
        LinearLayout.LayoutParams linearParams1 =(LinearLayout.LayoutParams) center.getLayoutParams(); //取控件textView当前的布局参数 linearParams.height = 20;// 控件的高强制设成20
        linearParams1.width = width/3;// 控件的宽强制设成30
        center.setLayoutParams(linearParams1); //使设置好的布局参数应用到控件

        LinearLayout right = (LinearLayout)view.findViewById(R.id.right);
        LinearLayout.LayoutParams linearParams2 =(LinearLayout.LayoutParams) right.getLayoutParams(); //取控件textView当前的布局参数 linearParams.height = 20;// 控件的高强制设成20
        linearParams2.width = width/3;// 控件的宽强制设成
        right.setLayoutParams(linearParams2); //使设置好的布局参数应用到控件
        Bundle bundle = getArguments();
        //权限
        String str2 = bundle.getString("personInformation");
        Log.e("warn","主页"+str2);
        //判断权限
        String arr[] =str2.split(";");
        Log.e("warn",String.valueOf(arr.length));
        for(int j=0;j<arr.length;j++){
            arr[j]=arr[j].substring(arr[j].indexOf("=")+1);
            Log.e("warn",arr[j]);
        }

        ZhuYe_BZ = (LinearLayout)view.findViewById(R.id.ZhuYe_BZ);//泵站
        ZhuYe_BZ.setOnClickListener(new ClickListener());


        ZhuYe_BJ = (LinearLayout)view.findViewById(R.id.ZhuYe_BJ);//报警
        ZhuYe_BJ.setOnClickListener(new ClickListener());
        ZhuYe_JK = (LinearLayout)view.findViewById(R.id.ZhuYe_JK);//监控
        ZhuYe_JK.setOnClickListener(new ClickListener());

        ZhuYe_Map = (LinearLayout)view.findViewById(R.id.ZhuYe_Map);//地图
        ZhuYe_Map.setOnClickListener(new ClickListener());


        ZhuYe_LS= (LinearLayout)view.findViewById(R.id.ZhuYe_LS);//历史
        ZhuYe_LS.setOnClickListener(new ClickListener());
        ZhuYe_DW = (LinearLayout)view.findViewById(R.id.ZhuYe_DW);//北斗定位
        ZhuYe_DW.setOnClickListener(new ClickListener());
        ZhuYe_SWJC= (LinearLayout)view.findViewById(R.id.ZhuYe_SWJC);//水位监测
        ZhuYe_SWJC.setOnClickListener(new ClickListener());
        ZhuYe_YLJC = (LinearLayout)view.findViewById(R.id.ZhuYe_YLJC);//雨量监测
        ZhuYe_YLJC.setOnClickListener(new ClickListener());
        ZhuYe_YLLS= (LinearLayout)view.findViewById(R.id.ZhuYe_YLLS);//雨量历史
        ZhuYe_YLLS.setOnClickListener(new ClickListener());
        ZhuYe_TuiChu= (LinearLayout)view.findViewById(R.id.ZhuYe_TuiChu);//雨量历史
        if(str2.contains("Power")){
            if(!arr[7].equals("anyType{}")){
                power = arr[7];
                if(arr[7].equals("2")){
                    ImageView iv = (ImageView)view.findViewById(R.id.TJ_IV);
                    TextView tv =(TextView)view.findViewById(R.id.TJ_tv);
                    iv.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.tuichuclick));
                    tv.setText("退出");
                }
            }
        }
        ZhuYe_TuiChu.setOnClickListener(new ClickListener());
    }
    //图片轮播 设置参数
    private void rollPagerViewSet(RollPagerView rollPagerView) {
        rollPagerView.setPlayDelay(3000);//*播放间隔
        rollPagerView.setAnimationDurtion(500);//透明度
        rollPagerView.setAdapter(new rollViewpagerAdapter());//配置适配器
    }
    //图片轮播 设置轮播的图片 执行轮播
    private class rollViewpagerAdapter extends StaticPagerAdapter {

        private int[] res={R.mipmap.banner1,R.mipmap.banner2,R.mipmap.banner3,};

        @Override
        public View getView(ViewGroup container, int position) {
            ImageView imageView=new ImageView(container.getContext());
            imageView.setImageResource(res[position]);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            return imageView;
        }
        @Override
        public int getCount() {
            return res.length;
        }
    }
    private View rootView;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //获取activity根视图,rootView设为全局变量
        rootView=activity.getWindow().getDecorView();
        if(rootView!=null) {

            Button Main_back = (Button)rootView.findViewById(R.id.Main_back);
            if(Main_back!=null){
                Main_back.setEnabled(false);
            }
            ImageView iv = (ImageView) rootView.findViewById(R.id.Main_Iv);
            if(iv!=null){
                iv.setVisibility(View.GONE);
            }
            myListener = (OnMyClikListener) getActivity();
        }
    }
    /*private class ZhuYeFragmentListener implements View.OnTouchListener{

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()){
               case R.id.ZhuYe_BZ:
                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下
                                        ZhuYe_BZ.setAlpha(0);
                                    }
                                    if (event.getAction() == MotionEvent.ACTION_UP) {//松开
                                        ZhuYe_BZ.setAlpha(1);
                                        myListener.onclik(0);
                                    }
                                    break;
                case R.id.ZhuYe_BJ:
                                        if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下
                                            ZhuYe_BJ.setAlpha(0);
                                        }
                                        if (event.getAction() == MotionEvent.ACTION_UP) {//松开
                                            ZhuYe_BJ.setAlpha(1);
                                            myListener.onclik(1);
                                        }
                                    break;
                case R.id.ZhuYe_JK:
                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下
                                        ZhuYe_JK.setAlpha(0);
                                    }
                                    if (event.getAction() == MotionEvent.ACTION_UP) {//松开
                                        ZhuYe_JK.setAlpha(1);
                                        myListener.onclik(2);
                                    }
                                   break;
              case R.id.ZhuYe_Map:
                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下
                                             ZhuYe_Map.setAlpha(0);
                                            }
                                    if (event.getAction() == MotionEvent.ACTION_UP) {//松开
                                        ZhuYe_Map.setAlpha(1);
                                        myListener.onclik(3);
                                    }
                                    break;
                case R.id.ZhuYe_LS:
                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下
                                        ZhuYe_LS.setAlpha(0);
                                    }
                                    if (event.getAction() == MotionEvent.ACTION_UP) {//松开
                                        ZhuYe_LS.setAlpha(1);
                                        myListener.onclik(4);
                                    }
                                    break;
                case R.id.ZhuYe_DW:
                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下
                                        ZhuYe_DW.setAlpha(0);
                                    }
                                    if (event.getAction() == MotionEvent.ACTION_UP) {//松开
                                        ZhuYe_DW.setAlpha(1);
                                        myListener.onclik(5);
                                    }
                                    break;
                case R.id.ZhuYe_SWJC:
                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下
                                        ZhuYe_SWJC.setAlpha(0);
                                    }
                                    if (event.getAction() == MotionEvent.ACTION_UP) {//松开
                                        ZhuYe_SWJC.setAlpha(1);
                                        myListener.onclik(6);
                                    }
                                    break;
                case R.id.ZhuYe_YLJC:
                                        if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下
                                            ZhuYe_YLJC.setAlpha(0);
                                        }
                                        if (event.getAction() == MotionEvent.ACTION_UP) {//松开
                                            ZhuYe_YLJC.setAlpha(1);
                                            myListener.onclik(7);
                                        }
                                         break;

                case R.id.ZhuYe_YLLS:
                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下
                                        ZhuYe_YLLS.setAlpha(0);
                                    }
                                    if (event.getAction() == MotionEvent.ACTION_UP) {//松开
                                        ZhuYe_YLLS.setAlpha(1);
                                        myListener.onclik(8);
                                    }
                                    break;
                case R.id.ZhuYe_TuiChu:
                                    if(power.equals("2")){
                                        getActivity().finish();
                                    }else {
                                        if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下
                                            ZhuYe_TuiChu.setAlpha(0);
                                        }
                                        if (event.getAction() == MotionEvent.ACTION_UP) {//松开
                                            ZhuYe_TuiChu.setAlpha(1);
                                            Intent intent = new Intent(getActivity(), TongJiFenXi.class);
                                            getActivity().startActivity(intent);
                                        }
                                    }
                                    //getActivity().finish();
                                    break;
            }
            return true;
        }
    }*/
    //定义主页fragment的回调接口 view1向view2传数据时，在view1中定义 0泵站列表1报警2监控3地图4历史5定位6水位监测7雨量监测8雨量里时9退出
    interface OnMyClikListener {
        public void onclik(int num);
    }

    /*//定义供fragment调用的函数
    public void setOnclikListener(OnMyClikListener onMyclikListener) {
        this.myListener = onMyclikListener;
    }*/
//主activity返回主页时 隐藏返回按钮
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden==false) {
            rootView.findViewById(R.id.Main_back).setEnabled(false);
            rootView.findViewById(R.id.Main_Iv).setVisibility(View.GONE);
        }
    }
    class ClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){

                case R.id.ZhuYe_BZ:

                        myListener.onclik(0);

                        break;
                case R.id.ZhuYe_BJ:

                        myListener.onclik(1);

                        break;
                case R.id.ZhuYe_JK:

                        myListener.onclik(2);

                        break;
                case R.id.ZhuYe_Map:

                        myListener.onclik(3);

                        break;
                case R.id.ZhuYe_LS:

                        myListener.onclik(4);

                        break;
                case R.id.ZhuYe_DW:

                        myListener.onclik(5);

                        break;
                case R.id.ZhuYe_SWJC:

                        myListener.onclik(6);

                        break;
                case R.id.ZhuYe_YLJC:

                        myListener.onclik(7);

                        break;

                case R.id.ZhuYe_YLLS:

                        myListener.onclik(8);

                        break;
                case R.id.ZhuYe_TuiChu:
                    if(power.equals("2")){
                        getActivity().finish();
                    }else {
                            Intent intent = new Intent(getActivity(), TongJiFenXi.class);
                            getActivity().startActivity(intent);
                    }
                    //getActivity().finish();
                    break;
            }

        }
    }

}
