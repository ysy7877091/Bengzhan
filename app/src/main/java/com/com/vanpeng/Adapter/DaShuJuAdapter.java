package com.com.vanpeng.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.benzhanzidonghua.R;
import com.vanpeng.javabeen.qiTiClass;

import java.util.List;

/**
 * Created by wypqwer on 2017/1/6.
 */

public class DaShuJuAdapter extends BaseAdapter {

    private List<qiTiClass> mList;
    private Context context;


    public DaShuJuAdapter(Context context, List<qiTiClass> List) {
        this.mList = List;
        this.context = context;
    }
    class ViewHolder{
        TextView tv_shiJian;
        TextView tv_yyht;
        TextView tv_liuHuaQing;
        TextView tv_jiaWan;
        TextView tv_anQi;
        TextView tv_yuliang;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        qiTiClass qt = mList.get(position);
        if (convertView==null) {

            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.dashujuadapter_layout, null);

            holder.tv_shiJian = (TextView) convertView.findViewById(R.id.tv_shiJian);
            holder.tv_yyht = (TextView) convertView.findViewById(R.id.tv_yyht);
            holder.tv_liuHuaQing = (TextView) convertView.findViewById(R.id.tv_liuHuaQing);
            holder.tv_jiaWan = (TextView) convertView.findViewById(R.id.tv_jiaWan);
            holder.tv_anQi = (TextView) convertView.findViewById(R.id.tv_anQi);
            holder.tv_yuliang = (TextView) convertView.findViewById(R.id.tv_yuliang);
            convertView.setTag(holder);

        }else{

            holder = (ViewHolder) convertView.getTag();

        }

        holder.tv_shiJian.setText(qt.getShiJian().trim());
        holder.tv_yuliang.setText(qt.getShuiwei().trim());
        holder.tv_yyht.setText(String.valueOf(qt.getYiYangHuaTan()).trim());
        holder.tv_liuHuaQing.setText(String.valueOf(qt.getLiuHuaQin()).trim());
        holder.tv_jiaWan.setText(String.valueOf(qt.getJiaWan()).trim());
        holder.tv_anQi.setText(String.valueOf(qt.getAnQi()).trim());

        return convertView;
    }
}