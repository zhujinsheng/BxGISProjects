package com.bxgis.bxportal.ui.inspection.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.bxgis.bxportal.MISystemApplication;
import com.bxgis.bxportal.api.Api;
import com.bxgis.bxportal.ui.inspection.contract.InspectionContract;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;

import io.reactivex.Observable;

/**
 * Created by xiaozhu on 2017/11/17.
 */
public class InspectionModel implements InspectionContract.Model {
    @Override
    public Observable<Response<String>> getInspections(String user_id, String inspection_type, int type, long currentPage) {
        HttpParams httpParams = new HttpParams();
//        httpParams.put("user_id", user_id);
        httpParams.put("inspection_type", inspection_type);
        httpParams.put("type", type);
        httpParams.put("currentPage", currentPage);
        Observable<Response<String>> resp = OkGo.<String>get(Api.INSPECTION_LIST)
                .params(httpParams).converter(new StringConvert()).adapt(new ObservableResponse<String>());
        return resp;
    }
}
