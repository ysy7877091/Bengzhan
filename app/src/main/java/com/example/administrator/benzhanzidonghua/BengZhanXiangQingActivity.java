package com.example.administrator.benzhanzidonghua;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.com.vanpeng.Adapter.MyAdapterShuiBeng;
import com.vanpeng.javabeen.BengZhanClass;
import com.vanpeng.javabeen.VideoMonitoring;

/**
 * 泵站详情
 */
public class BengZhanXiangQingActivity extends AppCompatActivity {

    private ListView lv_benZhanXiangQing;
    private Context mContent;
    private ImageView iv_left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.beng_zhan_xiang_qing);

       /* RelativeLayout bz_xiangqing = (RelativeLayout)findViewById(R.id.bz_xiangqing);
        int height = bz_xiangqing.getHeight();*/

        mContent = this;

        //设置控件的高度
        iv_left = (ImageView) findViewById(R.id.iv_left);
        Button iv_button= (Button)findViewById(R.id.iv_button);
        iv_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        /得到父布局的 布局
       /* RelativeLayout.LayoutParams lineParams = (RelativeLayout.LayoutParams)iv_left.getLayoutParams();
        lineParams.height=height;
        iv_left.setLayoutParams(lineParams);*/

        /*iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
        BengZhanClass bengZhanClass =(BengZhanClass)getIntent().getSerializableExtra("BengZhanClass");

        TextView txt_bengname=(TextView)findViewById(R.id.tv_bzName);
        txt_bengname.setText(bengZhanClass.getName());

        TextView txt_co2 = (TextView) findViewById(R.id.tv_yyht);
        if(bengZhanClass.getYiYangHuaTan()<60000) {
            txt_co2.setText(Double.toString(bengZhanClass.getYiYangHuaTan()));
        }
        else
        {
            txt_co2.setText("0");
        }

        TextView txt_ch3 = (TextView) findViewById(R.id.tv_jia_wan);
        if(bengZhanClass.getJiaWan()<60000) {
            txt_ch3.setText(Double.toString(bengZhanClass.getJiaWan()));
        }
        else
        {
            txt_ch3.setText("0");
        }

        TextView txt_soh = (TextView) findViewById(R.id.tv_liu_suan_an);
        if(bengZhanClass.getLiuHuaQin()<60000) {
            txt_soh.setText(Double.toString(bengZhanClass.getLiuHuaQin()));
        }
        else
        {
            txt_soh.setText("0");
        }

        TextView txt_n2 = (TextView) findViewById(R.id.tv_an_qi);
        if(bengZhanClass.getAnQi()<60000) {
            txt_n2.setText(Double.toString(bengZhanClass.getAnQi()));
        }
        else
        {
            txt_n2 .setText("0");
        }

        if(bengZhanClass.getVideoMonitoringList()!=null) {
            for (int i = 0; i < bengZhanClass.getVideoMonitoringList().size(); i++) {
                if (bengZhanClass.getVideoMonitoringList().get(i).getName().equals("视频1")) {
                    ImageView btn_onevideo = (ImageView) findViewById(R.id.btn摄像头室外);
                    btn_onevideo.setTag(bengZhanClass.getVideoMonitoringList().get(i));

                } else if (bengZhanClass.getVideoMonitoringList().get(i).getName().equals("视频2")) {
                    ImageView btn_twovideo = (ImageView) findViewById(R.id.btn摄像头室内);
                    btn_twovideo.setTag(bengZhanClass.getVideoMonitoringList().get(i));
                }
            }
        }

        lv_benZhanXiangQing = (ListView) findViewById(R.id.lv_benZhanXiangQing);
        MyAdapterShuiBeng adapterShuiBeng = new MyAdapterShuiBeng(mContent,bengZhanClass.getShuiBengClassList());
        if(adapterShuiBeng!=null) {

            lv_benZhanXiangQing.setAdapter(adapterShuiBeng);
            lv_benZhanXiangQing.setFocusable(true);
            lv_benZhanXiangQing.setFocusableInTouchMode(true);
            lv_benZhanXiangQing.requestFocus();
            lv_benZhanXiangQing.requestFocusFromTouch();
        }
    }

    public void btn1_OnClick(View view) {
        ImageView btn_one=(ImageView)findViewById(R.id.btn摄像头室外);
        VideoMonitoring videoMonitoring= (VideoMonitoring)btn_one.getTag();
        if(videoMonitoring==null)
        {
            Toast.makeText(getApplicationContext(), "此泵站第一路视频不存在。", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent=new Intent();

            intent.putExtra("VideoMonitoring", videoMonitoring);
            //intent.putExtra("IP", videoMonitoring.getIp());
            /*intent.putExtra("Port",videoMonitoring.getPort());
            intent.putExtra("LoginName",videoMonitoring.getLoginName());
            intent.putExtra("LoginPWD",videoMonitoring.getLoginPWD());
            intent.putExtra("ChannelId",videoMonitoring.getChannelID());*/
            intent.setClass(BengZhanXiangQingActivity.this, CameraPlayXXActivity.class);
            startActivity(intent);
        }
    }
    public void btn2_OnClick(View view) {
        ImageView btn_two=(ImageView)findViewById(R.id.btn摄像头室内);
        VideoMonitoring videoMonitoring= (VideoMonitoring)btn_two.getTag();
        if(videoMonitoring == null)
        {
            Toast.makeText(getApplicationContext(), "此泵站第二路视频不存在。", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent=new Intent();
            intent.setClass(BengZhanXiangQingActivity.this, CameraPlayXXActivity.class);
            /*intent.putExtra("IP", videoMonitoring.getIp());
            intent.putExtra("Port",videoMonitoring.getPort());
            intent.putExtra("LoginName",videoMonitoring.getLoginName());
            intent.putExtra("LoginPWD",videoMonitoring.getLoginPWD());
            intent.putExtra("ChannelId",videoMonitoring.getChannelID());*/
            intent.putExtra("VideoMonitoring", videoMonitoring);
            startActivity(intent);
        }
    }
}