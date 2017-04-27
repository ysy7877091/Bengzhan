package com.example.administrator.benzhanzidonghua;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.vanpeng.javabeen.JiangYuFragmentBeen;
import com.vanpeng.javabeen.PublicInterface;
import com.vanpeng.javabeen.RequestWebService;
import com.vanpeng.javabeen.StringTemplate;
import com.vanpeng.javabeen.YuLiangClass;

import java.util.ArrayList;
import java.util.List;

import static android.os.Build.ID;

/**
 * Created by Administrator on 2016/12/19.
 * 雨量fragment
 */

public class YuLiangFragment extends Fragment implements PublicInterface{
    private View view;
    private String serviceURL = Path.get_WebServicesYLURL();
    private LinearLayout lin_Rainfall;//降雨量title
    private TextView tv_Amount, tv_Average;//总降雨量，平均降雨量
    private HorizontalBarChart barChart;
    private MyProgressDialog progressDialog=null;
    private List<JiangYuFragmentBeen> list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflater=LayoutInflater.from(getActivity());
        view=inflater.inflate(R.layout.yulianglishifragment_layout,container,false);
        init ();
        YLRequest();
        return view;
    }
    //控件对象
    private void init (){
        LinearLayout YuLiang_ll=(LinearLayout)view.findViewById(R.id.YuLiang_ll);
        YuLiang_ll.setVisibility(View.GONE);
        barChart=(HorizontalBarChart)view.findViewById(R.id.YLchart);
        lin_Rainfall = (LinearLayout)view.findViewById(R.id.lin_Rainfall);
        lin_Rainfall.setVisibility(View.VISIBLE);
        tv_Amount = (TextView)view.findViewById(R.id.tv_Amount);
        tv_Average = (TextView)view.findViewById(R.id.tv_Average);
    }
    //请求雨量数据
    private void YLRequest(){
        /*if(getActivity()!=null){
            progressDialog =new MyProgressDialog(getActivity(),false,"正在加载中...");
        }*/
        //new Thread(networkGetYuLiangInfor).start();
        progressDialog = new MyProgressDialog(getActivity(),false,"正在加载中...");
        new Hour24SumJiangYuData().getShopsData(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(progressDialog !=null){
            if(progressDialog .isShowing()){
                progressDialog .dismiss();
            }
        }
    }

    //每次切换重新加载图表，保证数据的及时准确
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            //隐藏
        }else{
            //显示
            barChart.clear();//清除图表
            YLRequest();//每次显示时重新加载数据
        }
    }
    //根据柱形图数据不同设置不同柱形图对应的颜色
    private void ColorMethod(float y,List<Integer> list){
        if (y > 300) {
            list.add(getResources().getColor(R.color.yl17));
        } else if (y >= 200) {
            list.add(getResources().getColor(R.color.yl16));
        } else if (y >= 150) {
            list.add(getResources().getColor(R.color.yl15));
        } else if (y >= 130) {
            list.add(getResources().getColor(R.color.yl14));
        } else if (y >= 110) {
            list.add(getResources().getColor(R.color.yl13));
        } else if (y >= 90) {
            list.add(getResources().getColor(R.color.yl12));
        } else if (y >= 70) {
            list.add(getResources().getColor(R.color.yl11));
        } else if (y >= 50) {
            list.add(getResources().getColor(R.color.yl10));
        } else if (y >= 40) {
            list.add(getResources().getColor(R.color.yl09));
        } else if (y >= 30) {
            list.add(getResources().getColor(R.color.yl08));
        } else if (y >= 20) {
            list.add(getResources().getColor(R.color.yl07));
        } else if (y >= 15) {
            list.add(getResources().getColor(R.color.yl06));
        } else if (y >= 10) {
            list.add(getResources().getColor(R.color.yl05));
        } else if (y >= 6) {
            list.add(getResources().getColor(R.color.yl04));
        } else if (y >= 2) {
            list.add(getResources().getColor(R.color.yl03));
        } else if (y >= 1) {
            list.add(getResources().getColor(R.color.yl02));
        }else if(y>=0){
            list.add(getResources().getColor(R.color.yl01));
        }
    }
    private void closeDialog(){
        if(progressDialog !=null){

                progressDialog .dismiss();

        }
    }
    //请求成功的回调
    @Override
    public void onGetDataSuccess(List<JiangYuFragmentBeen> list) {
        this.list=list;
        Message msg = Message.obtain();
        msg.obj = "1";
        handler.sendMessage(msg);
    }

    //请求失败的回调
    @Override
    public void onGetDataError(String errmessage) {
       Message msg = Message.obtain();
        msg.obj = errmessage;
        handler.sendMessage(msg);

    }
    //请求无数据的回调
    @Override
    public void onEmptyData(String Emptymessage) {
        Message msg = Message.obtain();
        msg.obj =Emptymessage;
        handler.sendMessage(msg);

    }
    private void showData(){
        if(list!=null&list.size()>0){
            float Amount = 0;//总降雨量
            float Average = 0;//平均降雨量
            List<Integer> list1 = new ArrayList<Integer>();//装载柱状图颜色
            Float[] yl = new Float[list.size()];//雨量
            String[] appName = new String[list.size()];//区域名
            float start = 0f;
            ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
            barChart.getXAxis().setAxisMinValue(start);

            int len = StringTemplate.YLStringTemplate.length+1;//数据长度

            int[] myColors = new int[len];//取颜色长度
            String[] lbls = new String[len];//取比量值长度

            for (int i = 0; i < len; i++) {//取数据
                if(i==len-1){
                    lbls[i] = "警戒线";
                    myColors[i] = Color.rgb(255,33,33);
                }else{
                    lbls[i] = StringTemplate.YLStringTemplate[i];
                    myColors[i] = ColorTemplate.YL_Simaple[i];
                }
            }
            int j=0;
            for (int i = 0; i<list.size(); i++) {

                    if ( list.size()> 1) {
                        appName[list.size()-1-i] =list.get(i).getDevName();//区域名
                        yl[i] = Float.parseFloat(list.get(list.size()-1-i).getValueX());//数据
                        Amount = Amount + yl[i];//降雨总量

                        Average = list.size();//泵站总数

                        yVals1.add(new BarEntry(yl[i],i));
                        ColorMethod(yl[i],list1);//

                    }
                }

            String sum = Amount + "";
           // Log.e("warn",sum);
            float average = Amount / Average;
           // Log.e("warn",average+"");
            //降雨总量 四舍五入小数点后保留一位小数
            int index =sum.indexOf(".");
            String sum1 = sum.substring(index+1);
            if(sum1.length()>=3){
                float sum_dian_2=Float.valueOf(sum1.substring(1,2));
                if(sum_dian_2>=5){
                    float sum_dian_1=Float.valueOf(sum.substring(0,index+2));
                    double sum_num=sum_dian_1+0.1;
                    sum=String.valueOf(sum_num).substring(0,index+2);
                }else{
                    sum=sum.substring(0,index+2);
                }
                tv_Amount.setText(sum);//降雨总量
            }else{
                tv_Amount.setText(sum);//降雨总量
            }

            //平均降雨量 四舍五入小数点后保留一位小数
            String s = average+"";
            int index1 =s.indexOf(".");
            String average1= s.substring(index1+1);
            if(average1.length()>=3){
                float dianwei_2=Float.valueOf(average1.substring(1,2));
                //Log.e("warn",average1.substring(1,2));
                if(dianwei_2>=5){
                    float sum_dian_1=Float.valueOf(s.substring(0,index+2));
                    double sum_num=sum_dian_1+0.1;
                    s=String.valueOf(sum_num).substring(0,index1+2);
                }else{
                    s=s.substring(0,index+2);
                }
                tv_Average.setText(s + "");//降雨平均值
            }else{
                tv_Average.setText(s + "");//降雨平均值
            }
            barChart.setDrawBarShadow(false);
            barChart.setDrawValueAboveBar(true);
            barChart.setDescription("");
            barChart.setMaxVisibleValueCount(60);
            barChart.setPinchZoom(false);
            barChart.setDrawGridBackground(false);
            // 是否可以缩放
            barChart.setScaleEnabled(true);
            // 集双指缩放
            barChart.setPinchZoom(false);
            // 隐藏右边的坐标轴
            barChart.getAxisRight().setEnabled(false);
            //动画
            barChart.animateY(2500);
            barChart.animateX(2500);
            //X轴
            XAxis xAxis = barChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            //xAxis.setTypeface(mTfLight);
            xAxis.setDrawGridLines(false);
            xAxis.setSpaceBetweenLabels(1);
            xAxis.setDrawLabels(true);//是否显示X轴数值
            //xAxis.setGranularity(1f); // only intervals of 1 day
            //xAxis.setLabelCount(objects.length,true);//label长度=返回数据长度
            //xAxis.setValueFormatter(xAxisFormatter);//X轴数据
            xAxis.setTextSize(8);
            xAxis.setAxisMinValue(-0.5f); //设置x轴坐标起始值为-0.5 防止其实条形图被切去一半

            //AxisValueFormatter custom = new MyAxisValueFormatterYL();
            //Y轴
            YAxis leftAxis = barChart.getAxisLeft();
            //leftAxis.setTypeface(mTfLight);
            leftAxis.setLabelCount(5, false);//设置y轴数据个数
            //leftAxis.setLabelCount(8, false);
            //leftAxis.setValueFormatter(custom);
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            leftAxis.setSpaceTop(15f);
            leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
            leftAxis.setAxisMaxValue(100);
            leftAxis.setAxisMinValue(0);


            //可以设置一条警戒线，如下：
            LimitLine ll = new LimitLine(60, "");//第一个参数为警戒线在坐标轴的位置，第二个参数为警戒线描述
            ll.setLineColor(Color.rgb(255,33,33));
            ll.setLineWidth(1f);
            ll.setTextColor(Color.GRAY);
            ll.setTextSize(12f);
            // .. and more styling options
            leftAxis.addLimitLine(ll);






            Legend l = barChart.getLegend();//设置比例图
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            //l.setForm(Legend.LegendForm.CIRCLE);
            l.setFormSize(8f);
            l.setTextSize(8f);
            l.setXEntrySpace(4f);
            l.setCustom(myColors, lbls);

            BarDataSet set1;
            //判断图表中原来是否有数据
            if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
                set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
                //set1 = new BarDataSet(yVals1,"");
                set1.setYVals(yVals1);//设置图表柱形图数值
                barChart.getData().notifyDataChanged();
                barChart.notifyDataSetChanged();

            } else {
                set1 = new BarDataSet(yVals1, "雨量监测");
                set1.setColors(list1);//设置柱状图的各种颜色，上面已设置ColorTemplate.MATERIAL_COLORS的值
                set1.setBarSpacePercent(30f);//设置柱间空白的宽度
                //set1.setBarSpacePercent(10);
                // set custom labels and colors
                //设置柱状图颜色，第一个color.rgb为第一个柱状图颜色。以此类推
                //set1.setColors(new int[]{Color.rgb(255,241,226),Color.rgb(155,241,226)});
                ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
                dataSets.add(set1);

                //BarData data = new BarData(dataSets);
                BarData data = new BarData(appName,dataSets);
                data.setValueTextSize(7f);
                data.setDrawValues(true);
                data.setValueTextColor(Color.BLACK);
                //data.setValueFormatter(new CustomerValueFormatter());
                //data.setValueTypeface(mTfLight);
                //data.setBarWidth(0.8f);
                //mChart.setExtraOffsets(0,0,0,200);//此种方法可以一次设置上下左右偏移量。根据自己数据哪个地方显示不全，对应调用方法。
                barChart.setData(data);// 设置数据
            }
        }
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str =(String)msg.obj;
            if(str.equals("1")){
                closeDialog();
                showData();
            }else{
                closeDialog();
                Toast.makeText(getActivity(),str,Toast.LENGTH_SHORT).show();
            }

        }
    };
}
