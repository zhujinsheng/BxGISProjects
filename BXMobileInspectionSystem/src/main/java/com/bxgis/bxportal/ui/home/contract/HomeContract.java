package com.bxgis.bxportal.ui.home.contract;

import com.bxgis.bxportal.base.BaseModel;
import com.bxgis.bxportal.base.BaseView;
import com.bxgis.bxportal.bean.BannerOrTypeBean;
import com.bxgis.bxportal.bean.HiddenDangerReViewBean;
import com.bxgis.bxportal.bean.InspectionProjectBean;
import com.lzy.okgo.model.Response;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by xiaozhu on 2017/11/17.
 */
public interface HomeContract {
    interface Model extends BaseModel {
        Observable<Response<String>> getBanner(String type);
        Observable<Response<String>> getHiddenDangerList(int currentPage,int pageSize); //获取隐患列表
        Observable<Response<String>> getProjectList();
    }

    interface View extends BaseView {
        void showBannerData(List<BannerOrTypeBean> bOT); //返回banner
        void showProjectData(List<InspectionProjectBean> bOT); //返回现场发布的巡检类型
        void showReviewData(List<HiddenDangerReViewBean> bOT); //返回复查巡检清单列表
        void showReviewEmpty(String msg);
        void onSucceed();     //获取成功
        void onFaild(String msg);
    }

    interface Presenter {
        void getBanner(String type);
        void getProjectList();
        void getHiddenDangerList(int currentPage,int pageSize);
    }

}
