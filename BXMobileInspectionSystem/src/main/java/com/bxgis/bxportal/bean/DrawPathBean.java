package com.bxgis.bxportal.bean;

import android.graphics.Path;

/**
 * 路径实体类
 * Author:zjs
 * Date:2017/11/1
 */

public class DrawPathBean {
    private Path path;
    private int paintColor; //笔颜色
    private boolean isEraser;//是否橡皮擦去

    public Path getPath() {
        return path;
    }

    public DrawPathBean(Path path, int paintColor, boolean isEraser) {
        this.path = path;
        this.paintColor = paintColor;
        this.isEraser = isEraser;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public int getPaintColor() {
        return paintColor;
    }

    public void setPaintColor(int paintColor) {
        this.paintColor = paintColor;
    }

    public boolean isEraser() {
        return isEraser;
    }

    public void setEraser(boolean eraser) {
        isEraser = eraser;
    }
}
