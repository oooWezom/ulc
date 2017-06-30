package com.wezom.ulcv2.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.events.BackEvents;
import com.wezom.ulcv2.events.ConfirmChangePassEvent;
import com.wezom.ulcv2.exception.ApiException;
import com.wezom.ulcv2.mvp.view.ConfirmRecoveryPassView;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.models.Event;
import com.wezom.ulcv2.net.models.requests.ConfirmPasswordRequest;
import com.wezom.ulcv2.net.models.requests.PasswordRestoreRequest;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created by Android 3 on 24.03.2017.
 */

@InjectViewState
public class ConfirmRestorePassPresenter extends BasePresenter<ConfirmRecoveryPassView> {

    @Inject
    EventBus bus;
    @Inject
    ApiManager mApiManager;

    public ConfirmRestorePassPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void restorePassword(String key, String password) {
        getViewState().showLocalLoading();
        mApiManager.confirmRestorePassword(new ConfirmPasswordRequest(key, password))
                .subscribe(response -> {
                    getViewState().hideLocalLoading();
                    bus.post(new ConfirmChangePassEvent(response.getToken()));
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
        bus.post(new BackEvents());
    }
}
