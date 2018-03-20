package com.bxgis.yczw.base;


import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * author: chensen
 * date: 2017年03月23日9:19
 * desc:
 */

public abstract class BasePresenter<T extends BaseView, M extends BaseModel> {
    public T mView;
    public M mModel;

//    protected CompositeSubscription compositeSubscription;


    public BasePresenter(T mView, M mModel) {
        this.mView = mView;
        this.mModel = mModel;
    }


    public void onDestory() {
//        unSubscribe();
        dispose();
        mView = null;
    }

//    /**
//     * 用于解除订阅
//     *
//     * @param subscription
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
}
