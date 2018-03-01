package com.bxgis.bxportal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xiaozhu on 2017/12/3.
 */

public class HiddenDangerReViewBean implements Serializable{

    private static final long serialVersionUID = -2900329941694047415L;
    /**
     * __status : 200
     * __msg : success
     * id : 5a2224a165cfac718484d414
     * hid_code : YH20171202114499091
     * hid_name : 储罐
     * hid_level : 0
     * rectify_require : 11111

     * rectify_way : 111

     * rectify_pictrue_befroe : http://gisserver3:18080/static/files/e39236d2-d6bd-4d9a-b149-b917bc31a407.png
     * rectify_emergency_way : 11111

     * rectify_duty_unit : 1111
     * rectify_duty_person : 李李李李李李李李李李李李李李李李
     * rectify_maney : 11111.0
     * rectify_plan_performing :
     * rectify_finish_time :
     * rectify_finish_desc : 已经按上次要求整改完成8

     * rectify_deadline :
     * rectify_pictrue_after : http://gisserver3:18080/static/files/cda39063-9016-41db-ab02-a75bc0a19ea5.png
     * rectify_review_time :
     * rectify_review_picture : http://gisserver3:18080/static/files/47addc85-04ab-42d9-b692-92c61a9ae3b2.png
     * rectify_review_person : 科长
     * rectify_init_unit : 2
     * rectify_code : YH-ZG20171202120259188
     * rectify_opinion : 11111233

     * rectify_review_opinion :
     * rectify_review_finish_desc : 又不通过7

     * rectify_frequency : 0
     * remark :
     * creater : 20
     * create_time :
     * updater : 20
     * updater_time :
     * status : 0
     * sort :
     * inspection_id :
     * sysParamsList :
     * review : [{"__status":200,"__msg":"success","id":"5a23711765cfac6d08463f2b","rectify_review_time":"","rectify_review_picture":"undefined","rectify_review_person":"科长","rectify_code":"YH-ZG20171202120259188","rectify_review_opinion":"","rectify_review_finish_desc":"又不通过5\r\n","rectify_frequency":"0","remark":"","creater":"5a1c1350ba31230dec9a0c63","create_time":"","updater":"5a1c1350ba31230dec9a0c63","updater_time":"","status":"","sort":""},{"__status":200,"__msg":"success","id":"5a2371d965cfac6d0846407a","rectify_review_time":"","rectify_review_picture":"http://gisserver3:18080/static/files/47addc85-04ab-42d9-b692-92c61a9ae3b2.png","rectify_review_person":"科长","rectify_code":"YH-ZG20171202120259188","rectify_review_opinion":"","rectify_review_finish_desc":"又不通过6\r\n","rectify_frequency":"0","remark":"","creater":"5a1c1350ba31230dec9a0c63","create_time":"","updater":"5a1c1350ba31230dec9a0c63","updater_time":"","status":"","sort":""},{"__status":200,"__msg":"success","id":"5a23786065cfac638844a64c","rectify_review_time":"","rectify_review_picture":"http://gisserver3:18080/static/files/47addc85-04ab-42d9-b692-92c61a9ae3b2.png","rectify_review_person":"科长","rectify_code":"YH-ZG20171202120259188","rectify_review_opinion":"","rectify_review_finish_desc":"又不通过7\r\n","rectify_frequency":"0","remark":"","creater":"5a1c1350ba31230dec9a0c63","create_time":"","updater":"5a1c1350ba31230dec9a0c63","updater_time":"","status":"","sort":""}]
     */

    private int __status;
    private String __msg;
    private String id;
    private String hid_code; //隐患编码
    private String hid_name; //隐患名称
    private String hid_level;//隐患等级
    private String rectify_require;  //整改要求
    private String rectify_way;  //整改措施
    private String rectify_pictrue_befroe; //整改前照片
    private String rectify_emergency_way; //应急保证措施
    private String rectify_duty_unit; //责任部门
    private String rectify_duty_person; //整改责任人
    private double rectify_maney;  //整改资金
    private String rectify_plan_performing;  //计划完成时间
    private String rectify_finish_time; //完成时间
    private String rectify_finish_desc; //完成情况
    private String rectify_deadline; //整改期限
    private String rectify_pictrue_after; //整改后照片
    private String rectify_review_time;  //复查验收时间
    private String rectify_review_picture;  //复查照片
    private String rectify_review_person;  //复查确认人
    private String rectify_init_unit;  //整改状态（0，待整改;1，已整改，2 待 复查;3，复查完成;）
    private String rectify_code;  //整改单号
    private String rectify_opinion; //整改建议
    private String rectify_review_opinion; //复查情况及意见
    private String rectify_review_finish_desc; //复查完成情况
    private String rectify_frequency; //整改次数
    private String remark;
    private String creater;
    private String create_time;
    private String updater; //修改人
    private String updater_time; //修改时间
    private int status; //状态(0:有效,1:无效)
    private String sort; //排序
    private String inspection_id;
    private String sysParamsList;
    private List<ReviewBean> review;

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

    public String getHid_code() {
        return hid_code;
    }

    public void setHid_code(String hid_code) {
        this.hid_code = hid_code;
    }

    public String getHid_name() {
        return hid_name;
    }

    public void setHid_name(String hid_name) {
        this.hid_name = hid_name;
    }

    public String getHid_level() {
        return hid_level;
    }

    public void setHid_level(String hid_level) {
        this.hid_level = hid_level;
    }

    public String getRectify_require() {
        return rectify_require;
    }

    public void setRectify_require(String rectify_require) {
        this.rectify_require = rectify_require;
    }

    public String getRectify_way() {
        return rectify_way;
    }

    public void setRectify_way(String rectify_way) {
        this.rectify_way = rectify_way;
    }

    public String getRectify_pictrue_befroe() {
        return rectify_pictrue_befroe;
    }

    public void setRectify_pictrue_befroe(String rectify_pictrue_befroe) {
        this.rectify_pictrue_befroe = rectify_pictrue_befroe;
    }

    public String getRectify_emergency_way() {
        return rectify_emergency_way;
    }

    public void setRectify_emergency_way(String rectify_emergency_way) {
        this.rectify_emergency_way = rectify_emergency_way;
    }

    public String getRectify_duty_unit() {
        return rectify_duty_unit;
    }

    public void setRectify_duty_unit(String rectify_duty_unit) {
        this.rectify_duty_unit = rectify_duty_unit;
    }

    public String getRectify_duty_person() {
        return rectify_duty_person;
    }

    public void setRectify_duty_person(String rectify_duty_person) {
        this.rectify_duty_person = rectify_duty_person;
    }

    public double getRectify_maney() {
        return rectify_maney;
    }

    public void setRectify_maney(double rectify_maney) {
        this.rectify_maney = rectify_maney;
    }

    public String getRectify_plan_performing() {
        return rectify_plan_performing;
    }

    public void setRectify_plan_performing(String rectify_plan_performing) {
        this.rectify_plan_performing = rectify_plan_performing;
    }

    public String getRectify_finish_time() {
        return rectify_finish_time;
    }

    public void setRectify_finish_time(String rectify_finish_time) {
        this.rectify_finish_time = rectify_finish_time;
    }

    public String getRectify_finish_desc() {
        return rectify_finish_desc;
    }

    public void setRectify_finish_desc(String rectify_finish_desc) {
        this.rectify_finish_desc = rectify_finish_desc;
    }

    public String getRectify_deadline() {
        return rectify_deadline;
    }

    public void setRectify_deadline(String rectify_deadline) {
        this.rectify_deadline = rectify_deadline;
    }

    public String getRectify_pictrue_after() {
        return rectify_pictrue_after;
    }

    public void setRectify_pictrue_after(String rectify_pictrue_after) {
        this.rectify_pictrue_after = rectify_pictrue_after;
    }

    public String getRectify_review_time() {
        return rectify_review_time;
    }

    public void setRectify_review_time(String rectify_review_time) {
        this.rectify_review_time = rectify_review_time;
    }

    public String getRectify_review_picture() {
        return rectify_review_picture;
    }

    public void setRectify_review_picture(String rectify_review_picture) {
        this.rectify_review_picture = rectify_review_picture;
    }

    public String getRectify_review_person() {
        return rectify_review_person;
    }

    public void setRectify_review_person(String rectify_review_person) {
        this.rectify_review_person = rectify_review_person;
    }

    public String getRectify_init_unit() {
        return rectify_init_unit;
    }

    public void setRectify_init_unit(String rectify_init_unit) {
        this.rectify_init_unit = rectify_init_unit;
    }

    public String getRectify_code() {
        return rectify_code;
    }

    public void setRectify_code(String rectify_code) {
        this.rectify_code = rectify_code;
    }

    public String getRectify_opinion() {
        return rectify_opinion;
    }

    public void setRectify_opinion(String rectify_opinion) {
        this.rectify_opinion = rectify_opinion;
    }

    public String getRectify_review_opinion() {
        return rectify_review_opinion;
    }

    public void setRectify_review_opinion(String rectify_review_opinion) {
        this.rectify_review_opinion = rectify_review_opinion;
    }

    public String getRectify_review_finish_desc() {
        return rectify_review_finish_desc;
    }

    public void setRectify_review_finish_desc(String rectify_review_finish_desc) {
        this.rectify_review_finish_desc = rectify_review_finish_desc;
    }

    public String getRectify_frequency() {
        return rectify_frequency;
    }

    public void setRectify_frequency(String rectify_frequency) {
        this.rectify_frequency = rectify_frequency;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    public String getUpdater_time() {
        return updater_time;
    }

    public void setUpdater_time(String updater_time) {
        this.updater_time = updater_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getInspection_id() {
        return inspection_id;
    }

    public void setInspection_id(String inspection_id) {
        this.inspection_id = inspection_id;
    }

    public String getSysParamsList() {
        return sysParamsList;
    }

    public void setSysParamsList(String sysParamsList) {
        this.sysParamsList = sysParamsList;
    }

    public List<ReviewBean> getReview() {
        return review;
    }

    public void setReview(List<ReviewBean> review) {
        this.review = review;
    }

    public static class ReviewBean implements Serializable{
        private static final long serialVersionUID = -7332573462102864630L;
        /**
         * __status : 200
         * __msg : success
         * id : 5a23711765cfac6d08463f2b
         * rectify_review_time :
         * rectify_review_picture : undefined
         * rectify_review_person : 科长
         * rectify_code : YH-ZG20171202120259188
         * rectify_review_opinion :
         * rectify_review_finish_desc : 又不通过5

         * rectify_frequency : 0
         * remark :
         * creater : 5a1c1350ba31230dec9a0c63
         * create_time :
         * updater : 5a1c1350ba31230dec9a0c63
         * updater_time :
         * status :
         * sort :
         */

        private int __status;
        private String __msg;
        private String id;
        private String rectify_review_time;
        private String rectify_review_picture;
        private String rectify_review_person;
        private String rectify_code;
        private String rectify_review_opinion;
        private String rectify_review_finish_desc;
        private String rectify_frequency;
        private String remark;
        private String creater;
        private String create_time;
        private String updater;
        private String updater_time;
        private String status;
        private String sort;

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

        public String getRectify_review_time() {
            return rectify_review_time;
        }

        public void setRectify_review_time(String rectify_review_time) {
            this.rectify_review_time = rectify_review_time;
        }

        public String getRectify_review_picture() {
            return rectify_review_picture;
        }

        public void setRectify_review_picture(String rectify_review_picture) {
            this.rectify_review_picture = rectify_review_picture;
        }

        public String getRectify_review_person() {
            return rectify_review_person;
        }

        public void setRectify_review_person(String rectify_review_person) {
            this.rectify_review_person = rectify_review_person;
        }

        public String getRectify_code() {
            return rectify_code;
        }

        public void setRectify_code(String rectify_code) {
            this.rectify_code = rectify_code;
        }

        public String getRectify_review_opinion() {
            return rectify_review_opinion;
        }

        public void setRectify_review_opinion(String rectify_review_opinion) {
            this.rectify_review_opinion = rectify_review_opinion;
        }

        public String getRectify_review_finish_desc() {
            return rectify_review_finish_desc;
        }

        public void setRectify_review_finish_desc(String rectify_review_finish_desc) {
            this.rectify_review_finish_desc = rectify_review_finish_desc;
        }

        public String getRectify_frequency() {
            return rectify_frequency;
        }

        public void setRectify_frequency(String rectify_frequency) {
            this.rectify_frequency = rectify_frequency;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getCreater() {
            return creater;
        }

        public void setCreater(String creater) {
            this.creater = creater;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getUpdater() {
            return updater;
        }

        public void setUpdater(String updater) {
            this.updater = updater;
        }

        public String getUpdater_time() {
            return updater_time;
        }

        public void setUpdater_time(String updater_time) {
            this.updater_time = updater_time;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }
    }
}
