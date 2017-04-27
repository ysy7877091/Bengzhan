package com.example.administrator.benzhanzidonghua;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2017/3/28.
 */

public class TongJiFenXi extends AppCompatActivity{
    private Button JiangYUorPaiShui;//降雨/排水
    private Button TJ_car;//车辆
    private Button TJ_BengZhan;//泵站
    private Button TJ_baoJing;//报警
    private JiangYUorPaiShuiFragment JYPSF;
    private BaoJingFragment BJF;
    private carFragment Cf;
    private BengZhanTongJIFragment BZTJF;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.tongjifenxiactivity_layout);
        CommonMethod.setStatuColor(TongJiFenXi.this, R.color.blue);
        init();
    }
    private int height_sum;
    private void init(){
        RelativeLayout TongJi_top = (RelativeLayout)findViewById(R.id.TongJi_top);

        LinearLayout TJ_meau   = (LinearLayout)findViewById(R.id.TJ_meau);
        height_sum  = TongJi_top.getHeight()+TJ_meau.getHeight();
        Button Cancel  = (Button)findViewById(R.id.TJ_Cancel);
        Cancel.setOnClickListener(new TongJiFenXiListener());
        JiangYUorPaiShui = (Button)findViewById(R.id.JiangYUorPaiShui);
        JiangYUorPaiShui.setOnClickListener(new TongJiFenXiListener());
        TJ_car = (Button)findViewById(R.id.TJ_car);
        TJ_car.setOnClickListener(new TongJiFenXiListener());
        TJ_BengZhan = (Button)findViewById(R.id.TJ_BengZhan);
        TJ_BengZhan.setOnClickListener(new TongJiFenXiListener());
        TJ_baoJing = (Button)findViewById(R.id.TJ_baoJing);
        TJ_baoJing.setOnClickListener(new TongJiFenXiListener());

        JYPSF = new JiangYUorPaiShuiFragment();//降雨排水fragment
        Cf = new carFragment();//车fragment
        BJF = new BaoJingFragment();//基础设施fragment
        BZTJF = new BengZhanTongJIFragment();//泵站fragment
        JiangYUorPaiShui.setBackgroundColor(getResources().getColor(R.color.tj1));
        switchFragment(Cf,BJF,BZTJF,JYPSF);//默认进入降雨排水fragment
    }
    private class TongJiFenXiListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.TJ_Cancel:finish();break;
                case R.id.JiangYUorPaiShui:
                                            setJiangYUorPaiShuiMethod();
                                            break;
                case R.id.TJ_car:
                                            setTJ_carMethod();
                                            break;
                case R.id.TJ_BengZhan:
                                            setTJ_BengZhanMethod();
                                            break;
                case R.id.TJ_baoJing:
                                            setTJ_baoJingMethod();
                                            break;
            }
        }
    }
    //点击排水或者降雨的点击事件
    private void setJiangYUorPaiShuiMethod(){
        switchFragment(Cf,BJF,BZTJF,JYPSF);
        JiangYUorPaiShui.setBackgroundColor(getResources().getColor(R.color.tj1));
        TJ_car.setBackgroundColor(getResources().getColor(R.color.blue));
        TJ_BengZhan.setBackgroundColor(getResources().getColor(R.color.blue));
        TJ_baoJing.setBackgroundColor(getResources().getColor(R.color.blue));
    }
    //车辆的点击事件
    private void setTJ_carMethod(){
        switchFragment(JYPSF,BJF,BZTJF,Cf);
        JiangYUorPaiShui.setBackgroundColor(getResources().getColor(R.color.blue));
        TJ_car.setBackgroundColor(getResources().getColor(R.color.tj1));
        TJ_BengZhan.setBackgroundColor(getResources().getColor(R.color.blue));
        TJ_baoJing.setBackgroundColor(getResources().getColor(R.color.blue));
    }
    //泵站的点击事件
    private void setTJ_BengZhanMethod(){
        switchFragment(JYPSF,BJF,Cf,BZTJF);
        JiangYUorPaiShui.setBackgroundColor(getResources().getColor(R.color.blue));
        TJ_car.setBackgroundColor(getResources().getColor(R.color.blue));
        TJ_BengZhan.setBackgroundColor(getResources().getColor(R.color.tj1));
        TJ_baoJing.setBackgroundColor(getResources().getColor(R.color.blue));
    }
    //基础设施的点击事件
    private void setTJ_baoJingMethod(){
        switchFragment(Cf,JYPSF,BZTJF,BJF);
        JiangYUorPaiShui.setBackgroundColor(getResources().getColor(R.color.blue));
        TJ_car.setBackgroundColor(getResources().getColor(R.color.blue));
        TJ_BengZhan.setBackgroundColor(getResources().getColor(R.color.blue));
        TJ_baoJing.setBackgroundColor(getResources().getColor(R.color.tj1));
    }
    public void switchFragment(Fragment from, Fragment from1, Fragment from2, Fragment to) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        //设置切换的效果
        if (!to.isAdded()) {    // 先判断是否被add过
            if (from.isAdded()) {
                transaction.hide(from);
            } //隐藏当前的fragment
            if (from1.isAdded()) {
                transaction.hide(from1);
            } //隐藏当前的fragment
            if (from2.isAdded()) {
                transaction.hide(from2);
            } //隐藏当前的fragment
            Bundle bundle = new Bundle();
            bundle.putInt("height_sum", height_sum);//将标题栏目和菜单栏总高度传给各个fragment
            to.setArguments(bundle);
            transaction.add(R.id.TongJi_replace, to).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            if (from.isAdded()) {
                transaction.hide(from);
            } //隐藏当前的fragment
            if (from1.isAdded()) {
                transaction.hide(from1);
            } //隐藏当前的fragment
            if (from2.isAdded()) {
                transaction.hide(from2);
            } //隐藏当前的fragment
            transaction.show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);

    }
}
