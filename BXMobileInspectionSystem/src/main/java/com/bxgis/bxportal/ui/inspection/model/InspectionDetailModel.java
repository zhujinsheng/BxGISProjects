package com.bxgis.bxportal.ui.inspection.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.bxgis.bxportal.MISystemApplication;
import com.bxgis.bxportal.api.Api;
import com.bxgis.bxportal.bean.BaseInspection;
import com.bxgis.bxportal.ui.inspection.contract.InspectionDetailContract;
import com.bxgis.bxportal.utils.MyFileConvert;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by xiaozhu on 2017/11/17.
 */
public class InspectionDetailModel implements InspectionDetailContract.Model {

    @Override
    public Observable<Response<String>> commit(BaseInspection baseInspection, List<File> files,File signatureFile) {
        HttpParams httpParams = new HttpParams();

        httpParams.put("id", baseInspection.getId());
        httpParams.put("user_id", baseInspection.getUser_id());
        httpParams.put("inspection_feedback", baseInspection.getInspection_feedback());
        httpParams.put("inspection_type", baseInspection.getInspection_type()); //  巡检状态：0 巡检中 ， 1 巡检完成
        httpParams.put("type", 3);
        httpParams.put("signatureFile", signatureFile);
        httpParams.putFileParams("accessoryFile", files);
        return OkGo.<String>post(Api.INSPECTION_OK).params(httpParams).converter(new StringConvert()).adapt(new ObservableResponse<String>());
    }

    @Override
    public Observable<Response<String>> getReviewInspectionById(String basic_id) {
        return OkGo.<String>post(Api.SUBTYPE_BY_BASICID).params("basic_id",basic_id).converter(new StringConvert()).adapt(new ObservableResponse<String>());
    }

    @Override
    public Observable<Response<File>> downFile(String path) {
        return  OkGo.<File>get(path).converter(new MyFileConvert()).adapt(new ObservableResponse<File>());
    }

}
