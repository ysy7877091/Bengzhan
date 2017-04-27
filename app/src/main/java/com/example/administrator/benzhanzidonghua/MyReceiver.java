package com.example.administrator.benzhanzidonghua;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2017/3/31.
 */

public class MyReceiver extends BroadcastReceiver {
    private String carNum;
    private String ALLCARNUM;
    private BRInteraction brInteraction;
    @Override
    public void onReceive(Context context, Intent intent) {
        /*if(intent.getAction().equals("com.neter.broadcast.receiver.SendDownXMLBroadCast")){
            carNum=intent.getStringExtra("CARNUM");
            ALLCARNUM = intent.getStringExtra("ALLcarNum");
            brInteraction.setText(carNum, ALLCARNUM);
        }*/
    }

    public interface BRInteraction {
        public void setText(String content,String str);
    }
    public void setBRInteractionListener(BRInteraction brInteraction) {
        this.brInteraction = brInteraction;
    }
}
