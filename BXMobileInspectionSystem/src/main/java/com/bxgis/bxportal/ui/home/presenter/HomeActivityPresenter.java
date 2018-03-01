package com.bxgis.bxportal.ui.home.presenter;

import android.util.Log;

import com.bxgis.bxportal.MISystemApplication;
import com.bxgis.bxportal.api.Api;
import com.bxgis.bxportal.base.BasePresenter;
import com.bxgis.bxportal.bean.AppInfonBean;
import com.bxgis.bxportal.bean.BaseResultBean;
import com.bxgis.bxportal.bean.InspectionCountBean;
import com.bxgis.bxportal.bean.InspectionProjectBean;
import com.bxgis.bxportal.ui.home.MainActivity;
import com.bxgis.bxportal.ui.home.contract.HomeActivityContract;
import com.bxgis.bxportal.ui.home.model.HomeActivityModel;
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
public class HomeActivityPresenter extends BasePresenter<MainActivity, HomeActivityModel> implements HomeActivityContract.Presenter {
    public HomeActivityPresenter(MainActivity mView, HomeActivityModel mModel) {
        super(mView, mModel);
    }


    @Override
    public void getCountInspection(String userId) {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", userId);
        map.put("_sso_token", SPUtils.get(MISystemApplication.getContext(), "_sso_token", "").toString());
        String responS4 = HttpConnectionUtil.getHttp().postRequset(Api.COUNT_INSPECTION, map);
        JsonObject jsonObject = new JsonParser().parse(responS4).getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray("data");
        if (null != jsonArray && jsonArray.size() > 0) {
            for (JsonElement user : jsonArray) {
                InspectionCountBean bannerOrTypeBean = new Gson().fromJson(user, new TypeToken<InspectionCountBean>() {
                }.getType());
                mView.showCount(bannerOrTypeBean);
            }
        } else {
            mView.onFaild("获取巡检数量失败");
        }

//        mModel.getCountInspection(userId).subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
//            @Override
//            public void accept(@NonNull Disposable disposable) throws Exception {
//
//            }
//        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<String>>() {
//            @Override
//            public void onSubscribe(@NonNull Disposable d) {
//                addDisposable(d);
//            }
//
//            @Override
//            public void onNext(@NonNull Response<String> stringResponse) {
//                JsonObject jsonObject = new JsonParser().parse(stringResponse.body().toString()).getAsJsonObject();
//                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
//                if (null != jsonArray && jsonArray.size() > 0) {
//                    for (JsonElement user : jsonArray) {
//                        InspectionCountBean bannerOrTypeBean = new Gson().fromJson(user, new TypeToken<InspectionCountBean>() {
//                        }.getType());
//                        mView.showCount(bannerOrTypeBean);
//                    }
//                } else {
//                    mView.onFaild("获取巡检数量失败");
//                }
//
//            }
//
//            @Override
//            public void onError(@NonNull Throwable e) {
//                mView.onFaild("网络异常，请检查网络");
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });

    }

    @Override
    public void getAppInfo() {
//        Map<String,String> map = new HashMap<>();
//        map.put("_sso_token", SPUtils.get(MISystemApplication.getContext(),"_sso_token","").toString());
//        String responS5= HttpConnectionUtil.getHttp().postRequset(Api.USESR_INFO,map);
//        JsonObject jsonObject = new JsonParser().parse(responS5).getAsJsonObject();
//        JsonElement user = jsonObject.get("data");
//        if (null != user) {
//            AppInfonBean appInfonBean = new Gson().fromJson(user, new TypeToken<AppInfonBean>() {
//            }.getType());
//            mView.showAppInfo(appInfonBean);
//        } else
//        {
//            mView.onFaild("获取App版本信息失败");
//        }

        mModel.getAppInfo().subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(@NonNull Disposable disposable) throws Exception {

            }
        }).observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Observer<Response<BaseResultBean<AppInfonBean>>>() {
                              @Override
                              public void onSubscribe(@NonNull Disposable d) {
                                  addDisposable(d);
                              }

                              @Override
                              public void onNext(@NonNull Response<BaseResultBean<AppInfonBean>> appInfonBeanResponse) {
                                  Log.d("jieguo",appInfonBeanResponse.body().getData().getApp_name());
                              }

//                              @Override
//                              public void onNext(@NonNull Response<String> stringResponse) {
//                                  JsonObject jsonObject = new JsonParser().parse(stringResponse.body().toString()).getAsJsonObject();
//                                  JsonElement user = jsonObject.get("data");
//                                  if (null != user) {
//                                      AppInfonBean appInfonBean = new Gson().fromJson(user, new TypeToken<AppInfonBean>() {
//                                      }.getType());
//                                      mView.showAppInfo(appInfonBean);
//                                  } else {
//                                      mView.onFaild("获取App版本信息失败");
//                                  }
//                              }

                              @Override
                              public void onError(@NonNull Throwable e) {
                                  mView.onFaild("网络异常，请检查网络");
                              }

                              @Override
                              public void onComplete() {
                                  mView.onFaild("网络异常，请检查网络");
                              }
                          }

                );

    }

    @Override
    public void getProjectList() {

//        mModel.getProjectList().subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
//            @Override
//            public void accept(@NonNull Disposable disposable) throws Exception {
//
//            }
//        }).observeOn(AndroidSchedulers.mainThread()).
//                subscribe(new Observer<Response<String>>() {
//                              @Override
//                              public void onSubscribe(@NonNull Disposable d) {
//                                  addDisposable(d);
//                              }
//
//                              @Override
//                              public void onNext(@NonNull Response<String> stringResponse) {
//                                  JsonObject jsonObject2 = new JsonParser().parse(stringResponse.body().toString()).getAsJsonObject();
//                                  JsonArray jsonArray = jsonObject2.getAsJsonArray("data");
//                                  List<InspectionProjectBean> listP = new ArrayList<InspectionProjectBean>();
//                                  if (null != jsonArray && jsonArray.size() > 0) {
//                                      for (JsonElement user : jsonArray) {
//                                          InspectionProjectBean mInspeProje = new Gson().fromJson(user, new TypeToken<InspectionProjectBean>() {
//                                          }.getType());
//                                          listP.add(mInspeProje);
//                                      }
//                                      mView.showProjectList(listP);
//                                  } else {
//                                      mView.onFaild("暂无巡检项目数据");
//                                  }
//                              }
//
//                              @Override
//                              public void onError(@NonNull Throwable e) {
//                                  mView.onFaild("网络异常，请检查网络");
//                              }
//
//                              @Override
//                              public void onComplete() {
//
//                              }
//                          }
//
//                );
        mModel.getProjectList().subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(@NonNull Disposable disposable) throws Exception {

            }
        }).observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Observer<Response<BaseResultBean<List<InspectionProjectBean>>>>() {
                              @Override
                              public void onSubscribe(@NonNull Disposable d) {
                                  addDisposable(d);
                              }

                    @Override
                    public void onNext(@NonNull Response<BaseResultBean<List<InspectionProjectBean>>> baseResultBeanResponse) {
                        Log.d("jieguo",String.valueOf(baseResultBeanResponse.body().getData().size()));
                    }

//                    @Override
//                              public void onNext(@NonNull Response<String> stringResponse) {
//                                  JsonObject jsonObject2 = new JsonParser().parse(stringResponse.body().toString()).getAsJsonObject();
//                                  JsonArray jsonArray = jsonObject2.getAsJsonArray("data");
//                                  List<InspectionProjectBean> listP = new ArrayList<InspectionProjectBean>();
//                                  if (null != jsonArray && jsonArray.size() > 0) {
//                                      for (JsonElement user : jsonArray) {
//                                          InspectionProjectBean mInspeProje = new Gson().fromJson(user, new TypeToken<InspectionProjectBean>() {
//                                          }.getType());
//                                          listP.add(mInspeProje);
//                                      }
//                                      mView.showProjectList(listP);
//                                  } else {
//                                      mView.onFaild("暂无巡检项目数据");
//                                  }
//                              }

                              @Override
                              public void onError(@NonNull Throwable e) {
                                  mView.onFaild("网络异常，请检查网络");
                              }

                              @Override
                              public void onComplete() {
                                  Log.d("jieguo111", String.valueOf(111));
                              }
                          }

                );

    }
}
