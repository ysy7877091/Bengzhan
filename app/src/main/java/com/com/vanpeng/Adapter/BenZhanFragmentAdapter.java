package com.com.vanpeng.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.benzhanzidonghua.R;
import com.vanpeng.javabeen.BengZhanClass;

import java.util.List;

/**
 * Created by Administrator on 2016/12/21.
 */

public class BenZhanFragmentAdapter extends BaseAdapter {
    private Context context;
    private List<BengZhanClass> listBengZhan;
    public BenZhanFragmentAdapter(){}
    public BenZhanFragmentAdapter(Context context, List<BengZhanClass> listBengZhan){
        this.context=context;
        this.listBengZhan=listBengZhan;
    }
    @Override
    public int getCount() {
        return listBengZhan.size();
    }

    @Override
    public Object getItem(int i) {
        return listBengZhan.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.benzhanlistview_layout,null);
            holder.BZName=(TextView) convertView.findViewById(R.id.BZName);
            holder.bz_gridview=(GridView)convertView.findViewById(R.id.bz_gridView);
            holder.bz_LL=(LinearLayout)convertView.findViewById(R.id.bz_LL);
            holder.bz_firstLL=(LinearLayout)convertView.findViewById(R.id.bz_firstLL);
            holder.bz_et=(EditText)convertView.findViewById(R.id.bz_et);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        //获取屏幕的宽高
        WindowManager wm = (WindowManager)context
                .getSystemService(Context.WINDOW_SERVICE);

        int screenWidth =wm.getDefaultDisplay().getWidth();
        int screenHeigh = wm.getDefaultDisplay().getHeight();
        //设置控件宽（左）
        ViewGroup.LayoutParams mParams=(ViewGroup.LayoutParams)holder.bz_firstLL.getLayoutParams();
        mParams.width=screenWidth/3;
        holder.bz_firstLL.setLayoutParams(mParams);
        //设置控件宽（右 gridview）
        ViewGroup.LayoutParams HmParams=(ViewGroup.LayoutParams)holder.bz_LL.getLayoutParams();
        HmParams.width=screenWidth-screenWidth/3;
        holder.bz_LL.setLayoutParams(HmParams);
        //水位
        holder.bz_et.setText(String.valueOf(listBengZhan.get(position).getYeWei()/100)+"米");
        holder.bz_et.setTextSize(12);
        //设置泵站名称
        holder.BZName.setText("城管中心(Y)"+listBengZhan.get(position).getName());
        //设置gridview不能点击，否则与listview点击事件冲突
        holder.bz_gridview.setClickable(false);
        holder.bz_gridview.setPressed(false);
        holder.bz_gridview.setEnabled(false);
        //设置gridview数据
        holder.bz_gridview.setAdapter(new BenZhanAdapter(context,listBengZhan.get(position).getShuiBengClassList()));
        return convertView;
    }
    private class ViewHolder{
        private TextView BZName;//泵站名称
        private LinearLayout bz_LL;//右（gridview）布局
        private LinearLayout bz_firstLL;//左布局
        private GridView bz_gridview;
        private EditText bz_et;
    }
}
