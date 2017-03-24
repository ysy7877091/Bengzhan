package com.dashuju;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.com.vanpeng.Adapter.BaoJingGVAdapter;
import com.example.administrator.benzhanzidonghua.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wypqwer on 2017/1/6.
 */

public class DaShuJUListActivity extends AppCompatActivity {
    private GridView gv;
    private String StartTime="";
    private String EndTime="";
    private List<String> ListID;//泵站id
    private List<String> list;//泵站名称
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashuju_layout);
        init();
    }
    private void init(){
        Button BZl_button = (Button)findViewById(R.id.BZl_button);
        BZl_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        gv=(GridView)findViewById(R.id.DaShuJu_gridView);
        setGridViewListData();
    }

    private void setGridViewListData(){
        Intent intent = getIntent();
        String val = intent.getStringExtra("val");
        StartTime = intent.getStringExtra("sub_StartTime");
        EndTime = intent.getStringExtra("sub_EndTime");
        if (val.toString().equals("999999")) {
            Toast.makeText(getApplicationContext(), "获取泵站失败,网络或者服务器异常", Toast.LENGTH_SHORT).show();
            return;
        }else{
            String[] objects = val.split("\\|");
            list = new ArrayList<>();
            ListID = new ArrayList<>();
            for (int i = 0; i < objects.length-10; i++) {
                if (objects[i].length() > 0) {
                    String[] values = objects[i].split(",");
                    if (values.length > 1) {
                        //泵站名
                        list.add(values[1].toString());
                        ListID.add(values[0].toString());
                    }
                }
            }
            BaoJingGVAdapter Adapter=new BaoJingGVAdapter(DaShuJUListActivity.this,list);
            gv.setAdapter(Adapter);
            gv.setOnItemClickListener(new DaShuJUListActivity.gvListener());
        }
    }

    private class gvListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(DaShuJUListActivity.this,simpleBZ.class);
            intent.putExtra("StartTime",StartTime);
            intent.putExtra("EndTime",EndTime);
            intent.putExtra("ID",ListID.get(i));
            intent.putExtra("Name",list.get(i));
            startActivity(intent);
        }
    }
}
