package com.example.administrator.benzhanzidonghua;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.vanpeng.javabeen.VideoMonitoring;

/**
 * Created by Administrator on 2016/12/27.
 * 监控列表
 */

public class JianKongList extends AppCompatActivity{
    private VideoMonitoring in_VideoMonitoring;//室内监控
    private VideoMonitoring out_VideoMonitoring;//室外监控
    private String name;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jiankongtabnle_layout);
        init();
        getIntentData();
    }
    private void init(){
        ImageButton inButton =(ImageButton)findViewById(R.id.inButton);//室内监控
        ImageButton outButton=(ImageButton)findViewById(R.id.outButton);//室外监控
        inButton.setOnClickListener(new JianKongListListener());
        outButton.setOnClickListener(new JianKongListListener());
        Button video_button =(Button)findViewById(R.id.video_button);
        video_button.setOnClickListener(new JianKongListListener());
    }
    private void getIntentData(){
        //获取传过来的室内室外监控
        in_VideoMonitoring =(VideoMonitoring)getIntent().getSerializableExtra("in_VideoMonitoring");

        out_VideoMonitoring =(VideoMonitoring)getIntent().getSerializableExtra("out_VideoMonitoring");
        name = getIntent().getStringExtra("bz_Name");

    }
    private class JianKongListListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.inButton:
                                        Intent in_intent = new Intent(JianKongList.this,CameraPlayXXActivity.class);
                                        in_intent.putExtra("VideoMonitoring",in_VideoMonitoring);
                                        //in_intent.putExtra("bz_name",name);
                                        startActivity(in_intent);
                                        break;
                case R.id.outButton:
                                        Intent out_intent = new Intent(JianKongList.this,CameraPlayXXActivity.class);
                                        out_intent.putExtra("VideoMonitoring",out_VideoMonitoring);
                                        startActivity(out_intent);
                                        break;
                case R.id.video_button:finish();break;
            }
        }
    }
}
