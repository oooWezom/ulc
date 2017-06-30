package com.wezom.ulcv2.mvp.presenter;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.events.BackEvents;
import com.wezom.ulcv2.events.ChangeEvents;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.mvp.view.ChangeLoginView;
import com.wezom.ulcv2.net.ApiManager;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created by Sivolotskiy.v on 09.06.2016.
 */
@InjectViewState
public class ChangeLoginPresenter extends BasePresenter<ChangeLoginView> {

    @Inject
    ApiManager mApiManager;
    @Inject
    EventBus mBus;
    @Inject
    PreferenceManager mPreferenceManager;

    public ChangeLoginPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void saveLogin(String newLogin) {
        getViewState().showGlobalLoading(true);
      /*  ProfileUpdateRequest profile = new ProfileUpdateRequest();
        profile.setUsername(newLogin);
        mApiManager.updateProfile(profile).subscribe(response -> {
            if (isViewAttached()) {*/
                getViewState().showGlobalLoading(false);   //TODO uncomment after added method change login in API
                loginChanged(newLogin);
          /*  }
        }, throwable -> {
            if (isViewAttached()) {
                getView().showGlobalLoading(false);
                getView().showError(throwable.getMessage());
            }
        });*/
    }

    public void loginChanged(String login) {
        mBus.post(new ChangeEvents.ChangedLoginEvent(login));
        mBus.post(new BackEvents());
    }

    public void onBackPressed() {
        mBus.post(new BackEvents());
    }
}
