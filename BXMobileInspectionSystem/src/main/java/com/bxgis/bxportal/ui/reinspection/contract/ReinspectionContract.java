package com.bxgis.bxportal.ui.reinspection.contract;

import com.bxgis.bxportal.base.BaseModel;
import com.bxgis.bxportal.base.BaseView;
import com.bxgis.bxportal.bean.HiddenDangerReViewBean;
import com.lzy.okgo.model.Response;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by xiaozhu on 2017/11/17.
 */
public interface ReinspectionContract {
    interface Model extends BaseModel {
        Observable<Response<String>> commitReinspection(HiddenDangerReViewBean hiddenDangerReViewBean);
        Observable<Response<String>> commitFile(List<File> files);
    }

    interface View extends BaseView {
        void onSucceedFile(String filePath);
        void onSucceed();     //获取成功
        void onFaild(String msg);
    }

    interface Presenter {
        void commitFile(List<File> files);
        void commitReinspection(HiddenDangerReViewBean hiddenDangerReViewBean);
    }

}
