package com.bxgis.yczw.myInterface;

import android.view.View;

import com.bxgis.yczw.bean.BeachAndBenchlandBean;

/**
 * Created by xiaozhu on 2017/11/21.
 */

public interface MyOnItemClickListener {
    void onBeanchClick(View v, int position);
    void onItemClick(View v, int position,BeachAndBenchlandBean benchlandBean);
    void onBenchlandClick(View v, int position);
}
