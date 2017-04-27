package com.example.administrator.benzhanzidonghua;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.lang.ref.SoftReference;

/**
 * Created by Administrator on 2017/3/26.
 */

public class DownImgTask {

    ImageView iv;
    String img_url;// 图片的地址

    public DownImgTask(ImageView iv) {
        super();
        this.iv = iv;
    }
    //加载网络上的图片
    public void imageLoading(String Url, Context context,int width,int height) {
        img_url=Url;
        RequestQueue mQueue = Volley.newRequestQueue(context);

        ImageRequest imageRequest = new ImageRequest(Url
                ,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        Message message = new Message();
                        message.obj = response;
                        handler.sendMessage(message);
                    }
                },width , height,Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(imageRequest);
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bitmap bitmap = (Bitmap) msg.obj;
            SoftReference< Bitmap> softRef=new SoftReference<Bitmap>(bitmap);//软引用 softRef.get()获取图片
            if (bitmap != null && img_url.equals(iv.getTag())) {
                iv.setImageBitmap(softRef.get());
            }
        }
    };
}
