package com.bxgis.yczw.ui.login.model;


import com.bxgis.yczw.api.Api;
import com.bxgis.yczw.base.BaseModel;
import com.bxgis.yczw.ui.login.contract.LoginContract;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;

import io.reactivex.Observable;


/**
 * Created by xiaozhu on 2017/11/13.
 */
public class LoginModel implements LoginContract.Model, BaseModel {
//    SharedPreferences sp =MISystemApplication.getContext().getSharedPreferences("loginUser", Context.MODE_PRIVATE);
//    String token2 =  sp.getString("TOKEN1","");
    @Override
    public Observable<Response<String>> getUserInfo(String token) {
        return OkGo.<String>post(Api.USESR_INFO).headers("__userToken",token).converter(new StringConvert()).adapt(new ObservableResponse<String>());
    }

    @Override
    public Observable<Response<String>> login(String login_name, String password) {
        return OkGo.<String>post(Api.TOKEN_VERIFY).params("name", login_name)//
                .params("password", password).converter(new StringConvert()).adapt(new ObservableResponse<String>());
    }
}
