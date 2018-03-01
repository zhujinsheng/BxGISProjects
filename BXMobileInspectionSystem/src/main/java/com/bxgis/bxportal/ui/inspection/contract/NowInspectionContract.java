package com.bxgis.bxportal.ui.inspection.contract;

import com.bxgis.bxportal.base.BaseModel;
import com.bxgis.bxportal.base.BaseView;
import com.bxgis.bxportal.bean.BaseInspection;
import com.bxgis.bxportal.bean.CompanyBean;
import com.bxgis.bxportal.bean.InspectionSubProjectBean;
import com.bxgis.bxportal.bean.SysOrganizationBean;
import com.lzy.okgo.model.Response;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by xiaozhu on 2017/11/17.
 */
public interface NowInspectionContract {
    interface Model extends BaseModel {
        Observable<Response<String>> getOrgUsers(); //获取所有用户
        Observable<Response<String>> getOrgCompany(); //获取所有企业
        Observable<Response<String>> commit(BaseInspection baseInspection);
        Observable<Response<String>> getSubProjects(String project_id);
        Observable<Response<String>> commitFile2(List<File> files);
    }

    interface View extends BaseView {
        void showOrgUsers(List<SysOrganizationBean> list);
        void showOrgCompany(List<CompanyBean> companys);
        void showSubProjectData(List<InspectionSubProjectBean> subProjectBeen);
        void onSucceed();     //获取成功
        void onFaild(String msg);
        void onCommitSucced();
        void onCommitFaild();
        void onSucceedUpload(String path); //#号区分
        void onSucceedUploadSign(String path); //#号区分

    }

    interface Presenter {
        void getSubProjects(String project_id);
        void getOrgCompany(); //获取所有企业
        void getOrgUsers();
        void  commit(BaseInspection baseInspection);
        void  commitFile2(List<File> files,int fileType);
    }


}
