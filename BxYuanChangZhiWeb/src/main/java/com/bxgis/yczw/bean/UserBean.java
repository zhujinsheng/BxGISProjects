package com.bxgis.yczw.bean;

import java.io.Serializable;

/**
 * Created by xiaozhu on 2017/11/13.
 */

public class UserBean implements Serializable{

    /**
     * email : 1490565821@qq.com
     * head_img : 776f04f5-4780-498b-b531-5b7d925d2600.gif
     * id : 5a41e4d59e040d541c95febc
     * moblie : 18819448246
     * name : admin
     * nice_name : 超级管理员
     * org_id : 5a585ee1e0e1ce86acbe62cf
     * password :
     * type : 2
     * userToken : 5a41e4d59e040d541c95febc@732210ae-e014-4635-8f70-d65db8403634
     */

    private String email;
    private String head_img;
    private String id;
    private String moblie;
    private String name;
    private String nice_name;
    private String org_id;
    private String password;
    private String type;
    private String userToken;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHead_img() {
        return head_img;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMoblie() {
        return moblie;
    }

    public void setMoblie(String moblie) {
        this.moblie = moblie;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNice_name() {
        return nice_name;
    }

    public void setNice_name(String nice_name) {
        this.nice_name = nice_name;
    }

    public String getOrg_id() {
        return org_id;
    }

    public void setOrg_id(String org_id) {
        this.org_id = org_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
