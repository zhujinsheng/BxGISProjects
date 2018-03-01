package com.bxgis.bxportal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xiaozhu on 2017/11/27.
 */

public class SysOrganizationBean implements Serializable{

    private static final long serialVersionUID = -623564226615840558L;
    /**
     * id : 59f18566ba39f114388d5db6
     * org_id : 59f18566ba39f114388d5db5
     * orgName : 基建科
     * sysOrg : [{"__status":200,"__msg":"success","id":"62","login_name":"admin","password":"21232f297a57a5a743894a0e4a801fc3","real_name":"超级管理员","address":"茂名市港航局","tel":"0758-6521354","moblie":"13002046660","email":"admin@163.com","birthday":1462204800000,"oicq":"23654","reg_date":1463328000000,"bz":null,"user_type":1,"roleClass":"","creator_Id":null,"org_id":"59f18566ba39f114388d5db5","validity":"1","role_ids":"[\"5\"]","myRoles":null,"token_vals":"[\"e2eb6717-556a-4171-ba07-82382444b303\"]","userToken":null,"record_token":false}]
     * sys : null
     */

    private String id;
    private String org_id;
    private String orgName;
    private String sys;
    private List<UserBean> sysOrg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrg_id() {
        return org_id;
    }

    public void setOrg_id(String org_id) {
        this.org_id = org_id;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getSys() {
        return sys;
    }

    public void setSys(String sys) {
        this.sys = sys;
    }

    public List<UserBean> getSysOrg() {
        return sysOrg;
    }

    public void setSysOrg(List<UserBean> sysOrg) {
        this.sysOrg = sysOrg;
    }

    public static class SysOrgBean {
    }
}
