package com.com.vanpeng.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.benzhanzidonghua.R;
import com.vanpeng.javabeen.ShuiBengClass;

import java.util.List;

import static com.example.administrator.benzhanzidonghua.R.id.tv_bengZhanID;
import static com.example.administrator.benzhanzidonghua.R.id.tv_bengZhanName;
import static com.example.administrator.benzhanzidonghua.R.id.tv_dianliu;

/**
 * Created by wypqwer on 2016/12/20.
 */

public class MyAdapterShuiBeng extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private String inflater;
    private List<ShuiBengClass> shuiBengClassList;

    public MyAdapterShuiBeng(Context _context, List<ShuiBengClass> _shuiBengClassList) {

        this.context = _context;
        layoutInflater = (LayoutInflater) this.context.getSystemService(inflater);
        shuiBengClassList = _shuiBengClassList;
    }

    @Override
    public int getCount() {return shuiBengClassList.size();}

    @Override
    public Object getItem(int position) {return shuiBengClassList.get(position);}

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodler hodler =null;
        if (convertView == null) {
            hodler= new ViewHodler();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.bz_item_listview, null);
            hodler.tv_bengZhanID = (TextView) convertView.findViewById(tv_bengZhanID);
            hodler.tv_dianliu = (TextView) convertView.findViewById(tv_dianliu);
            hodler.tv_bengZhanName = (TextView) convertView.findViewById(tv_bengZhanName);
            hodler.imageView = (ImageView) convertView.findViewById(R.id.imgTmp1);
            convertView.setTag(hodler);
        }else{
            hodler=(ViewHodler)convertView.getTag();
        }
        if(shuiBengClassList.get(position).getShuiBengName()==null){//无泵站时
            LinearLayout BZ_state = (LinearLayout)convertView.findViewById(R.id.BZ_state);
            BZ_state.setVisibility(View.GONE);
            return convertView;
        }
        hodler.tv_bengZhanName.setText(shuiBengClassList.get(position).getShuizhanName());
        hodler.tv_bengZhanID.setText(shuiBengClassList.get(position).getShuiBengName() + "    ");

        if (shuiBengClassList.get(position).getDianLiu() != null) {
            hodler.tv_dianliu.setText(shuiBengClassList.get(position).getDianLiu());
        } else {
            hodler.tv_dianliu.setText("0");
        }

        if (shuiBengClassList.get(position).getZhuangTai() != null) {
            if (shuiBengClassList.get(position).getZhuangTai().equals("8")) {
                hodler.imageView.setImageResource(R.mipmap.zhuangtai_zhengchang);
            } else if(shuiBengClassList.get(position).getZhuangTai().equals("4")) {
                hodler.imageView.setImageResource(R.mipmap.zhuangtai_baojing);
            }else if(shuiBengClassList.get(position).getZhuangTai().equals("16")){
                hodler.imageView.setImageResource(R.mipmap.zhuangtai_tingzhi);
            }else {
                hodler.imageView.setImageResource(R.mipmap.zhuangtai_guzhang);
            }
        } else {
            hodler.imageView.setImageResource(R.mipmap.zhuangtai_guzhang);
        }
        return convertView;
    }
    private class ViewHodler{
        private TextView tv_bengZhanID;
        private TextView tv_dianliu;
        private TextView tv_bengZhanName;
        private ImageView imageView;
    }
}