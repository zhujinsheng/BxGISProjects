package com.bxgis.bxportal.ui.inspection.presenter;

import android.text.TextUtils;

import com.bxgis.bxportal.base.BasePresenter;
import com.bxgis.bxportal.bean.BaseInspection;
import com.bxgis.bxportal.bean.CompanyBean;
import com.bxgis.bxportal.bean.InspectionSubProjectBean;
import com.bxgis.bxportal.bean.SysOrganizationBean;
import com.bxgis.bxportal.ui.inspection.NowInspecrionRelaseActivity;
import com.bxgis.bxportal.ui.inspection.contract.NowInspectionContract;
import com.bxgis.bxportal.ui.inspection.model.NowInspectionModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
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
public class NowInspectionPresenter extends BasePresenter<NowInspecrionRelaseActivity, NowInspectionModel> implements NowInspectionContract.Presenter {
    public NowInspectionPresenter(NowInspecrionRelaseActivity mView, NowInspectionModel mModel) {
        super(mView, mModel);
    }

    @Override
    public void getSubProjects(String project_id) {
        mModel.getSubProjects(project_id).subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
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
                List<InspectionSubProjectBean> listI = new ArrayList<InspectionSubProjectBean>();
                if (null != jsonArray1 && jsonArray1.size() > 0) {
                    for (JsonElement sub : jsonArray1) {
                        InspectionSubProjectBean mBaseInspection = new Gson().fromJson(sub, new TypeToken<InspectionSubProjectBean>() {
                        }.getType());
                        listI.add(mBaseInspection);
                    }
                    mView.showSubProjectData(listI);
                } else {
                    mView.onFaild("暂无数据");
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
    public void getOrgCompany() {
        mModel.getOrgCompany().subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
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
                // 获得 解析者  并获得 根节点元素
                // 根据 文档判断根节点属于 什么类型的 Gson节点对象
                // 假如文档 显示 根节点 为对象类型,获得 根节点 的实际 节点类型
                JsonObject jsonObject1 = new JsonParser().parse(stringResponse.body().toString()).getAsJsonObject();
                JsonArray jsonArray1 = jsonObject1.getAsJsonArray("rows");
                List<String> listI = new ArrayList<String>();
                List<CompanyBean> listC = new ArrayList<CompanyBean>();
                if (null != jsonArray1 && jsonArray1.size() > 0) {

                    for (JsonElement jsonElement : jsonArray1) {// 遍历 根节点元素
                        CompanyBean companyBean = new Gson().fromJson(jsonElement, new TypeToken<CompanyBean>() {
                        }.getType());
                        JsonPrimitive jsonPrimitive = jsonElement.getAsJsonObject().getAsJsonPrimitive("co_name");
//                        JsonPrimitive jsonPrimitive=jsonElement.getAsJsonObject().getAsJsonPrimitive("cede");
                        listI.add(jsonPrimitive.getAsString());
                        listC.add(companyBean);
                    }
                    mView.showOrgCompany(listC);
                } else {
                    mView.onFaild("暂无数据");
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
    public void getOrgUsers() {
        mModel.getOrgUsers().subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
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
                List<SysOrganizationBean> listOrg = new ArrayList<SysOrganizationBean>();
                if (null != jsonArray1 && jsonArray1.size() > 0) {
                    for (JsonElement user : jsonArray1) {
                        SysOrganizationBean mBaseInspection = new Gson().fromJson(user, new TypeToken<SysOrganizationBean>() {
                        }.getType());
                        listOrg.add(mBaseInspection);
                    }
                    mView.showOrgUsers(listOrg);
                } else {
                    mView.onFaild("暂无数据");
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
    public void commit(BaseInspection baseInspection) {
        mModel.commit(baseInspection).subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
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
                    if ((!TextUtils.isEmpty(msg)) && msg.equalsIgnoreCase("success")) {
                        mView.onSucceed();
                    } else {
                        mView.onFaild("巡检发布失败！");
                    }
                } catch (JSONException e) {
                    mView.onFaild("巡检发布失败！");
                    e.printStackTrace();
                }
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(@NonNull Throwable e) {
                mView.onCommitFaild();

            }
        });
    }

    /*
    fileType :0为签名文件提交  1，为附件提交
     */
    @Override
    public void commitFile2(List<File> files, final int fileType) {
        mModel.commitFile2(files).subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
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
                if (null != bean) {
                    String images = bean.getAsString();
                    if (fileType == 0) {
                        mView.onSucceedUploadSign(images);

                    } else {
                        mView.onSucceedUpload(images);
                    }

                } else {
                    mView.onFaild("暂无附件路径返回");
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                mView.onFaild("网络异常，请检查网络！");
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
