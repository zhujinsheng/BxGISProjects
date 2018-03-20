package com.bxgis.yczw.ui.main.presenter;


import com.bxgis.yczw.base.BasePresenter;
import com.bxgis.yczw.ui.main.MainActivity;
import com.bxgis.yczw.ui.main.contract.HomeActivityContract;
import com.bxgis.yczw.ui.main.model.HomeActivityModel;

/**
 * Created by xiaozhu on 2017/11/17.
 */
public class HomeActivityPresenter extends BasePresenter<MainActivity, HomeActivityModel> implements HomeActivityContract.Presenter {
    public HomeActivityPresenter(MainActivity mView, HomeActivityModel mModel) {
        super(mView, mModel);
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

//        mModel.getAppInfo().subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
//            @Override
//            public void accept(@NonNull Disposable disposable) throws Exception {
//
//            }
//        }).observeOn(AndroidSchedulers.mainThread()).
//                subscribe(new Observer<Response<BaseResultBean<AppInfonBean>>>() {
//                              @Override
//                              public void onSubscribe(@NonNull Disposable d) {
//                                  addDisposable(d);
//                              }
//
//                              @Override
//                              public void onNext(@NonNull Response<BaseResultBean<AppInfonBean>> appInfonBeanResponse) {
//                                  Log.d("jieguo",appInfonBeanResponse.body().getData().getApp_name());
//                              }
//
////                              @Override
////                              public void onNext(@NonNull Response<String> stringResponse) {
////                                  JsonObject jsonObject = new JsonParser().parse(stringResponse.body().toString()).getAsJsonObject();
////                                  JsonElement user = jsonObject.get("data");
////                                  if (null != user) {
////                                      AppInfonBean appInfonBean = new Gson().fromJson(user, new TypeToken<AppInfonBean>() {
////                                      }.getType());
////                                      mView.showAppInfo(appInfonBean);
////                                  } else {
////                                      mView.onFaild("获取App版本信息失败");
////                                  }
////                              }
//                              @Override
//                              public void onError(@NonNull Throwable e) {
//                                  mView.onFaild("网络异常，请检查网络");
//                              }
//                              @Override
//                              public void onComplete() {
//                                  mView.onFaild("网络异常，请检查网络");
//                              }
//                          }
//                );

    }

    @Override
    public void getProjectList() {

    }

}
