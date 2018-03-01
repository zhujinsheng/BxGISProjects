package com.bxgis.bxportal.bean;

import java.io.Serializable;

/**
 * Created by xiaozhu on 2017/11/13.
 */

public class UserBean  implements Serializable{

    private static final long serialVersionUID = 7416516574312706127L;
    /**
     * __status : 200
     * __msg : success
     * id : 62
     * login_name : admin
     * password : 21232f297a57a5a743894a0e4a801fc3
     * real_name : 超级管理员
     * address : 茂名市港航局
     * tel : 0758-6521354
     * moblie : 13002046660
     * email : admin@163.com
     * birthday : 1462204800000
     * oicq : 23654
     * reg_date : 1463328000000
     * bz : null
     * user_type : 1
     * roleClass :
     * creator_Id : null
     * org_id : 59f18566ba39f114388d5db5
     * validity : 1
     * role_ids : ["5"]
     * myRoles : null
     * token_vals : ["e2eb6717-556a-4171-ba07-82382444b303"]
     * userToken : null
     * record_token : false
     */

    private int __status;
    private String __msg;
    private String id;
    private String login_name;
    private String password;
    private String real_name;
    private String address;
    private String tel;
    private String moblie;
    private String email;
    private long birthday;
    private String oicq;
    private long reg_date;
    private Object bz;
    private int user_type;
    private String roleClass;
    private Object creator_Id;
    private String org_id;
    private String validity;
    private String role_ids;
    private Object myRoles;
    private String token_vals;
    private Object userToken;
    private boolean record_token;

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

    public String getLogin_name() {
        return login_name;
    }

    public void setLogin_name(String login_name) {
        this.login_name = login_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMoblie() {
        return moblie;
    }

    public void setMoblie(String moblie) {
        this.moblie = moblie;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public String getOicq() {
        return oicq;
    }

    public void setOicq(String oicq) {
        this.oicq = oicq;
    }

    public long getReg_date() {
        return reg_date;
    }

    public void setReg_date(long reg_date) {
        this.reg_date = reg_date;
    }

    public Object getBz() {
        return bz;
    }

    public void setBz(Object bz) {
        this.bz = bz;
    }

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    public String getRoleClass() {
        return roleClass;
    }

    public void setRoleClass(String roleClass) {
        this.roleClass = roleClass;
    }

    public Object getCreator_Id() {
        return creator_Id;
    }

    public void setCreator_Id(Object creator_Id) {
        this.creator_Id = creator_Id;
    }

    public String getOrg_id() {
        return org_id;
    }

    public void setOrg_id(String org_id) {
        this.org_id = org_id;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getRole_ids() {
        return role_ids;
    }

    public void setRole_ids(String role_ids) {
        this.role_ids = role_ids;
    }

    public Object getMyRoles() {
        return myRoles;
    }

    public void setMyRoles(Object myRoles) {
        this.myRoles = myRoles;
    }

    public String getToken_vals() {
        return token_vals;
    }

    public void setToken_vals(String token_vals) {
        this.token_vals = token_vals;
    }

    public Object getUserToken() {
        return userToken;
    }

    public void setUserToken(Object userToken) {
        this.userToken = userToken;
    }

    public boolean isRecord_token() {
        return record_token;
    }

    public void setRecord_token(boolean record_token) {
        this.record_token = record_token;
    }
}
