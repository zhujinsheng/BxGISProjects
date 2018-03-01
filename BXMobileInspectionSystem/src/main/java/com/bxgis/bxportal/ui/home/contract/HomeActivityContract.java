package com.bxgis.bxportal.ui.home.contract;

import com.bxgis.bxportal.base.BaseModel;
import com.bxgis.bxportal.base.BaseView;
import com.bxgis.bxportal.bean.AppInfonBean;
import com.bxgis.bxportal.bean.BaseResultBean;
import com.bxgis.bxportal.bean.InspectionCountBean;
import com.bxgis.bxportal.bean.InspectionProjectBean;
import com.lzy.okgo.model.Response;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by xiaozhu on 2017/11/17.
 */
public interface HomeActivityContract {
    interface Model extends BaseModel {
        Observable<Response<String>> getCountInspection(String userId); //获取巡检数量
        Observable<Response<BaseResultBean<AppInfonBean>>> getAppInfo();//获取App版本
        Observable<Response<BaseResultBean<List<InspectionProjectBean>>>> getProjectList(); //获取所有的巡检类型
    }

    interface View extends BaseView {
        void showCount(InspectionCountBean inspectionCountBean); //已完成的所以巡检数量 、正在进行中的巡检数量
        void showAppInfo(AppInfonBean appInfonBean);
        void showProjectList(List<InspectionProjectBean> projectBeans);
        void onSuccedGetData();
        void onSucceed();     //获取成功
        void onFaild(String msg);
    }

    interface Presenter {
        void getCountInspection(String userId);
        void getAppInfo();
        void getProjectList();
    }


}
