package com.bxgis.yczw.utils.douglas;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import java.util.ArrayList;
import java.util.List;

/**
 *1）对曲线的首末点虚连一条直线，求曲线上所有点与直线的距离，并找出最大距离值dmax，用dmax与事先给定的阈值D相比：
 2）若dmax<D，则将这条曲线上的中间点全部舍去；则该直线段作为曲线的近似，该段曲线处理完毕。
 　 若dmax≥D，保留dmax对应的坐标点，并以该点为界，把曲线分为两部分，对这两部分重复使用该方法，即重复1），2）步，直到所有dmax均<D，即完成对曲线的抽稀。
 显然，本算法的抽稀精度也与阈值相关，阈值越大，简化程度越大，点减少的越多，反之，化简程度越低，点保留的越多，形状也越趋于原曲线。
 */
public class Douglas {

    /**
     * 存储采样点数据的链表
     */
    public List<Point> points = new ArrayList<Point>();

    /**
     * 控制数据压缩精度的极差
     */
    private static final double D = 5;

    private WKTReader reader;

    /**
     * 构造Geometry
     *
     * @param str
     * @return
     */
    public Geometry buildGeo(String str) {
        try {
            if (reader == null) {
                reader = new WKTReader();
            }
            return reader.read(str);
        } catch (ParseException e) {
            throw new RuntimeException("buildGeometry Error", e);
        }
    }

    /**
     * 读取采样点
     */
    private Point p;
    private Geometry g;
    private Coordinate[] coords;

    public void readPoint(StringBuilder stringBuilder) {
        g = buildGeo(String.valueOf(stringBuilder));
        //Geometry g = buildGeo("(1 4,2 3,4 2,6 6,7 7,8 6,9 5,10 10)");
        coords = g.getCoordinates();

        if (null != points) {
            points.clear();
        }

        for (int i = 0; i < coords.length; i++) {
            p = new Point(coords[i].x, coords[i].y, i);
            points.add(p);
        }
    }


    /**
     * 压缩算法的开关量
     */
    boolean switchvalue = false;

    /**
     * 对矢量曲线进行压缩
     *
     * @param from 曲线的起始点
     * @param to   曲线的终止点
     */
    public void compress(Point from, Point to) {

        /**
         * 由起始点和终止点构成的直线方程一般式的系数
         */
        System.out.println(from.getY());
        System.out.println(to.getY());
        double A = (from.getY() - to.getY())
                / Math.sqrt(Math.pow((from.getY() - to.getY()), 2)
                + Math.pow((from.getX() - to.getX()), 2));

        /**
         * 由起始点和终止点构成的直线方程一般式的系数
         */
        double B = (to.getX() - from.getX())
                / Math.sqrt(Math.pow((from.getY() - to.getY()), 2)
                + Math.pow((from.getX() - to.getX()), 2));

        /**
         * 由起始点和终止点构成的直线方程一般式的系数
         */
        double C = (from.getX() * to.getY() - to.getX() * from.getY())
                / Math.sqrt(Math.pow((from.getY() - to.getY()), 2)
                + Math.pow((from.getX() - to.getX()), 2));

        double d = 0;
        double dmax = 0;
        int m = points.indexOf(from);
        int n = points.indexOf(to);
        if (n == m + 1)
            return;
        Point middle = null;
        List<Double> distance = new ArrayList<Double>();
        for (int i = m + 1; i < n; i++) {
            d = Math.abs(A * (points.get(i).getX()) + B
                    * (points.get(i).getY()) + C)
                    / Math.sqrt(Math.pow(A, 2) + Math.pow(B, 2));
            distance.add(d);
        }
        dmax = distance.get(0);
        for (int j = 1; j < distance.size(); j++) {
            if (distance.get(j) > dmax)
                dmax = distance.get(j);
        }
        if (dmax > D)
            switchvalue = true;
        else
            switchvalue = false;
        if (!switchvalue) {
            // 删除Points(m,n)内的坐标
            for (int i = m + 1; i < n; i++) {
                points.get(i).setIndex(-1);
            }

        } else {
            for (int i = m + 1; i < n; i++) {
                if ((Math.abs(A * (points.get(i).getX()) + B
                        * (points.get(i).getY()) + C)
                        / Math.sqrt(Math.pow(A, 2) + Math.pow(B, 2)) == dmax))
                    middle = points.get(i);
            }
            compress(from, middle);
            compress(middle, to);
        }
    }
}
