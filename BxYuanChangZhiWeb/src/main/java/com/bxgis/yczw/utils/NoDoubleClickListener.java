package com.bxgis.yczw.utils;

import android.view.View;

/**
 * Created by xiaozhu on 2017/11/14.
 */

public abstract class NoDoubleClickListener implements View.OnClickListener {
    // 两次点击按钮之间的点击间隔不能少于1000毫秒s
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    @Override
    public void onClick(View v) {
//        long currentTime = Calendar.getInstance().getTimeInMillis();
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            // 超过点击间隔后再将lastClickTime重置为当前点击时间
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }

    protected abstract void onNoDoubleClick(View v);
}
