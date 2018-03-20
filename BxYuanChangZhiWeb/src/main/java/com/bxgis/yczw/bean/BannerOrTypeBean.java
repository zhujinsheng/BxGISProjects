package com.bxgis.yczw.bean;

import java.io.Serializable;

/**
 * Created by xiaozhu on 2017/11/17.
 */

public class BannerOrTypeBean implements Serializable {


    private static final long serialVersionUID = -4065967708042339385L;
    /**
     * __status : 200
     * __msg : success
     * id : 7
     * name : 首页广告图
     * piture_url : /static/img/banners/bannerImg_01.jpg
     * rank : 2
     * status : 1
     * jump_url : http://www.goole.com/
     * type : 0
     * create_time : 1508310620000
     */

    private int __status;

    private String __msg;
    private String id;
    private String name;
    private String piture_url;
    private String rank;
    private String status;
    private String jump_url;
    private String type;
    private  long create_time;

    public int get__status() {
        return __status;
    }

    public void set__status(int __status) {
        this.__status = __status;
    }

    public String get__msg() {
        return __msg;
    }

    public void set__msg(String __msg) {
        this.__msg = __msg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPiture_url() {
        return piture_url;
    }

    public void setPiture_url(String piture_url) {
        this.piture_url = piture_url;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJump_url() {
        return jump_url;
    }

    public void setJump_url(String jump_url) {
        this.jump_url = jump_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }
}
