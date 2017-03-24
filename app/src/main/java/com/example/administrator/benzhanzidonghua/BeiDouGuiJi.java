package com.example.administrator.benzhanzidonghua;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import com.esri.android.map.MapView;
import java.util.Calendar;

/**
 * Created by Administrator on 2017/2/6 0006.
 * 北斗轨迹
 */

public class BeiDouGuiJi extends AppCompatActivity {
    private MapView mMapView;//地图
    private String Start_year="";
    private String Start_monthOfYear="";
    private String Start_dayOfMonth="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beidouguiji_layout);
        CommonMethod.setStatuColor(BeiDouGuiJi.this, R.color.blue);
        Button selectTime= (Button)findViewById(R.id.selectTime);
        selectTime.setOnClickListener(new BeiDouGuiJiListener());
        Button BDButton = (Button)findViewById(R.id.BDButton);
        BDButton.setOnClickListener(new BeiDouGuiJiListener());
        mMapView=(MapView)findViewById(R.id.mapView);//地图控件

    }
    private class BeiDouGuiJiListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.selectTime:
                                        DialogTime();
                                        break;
                case R.id.BDButton:finish();break;
            }
        }
    }
  private void DialogTime(){

      Calendar c = Calendar.getInstance();
      int Now_year = c.get(Calendar.YEAR);
      int Now_monthOfYear = c.get(Calendar.MONTH)+1;
      int Now_dayOfMonth= c.get(Calendar.DAY_OF_MONTH);
      Start_year=String.valueOf(Now_year);
      Start_monthOfYear=String.valueOf(Now_monthOfYear);
      Start_dayOfMonth= String.valueOf(Now_dayOfMonth);
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      LayoutInflater inflater = LayoutInflater.from(this);
      View view = inflater.inflate(R.layout.baojingtime_layout, null);
      builder.setView(view);
      DatePicker dp = (DatePicker) view.findViewById(R.id.dp);
      //初始化日期时间
      dp.init(Now_year ,Now_monthOfYear,Now_dayOfMonth, new DatePicker.OnDateChangedListener() {
          //改变后的时间 时间改变后才执行这个方法
          public void onDateChanged(DatePicker view, int year,
                                    int monthOfYear, int dayOfMonth) {
              Start_year = String.valueOf(year);
              Start_monthOfYear = String.valueOf(monthOfYear + 1);
              Start_dayOfMonth = String.valueOf(dayOfMonth);
          }
      });

      builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){

          @Override
          public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
          }
      });
      builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
          }
      });
      builder.show();
  }
}
