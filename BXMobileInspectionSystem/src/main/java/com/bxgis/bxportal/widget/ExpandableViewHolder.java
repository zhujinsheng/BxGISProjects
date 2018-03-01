package com.bxgis.bxportal.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by haoyan on 2016/3/24.
 */
public class ExpandableViewHolder {
    //存储数据
    private SparseArray<View> mViews;

    private int mPosition;
    private static View mConvertView;
    private boolean misExpanded;

    public ExpandableViewHolder(Context context, ViewGroup parent, int layoutId, int position, boolean isExpanded) {

        this.mPosition = position;
        this.mViews = new SparseArray<View>();
        this.misExpanded = isExpanded;
        //三
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        //一
        mConvertView.setTag(this);
    }

    public static ExpandableViewHolder get(Context context, View convertView,
                                           ViewGroup parent, int layoutId, int position, boolean isExpanded) {

        if (convertView == null) {
            return new ExpandableViewHolder(context, parent, layoutId, position, isExpanded);
        } else {
            //五 六
            ExpandableViewHolder holder = (ExpandableViewHolder) convertView.getTag();
            holder.mPosition = position;
            return holder;
        }
    }

    //通过viewId获取控件
    //泛型T返回子类
    public <T extends View> T getView(int viewId) {

        View view = mViews.get(viewId);
        if (view == null) {
            //四
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    //八
    public static View getmConvertView() {
        return mConvertView;
    }

    /**
     * 设置TextView的值
     *
     * @param viewId
     * @param text
     * @return
     */
    //四 七 九
    public ExpandableViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        if (!TextUtils.isEmpty(text)) {
            tv.setText(text);
        }
        return this;
    }

    /**
     * 一参传id，二参传图片地址，三参为true则调用点击变化效果，默认为false.
     */
    public ExpandableViewHolder setImageResource(int viewId, int resId, boolean expanded) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        if (expanded) {
            if (misExpanded) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
        }
        return this;
    }

    public ExpandableViewHolder setImageBitamp(int viewId, Bitmap bitmap) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bitmap);
        return this;
    }

    public ExpandableViewHolder setImageURI(int viewId, String uri) {
        ImageView view = getView(viewId);
//        Imageloader.getInstance().loadImg(view,uri);
        return this;
    }

}
