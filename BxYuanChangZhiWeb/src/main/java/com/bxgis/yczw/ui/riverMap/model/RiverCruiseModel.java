package com.bxgis.yczw.ui.riverMap.model;


import com.bxgis.yczw.MyApp;
import com.bxgis.yczw.api.Api;
import com.bxgis.yczw.base.BaseModel;
import com.bxgis.yczw.ui.riverMap.contract.RiverCruiseContract;
import com.bxgis.yczw.utils.SPUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;

import io.reactivex.Observable;


/**
 * Created by xiaozhu on 2017/11/13.
 */
public class RiverCruiseModel implements RiverCruiseContract.Model, BaseModel {
    String cookie ="_sso_token=" +  (String) SPUtils.get(MyApp.getContext(),"_sso_token","")+";path=/";
    @Override
    public Observable<Response<String>> getReverInfo(int longitude,int latitude) {

        return OkGo.<String>post(Api.BEACHS_LIST).headers("Cookie",cookie).params("lon",longitude).params("lat",latitude).converter(new StringConvert()).adapt(new ObservableResponse<String>());
    }

    @Override
    public Observable<Response<String>> sendLocationData(String bay_id, String patrol_start, String lon, String lat) {
        HttpParams params = new HttpParams();
        params.put("bay_id",bay_id);
        params.put("patrol_start",patrol_start);
        params.put("lon",lon);
        params.put("lat",lat);
        return OkGo.<String>post(Api.BEACHS_START).headers("Cookie",cookie).params(params).converter(new StringConvert()).adapt(new ObservableResponse<String>());
    }

    @Override
    public Observable<Response<String>> endLocationData(String patrol_id, String patrol_end, String lon, String lat, String location) {
        HttpParams params = new HttpParams();
        params.put("patrol_id",patrol_id);
        params.put("patrol_end",patrol_end);
        params.put("lon",lon);
        params.put("lat",lat);
        params.put("location",location);
        return OkGo.<String>post(Api.BEACHS_END).headers("Cookie",cookie).params(params).converter(new StringConvert()).adapt(new ObservableResponse<String>());
    }
//    SharedPreferences sp =MISystemApplication.getContext().getSharedPreferences("loginUser", Context.MODE_PRIVATE);
//    String token2 =  sp.getString("TOKEN1","");

}
