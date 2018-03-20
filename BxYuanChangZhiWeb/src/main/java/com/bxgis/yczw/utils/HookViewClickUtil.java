package com.bxgis.yczw.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;

/**
 * Hook技术之View点击劫持  避免用户快速多次点击优化
 * 获取页面的所有View,调用 HookViewClickUtil.hookView(view)
 * Created by xiaozhu on 2017/11/15.
 */

public class HookViewClickUtil {

    public static HookViewClickUtil getInstance() {
        return UtilHolder.mHookViewClickUtil;
    }

    private static class UtilHolder {
        private static HookViewClickUtil mHookViewClickUtil = new HookViewClickUtil();
    }

    /**
     * hook掉viewGroup
     *
     * @param viewGroup
     * @param isScrollAbsListview lsitview或gridView是否滚动：true：滚动则重新hook，false：表示view不是listview或者gridview或者没滚动
     */

    public static void hookStart(ViewGroup viewGroup) {
        if (viewGroup == null) {
            return;
        }
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {//递归查询所有子view
                // 若是布局控件（LinearLayout或RelativeLayout）,继续查询子View
                hookStart((ViewGroup) view);
            } else {
                hookView(view);

            }
        }
        hookView(viewGroup);
    }

    /**
     * hook掉viewGroup
     *
     * @param activity
     */
    public static void hookStartActivity(Activity activity) {
        if (null != activity) {
            View view = activity.getWindow().getDecorView();
            if (null != view) {
                if (view instanceof ViewGroup) {
                    hookStart((ViewGroup) view);
                } else {
                    hookView(view);
                }
            }
        }
    }

    public static void hookView(View view) {
        try {
            Class viewClazz = Class.forName("android.view.View");
            //事件监听器都是这个实例保存的
            Method listenerInfoMethod = viewClazz.getDeclaredMethod("getListenerInfo");
            if (!listenerInfoMethod.isAccessible()) {
                listenerInfoMethod.setAccessible(true);
            }
            Object listenerInfoObj = listenerInfoMethod.invoke(view);

            Class listenerInfoClazz = Class.forName("android.view.View$ListenerInfo");

            Field onClickListenerField = listenerInfoClazz.getDeclaredField("mOnClickListener");

            if (!onClickListenerField.isAccessible()) {
                onClickListenerField.setAccessible(true);
            }
            View.OnClickListener mOnClickListener = (View.OnClickListener) onClickListenerField.get(listenerInfoObj);
            //自定义代理事件监听器
            View.OnClickListener onClickListenerProxy = new OnClickListenerProxy(mOnClickListener);
            //更换
            onClickListenerField.set(listenerInfoObj, onClickListenerProxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //自定义的代理事件监听器
    private static class OnClickListenerProxy implements View.OnClickListener {

        private View.OnClickListener object;

        private int MIN_CLICK_DELAY_TIME = 2000;

        private long lastClickTime = 0;

        private OnClickListenerProxy(View.OnClickListener object) {
            this.object = object;
        }

        @Override
        public void onClick(View v) {
            //点击时间控制
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime;
                Log.e("OnClickListenerProxy", "OnClickListenerProxy");
                if (object != null) object.onClick(v);
            }
        }
    }
}