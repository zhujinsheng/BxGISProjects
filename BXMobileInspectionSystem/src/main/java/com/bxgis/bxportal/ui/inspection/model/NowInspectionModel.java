package com.bxgis.bxportal.ui.inspection.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.bxgis.bxportal.MISystemApplication;
import com.bxgis.bxportal.api.Api;
import com.bxgis.bxportal.bean.BaseInspection;
import com.bxgis.bxportal.ui.inspection.contract.NowInspectionContract;
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
public class NowInspectionModel implements NowInspectionContract.Model {

    @Override
    public Observable<Response<String>> getOrgUsers() {
        HttpParams httpParams = new HttpParams();
        return OkGo.<String>post(Api.ORG_USERS_LIST).params(httpParams).converter(new StringConvert()).adapt(new ObservableResponse<String>());
    }

    @Override
    public Observable<Response<String>> getOrgCompany() {
        return OkGo.<String>post(Api.COMPANY_LIST).params("pageSize", 30).converter(new StringConvert()).adapt(new ObservableResponse<String>());
    }

    @Override
    public Observable<Response<String>> commit(BaseInspection baseInspection) {
        HttpParams httpParams = new HttpParams();

        httpParams.put("inspection_name", baseInspection.getInspection_name());
        httpParams.put("user_id", baseInspection.getUser_id());
        httpParams.put("inspector", baseInspection.getInspector());
        httpParams.put("inspector_id", baseInspection.getInspector_id());
        httpParams.put("project_content", baseInspection.getProject_content());
        httpParams.put("company_id", baseInspection.getCompany_id());
        httpParams.put("company_name", baseInspection.getCompany_name());
        httpParams.put("type", baseInspection.getType());
        httpParams.put("inspection_type", 0);
        httpParams.put("begin_time", baseInspection.getBegin_time());
        httpParams.put("end_time", baseInspection.getEnd_time());
        httpParams.put("inspection_project", baseInspection.getInspection_project());
        httpParams.put("other_inspectors", baseInspection.getOther_inspectors());
        httpParams.put("initiator", baseInspection.getInitiator());
        httpParams.put("other_requests", baseInspection.getOther_requests());
        httpParams.put("location", baseInspection.getLocation());
        httpParams.put("picture_signature", baseInspection.getPicture_signature());
        httpParams.put("accessory", baseInspection.getAccessory());
        return OkGo.<String>get(Api.NOW_INSPECTION_RELEASE1).params(httpParams).converter(new StringConvert()).adapt(new ObservableResponse<String>());
    }

    @Override
    public Observable<Response<String>> getSubProjects(String project_id) {
        return OkGo.<String>post(Api.SUBPROJECT_LIST).params("project_id", project_id).converter(new StringConvert()).adapt(new ObservableResponse<String>());
    }

    @Override
    public Observable<Response<String>> commitFile2(List<File> files) {
        HttpParams httpParams =new HttpParams();
        httpParams.putFileParams("file",files);
        return OkGo.<String>post(Api.UPLOAD_FILE).params(httpParams).converter(new StringConvert()).adapt(new ObservableResponse<String>());
    }
}
