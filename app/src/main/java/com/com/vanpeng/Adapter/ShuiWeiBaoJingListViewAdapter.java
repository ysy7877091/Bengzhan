package com.com.vanpeng.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.benzhanzidonghua.R;
import com.vanpeng.javabeen.ShuiWeiBaoJing;

import java.util.List;

/**
 * Created by Administrator on 2017/1/5.
 */

public class ShuiWeiBaoJingListViewAdapter extends BaseAdapter {
    private List<ShuiWeiBaoJing> list;
    private String name;
    private Context context;
    public ShuiWeiBaoJingListViewAdapter(String name, List<ShuiWeiBaoJing> list, Context context){
       this.list=list;
        this.name=name;
        this.context=context;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder=null;
        if(view==null){
            holder=new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            view=inflater.inflate(R.layout.shuiweibaojingadapter_layout,null);
            holder.tv_bengZhanName=(TextView)view.findViewById(R.id.tv_bengZhanName);
            holder.tv_shiJian=(TextView)view.findViewById(R.id.tv_shiJian);
            holder.tv_shuiWei=(TextView)view.findViewById(R.id.tv_shuiWei);
            view.setTag(holder);
        }else{
            holder=(ViewHolder) view.getTag();
        }
        holder.tv_bengZhanName.setText(name);
        holder.tv_shiJian.setText(list.get(i).getShiJian());
        holder.tv_shuiWei.setText(list.get(i).getShuiWei());
        holder.tv_shuiWei.setTextColor(context.getResources().getColor(R.color.red));
        return view;
    }
    private class ViewHolder{
        private TextView tv_bengZhanName;
        private TextView tv_shiJian;
        private TextView tv_shuiWei;
    }
}
