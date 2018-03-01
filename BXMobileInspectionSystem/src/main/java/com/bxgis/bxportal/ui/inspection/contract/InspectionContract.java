package com.bxgis.bxportal.ui.inspection.contract;

import com.bxgis.bxportal.base.BaseModel;
import com.bxgis.bxportal.base.BaseView;
import com.bxgis.bxportal.bean.BaseInspection;
import com.lzy.okgo.model.Response;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by xiaozhu on 2017/11/17.
 */
public interface InspectionContract {
    interface Model extends BaseModel {
//        Observable<Response<String>> getBanner(String type);
        Observable<Response<String>> getInspections(String user_id,String inspection_type,int type,long currentPage);
    }

    interface View extends BaseView {
        void showInspectionData(List<BaseInspection> bOT); //返回巡检未完成列表
        void showEmptyData(String msg);//返回数据为空
        void onSucceed();     //获取成功
        void onFaild(String msg);
    }

    interface Presenter {
        void getInspections(String user_id,String inspection_type,int type,long currentPages);
    }


}
