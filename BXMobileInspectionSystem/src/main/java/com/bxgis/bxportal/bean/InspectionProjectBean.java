package com.bxgis.bxportal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xiaozhu on 2017/11/20.
 */

public class InspectionProjectBean implements Serializable {

    private static final long serialVersionUID = -8707704617908716448L;
    /**
     * __status : 200
     * __msg : success
     * id : 4
     * project_name : 现场巡检
     * project_code : 12
     * icon_url :
     * type : 1
     * create_date : 1504592415000
     * update_date : -2209017600000
     * subTypeList : []
     */

    private int __status;
    private String __msg;
    private String id;
    private String project_name;
    private String project_code;
    private String icon_url;
    private int type;
    private long create_date;
    private long update_date;
    private List<InspectionSubProjectBean> subTypeList;

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

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getProject_code() {
        return project_code;
    }

    public void setProject_code(String project_code) {
        this.project_code = project_code;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getCreate_date() {
        return create_date;
    }

    public void setCreate_date(long create_date) {
        this.create_date = create_date;
    }

    public long getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(long update_date) {
        this.update_date = update_date;
    }

    public List<InspectionSubProjectBean> getSubTypeList() {
        return subTypeList;
    }

    public void setSubTypeList(List<InspectionSubProjectBean> subTypeList) {
        this.subTypeList = subTypeList;
    }
}
