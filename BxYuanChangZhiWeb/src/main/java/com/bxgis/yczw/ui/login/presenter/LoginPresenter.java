package com.bxgis.yczw.ui.login.presenter;

import com.bxgis.yczw.MyApp;
import com.bxgis.yczw.base.BasePresenter;
import com.bxgis.yczw.bean.UserBean;
import com.bxgis.yczw.ui.login.LoginActivity;
import com.bxgis.yczw.ui.login.contract.LoginContract;
import com.bxgis.yczw.ui.login.model.LoginModel;
import com.bxgis.yczw.utils.SPUtils;
import com.bxgis.yczw.utils.StringUtil;
import com.google.gson.Gson;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xiaozhu on 2017/11/13.
 */
public class LoginPresenter extends BasePresenter<LoginActivity, LoginModel> implements LoginContract.Presenter {

    public LoginPresenter(LoginActivity loginActivity, LoginModel loginModel) {
        super(loginActivity, loginModel);
    }

    @Override
    public void login(String login_name, String password) {


        if (login_name.isEmpty() || password.isEmpty()) {
            mView.onLoginFaild("请输入正确的帐号或者密码");
            return;
        }
//        Map<String,String> map = new HashMap<>();
//        map.put("login_name", login_name);
//        map.put("password", password);
//       String responS= HttpConnectionUtil.getHttp().postRequset(Api.TOKEN_VERIFY,map);
//        if(!TextUtils.isEmpty(responS)){
//            try {
//            JSONObject jsonObject = new JSONObject(responS);
//            String msg = jsonObject.getString("msg");
//            if (msg.contains("用户合法")) {
//                String _sso_token = jsonObject.getString("_sso_token");
//                SharedPreferences mSharedPreferences = MISystemApplication.getContext().getSharedPreferences("loginUser", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = mSharedPreferences.edit();
//                editor.putString("TOKEN1",_sso_token);
//                editor.commit();
//                SPUtils.put(MISystemApplication.getContext(), "_sso_token", _sso_token);
//                System.out.println("获取到的token =="+_sso_token);
//                mView.toRequsetUserInfo(_sso_token); //发起获取用户信息
//            } else {
//                mView.onLoginFaild(msg);
//            }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }

        mModel.login(login_name, password).subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(@NonNull Disposable disposable) throws Exception {

            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<String>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onNext(@NonNull Response<String> stringResponse) {
                try {
                    /**{
                     "__code":10199,
                     "__msg":"验证通过",
                     "sso_token":"5a41e4d59e040d541c95febc@4e023fa9-b912-4714-8280-708c066bdf3f"
                     }*/
                    JSONObject jsonObject = new JSONObject(stringResponse.body());
                    String msg = jsonObject.getString("__msg");
                    if (msg.equals("验证通过")) {
                        String sso_token = jsonObject.getString("sso_token");
                        SPUtils.put(MyApp.getContext(), "_sso_token", sso_token);
//                        System.out.println("获取到的token =="+sso_token);
                        mView.toRequsetUserInfo(sso_token); //发起获取用户信息
                    } else {
                        mView.onLoginFaild(msg);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mView.onLoginFaild("网络异常，请检查网络");
            }

            @Override
            public void onComplete() {

            }
        });


    }

    @Override
    public void getUserInfo(String token) {
//        Map<String,String> map = new HashMap<>();
//        map.put("sso_token",token);
//        String responS1= HttpConnectionUtil.getHttp().postRequset(Api.USESR_INFO,map);
//
//        JSONObject jsonObject = null;
//        String msg = "";
//        try {
//            jsonObject = new JSONObject(responS1);
//            msg = jsonObject.getString("__msg");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        if (msg.equals("未登录")) {
////                    mView.loginAgain();//再次登陆
//            System.out.println("测试  未登录====");
//        } else {
//            Gson gson = new Gson();
//            UserBean userBean = gson.fromJson(responS1, UserBean.class);
//            if (null != userBean) {
//                SPUtils.put(MISystemApplication.getContext(), "user_id", userBean.getId());
//                SPUtils.put(MISystemApplication.getContext(), "real_name", userBean.getReal_name());
//                mView.showData(userBean);
//                mView.onLoginSucceed();
//            }
//        }


//
        mModel.getUserInfo(token).observeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(@NonNull Disposable disposable) throws Exception {
                //显示加载
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<String>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onNext(@NonNull Response<String> stringResponse) {
                // 	body:{"__msg":"未登录","__status":405}
                try {
                    Gson gson = new Gson();
                    UserBean userBean = gson.fromJson(stringResponse.body(), UserBean.class);
                    if (null != userBean&& StringUtil.isNotEmpty(userBean.getId(),true)) {
                        SPUtils.put(MyApp.getContext(), "user_id", userBean.getId());
                        SPUtils.put(MyApp.getContext(), "nice_name", userBean.getNice_name());
                        SPUtils.put(MyApp.getContext(), "login_name", userBean.getName());
                        mView.showData(userBean);
                        mView.onLoginSucceed();
                    } else {
                        mView.onLoginFaild("获取用户信息失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.onLoginFaild("获取用户信息失败");
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mView.onLoginFaild("网络异常，请求失败！");
            }

            @Override
            public void onComplete() {
                mView.onLoginSucceed();
            }
        });
    }
}
