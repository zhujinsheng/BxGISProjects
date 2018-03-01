package com.bxgis.bxportal.bean;

import java.io.Serializable;

/**
 * App版本更新实体
 * Created by xiaozhu on 2017/12/2.
 */

public class AppInfonBean implements Serializable{

    private static final long serialVersionUID = 6668640423647349416L;
    /**
     * id : 4
     * app_name : 移动巡检综合管理平台
     * version_name : V.1.1.0
     * version_code : 2
     * update_content : 1.更新了结果
     2.测试新功能
     * apk_url :   更新版本的地址
     */

    private String id;
    private String app_name;
    private String version_name;
    private String version_code;
    private String update_content;
    private String apk_url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String getVersion_code() {
        return version_code;
    }

    public void setVersion_code(String version_code) {
        this.version_code = version_code;
    }

    public String getUpdate_content() {
        return update_content;
    }

    public void setUpdate_content(String update_content) {
        this.update_content = update_content;
    }

    public String getApk_url() {
        return apk_url;
    }

    public void setApk_url(String apk_url) {
        this.apk_url = apk_url;
    }
}
