package com.bxgis.yczw.ui.login.contract;

import com.bxgis.yczw.base.BaseModel;
import com.bxgis.yczw.base.BaseView;
import com.bxgis.yczw.bean.UserBean;
import com.lzy.okgo.model.Response;

import io.reactivex.Observable;

/**
 * Created by xiaozhu on 2017/11/13.
 */
public interface LoginContract {
    interface Model extends BaseModel {
        Observable<Response<String>>  getUserInfo(String token);
        Observable<Response<String>> login(String login_name, String password);

    }

    interface View extends BaseView {
        void showData(UserBean userBean);
        void toRequsetUserInfo(String token);
        void onLoginSucceed();     //登录成功
        void onLoginFaild(String msg);       //登录失败
        void loginAgain();

    }

    interface Presenter {
        void login(String login_name, String password);
        void getUserInfo(String token);
    }
}
