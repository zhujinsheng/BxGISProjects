package com.bxgis.bxportal.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

/**
 * author: chensen
 * date: 2017年04月05日8:14
 * desc: 圆形图片
 */

public class CircleImageView extends android.support.v7.widget.AppCompatImageView {


    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();
        Log.d("tag","roundBimmap-00--");
        //空值判断，必要步骤，避免由于没有设置src导致的异常错误
        if (drawable == null) {
            Log.d("tag","roundBimmap-2--");
            return;
        }

        if (!(drawable instanceof BitmapDrawable)) {
            return;
        }


        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        Bitmap roundBimmap = getCroppedBitmap(bitmap, getWidth());
        canvas.drawBitmap(roundBimmap, 0, 0, null);


    }


    public static Bitmap getCroppedBitmap(Bitmap bitmap, int radius) {
        final int color = 0xff424242;
        Paint paint = new Paint();

        //比较初始Bitmap宽高和给定的圆形直径，判断是否需要缩放裁剪Bitmap对象
        Bitmap temBitmap;//带有背景图的Bigmap

        if (bitmap.getWidth() != radius || bitmap.getHeight() != radius) {
            temBitmap = Bitmap.createScaledBitmap(bitmap, radius, radius, false);
        } else {
            temBitmap = bitmap;
        }

        //新建的空白的Bitmap
        Bitmap outBitmap = Bitmap.createBitmap(temBitmap.getWidth(), temBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Rect rect = new Rect(0, 0, outBitmap.getWidth(), outBitmap.getHeight());

        Canvas canvas = new Canvas(outBitmap);
        paint.setAntiAlias(true);
        paint.setColor(color);

        canvas.drawARGB(0, 0, 0, 0);//背景透明
        canvas.drawCircle(outBitmap.getWidth() / 2, outBitmap.getHeight() / 2, outBitmap.getWidth() / 2, paint);

        //核心部分，设置两张图片的相交模式，在这里就是上面绘制的Circle和下面绘制的Bitmap
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //将temBitmap画到temBitmap上
        canvas.drawBitmap(temBitmap, rect, rect, paint);

        return outBitmap;
    }
}
