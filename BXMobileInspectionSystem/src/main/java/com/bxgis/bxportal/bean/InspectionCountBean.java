package com.bxgis.bxportal.bean;

import java.io.Serializable;

/** 统计巡检单数实体
 * Created by xiaozhu on 2017/12/2.
 */

public class InspectionCountBean implements Serializable{

    private static final long serialVersionUID = -6211207530986526431L;
    /**
     *
     * user_id : 62
     * total : 15
     * count_unfinished : 10
     * count_complete : 5
     */

    private String user_id;
    private int total;   //巡检总数
    private int count_unfinished; //未完成的巡检数
    private int count_complete;  //完成的巡检数

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCount_unfinished() {
        return count_unfinished;
    }

    public void setCount_unfinished(int count_unfinished) {
        this.count_unfinished = count_unfinished;
    }

    public int getCount_complete() {
        return count_complete;
    }

    public void setCount_complete(int count_complete) {
        this.count_complete = count_complete;
    }
}
