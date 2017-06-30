package com.wezom.ulcv2.mvp.presenter;

import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.MvpView;
import com.wezom.ulcv2.managers.PreferenceManager;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import lombok.Data;
import lombok.experimental.Accessors;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by oskalenko.v on 10.12.2015.
 */
@Data
@Accessors(prefix = "m")
public class BasePresenter<View extends MvpView> extends MvpPresenter<View> {

    protected PreferenceManager mPreferenceManager;
    protected CompositeSubscription mSubscriptions;



    protected BasePresenter() {
    }

    @Override
    public void attachView(View view) {
        super.attachView(view);
    }

    @Override
    public void detachView(View view) {
        unsubscribe();
        super.detachView(view);
    }

    /**
     * Unsubscribes the subscriber and set it to null
     */
    protected void unsubscribe() {
        if (mSubscriptions != null && !mSubscriptions.isUnsubscribed()) {
            mSubscriptions.unsubscribe();
        }
    }
}
