package com.bxgis.bxportal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xiaozhu on 2017/11/20.
 */

public class BaseInspection implements Serializable{

    private static final long serialVersionUID = -3391911738604867180L;
    /**
     * __status : 200
     * __msg : success
     * id : 2072
     * inspection_name : 2018年上半年巡检会议召开（一）
     * company_id : 0
     * user_id : 62
     * inspection_project :
     * project_content :
     * expiration_reminder : 1
     * begin_time : 1506700800000
     * end_time : 1506787200000
     * type : 0
     * inspection_type : 1
     * inspector : admin
     * other_inspectors :
     * initiator : 超级管理员
     * create_time : 1510798804000
     * other_requests : 请及时安排落实
     * accessory : http://localhost:8080/file/eda7dc32-5236-4226-a695-facfbed22062.png
     * picture_signature :
     * inspection_feedback :
     * feedback_accessory :
     * location :
     * co_code :
     * rectify_url :
     * planSubProjectList : [{"__status":200,"__msg":"success","id":"48","project_id":22,"project_name":"安全管理制度和规程","project_code":"1","subproject_name":"安全生产责任制","subproject_code":"bx07","inspection_content":"建立安全生产责任制、划分与落实安全职责、明确第一责任人安全生产职责、建立和落实安全责任监督考核机制的情况","inspection_methods":"1.查看经营人建立的安全生产责任制，检查各部门、岗位安全职责划分情况，检查安全职责是否落实到位；\r\n2.查看责任制对主要负责人的安全职责是否有明确规定；\r\n3.查看安全生产责任考核与奖惩制度，检查有关岗位责任人员、责任范围和考核标准等是否明确；\r\n4.查阅考核记录、奖惩记录等。\r\n","review_basic":"《港口法》第三十二条\r\n《安全生产法》第四、五、十八、十九条\r\n《危险化学品安全管理条例》第四条\r\n","icon_url":"","type":1,"create_date":1510745384000,"update_date":1510745384000}]
     */

    private int __status;
    private String __msg;
    private String id;
    private String inspection_name;
    private String company_id;
    private String company_name; //企业名称
    private String user_id;
    private String inspection_project;
    private String project_content;
    private String rectify_url;
    private String expiration_reminder; //整改历史记录详情的路径（格式不包含域名）
    private String begin_time;
    private String end_time;
    private int type;
    private int inspection_type; //
    private String inspector;
    private String other_inspectors;
    private String initiator;
    private String initiator_id;
    private String create_time;  //时间戳 字符串
    private String other_requests;
    private String accessory;
    private String picture_signature;
    private String inspection_feedback;  //巡检结果内容反馈
    private String feedback_accessory;//巡检结果附件上传(以分号区分)
    private String location;
    private String inspector_id;
    private String co_code;
    private List<InspectionSubProjectBean> planSubProjectList;

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


    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getInspection_name() {
        return inspection_name;
    }

    public void setInspection_name(String inspection_name) {
        this.inspection_name = inspection_name;
    }

    public String getInspector_id() {
        return inspector_id;
    }

    public void setInspector_id(String inspector_id) {
        this.inspector_id = inspector_id;
    }

    public String getInitiator_id() {
        return initiator_id;
    }

    public void setInitiator_id(String initiator_id) {
        this.initiator_id = initiator_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getInspection_project() {
        return inspection_project;
    }

    public void setInspection_project(String inspection_project) {
        this.inspection_project = inspection_project;
    }

    public String getProject_content() {
        return project_content;
    }

    public void setProject_content(String project_content) {
        this.project_content = project_content;
    }

    public String getRectify_url() {
        return rectify_url;
    }

    public void setRectify_url(String rectify_url) {
        this.rectify_url = rectify_url;
    }

    public String getExpiration_reminder() {
        return expiration_reminder;
    }

    public void setExpiration_reminder(String expiration_reminder) {
        this.expiration_reminder = expiration_reminder;
    }

    public String getBegin_time() {
        return begin_time;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getInspection_type() {
        return inspection_type;
    }

    public void setInspection_type(int inspection_type) {
        this.inspection_type = inspection_type;
    }

    public String getInspector() {
        return inspector;
    }

    public void setInspector(String inspector) {
        this.inspector = inspector;
    }

    public String getOther_inspectors() {
        return other_inspectors;
    }

    public void setOther_inspectors(String other_inspectors) {
        this.other_inspectors = other_inspectors;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getOther_requests() {
        return other_requests;
    }

    public void setOther_requests(String other_requests) {
        this.other_requests = other_requests;
    }

    public String getAccessory() {
        return accessory;
    }

    public void setAccessory(String accessory) {
        this.accessory = accessory;
    }

    public String getPicture_signature() {
        return picture_signature;
    }

    public void setPicture_signature(String picture_signature) {
        this.picture_signature = picture_signature;
    }

    public String getInspection_feedback() {
        return inspection_feedback;
    }

    public void setInspection_feedback(String inspection_feedback) {
        this.inspection_feedback = inspection_feedback;
    }

    public String getFeedback_accessory() {
        return feedback_accessory;
    }

    public void setFeedback_accessory(String feedback_accessory) {
        this.feedback_accessory = feedback_accessory;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCo_code() {
        return co_code;
    }

    public void setCo_code(String co_code) {
        this.co_code = co_code;
    }

    public List<InspectionSubProjectBean> getPlanSubProjectList() {
        return planSubProjectList;
    }

    public void setPlanSubProjectList(List<InspectionSubProjectBean> planSubProjectList) {
        this.planSubProjectList = planSubProjectList;
    }


}
