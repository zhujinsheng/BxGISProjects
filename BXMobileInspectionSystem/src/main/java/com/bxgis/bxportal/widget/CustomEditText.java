package com.bxgis.bxportal.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;

import com.bxgis.bxportal.R;

/**
 * Created by xiaozhu on 2017/11/14.
 */
public class CustomEditText extends EditText {

    private final String TAG = "CustomEditText";
    //用来设置dRight这张图片
    private Drawable dRight;
    private Rect rBounds;

    public CustomEditText(Context context) {
        super(context);
        initEditText();
    }

    public CustomEditText(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        initEditText();
    }

    public CustomEditText(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        initEditText();
    }

    // 初始化edittext 控件
    private void initEditText() {
//        setEditTextDrawable();
//        dRight=getCompoundDrawables()[3];
        setCompoundDrawables(null, null, null, null);
        setRightPic(R.mipmap.ic_shut_down);
        addTextChangedListener(new TextWatcher() { // 对文本内容改变进行监听
            @Override
            public void afterTextChanged(Editable paramEditable) {
                if (paramEditable.length() == 0) {
                    setCompoundDrawables(null, null, null, null);
                } else {
                    setCompoundDrawables(null, null, dRight, null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {
            }

            @Override
            public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {

            }
        });
    }

//    // 控制图片的显示
//    public void setEditTextDrawable() {
//        if (getText().toString().length() == 0) {
//            setCompoundDrawables(null, null, null, null);
//        } else {
//            setCompoundDrawables(null, null, this.dRight, null);
//        }
//    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.dRight = null;
        this.rBounds = null;
    }

    /**
     * 设置右侧图片
     */
    public void setRightPic(int res)
    {
        Bitmap resBitmap = BitmapFactory.decodeResource(getResources(), res);
        dRight = new BitmapDrawable(getResources(),resBitmap);
        dRight.setBounds(0, 0, (int) this.getTextSize(), (int) this.getTextSize());
    }

    public void setRightPic(Bitmap bitmap)
    {
        dRight = new BitmapDrawable(getResources(),bitmap);
        dRight.setBounds(0, 0, (int) this.getTextSize(), (int) this.getTextSize());
    }

    public void setRightPic(Drawable drawable)
    {
        dRight = drawable;
        dRight.setBounds(0, 0, (int) this.getTextSize(), (int) this.getTextSize());
    }
    /**
     * 添加触摸事件 点击之后 出现 清空editText的效果
     */
    @Override
    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        if ((this.dRight != null) && (paramMotionEvent.getAction() == 1)) {
            this.rBounds = this.dRight.getBounds();
            int x = (int) paramMotionEvent.getRawX();// 距离屏幕的距离
//            int x = (int) paramMotionEvent.getX();//距离边框的距离
            int y = (int) paramMotionEvent.getY();
            int height = getHeight();
            Log.i(TAG,"X:"+x+" Y:"+y+" width:"+(getRight())+" height:"+(height>>1));
            Log.i(TAG,"rbw:"+(rBounds.width()>>1)+"rbh:"+(rBounds.height()>>1));
            if (x>getRight()-rBounds.width()&&y<(height>>1)+(rBounds.height()>>1)&&y>(height>>1)-(rBounds.height()>>1)) {
                setText("");
                paramMotionEvent.setAction(MotionEvent.ACTION_CANCEL);
            }

        }
        return super.onTouchEvent(paramMotionEvent);
    }

//    /**
//     * 显示右侧X图片的
//     * <p/>
//     * 左上右下
//     */
//    @Override
//    public void setCompoundDrawables(Drawable paramDrawable1, Drawable paramDrawable2, Drawable paramDrawable3, Drawable paramDrawable4) {
//        super.setCompoundDrawables(paramDrawable1, paramDrawable2, paramDrawable3, paramDrawable4);
//    }
}