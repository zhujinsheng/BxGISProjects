package com.bxgis.yczw.ui.main.contract;

import com.bxgis.yczw.base.BaseModel;
import com.bxgis.yczw.base.BaseView;
import com.lzy.okgo.model.Response;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by xiaozhu on 2017/11/17.
 */
public interface HomeActivityContract {
    interface Model extends BaseModel {
        Observable<Response<String>> getCountInspection(String userId); //获取巡检数量

    }

    interface View extends BaseView {

        void onSucceed();     //获取成功
        void onFaild(String msg);
    }

    interface Presenter {

        void getAppInfo();
        void getProjectList();
    }


}
