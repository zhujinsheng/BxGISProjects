package com.bxgis.bxportal.ui.inspection.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.bxgis.bxportal.MISystemApplication;
import com.bxgis.bxportal.api.Api;
import com.bxgis.bxportal.bean.BaseInspection;
import com.bxgis.bxportal.ui.inspection.contract.InspectionFeedBackContract;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.FileConvert;
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
public class InspectionFeedBackModel implements InspectionFeedBackContract.Model {

    @Override
    public Observable<Response<String>> commit(BaseInspection baseInspection, List<File> files, File signatureFile) {
        HttpParams httpParams = new HttpParams();
        if (!TextUtils.isEmpty(baseInspection.getInspection_feedback())) {
            httpParams.put("inspection_feedback", baseInspection.getInspection_feedback());
        }
        httpParams.put("id", baseInspection.getId());
        httpParams.put("inspection_type", "1");
        httpParams.putFileParams("accessoryFile", files);
        httpParams.put("signatureFile", signatureFile);
        return OkGo.<String>post(Api.INSPECTION_OK).params(httpParams).converter(new StringConvert()).adapt(new ObservableResponse<String>());
    }

    @Override
    public Observable<Response<String>> getReviewInspectionById(String basic_id) {
        return OkGo.<String>post(Api.SUBTYPE_BY_BASICID).params("basic_id", basic_id).converter(new StringConvert()).adapt(new ObservableResponse<String>());
    }


    @Override
    public Observable<Response<File>> downFile(String path) {
        return OkGo.<File>get(path).converter(new FileConvert()).adapt(new ObservableResponse<File>());
    }
}
