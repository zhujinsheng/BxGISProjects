package com.bxgis.bxportal.bean;

import java.io.Serializable;

/**
 * 巡检计划/任务基础信息和事项类的中间表实体
 * Created by xiaozhu on 2017/11/20.
 */

public class BaseInspectionSubProject implements Serializable{
    private static final long serialVersionUID = 5412175263263850733L;

    private String id; //自增ID
    private String subproject_name; //巡检事项的名称
    private String subproject_code; //巡检事项编码
    private String project_name; //项目名称
    private String project_code; //巡检项目编码
    private String inspection_content;  //巡检事项内容
    private String basic_id; //巡检计划/任务的基础表ID
    /**
     * __status : 200
     * __msg : success
     */

    private int __status;
    private String __msg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubproject_name() {
        return subproject_name;
    }

    public void setSubproject_name(String subproject_name) {
        this.subproject_name = subproject_name;
    }

    public String getSubproject_code() {
        return subproject_code;
    }

    public void setSubproject_code(String subproject_code) {
        this.subproject_code = subproject_code;
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

    public String getBasic_id() {
        return basic_id;
    }

    public void setBasic_id(String basic_id) {
        this.basic_id = basic_id;
    }

    public String getInspection_content() {
        return inspection_content;
    }

    public void setInspection_content(String inspection_content) {
        this.inspection_content = inspection_content;
    }

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
}
