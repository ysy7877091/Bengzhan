package com.com.vanpeng.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.benzhanzidonghua.R;
import com.vanpeng.javabeen.BengZhanClass;

import java.util.List;

/**
 * Created by User on 2016/12/11.
 * 监控和报警列表适配器
 */
public class BZAdapter extends BaseAdapter {
    private Context context;
    private List<BengZhanClass> listBengZhan;
    /*private List<BengZhanClass> mList;
    private Context context;


    public BZAdapter(Context context, List<BengZhanClass> List){
        this.mList = List;
        this.context = context;
    }*/
    //记录被选中的条目
    public BZAdapter(Context context,List<BengZhanClass> listBengZhan){
        this.context = context;
        this.listBengZhan=listBengZhan;
    }

    class ViewHolder {
        TextView btn_Name;
    }

    @Override
    public int getCount() {
        return listBengZhan.size();//mList.size()
    }

    @Override
    public Object getItem(int position) {
        return 0;//mList.get(position)
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Toast.makeText(context,"11111111111111",Toast.LENGTH_SHORT).show();
        ViewHolder holder = null;
        //BengZhanClass bz = mList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.bz_item_view, null);
            holder.btn_Name = (TextView) convertView.findViewById(R.id.tv_Name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.btn_Name.setText(listBengZhan.get(position).getName().trim());
        return convertView;
    }
}
