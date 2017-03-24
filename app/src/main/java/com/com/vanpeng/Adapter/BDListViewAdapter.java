package com.com.vanpeng.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.benzhanzidonghua.BeiDouPersonInformation;
import com.example.administrator.benzhanzidonghua.R;
import com.vanpeng.javabeen.BeiDouCarLieBiaoBeen;
import com.vanpeng.javabeen.ListItemClickHelp;

import java.util.List;

/**
 * Created by Administrator on 2017/2/6 0006.
 * 北斗车辆列表适配器
 */

public class BDListViewAdapter extends BaseAdapter{
    private Context context;
    private List<BeiDouCarLieBiaoBeen> list;
    private ListItemClickHelp callback;
    private int i=0;
    public BDListViewAdapter(Context context,List<BeiDouCarLieBiaoBeen> list,ListItemClickHelp callback){
        this.context=context;
        this.list=list;
        this.callback=callback;
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
            holder.carPai=(TextView)convertView.findViewById(R.id.CarId);//车牌
            holder.onLine=(TextView)convertView.findViewById(R.id.onLine);//是否在线
            holder.YouLiang=(TextView)convertView.findViewById(R.id.YouLiang);//姓名
            holder.Time=(TextView)convertView.findViewById(R.id.BD_Time);//电话
           // holder.x=(TextView)convertView.findViewById(R.id.BD_x);
            //holder.y=(TextView)convertView.findViewById(R.id.BD_y);
            holder.car=(ImageView)convertView.findViewById(R.id.car_photo);//车辆图片
            holder.in=(LinearLayout) convertView.findViewById(R.id.car_in);//进入图片
            holder.centerID=(LinearLayout)convertView.findViewById(R.id.centerID);//中心部分
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }
        holder.carPai.setText(list.get(position).getCARNUM());
        holder.onLine.setText(list.get(position).getONLINE());
        holder.YouLiang.setText(list.get(position).getNAME());

        final View carPhoto_view = convertView;
        final int carPhoto_p = position;
        final int carPhoto_one = holder.car.getId();

        holder.car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(carPhoto_view,parent,carPhoto_p,carPhoto_one);
            }
        });

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
        holder.Time.setText(list.get(position).getTELNUMBER());
        return convertView;
    }
    private static class ViewHolder{
        private TextView carPai;
        private TextView onLine;
        private TextView YouLiang;
        private TextView Time;
        private ImageView car;
        private LinearLayout in;
        private LinearLayout centerID;
    }

}
