package com.bxgis.bxportal.bean;

import java.util.List;

/**
 * Created by SK on 2017-05-05.
 */

public class BaseInspectionTotal {

    public int total;
    public int cnt;
    public int page;
    public int totalpage;
    public List<HiddenDangerReViewBean> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalpage() {
        return totalpage;
    }

    public void setTotalpage(int totalpage) {
        this.totalpage = totalpage;
    }

    public List<HiddenDangerReViewBean> getList() {
        return list;
    }

    public void setList(List<HiddenDangerReViewBean> list) {
        this.list = list;
    }
}
