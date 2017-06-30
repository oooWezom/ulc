package com.wezom.ulcv2.mvp.presenter;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.events.BackEvents;
import com.wezom.ulcv2.exception.ApiException;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.mvp.view.ChangePasswordView;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.models.requests.ChangePasswordRequest;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created by Sivolotskiy.v on 09.06.2016.
 */

@InjectViewState
public class ChangePasswordPresenter extends BasePresenter<ChangePasswordView> {

    @Inject
    ApiManager mApiManager;
    @Inject
    EventBus mBus;

    public ChangePasswordPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void onPasswordChange(String oldPassword, String password, String passwordRepeat) {
        if (password.length() < 8) {
            getViewState().showMessageToast(R.string.minimum_password);
        }else if (password.equals(passwordRepeat)) {
            getViewState().showGlobalLoading(true);
            mApiManager.changePassword(new ChangePasswordRequest(oldPassword, password))
                    .subscribe(response -> {
                            getViewState().showMessageDialog(R.string.password_changed, R.string.password_change_success);
                            getViewState().showGlobalLoading(false);
                            mBus.post(new BackEvents());
                    }, throwable -> {
                            if (throwable instanceof ApiException) {
                                getViewState().showMessageDialog(R.string.failed, R.string.invalid_password);
                            } else {
                                getViewState().showMessageDialog(R.string.network_error, R.string.network_error);
                            }
                            getViewState().showGlobalLoading(false);
                    });
        } else {
            getViewState().showGlobalLoading(false);
            getViewState().showMessageDialog(R.string.passwords_does_not_match);
        }
    }

    public void onBackPressed() {
        mBus.post(new BackEvents());
    }
}
