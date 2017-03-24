package com.com.vanpeng.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.benzhanzidonghua.R;
import com.vanpeng.javabeen.ShuiBengClass;

import java.util.List;

/**
 * Created by Administrator on 2016/12/29.
 */

public class BenZhanAdapter extends BaseAdapter {
    private Context context;
    private List<ShuiBengClass> listBengZhan;
    private int height;
    private int width;
    public BenZhanAdapter(){}
    public BenZhanAdapter(Context context,List<ShuiBengClass> listBengZhan){
        this.context=context;
        this.listBengZhan=listBengZhan;
    }
    @Override
    public int getCount() {
        return listBengZhan.size();
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
    public View getView(int postion, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            holder= new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.benzhangridview_layout,null);
            holder.iv=(ImageView)convertView.findViewById(R.id.bz_iv);
            holder.tv=(TextView) convertView.findViewById(R.id.bz_tv);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        if(listBengZhan.get(postion).getShuiBengName()==null) {
            holder.tv.setText("暂无此泵站信息");
            return convertView;
        }
        //根据状态值判断使用对应的图片 正常 报警 停止 故障
        String res = listBengZhan.get(postion).getZhuangTai();
        Log.e("warn","泵站状态"+res);
        if(res!=null) {
            holder.tv.setText(listBengZhan.get(postion).getShuiBengName()+"站状态");
            if (res.equals("8")) {//正常状态
                holder.iv.setImageResource(R.mipmap.zhuangtai_zhengchang);
            } else if (res.equals("4")) {//报警
                holder.iv.setImageResource(R.mipmap.zhuangtai_baojing);
            } else if(res.equals("16")){//停止
                holder.iv.setImageResource(R.mipmap.zhuangtai_tingzhi);
            }else{
                holder.iv.setImageResource(R.mipmap.zhuangtai_guzhang);
            }
        }else {
            holder.tv.setText(listBengZhan.get(postion).getShuiBengName()+"站状态");
            holder.iv.setImageResource(R.mipmap.zhuangtai_guzhang);
        }

        return convertView;
    }
    private class ViewHolder{
        private ImageView iv;
        private TextView tv;
    }
}
