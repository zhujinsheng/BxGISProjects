package com.bxgis.bxportal.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bxgis.bxportal.R;
import com.bxgis.bxportal.bean.DrawPathBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义画板View
 * Author:zjs
 * Date:2017/11/1 22:36
 */

public class SignatureView extends View {

    private Canvas paintCanvas;
    private Paint paint, eraserPaint;
    private Bitmap bitmap;    //涂鸦图层的位图
    private Bitmap backgroudBitmap; //传入的图片位图

    private float startX, startY, endX, endY;
    private Context context;
    boolean isEraser; //是否开启橡皮擦模式
    private List<DrawPathBean> drawPathList = new ArrayList<>();
    private Path path;
    private int bgWidth; //绘图的最大宽度
    private int bgHeight; //绘图的最大高度

    private int mBackColor = Color.TRANSPARENT;

    public SignatureView(Context context) {
        super(context);
        initPiant(context);
    }

    public SignatureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPiant(context);

    }

    public SignatureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPiant(context);
    }

    /**
     * 初始化
     */
    public void initPiant(Context context) {
        //画笔初始化
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);// 抗锯齿
        paint.setDither(true); // 防抖动
        paint.setFilterBitmap(true);
        paint.setColor(ContextCompat.getColor(context, R.color.black));
        paint.setStrokeWidth(5);
        //橡皮擦画笔初始化
        eraserPaint = new Paint();
        eraserPaint.setStyle(Paint.Style.STROKE);
        eraserPaint.setStrokeWidth(20);
        eraserPaint.setColor(Color.TRANSPARENT);
        Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        eraserPaint.setXfermode(xfermode);
        this.context = context;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //根据提供的测量值提取模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        //根据提供的测量值提取大小
        bgWidth = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        bgHeight = MeasureSpec.getSize(heightMeasureSpec);
        /**依据得到的specMode值，如果是AT_MOST，specSize 代表的是最大可获得的空间；如果EXACTLY，   specSize 代表的是精确的尺寸；如果是UNSPECIFIED，对于控件尺寸来说，没有任何参考意义。当以EXACT方式标记测量尺寸，父元素会坚持在一个指定的精确尺寸区域放置View。在父元素问子元素要多大空间时，AT_MOST指示者会说给我最大的范围。在很多情况下，你得到的值都是相同的。在两种情况下，你必须绝对的处理这些限制。在一些情况下，它可能会返回超出这些限制的尺寸，在这种情况下，你可以让父元素选择如何对待超出的View，使用裁剪还是滚动等技术。**/
        if(widthMode == MeasureSpec.EXACTLY){
            //不需要重新计算
            widthMeasureSpec = bgWidth;
        }else {
            //重新计算，这里计算你需要绘制的视图的宽
//            widthMeasureSpec = getPaddingLeft()+getPaddingRight()+rect.width();
        }

        if(heightMode == MeasureSpec.EXACTLY){
            heightMeasureSpec = bgHeight;
        }else {
//            heightMeasureSpec = getPaddingTop()+getPaddingBottom()+rect.height();
        }
        //然后调用自setMeasuredDimension()方法将测量好的宽高保存
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (backgroudBitmap != null && !backgroudBitmap.isRecycled()) {
            canvas.drawBitmap(backgroudBitmap, 0, 0, null);
        }

        if (bitmap != null && !bitmap.isRecycled()) {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
    }

    //触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.getParent().requestDisallowInterceptTouchEvent(true);
                startX = event.getX();
                startY = event.getY();
                path = new Path();
                path.moveTo(startX, startY);
                break;

            case MotionEvent.ACTION_MOVE:
                endX = event.getX();
                endY = event.getY();
                final float dx = Math.abs(endX - startX);
                final float dy = Math.abs(endY - startY);
                //两点之间的距离大于等于3时，生成贝塞尔绘制曲线
                if(dx >= 3 || dy >= 3){
                    //设置贝塞尔曲线的操作点为起点和终点的一半
                    float cX = (endX + startX) / 2;
                    float cY = (endY + startY) / 2;
                    path.quadTo(startX, startY, cX, cY);
                }

                paintCanvas.drawPath(path, isEraser ? eraserPaint : paint);
                startX = endX;
                startY = endY;
                postInvalidate();
                break;

            case MotionEvent.ACTION_UP:
                //临时保存画笔路径，用于撤消
                drawPathList.add(new DrawPathBean(path, isEraser ? eraserPaint.getColor() : paint.getColor(), isEraser));
                break;

            default:
                break;
        }
        return true;
    }

    /**
     * 开启橡皮擦功能
     */
    public void eraserClean() {
        isEraser = true;
    }

    /**
     * 撤销操作
     */
    public void cancelPath() {
        if (drawPathList != null && drawPathList.size() <= 0) {
            return;
        }
        drawPathList.remove(drawPathList.size() - 1);
        paintCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        for (DrawPathBean entry : drawPathList) {
            paint.setColor(entry.getPaintColor());
            paintCanvas.drawPath(entry.getPath(), entry.isEraser() ? eraserPaint : paint);
        }
        postInvalidate();
    }

    /**
     * 设置背景图片及处理新的用来涂鸦的Bitmap
     *
     * @param bitmap 传入背景图片
     */
    public void setBackgroud(Bitmap bitmap) {
        this.backgroudBitmap = Bitmap.createBitmap( bgWidth,bgHeight, Bitmap.Config.ARGB_8888);
        backgroudBitmap.eraseColor(Color.parseColor("#FFFFFF"));//填充颜色
        this.bitmap = Bitmap.createBitmap(backgroudBitmap.getWidth(), backgroudBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        paintCanvas = new Canvas(this.bitmap);
    }

    /**
     * 设置默认的背景图片
     *
     */
    public void setBackgroud2() {
        this.backgroudBitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        backgroudBitmap.eraseColor(Color.parseColor("#FF0000"));//填充颜色
        this.bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        paintCanvas = new Canvas(this.bitmap);
    }
    /**
     * 是否有签名
     *
     * @return
     */
    public boolean isSignature() {
        if (drawPathList != null && drawPathList.size() <= 0) {
            return false;
        }
        return true;
    }

    /**
     * 清空画板
     */
    public void clearDrawinglBoard() {
        paintCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        drawPathList.clear();
        postInvalidate();
    }

    /**
     * @return 返回最终的涂鸦好的位图
     */
    public Bitmap getDrawingBitmap() {
        Bitmap resultBit = Bitmap.createBitmap(backgroudBitmap.getWidth(), backgroudBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas resultCanvas = new Canvas(resultBit);
        resultCanvas.drawBitmap(backgroudBitmap, 0, 0, null);
        resultCanvas.drawBitmap(bitmap, 0, 0, null);
        resultCanvas.save();

        return resultBit;
    }

    /**
     * 逐行扫描 清楚边界空白。
     *
     * @param bp
     * @param blank 边距留多少个像素
     * @return
     */
    private Bitmap clearBlank(Bitmap bp, int blank) {
        int HEIGHT = bp.getHeight();
        int WIDTH = bp.getWidth();
        int top = 0, left = 0, right = 0, botton = 0;
        int[] pixs = new int[WIDTH];
        boolean isStop;
        for (int y = 0; y < HEIGHT; y++) {
            /*
             *public void getPixels(int[] pixels, int offset, int stride,int x, int y, int width, int height)
             *获取原Bitmap的像素值存储到pixels数组中
             *pixels     接收位图颜色值的数组
             *offset     写入到pixels[]中的第一个像素索引值
             *stride     pixels[]中的行间距个数值(必须大于等于位图宽度)。不能为负数
             *x          从位图中读取的第一个像素的x坐标值。
             *y          从位图中读取的第一个像素的y坐标值
             *width      从每一行中读取的像素宽度
             *height 读取的行数
             */
            bp.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    top = y;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        for (int y = HEIGHT - 1; y >= 0; y--) {
            bp.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    botton = y;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        pixs = new int[HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            bp.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    left = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        for (int x = WIDTH - 1; x > 0; x--) {
            bp.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    right = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        if (blank < 0) {
            blank = 0;
        }
        left = left - blank > 0 ? left - blank : 0;
        right = right - blank > 0 ? right - blank : 0;
        top = top - blank > 0 ? top - blank : 0;
        botton = botton - blank > 0 ? botton - blank : 0;
        return Bitmap.createBitmap(bp, left, top, right - left, botton - top);
    }


    /**
     * 设置画笔颜色及橡皮擦
     *
     * @param type
     */
    public void setPaintType(int type) {

        isEraser = false;
//        switch (type) {
//            case 1:
//                paint.setColor(ContextCompat.getColor(context, R.color.red_radio));
//                break;
//            case 2:
//                paint.setColor(ContextCompat.getColor(context, R.color.orange_radio));
//                break;
//            case 3:
//                paint.setColor(ContextCompat.getColor(context, R.color.yellow_radio));
//                break;
//            case 4:
//                paint.setColor(ContextCompat.getColor(context, R.color.green_radio));
//                break;
//            case 5:
//                paint.setColor(ContextCompat.getColor(context, R.color.blue_radio));
//                break;
//            case 6:
//                paint.setColor(ContextCompat.getColor(context, R.color.purple_radio));
//                break;
//            default:
//                break;
//        }
    }
}
