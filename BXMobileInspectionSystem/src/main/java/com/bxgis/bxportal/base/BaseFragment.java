package com.bxgis.bxportal.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bxgis.bxportal.event.UpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * author: chensen
 * date: 2017年03月23日9:28
 * desc:
 */

public abstract class BaseFragment<T extends BasePresenter> extends Fragment {
    protected String TAG;
    protected Context mContext;
    protected View mRootView;
//    private boolean isViewInitFinished; //当前页面是否对用户可见
    protected T mPresenter;
//    protected CompositeSubscription compositeSubscription;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
        mContext = (BaseActivity) getActivity();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, mRootView);
        mPresenter = initPresenter();
        //注册WVWNtBus
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initView(mRootView);
//        isViewInitFinished = true;
        return mRootView;
    }
//    public void requestData(boolean isVisibleToUser){
//        if(isViewInitFinished && isVisibleToUser){
//            getData();
//        }
//    }
    /**
     *  this moment the fragment is visiable to user, so request data
     */
//    public abstract void getData();
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

//        requestData(isVisibleToUser);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(UpdateEvent event) {

    }
    public abstract int getLayoutId();

    protected abstract void initView(View v);

    protected abstract T initPresenter();

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

//    /**
//     * 用于解除订阅
//     *
//     * @param
//     */
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
//    }


    public void showLoading() {
        //LoadingDialog.showLoadingDialog(getActivity());


    }

    public void showLoading(String msg) {
        // LoadingDialog.showLoadingDialog(getActivity(), msg, false);
    }

    public void hideLoading() {
        //  LoadingDialog.cancleDialog();

    }

    public void showEmpty() {

    }

    public void showError() {

    }
    public  void showSuccess(){

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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            mPresenter.onDestory();
        }
        //解注册EventBus
        EventBus.getDefault().unregister(this);
//        unSubscribe();
        dispose();
        ButterKnife.unbind(this);
    }
}
