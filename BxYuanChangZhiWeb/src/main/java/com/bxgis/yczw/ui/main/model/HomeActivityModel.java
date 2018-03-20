package com.bxgis.yczw.ui.main.model;


import com.bxgis.yczw.ui.main.contract.HomeActivityContract;
import com.lzy.okgo.model.Response;

import io.reactivex.Observable;

/**
 * Created by xiaozhu on 2017/11/17.
 */
public class HomeActivityModel implements HomeActivityContract.Model {
    @Override
    public Observable<Response<String>> getCountInspection(String userId) {
        return null;
    }



}
