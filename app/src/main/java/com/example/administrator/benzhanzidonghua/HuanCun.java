
package com.example.administrator.benzhanzidonghua;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by Administrator on 2017/4/21.
 */


public class HuanCun {
    private Context context;
    // 内存缓存默认 5M
    static final int MEM_CACHE_DEFAULT_SIZE = 10 * 1024 * 1024;

    // 一级内存缓存基于 LruCache
    private LruCache<String, Bitmap> memCache;

    public HuanCun(Context context) {
        this.context = context;
        initMemCache();
    }


    /**
     * 初始化内存缓存
     */

    private void initMemCache() {
        memCache = new LruCache<String, Bitmap>(MEM_CACHE_DEFAULT_SIZE) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes()*bitmap.getHeight();
            }
        };
    }


    /**
     * 从内存缓存中拿
     *
     * @param url
     */

    public Bitmap getBitmapFromMem(String url) {
        return memCache.get(url);
    }


    /**
     * 加入到内存缓存中
     *
     * @param url
     * @param bitmap
     */

    public void putBitmapToMem(String url, Bitmap bitmap) {
        memCache.put(url, bitmap);
    }


    /**
     * 从 url 加载图片
     *
     * @param imageView
     * @param imageUrl
     */

    public void loadImage(ImageView imageView, String imageUrl) {
        // 先从内存中拿
        Bitmap bitmap = getBitmapFromMem(imageUrl);//imageUrl存入内存中的路径

        if (bitmap != null) {
            Log.i("leslie", "image exists in memory");
            if (imageView.getTag() != null && imageView.getTag().equals(imageUrl)) {
                imageView.setImageBitmap(bitmap);
            }
        }


        // 内存没有再从网络下载
        if (!TextUtils.isEmpty(imageUrl)) {
            // new ImageDownloadTask(imageView).execute(imageUrl);
            bitmap = getBitmap(imageView, imageUrl);

            if (imageView.getTag() != null && imageView.getTag().equals(imageUrl)) {
                imageView.setImageBitmap(bitmap);
            }

        }

    }

    private Bitmap getBitmap(ImageView iv, String url) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(url, Base64.DEFAULT);


            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
            bmpFactoryOptions.inSampleSize = 8;
            //把图片压缩成原来的四分之一大小
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length, bmpFactoryOptions);
            if (bitmap != null) {
                // 将图片加入到内存缓存中
                putBitmapToMem(url, bitmap);
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


  /*class ImageDownloadTask extends AsyncTask<String, Integer, Bitmap> {
        private String imageUrl;
        private ImageView imageView;

        public ImageDownloadTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                imageUrl = params[0];
                String key = hashKeyForDisk(imageUrl);
                // 下载成功后直接将图片流写入文件缓存

                Bitmap bitmap = getBitmapFromDisk(imageUrl);
                if (bitmap != null) {
                    // 将图片加入到内存缓存中
                    putBitmapToMem(imageUrl, bitmap);
                }

                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result != null) {
                // 通过 tag 来防止图片错位
                if (imageView.getTag() != null && imageView.getTag().equals(imageUrl)) {
                    imageView.setImageBitmap(result);
                }
            }
        }*/


        private File getDiskCacheDir(Context context, String uniqueName) {
            String cachePath;
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {
                cachePath = context.getExternalCacheDir().getPath();
            } else {
                cachePath = context.getCacheDir().getPath();
            }
            return new File(cachePath + File.separator + uniqueName);
        }

        private int getAppVersion(Context context) {
            try {
                PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                return info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return 1;
        }

        private String hashKeyForDisk(String key) {
            String cacheKey;
            try {
                final MessageDigest mDigest = MessageDigest.getInstance("MD5");
                mDigest.update(key.getBytes());
                cacheKey = bytesToHexString(mDigest.digest());
            } catch (NoSuchAlgorithmException e) {
                cacheKey = String.valueOf(key.hashCode());
            }
            return cacheKey;
        }

        private String bytesToHexString(byte[] bytes) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        }

}

