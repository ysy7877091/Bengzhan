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
 * Created by Administrator on 2017/3/27.
 */

public class Select_CarNumListViewAdapter extends BaseAdapter{
    private Context context;
    private List<String> list;
    public Select_CarNumListViewAdapter(){}
    public Select_CarNumListViewAdapter(Context context, List<String> list){
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
            ViewHodler hodler;
        if(convertView==null){
            hodler = new ViewHodler();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView= inflater.inflate(R.layout.allcarnum_layout,null);
            hodler.tv= (TextView)convertView.findViewById(R.id.allCarNum);
            convertView.setTag(hodler);
        }else{
            hodler =(ViewHodler)convertView.getTag();
        }
        hodler.tv.setText(list.get(position));
        return convertView;
    }
    static class ViewHodler{
        TextView tv;
    }
}
