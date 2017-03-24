package com.com.vanpeng.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.benzhanzidonghua.R;
import com.vanpeng.javabeen.QiTiBaoJing;

import java.util.List;

/**
 * Created by wypqwer on 2016/12/26.
 */

public class qiTiAdapter extends BaseAdapter {

    private List<QiTiBaoJing> mList;
    private Context context;
    private String name;

    public qiTiAdapter(Context context, List<QiTiBaoJing> List,String name) {
        this.mList = List;
        this.context = context;
        this.name=name;
    }
    class ViewHolder{
        TextView tv_bengZhanName;
        TextView tv_shiJian;
        TextView tv_yyht;
        TextView tv_liuHuaQing;
        TextView tv_jiaWan;
        TextView tv_anQi;
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
        QiTiBaoJing qt = mList.get(position);
        if (convertView==null) {

            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_item, null);
            holder.tv_bengZhanName = (TextView) convertView.findViewById(R.id.tv_bengZhanName);
            holder.tv_shiJian = (TextView) convertView.findViewById(R.id.tv_shiJian);
            holder.tv_yyht = (TextView) convertView.findViewById(R.id.tv_yyht);
            holder.tv_liuHuaQing = (TextView) convertView.findViewById(R.id.tv_liuHuaQing);
            holder.tv_jiaWan = (TextView) convertView.findViewById(R.id.tv_jiaWan);
            holder.tv_anQi = (TextView) convertView.findViewById(R.id.tv_anQi);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        String LiuHuaQing = String.valueOf(qt.getLiuHuaQing()).trim();
        String YiYangHuaTan = String.valueOf(qt.getYiYangHuaTan()).trim();
        String JiaWan = String.valueOf(qt.getJiaWan()).trim();
        String AnQi=String.valueOf(qt.getAnQi()).trim();

            if(LiuHuaQing.equals("65535")){
                holder.tv_liuHuaQing.setText("");
            }else{
                holder.tv_liuHuaQing.setText(String.valueOf(qt.getLiuHuaQing()).trim());
            }
            if(YiYangHuaTan.equals("65535")){
                holder.tv_yyht.setText("");
            }else{
                holder.tv_yyht.setText(String.valueOf(qt.getYiYangHuaTan()).trim());
            }
            if(JiaWan.equals("65535")){
                holder.tv_jiaWan.setText("");
            }else{
                holder.tv_jiaWan.setText(String.valueOf(qt.getJiaWan()).trim());
            }
            if(AnQi.equals("65535")){
                holder.tv_anQi.setText("");
            }
            else{
                holder.tv_anQi.setText(String.valueOf(qt.getAnQi()).trim());
            }
            holder.tv_bengZhanName.setText(name);
            holder.tv_shiJian.setText(qt.getTime().trim());
            //holder.tv_yyht.setText(String.valueOf(qt.getYiYangHuaTan()).trim());
            //holder.tv_liuHuaQing.setText(String.valueOf(qt.getLiuHuaQing()).trim());
            //holder.tv_jiaWan.setText(String.valueOf(qt.getJiaWan()).trim());
            //holder.tv_anQi.setText(String.valueOf(qt.getAnQi()).trim());

        return convertView;
    }
}
