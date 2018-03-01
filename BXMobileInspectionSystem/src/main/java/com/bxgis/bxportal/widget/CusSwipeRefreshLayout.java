package com.bxgis.bxportal.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * 这属于外部控制法--
 * viewpager和SwipeRefreshLayout下拉刷新冲突解决
 * 重写SwipeRefreshLayout的onIntercept方法就可以很简单的解决了。
 * 思路：
 1. 因为下拉刷新，只有纵向滑动的时候才有效，那么我们就判断此时是纵向滑动还是横向滑动就可以了。
 2. 纵向滑动就拦截事件，横向滑动不拦截。
 3. 怎么判断是纵向滑动还是横向滑动，只要判断Y轴的移动距离大于X轴的移动距离那么就判定为纵向滑动就行了
 * Created by xiaozhu on 2017/12/11.
 */

public class CusSwipeRefreshLayout extends SwipeRefreshLayout {
    private Float mStartX;
    private Float mStartY;
    private boolean mIsVpDragger;//是否拖动中

    private int mTouchSlop; //滑动距离监听

    public CusSwipeRefreshLayout(Context context) {
        super(context, null);
    }

    public CusSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = MotionEventCompat.getActionMasked(ev);


        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartX = ev.getX();
                mStartY = ev.getY();
                mIsVpDragger = false;
                break;

            case MotionEvent.ACTION_MOVE:
                // 如果viewpager正在拖拽中，那么不拦截它的事件，直接return false；
                if (mIsVpDragger) {
                    return false;
                }
                //获取当前手的位置

                float endY = ev.getY();
                float endX = ev.getX();
                float distanceX = Math.abs(endX - mStartX);
                float distanceY = Math.abs(endY - mStartY);
                if (distanceX > mTouchSlop && distanceX > distanceY) {
                    mIsVpDragger = true;
                    return false;
                }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsVpDragger = false;
                break;
        }


        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

}
