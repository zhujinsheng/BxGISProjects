package com.bxgis.yczw.widget.toast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * 处理场景：Android 7.1 机型上报告的由Toast引起的BadTokenException错误？
 * 原因：这个问题由于targetSDKVersion升到26之后，在7.1.1机型上概率性出现。稳定复现的步骤是，在Toast.show()之后，UI线程做了耗时的操作阻塞了Handler message的处理，
 *       如使用Thread.sleep(5000)，然后这个崩溃就出现了。原因是7.1.1系统对TYPE_TOAST的Window类型做了超时限制，绑定了Window Token，最长超时时间是3.5s，如果UI在这段时间内没有执行完，
 *       Toast.show()内部的handler message得不到执行，NotificationManageService那端会把这个Toast取消掉，同时把Toast对于的window token置为无效。等App端真正需要显示Toast时，
 *       因为window token已经失效，ViewRootImpl就抛出了上面的异常。
 *       Android 8.0上面，google意识到这个bug，在Toast的内部加了try-catch保护。目前只有7.1.1上面的Toast存在这个问题
 *
 *  现阶段解决方法也是继续Toast重写Toast的try-Catch保护
 */
public final class ToastCompat extends Toast {

    private final @NonNull Toast toast;


    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     * or {@link Activity} object.
     * @param base The base toast
     */
    private ToastCompat(Context context, @NonNull Toast base) {
        super(context);
        this.toast = base;
    }


    /**
     * Make a standard toast that just contains a text view.
     *
     * @param context The context to use.  Usually your {@link Application}
     * or {@link Activity} object.
     * @param text The text to show.  Can be formatted text.
     * @param duration How long to display the message.  Either {@link #LENGTH_SHORT} or
     * {@link #LENGTH_LONG}
     */
    public static ToastCompat makeText(Context context, CharSequence text, int duration) {
        // We cannot pass the SafeToastContext to Toast.makeText() because
        // the View will unwrap the base context and we are in vain.
        @SuppressLint("ShowToast")
        Toast toast = Toast.makeText(context, text, duration);
        setContext(toast.getView(), new SafeToastContext(context, toast));
        return new ToastCompat(context, toast);
    }


    /**
     * Make a standard toast that just contains a text view with the text from a resource.
     *
     * @param context The context to use.  Usually your {@link Application}
     * or {@link Activity} object.
     * @param resId The resource id of the string resource to use.  Can be formatted text.
     * @param duration How long to display the message.  Either {@link #LENGTH_SHORT} or
     * {@link #LENGTH_LONG}
     * @throws Resources.NotFoundException if the resource can't be found.
     */
    public static Toast makeText(Context context, @StringRes int resId, int duration)
        throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(resId), duration);
    }

    // 监听异常返回
    public @NonNull ToastCompat setBadTokenListener(@NonNull BadTokenListener listener) {
        ((SafeToastContext) getView().getContext()).setBadTokenListener(listener);
        return this;
    }


    @Override
    public void show() {
        toast.show();
    }


    @Override
    public void setDuration(int duration) {
        toast.setDuration(duration);
    }


    @Override
    public void setGravity(int gravity, int xOffset, int yOffset) {
        toast.setGravity(gravity, xOffset, yOffset);
    }


    @Override
    public void setMargin(float horizontalMargin, float verticalMargin) {
        toast.setMargin(horizontalMargin, verticalMargin);
    }


    @Override
    public void setText(int resId) {
        toast.setText(resId);
    }


    @Override
    public void setText(CharSequence s) {
        toast.setText(s);
    }


    @Override
    public void setView(View view) {
        toast.setView(view);
        setContext(view, new SafeToastContext(view.getContext(), this));
    }


    @Override
    public float getHorizontalMargin() {
        return toast.getHorizontalMargin();
    }


    @Override
    public float getVerticalMargin() {
        return toast.getVerticalMargin();
    }


    @Override
    public int getDuration() {
        return toast.getDuration();
    }


    @Override
    public int getGravity() {
        return toast.getGravity();
    }


    @Override
    public int getXOffset() {
        return toast.getXOffset();
    }


    @Override
    public int getYOffset() {
        return toast.getYOffset();
    }


    @Override
    public View getView() {
        return toast.getView();
    }


    public @NonNull Toast getBaseToast() {
        return toast;
    }


    private static void setContext(@NonNull View view, @NonNull Context context) {
        try {
            Field field = View.class.getDeclaredField("mContext");
            field.setAccessible(true);
            field.set(view, context);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
