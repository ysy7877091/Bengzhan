package com.example.administrator.benzhanzidonghua;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/2/20 0020.
 * 北斗车辆列表搜索按钮进入的界面搜索
 *
 */

public class BeiDouSouSuo extends AppCompatActivity {
    private ImageButton carChaXun;
    private Button BD_ssButton;
    private EditText DSJ_Name;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carsousuo_layout);
        DSJ_Name = (EditText)findViewById(R.id.DSJ_Name);
        BD_ssButton = (Button)findViewById(R.id.BD_ssButton);
        BD_ssButton.setOnTouchListener(new BeiDouSouSuoListener());
        carChaXun=(ImageButton)findViewById(R.id.carChaXun);
        carChaXun.setOnTouchListener(new BeiDouSouSuoListener());

    }
    private class BeiDouSouSuoListener implements View.OnTouchListener{

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()){
                case R.id.carChaXun:if(event.getAction()==MotionEvent.ACTION_DOWN){
                                        carChaXun.setAlpha(0);
                                        }
                                        if(event.getAction()==MotionEvent.ACTION_UP){
                                            carChaXun.setAlpha(255);
                                        }
                                        if (DSJ_Name.getText().toString().trim().equals("请输入车牌号")||DSJ_Name.getText().toString().trim().equals("")){
                                            Toast.makeText(getApplicationContext(),"请输入车牌号",Toast.LENGTH_SHORT).show();
                                        }else {
                                            Intent intent = new Intent();
                                            intent.putExtra("value", DSJ_Name.getText().toString().trim());
                                            BeiDouSouSuo.this.setResult(1, intent);
                                        }
                                     break;
                case R.id.BD_ssButton:if(event.getAction()==MotionEvent.ACTION_DOWN){
                                            BD_ssButton.setAlpha(0);
                                            }
                                        if(event.getAction()==MotionEvent.ACTION_UP){
                                            BD_ssButton.setAlpha(1);
                                            }
                                        Intent intent = new Intent();
                                        intent.putExtra("value", DSJ_Name.getText().toString().trim());
                                        BeiDouSouSuo.this.setResult(2, intent);
                                        finish();
                                        break;
            }
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            //do something...
            Intent intent = new Intent();
            intent.putExtra("value",DSJ_Name.getText().toString().trim());
            BeiDouSouSuo.this.setResult(3, intent);
            finish();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
