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
 * Created by Administrator on 2017/1/13 0013.
 * 泵站popwindow
 */

public class BZ_Pop_Listview extends BaseAdapter{
    private List<BengZhanClass> list;
    private Context context;
    public  BZ_Pop_Listview(List<BengZhanClass> list,Context context){
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder hodler=null;
        if(view==null){
            hodler = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.bz_pop_listview_layout,null);
            hodler.BZ_Name=(TextView)view.findViewById(R.id.bz_Pop_Name);
            view.setTag(hodler);
        }else{
            hodler = (ViewHolder) view.getTag();
        }
        hodler.BZ_Name.setText(list.get(i).getName());
        return view;
    }
    private class ViewHolder{
        private TextView BZ_Name;
    }
}
