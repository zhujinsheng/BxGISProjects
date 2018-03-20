package com.bxgis.yczw.bean;

import java.io.Serializable;

/**
 * Author : xiaozhu
 * Time:2018/3/19
 * Description:
 */
public class BeachAndBenchlandBean implements Serializable {

    private static final long serialVersionUID = 4481226316911198746L;
    /**
     * distance : 3.22km
     * name : 测试县海湾
     * bay_id : 5aab7df49397fad6d0447873
     */

    private String distance;
    private String name;
    private String bay_id;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBay_id() {
        return bay_id;
    }

    public void setBay_id(String bay_id) {
        this.bay_id = bay_id;
    }
}
