package com.bxgis.yczw.utils.douglas;

/**
 * Created by Administrator on 2017/3/1.
 */
/**

 * Class Point.java

 * Description

 * Company mapbar

 * author Chenll E-mail: Chenll@mapbar.com

 * Version 1.0

 * Date 2012-6-28 下午05:51:09

 */
public class Point {
    /**
     * 点的X坐标
     */
    private double x = 0;

    /**
     * 点的Y坐标
     */
    private double y = 0;

    /**
     * 点所属的曲线的索引
     */
    private int index = 0;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * 点数据的构造方法
     *
     * @param x
     *               点的X坐标
     * @param y
     *               点的Y坐标
     * @param index 点所属的曲线的索引
     */
    public Point(double x, double y, int index) {
        this.x = x;
        this.y = y;
        this.index = index;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                ", index=" + index +
                '}';
    }
}
