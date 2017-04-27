package com.com.vanpeng.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.benzhanzidonghua.R;
import com.vanpeng.javabeen.BengZhanClass;
import com.vanpeng.javabeen.YouLiangBaoJiangTongJi;

import java.util.List;

/**
 * Created by Administrator on 2017/3/25.
 */

public class YouLiangBaoJiangAdapter extends BaseAdapter {

    private Context context;
    private List<BeiDouOilWarnJavaBeen> YouLiangBaoJiangTongJi;


    public YouLiangBaoJiangAdapter(Context context,List<BeiDouOilWarnJavaBeen> YouLiangBaoJiangTongJi){
        this.context = context;
        this.YouLiangBaoJiangTongJi=YouLiangBaoJiangTongJi;
    }

    class ViewHolder {

        TextView tv_youliangbaojing_youliang;
        TextView tv_youliangbaojing_shijian;
        TextView tv_youliangbaojing_carNumber;
        ImageView iv_youliang_zhuangtai;

    }
    @Override
    public int getCount() {
        return YouLiangBaoJiangTongJi.size();
    }

    @Override
    public Object getItem(int position) {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        //BengZhanClass bz = mList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.youliangbaojing_listview, null);
            holder.tv_youliangbaojing_carNumber = (TextView) convertView.findViewById(R.id.tv_youliangbaojing_carNumber);
            holder.tv_youliangbaojing_shijian = (TextView) convertView.findViewById(R.id.tv_youliangbaojing_shijian);
            holder.tv_youliangbaojing_youliang = (TextView) convertView.findViewById(R.id.tv_youliangbaojing_youliang);
            holder.iv_youliang_zhuangtai= (ImageView) convertView.findViewById(R.id.iv_youliang_zhuangtai);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        if(YouLiangBaoJiangTongJi.get(position).getUpOrDown().contains("↑")){
            holder.iv_youliang_zhuangtai.setImageResource(R.mipmap.youliang_lvse);
            if(!YouLiangBaoJiangTongJi.get(position).getUpOrDown().equals("anyType{}")) {
                holder.tv_youliangbaojing_youliang.setText(YouLiangBaoJiangTongJi.get(position).getUpOrDown());
            }
            holder.tv_youliangbaojing_youliang.setTextColor(context.getResources().getColor(R.color.green));
        }else{
            holder.iv_youliang_zhuangtai.setImageResource(R.mipmap.youliang_red);
            if(!YouLiangBaoJiangTongJi.get(position).getUpOrDown().equals("anyType{}")) {
                holder.tv_youliangbaojing_youliang.setText(YouLiangBaoJiangTongJi.get(position).getUpOrDown());
            }
            holder.tv_youliangbaojing_youliang.setTextColor(context.getResources().getColor(R.color.red));
        }
        if(!YouLiangBaoJiangTongJi.get(position).getTime().equals("anyType{}")) {
            holder.tv_youliangbaojing_shijian.setText(YouLiangBaoJiangTongJi.get(position).getTime());
        }
        if(!YouLiangBaoJiangTongJi.get(position).getCarNum().equals("anyType{}")){
            holder.tv_youliangbaojing_carNumber.setText(YouLiangBaoJiangTongJi.get(position).getCarNum());
        }



        return convertView;
    }
}
