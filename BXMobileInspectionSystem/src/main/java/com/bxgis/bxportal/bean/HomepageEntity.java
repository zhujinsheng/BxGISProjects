package com.bxgis.bxportal.bean;

import java.util.List;

/**
 * Created by SK on 2017-05-05.
 */

public class HomepageEntity  {

    public List<BannerOrTypeBean> A;
    public List<InspectionProjectBean> B;
    public BaseInspectionTotal C;


    public List<BannerOrTypeBean> getA() {
        return A;
    }

    public void setA(List<BannerOrTypeBean> a) {
        A = a;
    }

    public List<InspectionProjectBean> getB() {
        return B;
    }

    public void setB(List<InspectionProjectBean> b) {
        B = b;
    }

    public BaseInspectionTotal getC() {
        return C;
    }

    public void setC(BaseInspectionTotal c) {
        C = c;
    }
}
