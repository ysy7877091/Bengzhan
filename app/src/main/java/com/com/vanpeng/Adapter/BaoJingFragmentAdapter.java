package com.com.vanpeng.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.benzhanzidonghua.R;
import com.vanpeng.javabeen.BaoJingFragmentJavaBeen;

import java.util.List;

/**
 * Created by Administrator on 2017/3/28.
 */

public class BaoJingFragmentAdapter extends BaseAdapter {
    private List<BaoJingFragmentJavaBeen> list;
    private Context context;
    public BaoJingFragmentAdapter(){

    }
    public BaoJingFragmentAdapter(List<BaoJingFragmentJavaBeen> list,Context context){
                this.context=context;
                this.list=list;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView==null){
            holder = new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.jichusheshiadapter_layout,null);
            holder.tv_name = (TextView)convertView.findViewById(R.id.name_js);
            holder.num = (TextView)convertView .findViewById(R.id.num);
            holder.mile = (TextView)convertView.findViewById(R.id.mile);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_name.setText(list.get(position).getName());
        holder.num.setText(list.get(position).getNum());
        holder.mile.setText(list.get(position).getDanwei());
        return convertView;
    }
    static class ViewHolder{
        TextView tv_name;
        TextView num;
        TextView mile;
    }
}
