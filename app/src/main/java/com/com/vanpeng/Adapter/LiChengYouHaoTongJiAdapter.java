package com.com.vanpeng.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.benzhanzidonghua.R;
import com.vanpeng.javabeen.BengZhanClass;
import com.vanpeng.javabeen.LiChengYouHaoTongJiJavaBean;

import java.util.List;

/**
 * Created by Administrator on 2017/3/25.
 */

public class LiChengYouHaoTongJiAdapter extends BaseAdapter {
    private Context mcontext;
    private List<LiChengYouHaoTongJiJavaBean> listLiChengYouHaoTongJi;


    public LiChengYouHaoTongJiAdapter(Context mcontext,List<LiChengYouHaoTongJiJavaBean> listLiChengYouHaoTongJi){
        this.mcontext = mcontext;
        this.listLiChengYouHaoTongJi=listLiChengYouHaoTongJi;
    }
    @Override
    public int getCount() {
        return listLiChengYouHaoTongJi.size();
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
            convertView = LayoutInflater.from(mcontext).inflate(R.layout.lichengyouhaoonclick_listview, null);
            holder.tv_LiCheng_CarNumber = (TextView) convertView.findViewById(R.id.tv_LiCheng_carNumber);
            holder.tv_LiCheng_YouHao = (TextView) convertView.findViewById(R.id.tv_LiCheng_youHao);
            holder.tv_LiCheng_PingJunYouHao = (TextView) convertView.findViewById(R.id.tv_LiCheng_pingJunYouHao);
            holder.tv_LiCheng_LiCheng = (TextView) convertView.findViewById(R.id.tv_LiCheng_liCheng);
            holder.tv_LiCheng_GongZuoShiJian = (TextView) convertView.findViewById(R.id.tv_LiCheng_gongZuoShiJian);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        Log.e("warn","里程:"+listLiChengYouHaoTongJi.get(position).getLiChengCarNumber().toString().trim()+";"+listLiChengYouHaoTongJi.get(position).getYouHao().toString().trim()
                +";"+listLiChengYouHaoTongJi.get(position).getPingJunYouHao().toString().trim() +";"+listLiChengYouHaoTongJi.get(position).getLiCheng().toString().trim()+";"+
                listLiChengYouHaoTongJi.get(position).getGongZuoShiJian().toString().trim());
        holder.tv_LiCheng_CarNumber.setText(listLiChengYouHaoTongJi.get(position).getLiChengCarNumber().toString().trim());
        holder.tv_LiCheng_YouHao.setText(listLiChengYouHaoTongJi.get(position).getYouHao().toString().trim()+"L");
        holder.tv_LiCheng_PingJunYouHao.setText(listLiChengYouHaoTongJi.get(position).getPingJunYouHao().toString().trim()+"L");
        holder.tv_LiCheng_LiCheng.setText(listLiChengYouHaoTongJi.get(position).getLiCheng().toString().trim()+"公里");
        holder.tv_LiCheng_GongZuoShiJian.setText(listLiChengYouHaoTongJi.get(position).getGongZuoShiJian().toString().trim());

        return convertView;
    }
    static class ViewHolder {
        TextView tv_LiCheng_CarNumber;
        TextView tv_LiCheng_YouHao;
        TextView tv_LiCheng_PingJunYouHao;
        TextView tv_LiCheng_LiCheng;
        TextView tv_LiCheng_GongZuoShiJian;

    }
}
