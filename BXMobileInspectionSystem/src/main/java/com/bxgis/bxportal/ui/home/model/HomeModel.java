package com.bxgis.bxportal.ui.home.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.bxgis.bxportal.api.Api;
import com.bxgis.bxportal.ui.home.contract.HomeContract;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.Converter;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;

import io.reactivex.Observable;

import static com.bxgis.bxportal.MISystemApplication.*;

/**
 * Created by xiaozhu on 2017/11/17.
 */
public class HomeModel implements HomeContract.Model {
    //返回banner
    @Override
    public Observable<Response<String>> getBanner(String type) {
        return OkGo.<String>post(Api.BANNER_LIST).params("type",type)
                .converter(new StringConvert()).adapt(new ObservableResponse<String>());
    }

    @Override
    public Observable<Response<String>> getHiddenDangerList(int currentPage, int pageSize) {
        HttpParams params =new HttpParams();
        params.put("currentPage",currentPage);
        params.put("pageSize",pageSize);
//        return OkGo.<String>post(Api.HIDDEN_DANGER_LIST).params(params).converter(new StringConvert()).adapt(new ObservableResponse<String>());
        return OkGo.<String>post(Api.HIDDEN_DANGER_LIST).params(params).converter(new Converter<String>() {
            @Override
            public String convertResponse(okhttp3.Response response) throws Throwable {
                return null;
            }
        }).adapt(new ObservableResponse<String>());
    }

    @Override
    public Observable<Response<String>> getProjectList() {
        return OkGo.<String>get(Api.PROJECT_LIST)
                .converter(new StringConvert()).adapt(new ObservableResponse<String>());
    }
}
