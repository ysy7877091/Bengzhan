package com.com.vanpeng.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.administrator.benzhanzidonghua.R;
import com.vanpeng.javabeen.BeiDouCarLieBiaoBeen;
import com.vanpeng.javabeen.ListItemClickHelp;

import java.util.List;

/**
 * Created by Administrator on 2017/2/6 0006.
 * 北斗车辆列表适配器
 */

public class BDListViewAdapter extends BaseAdapter implements SectionIndexer {
    private Context context;
    private List<BeiDouCarLieBiaoBeen> list;
    private ListItemClickHelp callback;
    private ImageLoader imageLoader;
    private int i=0;
    private RequestQueue queue;   //volley请求
    public BDListViewAdapter(Context context,List<BeiDouCarLieBiaoBeen> list,ListItemClickHelp callback){
        this.context=context;
        this.list=list;
        this.callback=callback;
        queue = Volley.newRequestQueue(context);
        imageLoader =new ImageLoader(queue, new BitmapCache());
    }
    public void updateListView(List<BeiDouCarLieBiaoBeen> list){
        this.list = list;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;
        i=position;
        if(convertView==null){
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView= inflater.inflate(R.layout.beidoulistviewadapter_layout,null);
            holder.DingWeiIcon = (ImageView) convertView.findViewById(R.id.iv_dingweiIcon);
            holder.carPai=(TextView)convertView.findViewById(R.id.car_pai);//车牌
            //holder.onLine=(TextView)convertView.findViewById(R.id.onLine);//是否在线
            holder.YouLiang=(TextView)convertView.findViewById(R.id.CarId);//姓名
            //holder.Time=(TextView)convertView.findViewById(R.id.BD_Time);//电话
           // holder.x=(TextView)convertView.findViewById(R.id.BD_x);
            //holder.y=(TextView)convertView.findViewById(R.id.BD_y);
            holder.car=(NetworkImageView)convertView.findViewById(R.id.car_photo);//车辆图片
            holder.in=(LinearLayout) convertView.findViewById(R.id.car_in);//定位图片
            holder.centerID=(LinearLayout)convertView.findViewById(R.id.centerID);//中心部分
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }
        /*holder.car.setImageResource(R.mipmap.zaixian_car);
        int height = holder.car.getHeight();
        int width=holder.car.getWidth();*/
        int section = getSectionForPosition(position);
        holder.car.setTag("http://beidoujieshou.sytxmap.com:5963/CarPic/"+list.get(i).getNUM().toString().trim()+".png");
        holder.carPai.setText(list.get(position).getCARNUM());
        //holder.onLine.setText(list.get(position).getONLINE());
        holder.YouLiang.setText(list.get(position).getNAME());
        //holder.car.setImageResource(R.mipmap.car_weixianshi);
        if(list.get(position).getONLINE().equals("在线")){//在线时车牌的颜色
            holder.carPai.setTextColor(context.getResources().getColor(R.color.blue));
            holder.YouLiang.setTextColor(context.getResources().getColor(R.color.blue));
            holder.DingWeiIcon.setImageResource(R.mipmap.zaixian_icon);
            //holder.car.setImageResource(R.mipmap.zaixian_car);
        }else{//不在线时车牌的颜色
            //holder.carPai.setTextColor(context.getResources().getColor(R.color.an));
           // holder.YouLiang.setTextColor(context.getResources().getColor(R.color.an));
            holder.carPai.setTextColor(context.getResources().getColor(R.color.hui));
            holder.YouLiang.setTextColor(context.getResources().getColor(R.color.hui));
            holder.DingWeiIcon.setImageResource(R.mipmap.buzaixian_icon);
            //holder.car.setImageResource(R.mipmap.car_weixianshi);
        }
        int height = holder.car.getHeight();
        int width=holder.car.getWidth();

       /* new ImageLoading() {
            @Override
            public void loadImage(ImageView imageView, Bitmap bitmap) {
                if(imageView.getTag()!=null && imageView.getTag().equals("http://beidoujieshou.sytxmap.com:5963/CarPic/"+list.get(i).getNUM().toString().trim()+".png")){
                    imageView.setImageBitmap(bitmap);
                    notifyDataSetChanged();
                }
            }
        }.imageLoading(holder.car,"http://beidoujieshou.sytxmap.com:5963/CarPic/"+list.get(i).getNUM().toString().trim()+".png",context,height,width);*/
        /*Bitmap bitmap  = imageLoading(position);
        if(bitmap!=null&&holder.car.getTag().equals("http://beidoujieshou.sytxmap.com:5963/CarPic/"+list.get(i).getNUM().toString().trim()+".png")){
            holder.car.setImageBitmap(bitmap);
        }*/
        //volley 异步加载图片
        //new DownImgTask(holder.car).imageLoading("http://beidoujieshou.sytxmap.com:5963/CarPic/"+list.get(i).getNUM().toString().trim()+".png",context,width,height);
        final View carIn_view = convertView;
        final int carIn_p = position;
        final int carIn_one = holder.in.getId();

        holder.in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(carIn_view,parent,carIn_p,carIn_one);
            }
        });
        final View centerID_view = convertView;//当前点击的条目
        final int centerID_p = position;//当前点击条目按钮的条目位置
        final int centerID_one = holder.centerID.getId();

        holder.centerID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(centerID_view,parent,centerID_p,centerID_one);
            }
        });
        //holder.y.setText(list.get(position).getY()+"");
        //holder.x.setText(list.get(position).getX()+"");
        //holder.Time.setText(list.get(position).getTELNUMBER());
        loadImageByImageLoader(holder.car,"http://beidoujieshou.sytxmap.com:5963/CarPic/"+list.get(i).getNUM().toString().trim()+".png");
        return convertView;
    }


    private static class ViewHolder{
        private TextView carPai;
        private TextView onLine;
        private TextView YouLiang;
        private TextView Time;
        private NetworkImageView car;
        private LinearLayout in;
        private LinearLayout centerID;
        private ImageView DingWeiIcon;
        private ImageView carPhoto;
    }
    //加载图片
    private void loadImageByImageLoader(NetworkImageView image, String goodsUrl) {

        //创建ImageLoader对象，参数（加入请求队列，自定义缓存机制）

        image.setDefaultImageResId(R.mipmap.car_weixianshi);
        image.setErrorImageResId(R.mipmap.car_weixianshi);
        image.setImageUrl(goodsUrl, imageLoader);

    }


    //自定义图片缓存BitmapCache
    private class BitmapCache implements ImageLoader.ImageCache {

        //使用安卓提供的缓存机制
        private LruCache<String , Bitmap> mCache;

        //重写构造方法
        private BitmapCache() {
            int maxSize = 10*1024*1024;  //缓存大小为10兆
            mCache = new LruCache<String ,Bitmap>(maxSize){
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getRowBytes()*value.getHeight();
                }
            };

        }


        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);//获取缓存中的图片
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url,bitmap);//放入缓存

        }
    }

// 搜索

    @Override
    public Object[] getSections() {
        return new Object[0];
    }
    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }

        return -1;
    }
    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    @Override
    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }
    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String  sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }
}
