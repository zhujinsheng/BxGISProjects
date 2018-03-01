package com.bxgis.bxportal.widget;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.shizhefei.view.indicator.slidebar.ScrollBar;

/**
 * Created by xiaozhu on 2017/12/22.
 */

public class MyColorBar implements ScrollBar{
    protected ScrollBar.Gravity gravity;
    protected TextView view;
    protected int color;
    protected int height;
    protected int width;

    public MyColorBar(Context context, int color, int height) {
        this(context, color, height, ScrollBar.Gravity.BOTTOM);
    }

    public MyColorBar(Context context, int color, int height, ScrollBar.Gravity gravity) {
        view = new TextView(context);
        this.color = color;
        view.setBackgroundColor(color);
        this.height = height;
        this.gravity = gravity;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        view.setBackgroundColor(color);
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getHeight(int tabHeight) {
        if (height == 0) {
            return tabHeight;
        }

        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getWidth(int tabWidth) {
        if (width == 0) {
            return tabWidth;
        }
        return width;
    }

    @Override
    public View getSlideView() {
        return view;
    }

    @Override
    public ScrollBar.Gravity getGravity() {
        return gravity;
    }

    public void setGravity(ScrollBar.Gravity gravity) {
        this.gravity = gravity;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }
}

