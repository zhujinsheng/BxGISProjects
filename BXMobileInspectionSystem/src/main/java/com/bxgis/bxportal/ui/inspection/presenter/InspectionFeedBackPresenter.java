package com.bxgis.bxportal.ui.inspection.presenter;

import android.text.TextUtils;

import com.bxgis.bxportal.base.BasePresenter;
import com.bxgis.bxportal.bean.BaseInspection;
import com.bxgis.bxportal.bean.BaseInspectionSubProject;
import com.bxgis.bxportal.ui.inspection.InspectionFeedBackActivity;
import com.bxgis.bxportal.ui.inspection.contract.InspectionFeedBackContract;
import com.bxgis.bxportal.ui.inspection.model.InspectionFeedBackModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
public class InspectionFeedBackPresenter extends BasePresenter<InspectionFeedBackActivity, InspectionFeedBackModel> implements InspectionFeedBackContract.Presenter {

    public InspectionFeedBackPresenter(InspectionFeedBackActivity mView, InspectionFeedBackModel mModel) {
        super(mView, mModel);
    }


    @Override
    public void commit(BaseInspection baseInspection, List<File> files, File signatureFile) {
        mModel.commit(baseInspection, files, signatureFile).subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
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
                try {
                    JSONObject obj = new JSONObject(stringResponse.body());
                    String msg = obj.getString("__msg");
                    if ((!TextUtils.isEmpty(msg))&&msg.equalsIgnoreCase("success")) {
                        mView.onSucceed();
                    } else {
                        mView.onFaild("反馈失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            @Override
            public void onComplete() {
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mView.onFaild("获取巡检数据失败");

            }
        });
    }

    @Override
    public void getReviewInspectionById(String basic_id) {
        mModel.getReviewInspectionById(basic_id).subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
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
                JsonArray jsonArray1 = jsonObject1.getAsJsonArray("data");
                List<BaseInspectionSubProject> listI = new ArrayList<BaseInspectionSubProject>();
                if (null != jsonArray1 && jsonArray1.size() > 0) {
                    for (JsonElement user : jsonArray1) {
                        BaseInspectionSubProject mBaseInspection = new Gson().fromJson(user, new TypeToken<BaseInspectionSubProject>() {
                        }.getType());
                        listI.add(mBaseInspection);
                    }
                    mView.showBaseSubProject(listI);
                } else {
                    mView.onFaild("暂无数据");
                }
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(@NonNull Throwable e) {
                mView.onFaild("获取巡检清单失败");

            }
        });
    }

    @Override
    public void downFile(String filePath) {
        mModel.downFile(filePath).subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(@NonNull Disposable disposable) throws Exception {
                //发起加载框
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response<File>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                addDisposable(d);
            }

            @Override
            public void onNext(@NonNull Response<File> fileResponse) {
                mView.downSucceed(fileResponse.body().getAbsolutePath());
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mView.openFileFaild("打开文件异常");
            }

            @Override
            public void onComplete() {

            }
        });
    }

}
