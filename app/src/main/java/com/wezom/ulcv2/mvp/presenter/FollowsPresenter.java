package com.wezom.ulcv2.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.Screens;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.events.On2PlayClickEvent;
import com.wezom.ulcv2.events.On2TalkClickEvent;
import com.wezom.ulcv2.mvp.view.FollowsView;
import com.wezom.ulcv2.net.ApiManager;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import ru.terrakok.cicerone.Router;

/**
 * Created by sivolotskiy.v on 30.05.2016.
 */

@InjectViewState
public class FollowsPresenter extends BasePresenter<FollowsView> {
    @Inject
    ApiManager mApiManager;
    @Inject
    EventBus mBus;
    @Inject
    Router mRouter;

    public FollowsPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void callSettingsFilter(){
        mRouter.replaceScreen(Screens.SEARCH_SCREEN);
    }

    public void onClick2Play(){
        mBus.post(new On2PlayClickEvent());
    }

    public void onClick2Talk() {
        mBus.post(new On2TalkClickEvent());
    }
}
