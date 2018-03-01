package com.bxgis.bxportal.ui.home.model;

import com.bxgis.bxportal.MISystemApplication;
import com.bxgis.bxportal.api.Api;
import com.bxgis.bxportal.bean.AppInfonBean;
import com.bxgis.bxportal.bean.BaseResultBean;
import com.bxgis.bxportal.bean.InspectionProjectBean;
import com.bxgis.bxportal.net.callback.JsonConvert;
import com.bxgis.bxportal.ui.home.contract.HomeActivityContract;
import com.bxgis.bxportal.utils.SPUtils;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;

import java.lang.reflect.Type;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by xiaozhu on 2017/11/17.
 */
public class HomeActivityModel implements HomeActivityContract.Model {


    @Override
    public Observable<Response<String>> getCountInspection(String userId) {
        return OkGo.<String>get(Api.COUNT_INSPECTION) .params("_sso_token", SPUtils.get(MISystemApplication.getContext(),"_sso_token","").toString()).params("user_id",userId).converter(new StringConvert()).adapt(new ObservableResponse<String>());
    }
    Type type = new TypeToken<BaseResultBean<AppInfonBean>>() {}.getType();
    @Override
    public Observable<Response<BaseResultBean<AppInfonBean>>> getAppInfo() {
        return OkGo.<BaseResultBean<AppInfonBean>>get(Api.APP_UPDATE).params("_sso_token", SPUtils.get(MISystemApplication.getContext(),"_sso_token","").toString()).converter(new JsonConvert<BaseResultBean<AppInfonBean>>(type)).adapt(new ObservableResponse<BaseResultBean<AppInfonBean>>());
    }

//    @Override
//    public Observable<Response<String>> getProjectList() {
//      return OkGo.<String>get(Api.PROJECT_LIST).params("_sso_token", SPUtils.get(MISystemApplication.getContext(),"_sso_token","").toString()).converter(new StringConvert()).adapt(new ObservableResponse<String>());
//    }
@Override
public Observable<Response<BaseResultBean<List<InspectionProjectBean>>>> getProjectList() {
    return OkGo.<BaseResultBean<List<InspectionProjectBean>>>get(Api.PROJECT_LIST).params("_sso_token", SPUtils.get(MISystemApplication.getContext(),"_sso_token","").toString()).converter(new JsonConvert<BaseResultBean<List<InspectionProjectBean>>>(){}).adapt(new ObservableResponse<BaseResultBean<List<InspectionProjectBean>>>());
}
}
