package com.bxgis.bxportal.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.bxgis.bxportal.event.UpdateEvent;
import com.bxgis.bxportal.utils.StatusBarUtils;
import com.bxgis.bxportal.widget.CustomEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


/**
 * @Author:zjs
 * @Description: Activity基类
 * @Created in  2017/11/11.
 * @modified By:
 */

public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity {
    public String TAG;
    protected Context mContext;
    protected T mPresenter;
//    private CompositeSubscription compositeSubscription;
    private boolean doubleBackExit = false;
    private boolean doubleBackExitPressedOnce = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
        doBeforeSetContentView();
        setContentView(getLayoutId());
        StatusBarUtils.with(this).init();
        ButterKnife.bind(this);
        mContext = this;
        //注册WVWNtBus
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        //创建Presenter
        mPresenter = initPresenter();
        // mPresenter.onAttach(this);
        initView();
    }

    @Subscribe
    public void onEvent(UpdateEvent event) {

    }
    /**
     * Android输入键盘隐藏解决方案
//     * @param ev
     * @return
     */
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            View v = getCurrentFocus();
//
//            //如果不是落在EditText区域，则需要关闭输入法
//            if (isShouldHideKeyboard(v, ev)) {
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//            }
//        }
//        return super.dispatchTouchEvent(ev);
//    }
//    // 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘
//    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
//        if (v != null && (v instanceof CustomEditText)) {
//            int[] l = {0, 0};
//            v.getLocationInWindow(l);
//            //获取现在拥有焦点的控件view的位置，即EditText
//            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
//
//            //判断我们手指点击的区域是否落在EditText上面，如果不是，则返回true，否则返回false
//            return !(event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom);
//        }
//        return false;
//    }
    //设置layout前配置
    private void doBeforeSetContentView() {
    }

    //获取布局文件
    public abstract int getLayoutId();

    //初始化view
    protected abstract void initView();

    //简单页面无需mvp就不用管此方法即可,完美兼容各种实际场景的变通
    protected abstract T initPresenter();

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestory();
        }
        //解注册EventBus
        EventBus.getDefault().unregister(this);
        dispose();
        ButterKnife.unbind(this);
    }
    private CompositeDisposable compositeDisposable;

    public void addDisposable(Disposable disposable) {
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.add(disposable);
    }

    public void dispose() {
        if (compositeDisposable != null) compositeDisposable.dispose();
    }

//    public void addSubscribe(Subscription subscription) {
//        if (compositeSubscription == null) {
//            compositeSubscription = new CompositeSubscription();
//        }
//        compositeSubscription.add(subscription);
//    }
//
//    public void unSubscribe() {
//        if (compositeSubscription != null)
//            compositeSubscription.clear();
//
//    }

    public void showLoading() {

    }

    public void hideLoading() {

    }

    public void showEmpty() {

    }

    public void showError() {

    }

    protected void showShortToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    protected void showLongToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    protected void showLog(String msg) {
        Log.d(TAG, msg);
    }

    public void startActivity(Class<?> clz) {
        startActivity(clz, null);
    }

    /**
     *携带数据的页面跳转
     * @param clz
     * @param bundle
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 含有Bundle通过Class打开编辑界面
     *
     * @param cls
     * @param bundle
     * @param requestCode
     */
    public void startActivityForResult(Class<?> cls, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }
    /**
     * 双击退出
     *
     * @param doubleBackExit
     */
    public void setDoubleBackExit(boolean doubleBackExit) {
        this.doubleBackExit = doubleBackExit;
    }




}
