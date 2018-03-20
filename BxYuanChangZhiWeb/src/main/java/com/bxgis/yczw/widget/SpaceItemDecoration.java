package com.bxgis.yczw.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 *简单设置间隔大小
 *int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.space);
  mRecyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
 * Created by xiaozhu on 2017/12/21.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        if(parent.getChildPosition(view) > 2)
            outRect.top = space;
    }
}