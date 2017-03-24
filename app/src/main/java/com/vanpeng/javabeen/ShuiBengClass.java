package com.vanpeng.javabeen;

import java.io.Serializable;

/**
 * Created by Administrator on 2016-12-22.
 */
public class ShuiBengClass implements Serializable {
    private String id;
    private String shuiBengName;
    private String bengZhanID;
    private String beiZhu;
    private String sortX;
    private String dianLiu;
    private String zhuangTai;
    private String uploadTimeX;
    private String shuizhanName;

    public void setId(String _id)
    {
        id=_id;
    }
    public void setShuizhanName(String _name){
        this.shuizhanName = _name;
    }
    public String getShuizhanName(){
        return this.shuizhanName;
    }
    public  void setShuiBengName(String _shuiBengName)
    {
        shuiBengName=_shuiBengName;
    }
    public void setBengZhanID(String _bengZhanID)
    {
        bengZhanID=_bengZhanID;
    }
    public void setBeiZhu(String _beiZhu)
    {
        beiZhu=_beiZhu;
    }
    public void setSortX(String _sortX)
    {
        sortX=_sortX;
    }
    public void setDianLiu(String _dianLiu)
    {
        dianLiu=_dianLiu;
    }
    public void setZhuangTai(String _zhuangtai)
    {
        zhuangTai=_zhuangtai;
    }
    public  void setUploadTimeX(String _uploadTimeX)
    {

        uploadTimeX=_uploadTimeX;
    }

    public String getId()
    {
        return id;
    }
    public String getShuiBengName()
    {
        return shuiBengName;
    }
    public String getBengZhanID()
    {
        return bengZhanID;
    }
    public String getBeiZhu()
    {
        return beiZhu;
    }
    public String getSortX()
    {
        return sortX;
    }
    public String getDianLiu()
    {
        return dianLiu;
    }
    public String getZhuangTai()
    {
        return zhuangTai;
    }
    public String getUploadTimeX()
    {
        return uploadTimeX;
    }
}
