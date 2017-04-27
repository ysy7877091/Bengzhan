package com.com.vanpeng.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.benzhanzidonghua.R;
import com.example.administrator.benzhanzidonghua.Text;
import com.vanpeng.javabeen.WorkWarnJavaBeen;

import java.util.List;

/**
 * Created by Administrator on 2017/3/28.
 */

public class WorkWarnListViewAdapter extends BaseAdapter {
    private Context context;
    private List<WorkWarnJavaBeen> list;
    public WorkWarnListViewAdapter(){}
    public WorkWarnListViewAdapter(Context context, List<WorkWarnJavaBeen > list){
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
        ViewHolder holder =null;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.gongzuobaojing_listview, null);
            holder.carNum_tv = (TextView)convertView.findViewById(R.id.tv_gongZuoBaoJingTongJi_carNumber);
            holder.name_tv = (TextView)convertView.findViewById(R.id.tv_gongZuoBaoJingTongJi_jiaShiYuan);
            holder.time_tv = (TextView)convertView.findViewById(R.id.tv_gongZuoBaoJingTongJi_shiJian);
            holder.dingWei_iv = (ImageView)convertView.findViewById(R.id.iv_gongZuoBaoJingTongJi_dingwei);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }


        return convertView;
    }
    static class ViewHolder{
        TextView carNum_tv;
        TextView name_tv;
        TextView time_tv;
        ImageView dingWei_iv;
    }
}
