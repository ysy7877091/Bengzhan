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

/**
 * Created by Administrator on 2017/3/26.
 */

public abstract  class ImageLoading {

    private ImageView imageView;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bitmap bitmap = (Bitmap) msg.obj;
            loadImage(imageView, bitmap);
        }
    };


    public abstract void loadImage(ImageView imageView, Bitmap bitmap);

    //加载网络上的图片
    public void imageLoading(ImageView imageView, String Url, Context context,int height,int width) {
        this.imageView  =imageView;
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
                }, width, height, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mQueue.add(imageRequest);
    }


}
