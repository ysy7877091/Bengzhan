package com.vanpeng.javabeen;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016-12-15.
 */
public class BengZhanClass implements Serializable {
    private String mServiceURL = "";
    private String id;
    private String name;//泵站名称
    private double yeWei;//液位
    private double jiaWan;//甲烷
    private double yiYangHuaTan;//一氧化碳;
    private double liuHuaQin;//硫化氢
    private double anQi;//氨气
    private String beiZhu;
    private List<ShuiBengClass> shuiBengClassList;
    private List<VideoMonitoring> videoMonitoringList;//视频

    @Override
    public String toString() {
        return name;
    }

    public BengZhanClass() {

    }


    public BengZhanClass(String _id, String _name, double _yeWei, double _jiaWan, double _yiYangHuaTan, double _liuHuaQin, double _anQi) {
        id = _id;
        name = _name;
        yeWei = _yeWei;
        jiaWan = _jiaWan;
        yiYangHuaTan = _yiYangHuaTan;
        liuHuaQin = _liuHuaQin;
        anQi = _anQi;
    }

    public String getBeiZhu()
    {
        return beiZhu;
    }
    public void setBeiZhu(String _beiZhu)
    {
        beiZhu=_beiZhu;
    }

    public List<BengZhanClass> getBengZhanList() {


        return null;
    }

    public List<VideoMonitoring> getVideoMonitoringList() {
        return videoMonitoringList;
    }

    public List<ShuiBengClass> getShuiBengClassList() {
        return shuiBengClassList;
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getYeWei() {
        return yeWei;
    }

    public double getJiaWan() {
        return jiaWan;
    }

    public double getYiYangHuaTan() {
        return yiYangHuaTan;
    }

    public double getLiuHuaQin() {
        return liuHuaQin;
    }

    public double getAnQi() {
        return anQi;
    }

    public void setID(String _id) {
        id = _id;
    }

    public void setName(String _name) {
        name = _name;
    }

    public void setYeWei(double _yeWei) {
        yeWei = _yeWei;
    }

    public void setJiaWan(double _jiaWan) {
        jiaWan = _jiaWan;
    }

    public void setYiYangHuaTan(double _YiYangHuaTan) {
        yiYangHuaTan = _YiYangHuaTan;
    }

    public void setLiuHuaQin(double _liuHuaQin) {
        liuHuaQin = _liuHuaQin;
    }

    public void setAnQi(double _anQi) {
        anQi = _anQi;
    }

    public void setShuiBengClassList(List<ShuiBengClass> _ShuiBengClassList) {
        shuiBengClassList = _ShuiBengClassList;
    }
    public void setVideoMonitoringList(List<VideoMonitoring> _VideoMonitoringList)
    {
        videoMonitoringList=_VideoMonitoringList;
    }
}
