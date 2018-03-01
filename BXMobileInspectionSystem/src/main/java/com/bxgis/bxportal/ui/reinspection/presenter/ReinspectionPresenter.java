package com.bxgis.bxportal.ui.reinspection.presenter;

import com.bxgis.bxportal.base.BasePresenter;
import com.bxgis.bxportal.bean.HiddenDangerReViewBean;

import com.bxgis.bxportal.ui.reinspection.ReinspectionFeedBackActivity;
import com.bxgis.bxportal.ui.reinspection.contract.ReinspectionContract;
import com.bxgis.bxportal.ui.reinspection.model.ReinspectionModel;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lzy.okgo.model.Response;

import java.io.File;
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
public class ReinspectionPresenter extends BasePresenter<ReinspectionFeedBackActivity,ReinspectionModel> implements ReinspectionContract.Presenter {
    public ReinspectionPresenter(ReinspectionFeedBackActivity mView, ReinspectionModel mModel) {
        super(mView, mModel);
    }

    @Override
    public void commitFile(List<File> file) {
        mModel.commitFile(file).subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
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
                JsonElement bean = jsonObject2.get("url");
                if(null != bean){
                    String images=bean.getAsString();
                    mView.onSucceedFile(images);
                }
                else{
                    mView.onFaild("暂无附件路径返回");
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
    public void commitReinspection(HiddenDangerReViewBean hiddenDangerReViewBean) {
        mModel.commitReinspection(hiddenDangerReViewBean).subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
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
                JsonObject jsonObject2 = new JsonParser().parse(stringResponse.body().toString()).getAsJsonObject();
                JsonElement bean1 = jsonObject2.get("__msg");
                if(null != bean1&&bean1.getAsString().equals("success")){
                    mView.onSucceed();

                }else{
                    mView.onFaild("复查反馈失败!");
                } }catch (Exception e){
                    mView.onFaild("复查反馈失败!");
                    e.printStackTrace();
                }
//                List<InspectionProjectBean> listP = new ArrayList<InspectionProjectBean>();
//                if(null!=jsonArray&&jsonArray.size()>0){
//                    for(JsonElement user : jsonArray){
//                        InspectionProjectBean mInspeProje= new Gson().fromJson(user, new TypeToken<InspectionProjectBean>() {
//                        }.getType());
//                        listP.add(mInspeProje);
//                    }
//
//                }else{
//                    mView.onFaild("暂无巡检项目数据");
//                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mView.onFaild("网络异常!");
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
