package com.example.administrator.benzhanzidonghua;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.com.vanpeng.Adapter.BaoJingGVAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/22.
 * 报警列表activity
 */

public class BaoJingListActivity extends AppCompatActivity {
    private GridView gv;
    private String StartTime="";
    private String EndTime="";
    private List<String> ListID;//泵站id
    private List<String> list;//泵站名称
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.baojinglist_layout);
        init();
    }
    private void init(){
        Button BJL_button = (Button)findViewById(R.id.BJL_button);
        BJL_button.setOnClickListener(new BaoJingListener());
        /*ImageView back=(ImageView)findViewById(R.id.BaoJing_back);
        back.setOnClickListener(new BaoJingListener());*/
        gv=(GridView)findViewById(R.id.BJ_gridView);
        gv.setOnItemClickListener(new gvListener());
        setGridViewListData();
    }
    private String getIntentDate(){
        Intent intent = getIntent();
        String val = intent.getStringExtra("val");
        StartTime = intent.getStringExtra("sub_StartTime");
        EndTime = intent.getStringExtra("sub_EndTime");
        return val;
    }
    private void setGridViewListData(){
        String val = getIntentDate();
        if (val.toString().equals("999999")) {
            Toast.makeText(getApplicationContext(), "获取泵站失败,网络或者服务器异常", Toast.LENGTH_SHORT).show();
            return;
        }else{
            String[] objects = val.split("\\|");
           list = new ArrayList<String>();
            ListID = new ArrayList<String>();
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
            for(String s:ListID){
                Log.e("warn",s);
            }
            BaoJingGVAdapter Adapter=new BaoJingGVAdapter(BaoJingListActivity.this,list);
            gv.setAdapter(Adapter);
        }
    }
    private class BaoJingListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            finish();
        }
    }
    private class gvListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(BaoJingListActivity.this,BaoJingMeauActivity.class);
            intent.putExtra("StartTime",StartTime);
            intent.putExtra("EndTime",EndTime);
            intent.putExtra("ID",ListID.get(i));
            intent.putExtra("Name",list.get(i));
            Log.e("warn",String.valueOf(i)+":"+ListID.get(i));
            startActivity(intent);
        }
    }
}
