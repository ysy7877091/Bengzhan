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
import com.vanpeng.javabeen.BD_carPOPListView;

import java.util.List;

/**
 * Created by Administrator on 2017/2/28 0028.
 */

public class BD_POPListviewAdapter extends BaseAdapter {
    private List<BD_carPOPListView> list;
    private Context context;
    public BD_POPListviewAdapter(){

    }
    public BD_POPListviewAdapter(Context context,List<BD_carPOPListView> list){
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
            convertView = LayoutInflater.from(context).inflate(R.layout.bd_pop_listview_layout,null);
            holder.iv=(ImageView)convertView.findViewById(R.id.bd_iv);
            holder.tv=(TextView)convertView.findViewById(R.id.bd_tv);
            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder)convertView.getTag();
        }
        holder.iv.setImageResource(list.get(position).getImageview());
        holder.tv.setText(list.get(position).getText());
        return convertView;
    }
    private class ViewHolder{
        private ImageView iv;
        private TextView tv;
    }
}
