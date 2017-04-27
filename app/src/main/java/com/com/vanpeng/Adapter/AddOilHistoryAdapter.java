package com.com.vanpeng.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.benzhanzidonghua.AddOilHistoryImageLoading;
import com.example.administrator.benzhanzidonghua.HuanCun;
import com.example.administrator.benzhanzidonghua.R;
import com.vanpeng.javabeen.AddOilHistoryBeen;

import java.util.List;

/**
 * Created by Administrator on 2017/4/1.
 */

public class AddOilHistoryAdapter extends BaseAdapter {
    private   HuanCun hc;
    private Context context;
    private List<AddOilHistoryBeen> list;
    public AddOilHistoryAdapter(Context context, List<AddOilHistoryBeen> list){
        this.context=context;
        this.list=list;
        hc =new HuanCun(context);
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
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.jiayoujilu_list, null);
            holder.photo_iv = (ImageView)convertView.findViewById(R.id.iv_jiayoujilu_person);

            holder.carNum = (TextView) convertView.findViewById(R.id.tv_jiayouJiLu_carNumber);

            holder.name= (TextView)convertView.findViewById(R.id.tv_jiaYouJiLu_Name);

            holder.oil=(TextView)convertView.findViewById(R.id.tv_jiayoujilu_shuliang);

            holder.money=(TextView)convertView.findViewById(R.id.tv_jiayoujilu_RMB);

            holder.time = (TextView)convertView.findViewById(R.id.tv_jiayoujilu_shiJian);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.photo_iv.setTag(list.get(position).getImage());
        holder.photo_iv.setImageDrawable(context.getResources().getDrawable(R.mipmap.renyuanxinxitouxiang));
        holder.carNum.setText(list.get(position).getCarNum());
        holder.name.setText(list.get(position).getName());
        holder.oil.setText(list.get(position).getOilNum()+"L");
        holder.money.setText(list.get(position).getMoney()+"元");
        holder.time.setText(list.get(position).getTime());

        //new HuanCun(context).loadImage(holder.photo_iv,list.get(position).getImage());

        if(!list.get(position).getImage().equals("kong")){
            //new AddOilHistoryImageLoading(holder.photo_iv).stringtoBitmap(list.get(position).getImage());
            hc.loadImage(holder.photo_iv,list.get(position).getImage());
        }
        return convertView;
    }

    static class ViewHolder {
        TextView carNum;
        ImageView photo_iv;
        TextView name;
        TextView oil;
        TextView money;
        TextView time;

    }
}
