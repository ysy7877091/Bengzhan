package com.example.administrator.benzhanzidonghua;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by Administrator on 2017/3/28.
 * 工作报警统计
 */

public class WorkWarnLog extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gongzuobaojing_layout);
        init();
    }
    private void init(){
        ListView lv_gongZuoBaoJingTongJi = (ListView)findViewById(R.id.lv_gongZuoBaoJingTongJi);
        Button worh_Back = (Button)findViewById(R.id.worh_Back);
        worh_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
