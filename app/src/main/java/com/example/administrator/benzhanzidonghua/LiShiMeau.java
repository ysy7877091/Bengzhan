package com.example.administrator.benzhanzidonghua;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/1/5.
 * 历史记录查询 水位和气体页面
 */

public class LiShiMeau extends AppCompatActivity {
    private TextView tv_shuiWeiText;//水位
    private ImageView ShuiWeiImage_IV;//水位下横线
    private TextView tv_LiShiQiTiText;//气体
    private ImageView QiTiImage_IV;//气体下横线

    private String ID="";//泵站ID
    private String StartTime="";//开始时间
    private String EndTime="";//结束时间
    private String Name="";//泵站名称

    private QiTiFragment qiTi;//气体fragment
    private LiShiShuiWeiFragment water;//水位fragment
    private RelativeLayout LSlist_top;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.lishichaxunmeau_layout);
        CommonMethod.setStatuColor(this,R.color.blue);
        getIntentData();
        init();
    }
    private void init(){
        //实例化fragment
        qiTi = new QiTiFragment();
        water =new LiShiShuiWeiFragment();
        LSlist_top = (RelativeLayout)findViewById(R.id.LSlist_top);
        LSlist_top.setVisibility(View.VISIBLE);
        //返回view
        //Log.e("warn","历史菜单界面"+"wakaka");
        Button LSM_button = (Button)findViewById(R.id.LSM_button);
        LSM_button.setOnClickListener(new LiShiMeauListener());
        /*ImageView LiShi_back = (ImageView)findViewById(LiShi_back);
        LiShi_back.setOnClickListener(new LiShiMeauListener());*/
        //首次进入页面的初始页面状态 水位
        LinearLayout ll_LiShiShuiWei=(LinearLayout)findViewById(R.id.ll_LiShiShuiWei);
        ll_LiShiShuiWei.setOnClickListener(new LiShiMeauListener());
        tv_shuiWeiText=(TextView)findViewById(R.id.tv_shuiWeiText);
        //tv_shuiWeiText.setTextColor(getResources().getColor(R.color.yl04));
        ShuiWeiImage_IV=(ImageView)findViewById(R.id.ShuiWeiImage_IV);
        //ShuiWeiImage_IV.setVisibility(View.VISIBLE);
        //气体
        LinearLayout ll_LiShiQiTi=(LinearLayout)findViewById(R.id.ll_LiShiQiTi);
        ll_LiShiQiTi.setOnClickListener(new LiShiMeauListener());
        tv_LiShiQiTiText=(TextView)findViewById(R.id.tv_LiShiQiTiText);
        tv_LiShiQiTiText.setTextColor(getResources().getColor(R.color.yl04));
        QiTiImage_IV=(ImageView)findViewById(R.id.QiTiImage_IV);
        QiTiImage_IV.setVisibility(View.VISIBLE);
        switchFragment(water,qiTi);
    }
    private void getIntentData(){
        Intent intent = getIntent();
        ID = intent.getStringExtra("ID");
        StartTime = intent.getStringExtra("StartTime");
        EndTime = intent.getStringExtra("EndTime");
        Name = intent.getStringExtra("Name");
    }
    public void switchFragment(Fragment from, Fragment to) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        //设置切换的效果
        if (!to.isAdded()) {    // 先判断是否被add过
            if (from.isAdded()) {
                transaction.hide(from);
            } //隐藏当前的fragment
            //向fragment传值 判断是否为报警的还是历史的
            Bundle bundle = new Bundle();
            bundle.putString("ID",ID);
            bundle.putString("StartTime",StartTime);
            bundle.putString("EndTime",EndTime);
            bundle.putString("Name",Name);
            bundle.putString("bool","1");//1代表进入历史气体fragment 0代表进入报警气体fragment 用同一界面和布局
            to.setArguments(bundle);
            transaction.add(R.id.LiShi_replace, to, "tag").commit(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            if (from.isAdded()) {
                transaction.hide(from);
            } //隐藏当前的fragment
            transaction.show(to).commit(); // 隐藏当前的fragment，显示下一个
        }
    }
    //水位点击事件
    private void ShuiWeiClickMethod(){
        tv_shuiWeiText.setTextColor(getResources().getColor(R.color.yl04));
        ShuiWeiImage_IV.setVisibility(View.VISIBLE);
        tv_LiShiQiTiText.setTextColor(getResources().getColor(R.color.btn_login_normal));
        QiTiImage_IV.setVisibility(View.INVISIBLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        LSlist_top.setVisibility(View.GONE);
        switchFragment(qiTi,water);
    }
    //气体点击事件
    private void QiTiClickMethod(){
        tv_shuiWeiText.setTextColor(getResources().getColor(R.color.btn_login_normal));
        ShuiWeiImage_IV.setVisibility(View.INVISIBLE);
        tv_LiShiQiTiText.setTextColor(getResources().getColor(R.color.yl04));
        QiTiImage_IV.setVisibility(View.VISIBLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        LSlist_top.setVisibility(View.VISIBLE);
        switchFragment(water,qiTi);
    }
    private class LiShiMeauListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.ll_LiShiShuiWei:ShuiWeiClickMethod();
                                            break;
                case R.id.LSM_button:
                                        finish();
                                        break;
                case R.id.ll_LiShiQiTi:
                                        QiTiClickMethod();
                                        break;
            }
        }
    }
}
