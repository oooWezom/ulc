package com.wezom.ulcv2.mvp.presenter;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.events.BackEvents;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.mvp.view.BlackListView;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.models.requests.BlacklistRequest;
import com.wezom.ulcv2.net.models.requests.RemoveFromBlackListRequest;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Created by Sivolotskiy.v on 13.06.2016.
 */

@InjectViewState
public class BlackListPresenter extends BasePresenter<BlackListView> {

    @Inject
    ApiManager mApiManager;
    @Inject
    EventBus mBus;

    @Setter
    @Accessors(prefix = "m")
    private int mOffset = 0;
    private int mCount = 100;
    private String mQuery ;


    public BlackListPresenter() {
        ULCApplication.mApplicationComponent.inject(this);

    }

    public void loadBlacklist(@Constants.LoadingMode int loadMode, String query) {
        if (loadMode == Constants.LOADING_MODE_REFRESH || loadMode == Constants.LOADING_MODE_INITIAL) {
            mOffset = 0;
        }

        mQuery = query;
        mApiManager.getBlacklist(new BlacklistRequest(mOffset, mCount, query))
                .subscribe(blacklistResponse -> {
                        if (loadMode != Constants.LOADING_MODE_ENDLESS) {
                            getViewState().setData(blacklistResponse.getResponse());
                        } else {
                            getViewState().addData(blacklistResponse.getResponse());
                        }
                        getViewState().showContent();
                    
                }, throwable -> {
                    throwable.getMessage();
                        getViewState().showError(throwable, false);
                });

        mOffset += mCount;
    }

    public void onRemoveClick(int id, String query) {
        getViewState().showLoading(false);
        mApiManager.removeFromBlacklist(new RemoveFromBlackListRequest(id))
                .subscribe(response -> {
                        loadBlacklist(Constants.LOADING_MODE_INITIAL, query);
                }, throwable -> {
                        getViewState().showContent();
                });
    }

    public void onBackPressed() {
        mBus.post(new BackEvents());
    }
}
