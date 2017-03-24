package com.example.administrator.benzhanzidonghua;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/12/23.
 * 报警记录二块  水位 气体
 */

public class BaoJingMeauActivity extends AppCompatActivity {
    private TextView LSList_change;
    //private TextView LSList_YuLiang;
    //private ImageView YuLiang_IV;
    private TextView LSList_ShuiWei;
    private ImageView ShuiWei_IV;
    private TextView LSList_QiTi;
    private ImageView QiTi_IV;
    //private TextView LSList_JiShuiDian;
    //private ImageView JiShuiDian_IV;
    //private YuLiangBaoJingFragment yuLiang;//雨量
    private ShuiWeiBaoJingFragment shuiWei;//水位fragment
    //private JiShuiDianFragment JSDF;//积水点fragment
    private QiTiFragment qiTi;//气体fragment
    private String ID;
    private String StartTime;
    private String EndTime;
    private String Name;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lishilist_layout);
        CommonMethod.setStatuColor(this,R.color.blue);
        getIntentData();
        init();
    }
    private void init(){
        //fragment
        shuiWei= new ShuiWeiBaoJingFragment();
        qiTi=new QiTiFragment();
        //后退
        Button BJB_button = (Button)findViewById(R.id.BJB_button);
        BJB_button.setOnClickListener(new LiShiListActivityListener());
       /* ImageView LSList_back=(ImageView)findViewById(R.id.LSList_back);
        LSList_back.setOnClickListener(new LiShiListActivityListener());*/
        //标题
        LSList_change=(TextView)findViewById(R.id.LSList_change);
        LSList_change.setText("报警记录");
        //雨量
        /*LinearLayout LSList_YuLiangLL=(LinearLayout)findViewById(R.id.LSList_YuLiangLL);
        LSList_YuLiangLL.setOnClickListener(new LiShiListActivityListener());
        LSList_YuLiang=(TextView)findViewById(R.id.LSList_YuLiang);
        LSList_YuLiang.setTextColor(getResources().getColor(R.color.yl04));*/
        //YuLiang_IV=(ImageView)findViewById(R.id.YuLiang_IV);
        //YuLiang_IV.setVisibility(View.VISIBLE);
        //水位
        LinearLayout LSList_ShuiWeiLL=(LinearLayout)findViewById(R.id.LSList_ShuiWeiLL);
        LSList_ShuiWeiLL.setOnClickListener(new LiShiListActivityListener());
        LSList_ShuiWei=(TextView)findViewById(R.id.LSList_ShuiWei);
        LSList_ShuiWei.setTextColor(getResources().getColor(R.color.yl04));
        ShuiWei_IV=(ImageView)findViewById(R.id.ShuiWei_IV);
        ShuiWei_IV.setVisibility(View.VISIBLE);
        switchFragment(qiTi,shuiWei);

        //气体
        LinearLayout LSList_QiTiLL=(LinearLayout)findViewById(R.id.LSList_QiTiLL);
        LSList_QiTiLL.setOnClickListener(new LiShiListActivityListener());
        LSList_QiTi=(TextView)findViewById(R.id.LSList_QiTi);
        QiTi_IV=(ImageView)findViewById(R.id.QiTi_IV);
        //积水点

    }
    private void getIntentData(){
        Intent intent = getIntent();
        ID = intent.getStringExtra("ID");
        StartTime = intent.getStringExtra("StartTime");
        EndTime = intent.getStringExtra("EndTime");
        Name = intent.getStringExtra("Name");
    }
    public void switchFragment(Fragment from,Fragment to) {
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
            bundle.putString("bool","0");//1代表进入历史气体fragment 0代表进入报警气体fragment 用同一界面和布局
            to.setArguments(bundle);
            transaction.add(R.id.LL_replace, to, "tag").commit(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            if (from.isAdded()) {
                transaction.hide(from);
            } //隐藏当前的fragment
            transaction.show(to).commit(); // 隐藏当前的fragment，显示下一个
        }
    }
    private class LiShiListActivityListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.BJB_button:
                                        finish();
                                        break;
                /*case LSList_YuLiangLL:
                                            YuLiangClickMethod();
                                            break;*/
                case R.id.LSList_ShuiWeiLL:
                                            ShuiWeiClickMethod();
                                            break;
                case R.id.LSList_QiTiLL:
                                            QiTiClickMethod();
                                            break;
               /* case R.id.LSList_JiShuiDianLL:
                                                JiShuiDianClickMethod();
                                                break;
*/
            }
        }
    }
    //雨量点击事件
    /*private void YuLiangClickMethod(){
        LSList_change.setText("雨量报警记录");
        LSList_YuLiang.setTextColor(getResources().getColor(R.color.yl04));
        YuLiang_IV.setVisibility(View.VISIBLE);

        LSList_ShuiWei.setTextColor(getResources().getColor(R.color.btn_login_normal));
        ShuiWei_IV.setVisibility(View.INVISIBLE);

        LSList_QiTi.setTextColor(getResources().getColor(R.color.btn_login_normal));
        QiTi_IV.setVisibility(View.INVISIBLE);

        //LSList_JiShuiDian.setTextColor(getResources().getColor(R.color.btn_login_normal));
        //JiShuiDian_IV.setVisibility(View.INVISIBLE);
        //switchFragment(shuiWei,qiTi,JSDF,yuLiang);
    }*/
    //水位点击事件
    private void ShuiWeiClickMethod(){
        LSList_change.setText("水位报警记录");
        //LSList_YuLiang.setTextColor(getResources().getColor(R.color.btn_login_normal));
        //YuLiang_IV.setVisibility(View.INVISIBLE);

        LSList_ShuiWei.setTextColor(getResources().getColor(R.color.yl04));
        ShuiWei_IV.setVisibility(View.VISIBLE);

        LSList_QiTi.setTextColor(getResources().getColor(R.color.btn_login_normal));
        QiTi_IV.setVisibility(View.INVISIBLE);

        //LSList_JiShuiDian.setTextColor(getResources().getColor(R.color.btn_login_normal));
        //JiShuiDian_IV.setVisibility(View.INVISIBLE);
        switchFragment(qiTi,shuiWei);
    }
    //气体点击事件
    private void QiTiClickMethod(){
        LSList_change.setText("气体报警记录");
        //LSList_YuLiang.setTextColor(getResources().getColor(R.color.btn_login_normal));
        //YuLiang_IV.setVisibility(View.INVISIBLE);

        LSList_ShuiWei.setTextColor(getResources().getColor(R.color.btn_login_normal));
        ShuiWei_IV.setVisibility(View.INVISIBLE);

        LSList_QiTi.setTextColor(getResources().getColor(R.color.yl04));
        QiTi_IV.setVisibility(View.VISIBLE);

        //LSList_JiShuiDian.setTextColor(getResources().getColor(R.color.btn_login_normal));
        //JiShuiDian_IV.setVisibility(View.INVISIBLE);
        switchFragment(shuiWei,qiTi);
    }
    //积水点点击事件
   /* private void JiShuiDianClickMethod(){
        LSList_change.setText("积水点报警记录");
        LSList_YuLiang.setTextColor(getResources().getColor(R.color.btn_login_normal));
        YuLiang_IV.setVisibility(View.INVISIBLE);

        LSList_ShuiWei.setTextColor(getResources().getColor(R.color.btn_login_normal));
        ShuiWei_IV.setVisibility(View.INVISIBLE);

        LSList_QiTi.setTextColor(getResources().getColor(R.color.btn_login_normal));
        QiTi_IV.setVisibility(View.INVISIBLE);

        LSList_JiShuiDian.setTextColor(getResources().getColor(R.color.yl04));
        JiShuiDian_IV.setVisibility(View.VISIBLE);
        switchFragment(yuLiang,shuiWei,qiTi,JSDF);
    }*/
}
