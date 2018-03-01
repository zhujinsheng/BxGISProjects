package com.bxgis.bxportal.ui.inspection.presenter;

import com.bxgis.bxportal.base.BasePresenter;
import com.bxgis.bxportal.bean.BaseInspection;
import com.bxgis.bxportal.ui.inspection.SubInspectionFragment;
import com.bxgis.bxportal.ui.inspection.contract.InspectionContract;
import com.bxgis.bxportal.ui.inspection.model.InspectionModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xiaozhu on 2017/11/17.
 */
public class InspectionPresenter extends BasePresenter<SubInspectionFragment, InspectionModel> implements InspectionContract.Presenter {
    public InspectionPresenter(SubInspectionFragment mView, InspectionModel mModel) {
        super(mView, mModel);
    }


    @Override
    public void getInspections(String user_id, String inspection_type, int type,long currentPages) {
        System.out.println("请求次数22222");

//                Map<String,String> map = new HashMap<>();
//        map.put("inspection_type",inspection_type);
//        map.put("type", String.valueOf(type));
//        map.put("currentPage", String.valueOf(currentPages));
//        String token = SPUtils.get(MISystemApplication.getContext(),"_sso_token","").toString();
//        SharedPreferences sp =MISystemApplication.getContext().getSharedPreferences("loginUser", Context.MODE_PRIVATE);
//        String token1 =  sp.getString("TOKEN1","");
//        map.put("_sso_token",token1);
//        System.out.println("请求次数22222444"+token);
//        System.out.println("请求次数2222token1"+token1);
//        System.out.println("请求次数22222555"+MISystemApplication.getToken());
//        String responS4= HttpConnectionUtil.getHttp().getRequset1(Api.INSPECTION_LIST,map);
//        mView.showEmptyData("暂无再多数据");
//        if(TextUtils.isEmpty(responS4))
//            return;
//        JsonObject jsonObject1 = new JsonParser().parse(responS4).getAsJsonObject();
//        JsonArray jsonArray1 = jsonObject1.getAsJsonArray("rows");
//        List<BaseInspection> listI = new ArrayList<BaseInspection>();
//        if(null!=jsonArray1&&jsonArray1.size()>0){
//            for(JsonElement user : jsonArray1){
//                BaseInspection mBaseInspection = new Gson().fromJson(user, new TypeToken<BaseInspection>() {
//                }.getType());
//                listI.add(mBaseInspection);
//            }
//            mView.showInspectionData(listI);
//        }else{
//            mView.showEmptyData("暂无再多数据");
//        }

        mModel.getInspections(user_id, inspection_type, type,currentPages).subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
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
                JsonObject jsonObject1 = new JsonParser().parse(stringResponse.body().toString()).getAsJsonObject();
                JsonArray jsonArray1 = jsonObject1.getAsJsonArray("rows");
                List<BaseInspection> listI = new ArrayList<BaseInspection>();
                if(null!=jsonArray1&&jsonArray1.size()>0){
                    for(JsonElement user : jsonArray1){
                        BaseInspection mBaseInspection = new Gson().fromJson(user, new TypeToken<BaseInspection>() {
                        }.getType());
                        listI.add(mBaseInspection);
                    }
                    mView.showInspectionData(listI);
                }else{
                   mView.showEmptyData("暂无再多数据");
                }
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(@NonNull Throwable e) {
                mView.onFaild("网络异常,获取数据失败!");
            }
        });
    }
}
