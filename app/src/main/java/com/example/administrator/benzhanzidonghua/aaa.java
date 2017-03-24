package com.example.administrator.benzhanzidonghua;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.viewpagerlunbo.RollPagerView;
import com.viewpagerlunbo.adapter.StaticPagerAdapter;

/**
 * Created by Administrator on 2017/3/23.
 */

public class aaa extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhuyefragment_layout);
        init();
    }
    private void init(){
        RollPagerView mRollPagerView = (RollPagerView)findViewById(R.id.mRollPagerView);//图片轮播
        rollPagerViewSet(mRollPagerView);
        LinearLayout ZhuYe_BZ = (LinearLayout)findViewById(R.id.ZhuYe_BZ);//泵站
        LinearLayout ZhuYe_BJ = (LinearLayout)findViewById(R.id.ZhuYe_BJ);//报警
        LinearLayout ZhuYe_JK = (LinearLayout)findViewById(R.id.ZhuYe_JK);//监控
        LinearLayout ZhuYe_Map = (LinearLayout)findViewById(R.id.ZhuYe_Map);//地图
        LinearLayout ZhuYe_LS= (LinearLayout)findViewById(R.id.ZhuYe_LS);//历史
        LinearLayout ZhuYe_DW = (LinearLayout)findViewById(R.id.ZhuYe_DW);//北斗定位
        LinearLayout ZhuYe_SWJC= (LinearLayout)findViewById(R.id.ZhuYe_SWJC);//水位监测
        LinearLayout ZhuYe_YLJC = (LinearLayout)findViewById(R.id.ZhuYe_YLJC);//雨量监测
        LinearLayout ZhuYe_YLLS= (LinearLayout)findViewById(R.id.ZhuYe_YLLS);//雨量历史
        LinearLayout ZhuYe_TuiChu= (LinearLayout)findViewById(R.id.ZhuYe_TuiChu);//雨量历史
    }
    //图片轮播 设置参数
    private void rollPagerViewSet(RollPagerView rollPagerView) {
        rollPagerView.setPlayDelay(3000);//*播放间隔
        rollPagerView.setAnimationDurtion(500);//透明度
        rollPagerView.setAdapter(new rollViewpagerAdapter());//配置适配器
    }
    //图片轮播 设置轮播的图片 执行轮播
    private class rollViewpagerAdapter extends StaticPagerAdapter {

        private int[] res={R.mipmap.banner1,R.mipmap.banner2,R.mipmap.banner3,};

        @Override
        public View getView(ViewGroup container, int position) {
            ImageView imageView=new ImageView(container.getContext());
            imageView.setImageResource(res[position]);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            return imageView;
        }
        @Override
        public int getCount() {
            return res.length;
        }
    }
}
