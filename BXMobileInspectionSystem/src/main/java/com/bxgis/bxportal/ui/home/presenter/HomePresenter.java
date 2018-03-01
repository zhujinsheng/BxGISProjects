package com.bxgis.bxportal.ui.home.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.bxgis.bxportal.MISystemApplication;
import com.bxgis.bxportal.api.Api;
import com.bxgis.bxportal.base.BasePresenter;
import com.bxgis.bxportal.bean.BannerOrTypeBean;
import com.bxgis.bxportal.bean.HiddenDangerReViewBean;
import com.bxgis.bxportal.bean.InspectionProjectBean;
import com.bxgis.bxportal.ui.home.HomeFragment;
import com.bxgis.bxportal.ui.home.contract.HomeContract;
import com.bxgis.bxportal.ui.home.model.HomeModel;
import com.bxgis.bxportal.utils.HttpConnectionUtil;
import com.bxgis.bxportal.utils.SPUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xiaozhu on 2017/11/17.
 */
public class HomePresenter extends BasePresenter<HomeFragment,HomeModel> implements HomeContract.Presenter {
    public HomePresenter(HomeFragment mView, HomeModel mModel) {
        super(mView, mModel);
    }
    @Override
    public void getBanner(String type) {
//        Map<String,String> map = new HashMap<>();
//        map.put("type",type);
//        map.put("_sso_token", token2);
//        String respone = HttpConnectionUtil.getHttp().postRequset(Api.BANNER_LIST,map);
//        JsonObject jsonObject = new JsonParser().parse(respone).getAsJsonObject();
//        JsonArray jsonArray = jsonObject.getAsJsonArray("data");
//        List<BannerOrTypeBean> listB = new ArrayList<BannerOrTypeBean>();
//        if(null!=jsonArray&&jsonArray.size()>0){
//            for(JsonElement user : jsonArray){
//                BannerOrTypeBean bannerOrTypeBean = new Gson().fromJson(user, new TypeToken<BannerOrTypeBean>() {
//                }.getType());
//                listB.add(bannerOrTypeBean);
//            }
//            mView.showBannerData(listB);
//        }else{
//            mView.onFaild("暂无Banner图数据");
//        }

        mModel.getBanner(type).subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(@NonNull Disposable disposable) throws Exception {
                //发起加载框
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<String>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                 addDisposable(d);
            }

            @Override
            public void onNext(@NonNull Response<String> stringResponse) {
                JsonObject jsonObject = new JsonParser().parse(stringResponse.body().toString()).getAsJsonObject();
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                List<BannerOrTypeBean> listB = new ArrayList<BannerOrTypeBean>();
                if(null!=jsonArray&&jsonArray.size()>0){
                    for(JsonElement user : jsonArray){
                        BannerOrTypeBean bannerOrTypeBean = new Gson().fromJson(user, new TypeToken<BannerOrTypeBean>() {
                        }.getType());
                        listB.add(bannerOrTypeBean);
                    }
                    mView.showBannerData(listB);
                }else{
                    mView.onFaild("暂无Banner图数据");
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
               mView.onFaild("获取Banner图数据失败");
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void getProjectList() {
//        Map<String,String> map = new HashMap<>();
//        map.put("_sso_token", token2);
//        String respone1 = HttpConnectionUtil.getHttp().postRequset(Api.PROJECT_LIST,map);
//        JsonObject jsonObject2 = new JsonParser().parse(respone1).getAsJsonObject();
//        JsonArray jsonArray = jsonObject2.getAsJsonArray("data");
//        List<InspectionProjectBean> listP = new ArrayList<InspectionProjectBean>();
//        if(null!=jsonArray&&jsonArray.size()>0){
//            for(JsonElement user : jsonArray){
//                InspectionProjectBean mInspeProje= new Gson().fromJson(user, new TypeToken<InspectionProjectBean>() {
//                }.getType());
//                listP.add(mInspeProje);
//            }
//            mView.showProjectData(listP);
//        }else{
//            mView.onFaild("暂无巡检项目数据");
//        }

        mModel.getProjectList().subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
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
                JsonObject jsonObject2 = new JsonParser().parse(stringResponse.body().toString()).getAsJsonObject();
                JsonArray jsonArray = jsonObject2.getAsJsonArray("data");
                List<InspectionProjectBean> listP = new ArrayList<InspectionProjectBean>();
                if(null!=jsonArray&&jsonArray.size()>0){
                    for(JsonElement user : jsonArray){
                        InspectionProjectBean mInspeProje= new Gson().fromJson(user, new TypeToken<InspectionProjectBean>() {
                        }.getType());
                        listP.add(mInspeProje);
                    }
                    mView.showProjectData(listP);
                }else{
                    mView.onFaild("暂无巡检项目数据");
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void getHiddenDangerList(int currentPage, int pageSize) {
              Map<String,String> map = new HashMap<>();
      map.put("_sso_token", SPUtils.get(MISystemApplication.getContext(),"_sso_token","").toString());
        String respone2 = HttpConnectionUtil.getHttp().postRequset(Api.HIDDEN_DANGER_LIST,map);
        JsonObject jsonObject1 = new JsonParser().parse(respone2).getAsJsonObject();
        JsonArray jsonArray1 = jsonObject1.getAsJsonArray("rows");
        List<HiddenDangerReViewBean> listI = new ArrayList<HiddenDangerReViewBean>();
        if(null!=jsonArray1&&jsonArray1.size()>0){
            listI.clear();
            for(JsonElement user : jsonArray1){
                HiddenDangerReViewBean mBaseInspection = new Gson().fromJson(user, new TypeToken<HiddenDangerReViewBean>() {
                }.getType());
                listI.add(mBaseInspection);
            }
            mView.showReviewData(listI);
        }else{
            mView.showReviewData(listI);
            mView.onFaild("暂无数据");
        }

//        mModel.getHiddenDangerList(currentPage, pageSize).subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
//            @Override
//            public void accept(@NonNull Disposable disposable) throws Exception {
//                //发起加载框
//            }
//        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<String>>() {
//            @Override
//            public void onSubscribe(@NonNull Disposable d) {
//                addDisposable(d);
//            }
//
//            @Override
//            public void onNext(@NonNull Response<String> stringResponse) {
//                Log.d("数据返回","ssssss");
////                {"__status":200,"__msg":"success","totalPage":1,"totalRow":2,"currentPage":1,"pageSize":10,"rows":[{"__status":200,"__msg":"success","id":"2072","inspection_name":"2018年上半年巡检会议召开（一）","company_id":"0","user_id":"62","inspection_project":null,"project_content":null,"expiration_reminder":"1","begin_time":1506700800000,"end_time":1506787200000,"type":0,"inspection_type":1,"inspector":"admin","other_inspectors":null,"initiator":"超级管理员","create_time":1510798804000,"other_requests":"请及时安排落实","accessory":"http://localhost:8080/file/eda7dc32-5236-4226-a695-facfbed22062.png","picture_signature":null,"inspection_feedback":null,"feedback_accessory":null,"location":null,"co_code":"","planSubProjectList":null},{"__status":200,"__msg":"success","id":"2073","inspection_name":"2018年上半年巡检会议召开（二）","company_id":"0","user_id":"62","inspection_project":null,"project_content":null,"expiration_reminder":"1","begin_time":1507132800000,"end_time":1509120000000,"type":0,"inspection_type":1,"inspector":"admin","other_inspectors":null,"initiator":"超级管理员","create_time":1507621510000,"other_requests":"各部门一把手参会!","accessory":"http://localhost:8080/file/eda7dc32-5236-4226-a695-facfbed22062.png","picture_signature":null,"inspection_feedback":null,"feedback_accessory":null,"location":null,"co_code":null,"planSubProjectList":null}]}
////                JsonObject jsonObject1 = new JsonParser().parse(stringResponse.body().toString()).getAsJsonObject();
////                JsonArray jsonArray1 = jsonObject1.getAsJsonArray("rows");
////                List<HiddenDangerReViewBean> listI = new ArrayList<HiddenDangerReViewBean>();
////                if(null!=jsonArray1&&jsonArray1.size()>0){
////                    listI.clear();
////                    for(JsonElement user : jsonArray1){
////                        HiddenDangerReViewBean mBaseInspection = new Gson().fromJson(user, new TypeToken<HiddenDangerReViewBean>() {
////                        }.getType());
////                        listI.add(mBaseInspection);
////                    }
////                    mView.showReviewData(listI);
////                }else{
////                    mView.showReviewData(listI);
////                    mView.onFaild("暂无数据");
////                }
//            }
//
//            @Override
//            public void onError(@NonNull Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
    }

}
