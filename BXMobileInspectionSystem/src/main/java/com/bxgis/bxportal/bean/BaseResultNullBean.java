package com.bxgis.bxportal.bean;

import java.io.Serializable;

/**
 * 空的基本类
 * Created by xiaozhu on 2017/11/17.
 */
//{"data":[],"__msg":"success","__status":200}
public class BaseResultNullBean<T> implements Serializable {

    private static final long serialVersionUID = -7494616705477767172L;
    private String  __msg;
    private int __status;

    public String get__msg() {
        return __msg;
    }

    public void set__msg(String __msg) {
        this.__msg = __msg;
    }

    public int get__status() {
        return __status;
    }

    public void set__status(int __status) {
        this.__status = __status;
    }

    public BaseResultBean toBaseResultBean(){
        BaseResultBean baseResultBean = new BaseResultBean();
        baseResultBean.set__msg(__msg);
        baseResultBean.set__status(__status);
        return  baseResultBean;
    }
}
