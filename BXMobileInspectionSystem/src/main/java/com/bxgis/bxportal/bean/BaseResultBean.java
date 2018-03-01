package com.bxgis.bxportal.bean;

import java.io.Serializable;

/**
 * Created by xiaozhu on 2017/11/17.
 */
//{"data":[{"__status":200,"__msg":"success","id":"7","name":"首页广告图","piture_url":"/static/img/banners/bannerImg_01.jpg","rank":"2","status":"1","jump_url":"http://www.goole.com/","type":"0","create_time":1508310620000},{"__status":200,"__msg":"success","id":"8","name":"首页广告图","piture_url":"/static/img/banners/banner3.jpg","rank":"3","status":"1","jump_url":"http://www.goole.com/","type":"0","create_time":1508310620000},{"__status":200,"__msg":"success","id":"9","name":"首页广告图","piture_url":"/static/img/banners/bannerImg_02.jpg","rank":"4","status":"1","jump_url":"http://www.goole.com/","type":"0","create_time":1508310620000},{"__status":200,"__msg":"success","id":"39","name":"首页广告图","piture_url":"/static/img/banners/bannerImg_02.jpg","rank":"4","status":"1","jump_url":"http://www.goole.com/搜索","type":"0","create_time":null},{"__status":200,"__msg":"success","id":"40","name":"首页广告图","piture_url":"/static/img/banners/bannerImg_02.jpg","rank":"4","status":"1","jump_url":"http://www.goole.com/","type":"0","create_time":null}],"__msg":"success","__status":200}
public class BaseResultBean<T> implements Serializable {

    private static final long serialVersionUID = -5079689622244668545L;
    private String  __msg;
    private T data;
    private int __status;

    public String get__msg() {
        return __msg;
    }

    public void set__msg(String __msg) {
        this.__msg = __msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int get__status() {
        return __status;
    }

    public void set__status(int __status) {
        this.__status = __status;
    }
    @Override
    public String toString() {
        return "LzyResponse{\n" +//
                "\t__status=" + __status + "\n" +//
                "\t__msg='" + __msg + "\'\n" +//
                "\tdata=" + data + "\n" +//
                '}';
    }
}
