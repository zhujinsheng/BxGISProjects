package com.bxgis.bxportal.bean;

import java.io.Serializable;

/**
 * Created by xiaozhu on 2017/11/20.
 */

public class InspectionSubProjectBean  implements Serializable{


    private static final long serialVersionUID = 8579627771695256817L;
    /**
     * __status : 200
     * __msg : success
     * id : 48
     * project_id : 22
     * project_name : 安全管理制度和规程
     * project_code : 1
     * subproject_name : 安全生产责任制
     * subproject_code : bx07
     * inspection_content : 建立安全生产责任制、划分与落实安全职责、明确第一责任人安全生产职责、建立和落实安全责任监督考核机制的情况
     * inspection_methods : 1.查看经营人建立的安全生产责任制，检查各部门、岗位安全职责划分情况，检查安全职责是否落实到位；
     2.查看责任制对主要负责人的安全职责是否有明确规定；
     3.查看安全生产责任考核与奖惩制度，检查有关岗位责任人员、责任范围和考核标准等是否明确；
     4.查阅考核记录、奖惩记录等。

     * review_basic : 《港口法》第三十二条
     《安全生产法》第四、五、十八、十九条
     《危险化学品安全管理条例》第四条

     * icon_url :
     * type : 1
     * create_date : 1510745384000
     * update_date : 1510745384000
     */

    private int __status;
    private String __msg;
    private String id;
    private int project_id;
    private String project_name;
    private String project_code;
    private String subproject_name;
    private String subproject_code;
    private String inspection_content;
    private String inspection_methods;
    private String review_basic;
    private String icon_url;
    private int type;
    private long create_date;
    private long update_date;

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

    public int getProject_id() {
        return project_id;
    }

    public void setProject_id(int project_id) {
        this.project_id = project_id;
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

    public String getInspection_content() {
        return inspection_content;
    }

    public void setInspection_content(String inspection_content) {
        this.inspection_content = inspection_content;
    }

    public String getInspection_methods() {
        return inspection_methods;
    }

    public void setInspection_methods(String inspection_methods) {
        this.inspection_methods = inspection_methods;
    }

    public String getReview_basic() {
        return review_basic;
    }

    public void setReview_basic(String review_basic) {
        this.review_basic = review_basic;
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

    public InspectionSubProjectBean() {
    }

}
