package com.bxgis.bxportal.ui.reinspection.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.bxgis.bxportal.MISystemApplication;
import com.bxgis.bxportal.api.Api;
import com.bxgis.bxportal.bean.HiddenDangerReViewBean;
import com.bxgis.bxportal.ui.reinspection.contract.ReinspectionContract;
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
public class ReinspectionModel implements ReinspectionContract.Model {
    @Override
    public Observable<Response<String>> commitReinspection(HiddenDangerReViewBean hiddenDangerReViewBean) {
        HttpParams params =new HttpParams();
        params.put("id",hiddenDangerReViewBean.getId());
        params.put("rectify_review_finish_desc",hiddenDangerReViewBean.getRectify_review_finish_desc());
        params.put("rectify_code",hiddenDangerReViewBean.getRectify_code());
        params.put("rectify_review_time",hiddenDangerReViewBean.getRectify_review_time());
        params.put("rectify_init_unit",hiddenDangerReViewBean.getRectify_init_unit());
        params.put("rectify_review_pictur",hiddenDangerReViewBean.getRectify_review_picture());
        params.put("rectify_review_person",hiddenDangerReViewBean.getRectify_duty_person());
        return OkGo.<String>post(Api.HIDDEN_DANGER_COMMIT).params(params).converter(new StringConvert()).adapt(new ObservableResponse<String>());
    }

    @Override
    public Observable<Response<String>> commitFile(List<File> file) {
        HttpParams httpParams =new HttpParams();
        httpParams.putFileParams("file",file);
        return OkGo.<String>post(Api.UPLOAD_FILE).params(httpParams).converter(new StringConvert()).adapt(new ObservableResponse<String>());
    }
}
