package com.wezom.ulcv2.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.events.BackEvents;
import com.wezom.ulcv2.exception.ApiException;
import com.wezom.ulcv2.mvp.view.PasswordRecoveryView;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.models.requests.PasswordRestoreRequest;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created by Sivolotskiy.v on 27.05.2016.
 */

@InjectViewState
public class PasswordRecoveryPresenter extends BasePresenter<PasswordRecoveryView> {
    @Inject
    EventBus mBus;
    @Inject
    ApiManager mApiManager;

    public PasswordRecoveryPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void restorePassword(String key) {
        getViewState().showLocalLoading();
        mApiManager.restorePassword(new PasswordRestoreRequest(key)).subscribe(response -> {
            getViewState().showLocalLoading();
            getViewState().showSuccessRestoreText();
        }, throwable -> {
            getViewState().hideLocalLoading();
            if (throwable instanceof ApiException) {
                getViewState().showErrorText(R.string.wrong_email);
            } else {
                getViewState().showErrorText(R.string.network_error);
            }
        }, () -> {
            getViewState().hideLocalLoading();
        });
    }

    public void onBackPressed() {
        mBus.post(new BackEvents());
    }
}
