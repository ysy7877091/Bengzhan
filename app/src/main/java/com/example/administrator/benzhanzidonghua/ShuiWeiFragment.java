package com.example.administrator.benzhanzidonghua;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/19.
 *水位fragment
 */

public class ShuiWeiFragment extends Fragment {
    private View view;
    private HorizontalBarChart mChart;
    private String value="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflater=LayoutInflater.from(getActivity());
        view =inflater.inflate(R.layout.shuiwei_layout,container,false);
        mChart=(HorizontalBarChart)view.findViewById(R.id.chart1);
        LinearLayout YuLiangLiShi_ll=(LinearLayout)view.findViewById(R.id.YuLiangLiShi_ll);
        while(true) {
            if (value.equals("")) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{break;}
        }
        ZhanShiMethod(value);
        //a(value);
        return view;
    }
//获取从activity传过来的水位数据
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        String value =((MainActivity) activity).getBenZhanData();
        //Toast.makeText(getActivity(),value,Toast.LENGTH_SHORT).show();
        this.value=value;

    }
    //展示数据制作表格
    private void ZhanShiMethod(String value){
        if (value.equals("999999")) {
            Toast.makeText(getActivity(), "获取泵站失败,网络或者服务器异常", Toast.LENGTH_SHORT).show();
        } else {
            String[] objects = value.split("\\|");
            Log.d("DEBUG", "获取泵站列表WebService结果_回调函数：" + value);
            String[] lbls = new String[objects.length-10];//示例说明
            Float[] it = new Float[objects.length-10];//水位


            String[] lbls1 = new String[ColorTemplate.SW_Simaple.length];//示例说明
            int[] myColors = new int[ColorTemplate.SW_Simaple.length];//示例颜色
            for(int i=0;i<ColorTemplate.SW_Simaple.length;i++){
                myColors[i]=ColorTemplate.SW_Simaple[i];
            }
            lbls1[0]="0-1";lbls1[1]="1-2";lbls1[2]="2-4";lbls1[3]="4-6";lbls1[4]="6-8";lbls1[5]="以上";


            float start = 0f;
            //
            List<Integer> list = new ArrayList<Integer>();//装载柱状图颜色
            ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
            ArrayList<String> xVals = new ArrayList<String>();//x轴数据
           mChart.getXAxis().setAxisMinValue(start);//获取x轴对象并设置x轴最小值

            for (int i = 0; i < objects.length-10; i++) {
                Log.d("DEBUG", "WebService结果_回调函数objects[i]：" + objects[i].toString());
                if (objects[i].length() > 0) {
                    String[] values = objects[i].split(",");
                    Log.d("DEBUG", "WebService结果_回调函数values.length：" + values.length);
                    if (values.length > 1) {
                        lbls[i] = values[1].toString();
                        it[i] = Float.parseFloat(values[5].toString());
                        xVals.add(values[1].toString());
                        yVals1.add(new BarEntry(it[i]/100,i));
                        ColorMethod(it[i]/100, list);
                        // new BarEntry(i + 1f, it[i] / 100)第二个参数为条形图实际大小（参数），第一个为条形图对应x轴的坐标
                        //第一个参数为y轴数值
                    }
                }
            }
            mChart.setDrawBarShadow(false);//柱状图没有数据的部分是否显示阴影效果
            mChart.setDrawValueAboveBar(true);//柱状图上面的数值是否在柱子上面
            mChart.setDescription("");// 数据描述
            mChart.setDescriptionPosition(90,15);//数据描述的位置
            mChart.setDescriptionColor(Color.GREEN);//数据的颜色
            mChart.setDescriptionTextSize(10);//数据字体大小

            // if more than 60 entries are displayed in the chart, no values will be
            // drawn
            mChart.setMaxVisibleValueCount(60); // 如果60多个条目显示在图表,drawn没有值
            //mChart.getXAxis().setDrawGridLines(false);//是否显示竖直标尺线
            // scaling can now only be done on x- and y-axis separately
            mChart.setPinchZoom(false);// 扩展现在只能分别在x轴和y轴
            mChart.setDrawGridBackground(false);// 是否显示表格颜色
            // 是否可以缩放
            mChart.setScaleEnabled(true);
            // 集双指缩放
            mChart.setPinchZoom(false);
            // 隐藏右边的坐标轴
            mChart.getAxisRight().setEnabled(false);
            //动画
            mChart.animateY(2500);
            //X轴
            mChart.setDrawGridBackground(false);
            XAxis xAxis = mChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            //xAxis.setTypeface(mTfLight);
            //xAxis.setLabelRotationAngle();
            xAxis.setSpaceBetweenLabels(1);
            xAxis.setDrawLabels(true);//是否显示X轴数值
            xAxis.setDrawGridLines(false);
            //xAxis.setGranularity(1f); // only intervals of 1 day
            //xAxis.setEnabled(false);

            //AxisValueFormatter custom = new MyAxisValueFormatter();
            //Y轴
            YAxis leftAxis = mChart.getAxisLeft();
            //leftAxis.setTypeface(mTfLight);
            leftAxis.setLabelCount(4, false);//设置y轴数据个数
            //leftAxis.setValueFormatter(custom);
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            leftAxis.setSpaceTop(15f);
            leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)



            //例示
            Legend l = mChart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);//RIGHT_OF_CHART
            l.setForm(Legend.LegendForm.CIRCLE);
            l.setFormSize(8f);//8f
            l.setTextSize(8f);
            l.setXEntrySpace(4f);
            l.setCustom(myColors, lbls1);//设置每个柱状图颜色缩小版提示 颜色和文字 例如此应用的右上角显示的一列各色圆形提示
            BarDataSet set1;
                //判断图表中原来是否有数据
            if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
                set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
                //set1 = new BarDataSet(yVals1, "");
                set1.setYVals(yVals1);
                mChart.getData().notifyDataChanged();
                mChart.notifyDataSetChanged();
            } else {
                set1 = new BarDataSet(yVals1, "水位监测");
                //set1.setColors(ColorTemplate.MATERIAL_COLORS);//设置柱状图的各种颜色，
                set1.setBarSpacePercent(50f);//设置柱间空白的宽度
                set1.setColors(list);
                //set1.setBarSpacePercent(10);
                // set custom labels and colors
                //设置柱状图颜色，第一个color.rgb为第一个柱状图颜色。以此类推
                //set1.setColors(new int[]{Color.rgb(255,241,226),Color.rgb(155,241,226)});
                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                dataSets.add(set1);
                //BarData data = new BarData(dataSets);
                BarData data = new BarData(xVals,dataSets);
                data.setValueTextSize(7f);
                data.setDrawValues(true);
                data.setValueTextColor(Color.BLACK);
                //data.setValueFormatter(new CustomerValueFormatter());
                //data.setValueTypeface(mTfLight);
                //data.setBarWidth(0.8f);
                //mChart.setExtraOffsets(0,0,0,200);//此种方法可以一次设置上下左右偏移量。根据自己数据哪个地方显示不全，对应调用方法。
                mChart.setData(data);// 设置数据
            }
        }
    }
 //fragment每次从隐藏到显示的时候调用的方法
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            //隐藏
        }else{
           //show出来
            mChart.clear();//清除图表
            ZhanShiMethod(value);//每次显示时重新加载数据
        }

    }
    //根据柱形图数据不同设置不同柱形图对应的颜色
    private void ColorMethod(float y,List<Integer> list){
        if(y>0&&y<=1){
            list.add(getResources().getColor(R.color.yl01));//灰色
        }else if(y>1&&y<=2){
            list.add(getResources().getColor(R.color.yl03));//绿色
        }else if(y>2&&y<=4){
            list.add(getResources().getColor(R.color.yl08));//黄色
        }else if(y>4&&y<=6){
            list.add(getResources().getColor(R.color.red));//红色
        }else if(y>6&&y<=8){
            list.add(getResources().getColor(R.color.yl16));//紫色
        }else{
            list.add(getResources().getColor(R.color.yl13));//棕色
        }
    }
}
