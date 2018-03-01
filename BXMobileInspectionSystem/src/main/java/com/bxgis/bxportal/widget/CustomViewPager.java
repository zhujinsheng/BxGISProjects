package com.bxgis.bxportal.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * Created by huxq17 on 2016/2/1.
 */
public class CustomViewPager extends ViewPager {
    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomViewPager(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private ViewGroup parent;

    public void setParent(ViewGroup parent) {
        this.parent = parent;
    }

}
