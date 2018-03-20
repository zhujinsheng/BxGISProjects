package com.bxgis.yczw.bean;

import java.io.Serializable;

/**
 * Author : xiaozhu
 * Time:2018/3/15
 * Description:
 */
public class ProjectTypeBean implements Serializable {
    private static final long serialVersionUID = -8871243708466133563L;

    private String title;
    private int iconId;
    private String url;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
