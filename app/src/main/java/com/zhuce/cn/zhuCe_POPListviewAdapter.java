package com.zhuce.cn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.benzhanzidonghua.R;

import java.util.List;

/**
 * Created by wypqwer on 2017/3/31.
 */

public class zhuCe_POPListviewAdapter extends BaseAdapter {
    private List<zhuCe_POPListView> list;
    private Context context;
    public zhuCe_POPListviewAdapter(){

    }
    public zhuCe_POPListviewAdapter(Context context,List<zhuCe_POPListView> list){
        this.context=context;
        this.list=list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.pop_listiem,null);
            holder.tv=(TextView)convertView.findViewById(R.id.bd_tv);
            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder)convertView.getTag();
        }
        holder.tv.setText(list.get(position).getText());
        return convertView;
    }
    private class ViewHolder{
        private TextView tv;
    }
}
