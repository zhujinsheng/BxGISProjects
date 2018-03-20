package com.bxgis.yczw.ui.riverMap.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.bxgis.yczw.MyApp;
import com.bxgis.yczw.base.BasePresenter;
import com.bxgis.yczw.bean.BeachAndBenchlandBean;
import com.bxgis.yczw.bean.BeachTrackBean;
import com.bxgis.yczw.bean.CommonBeachBean;
import com.bxgis.yczw.bean.UserBean;
import com.bxgis.yczw.ui.login.LoginActivity;
import com.bxgis.yczw.ui.riverMap.RiverCruiseActivity;
import com.bxgis.yczw.ui.riverMap.contract.RiverCruiseContract;
import com.bxgis.yczw.ui.riverMap.model.RiverCruiseModel;
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
public class RiverCruisePresenter extends BasePresenter<RiverCruiseActivity, RiverCruiseModel> implements RiverCruiseContract.Presenter {

    public RiverCruisePresenter(RiverCruiseActivity riverCruiseActivity, RiverCruiseModel loginModel) {
        super(riverCruiseActivity, loginModel);
    }

    @Override
    public void getReverInfo(int longitude,int latitude ) {
       mModel.getReverInfo(longitude,latitude).observeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(@NonNull Disposable disposable) throws Exception {
                //显示加载
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<String>>() {
           @Override
           public void onSubscribe(@NonNull Disposable d) {

           }

           @Override
           public void onNext(@NonNull Response<String> stringResponse) {

               JSONObject jsonObject = null;
               int msg ;
               try {
                   jsonObject = new JSONObject(stringResponse.body());
                   msg = jsonObject.getInt("code");
                   StringBuffer stringBuffer =new StringBuffer();
                   stringBuffer.append(jsonObject.getString("obj"));
               if (msg ==200){
                   Gson gson = new Gson();

                   CommonBeachBean<BeachAndBenchlandBean> beachBean = new CommonBeachBean<BeachAndBenchlandBean>();
                   beachBean =gson.fromJson(stringBuffer.toString(), CommonBeachBean.class);
                   if (null != beachBean) {
                       mView.showData(beachBean);
                       mView.onSucceed();
                   }
               }
               } catch (JSONException e) {
                   e.printStackTrace();
                   mView.onFaild("数据异常！");
               }

           }

           @Override
           public void onError(@NonNull Throwable e) {
               mView.onFaild("请求失败");
           }

           @Override
           public void onComplete() {

           }
       });
    }

    @Override
    public void sendLocationData(String bay_id, String patrol_start, String lon, String lat) {
        mModel.sendLocationData(bay_id, patrol_start, lon, lat).observeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(@NonNull Disposable disposable) throws Exception {
                //显示加载
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<String>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Response<String> stringResponse) {
                JSONObject jsonObject = null;
                int msg;
                try {
                    jsonObject = new JSONObject(stringResponse.body());
                    msg = jsonObject.getInt("code");
                    if(msg==200){
                    StringBuffer stringBuffer =new StringBuffer();
                    stringBuffer.append(jsonObject.getString("obj"));
                    Gson gson =new Gson();
                        BeachTrackBean beachTrackBean =gson.fromJson(stringBuffer.toString(), BeachTrackBean.class);
                        if(StringUtil.isNotEmpty(beachTrackBean,true)){
                            mView.uploadLocationSucced(beachTrackBean);
                        }else {
                            mView.onFaild("提交开始巡查轨迹失败");
                        }
                    }else{
                        mView.onFaild("服务器返回异常！");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    mView.onFaild("提交开始巡查轨迹失败");
                }
            }

                @Override
            public void onError(@NonNull Throwable e) {
                    mView.onFaild("网络异常，请求失败！");
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void endLocationData(String patrol_id, String patrol_end, String lon, String lat, String location) {
        mModel.endLocationData(patrol_id, patrol_end, lon, lat, location).observeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(@NonNull Disposable disposable) throws Exception {
                //显示加载

            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<String>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Response<String> stringResponse) {
                JSONObject jsonObject = null;
                int msg;
                try {
                    jsonObject = new JSONObject(stringResponse.body());
                    msg = jsonObject.getInt("code");
                    if(msg==200){
                        mView.endLocationSucced("巡查已完成");
//                        StringBuffer stringBuffer =new StringBuffer();
//                        stringBuffer.append(jsonObject.getString("obj"));
//                        Gson gson =new Gson();
//                        BeachTrackBean beachTrackBean =gson.fromJson(stringBuffer.toString(), BeachTrackBean.class);
//                        if(StringUtil.isNotEmpty(beachTrackBean,true)){
//                            mView.uploadLocationSucced(beachTrackBean);
//                        }else {
//                            mView.onFaild("提交开始巡查轨迹失败");
//                        }
                    }else{
                        mView.onFaild("服务器返回异常！");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    mView.onFaild("巡查结束提交失败！");
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mView.onFaild("网络异常,请检查！");
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
