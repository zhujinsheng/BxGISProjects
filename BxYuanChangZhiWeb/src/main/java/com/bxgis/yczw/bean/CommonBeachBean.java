package com.bxgis.yczw.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author : xiaozhu
 * Time:2018/3/19
 * Description:
 */
public class CommonBeachBean<T> implements Serializable{

    private List<BeachAndBenchlandBean> hws;
    private List<BeachAndBenchlandBean> has;

    public List<BeachAndBenchlandBean> getHws() {
        return hws;
    }

    public void setHws(List<BeachAndBenchlandBean> hws) {
        this.hws = hws;
    }

    public List<BeachAndBenchlandBean> getHas() {
        return has;
    }

    public void setHas(List<BeachAndBenchlandBean> has) {
        this.has = has;
    }
}
