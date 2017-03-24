package com.vanpeng.javabeen;

import java.io.Serializable;

/**
 * Created by Administrator on 2016-12-31.
 * 视频信息
 */
public class VideoMonitoring implements Serializable{
    private String id;
    private String name;
    private String bengZhanID;
    private String ip;
    private String port;
    private String loginName;
    private String loginPWD;
    private String channelID;
    private String sortX;


    public String getId()
    {
        return id;
    }
    public void setId(String _id)
    {
        id=_id;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String _name)
    {
        name=_name;
    }
    public String getBengZhanID()
    {
        return bengZhanID;
    }
    public void setBengZhanID(String _bengZhanID)
    {
        bengZhanID=_bengZhanID;
    }
    public String getIp()
    {
        return ip;
    }
    public void setIp(String _Ip)
    {
        ip=_Ip;
    }
    public String getPort()
    {
        return  port;
    }
    public void setPort(String _port)
    {
        port=_port;
    }
    public String getLoginName()
    {
        return loginName;
    }
    public void setLoginName(String _loginName)
    {
        loginName=_loginName;
    }
    public String getLoginPWD()
    {
        return loginPWD;
    }
    public void setLoginPWD(String _loginPWD)
    {
        loginPWD=_loginPWD;
    }
    public String getChannelID()
    {
        return  channelID;
    }
    public void setChannelID(String _channelID)
    {
        channelID=_channelID;
    }
    public String getSortX()
    {
        return  sortX;
    }
    public void setSortX(String _sortX)
    {
        sortX=_sortX;
    }
}
