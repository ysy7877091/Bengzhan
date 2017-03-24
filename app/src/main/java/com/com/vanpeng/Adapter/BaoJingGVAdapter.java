package com.com.vanpeng.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.benzhanzidonghua.R;

import java.util.List;

/**
 * Created by Administrator on 2017/1/4.
 */

public class BaoJingGVAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;
    public BaoJingGVAdapter(Context context, List<String> list){
        this.context=context;
        this.list=list;
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        //BengZhanClass bz = mList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.bz_item_view, null);
            holder.BJ_Name = (TextView) convertView.findViewById(R.id.tv_Name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.BJ_Name.setText(list.get(position));
        return convertView;
    }
    class ViewHolder {
        TextView BJ_Name;
    }
}
