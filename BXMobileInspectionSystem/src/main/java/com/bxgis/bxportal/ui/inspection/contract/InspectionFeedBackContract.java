package com.bxgis.bxportal.ui.inspection.contract;

import com.bxgis.bxportal.base.BaseModel;
import com.bxgis.bxportal.base.BaseView;
import com.bxgis.bxportal.bean.BaseInspection;
import com.bxgis.bxportal.bean.BaseInspectionSubProject;
import com.lzy.okgo.model.Response;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by xiaozhu on 2017/11/17.
 */
public interface InspectionFeedBackContract {
    interface Model extends BaseModel {
        Observable<Response<String>> commit(BaseInspection baseInspection, List<File> accessoryFile,File signatureFile);
        Observable<Response<String>> getReviewInspectionById(String basic_id);
        Observable<Response<File>> downFile(String path);
    }

    interface View extends BaseView {
        void showBaseSubProject(List<BaseInspectionSubProject> bis);
        void onSucceed();     //获取成功
        void downSucceed(String path);
        void onFaild(String msg);
        void openFileFaild(String msg);
    }

    interface Presenter {
        void getReviewInspectionById(String basic_id); //获取巡检清单
        void downFile(String filePath);
        void commit(BaseInspection baseInspection, List<File> files,File signatureFile);
    }
}
