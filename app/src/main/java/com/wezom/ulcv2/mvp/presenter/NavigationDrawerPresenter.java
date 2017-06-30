package com.wezom.ulcv2.mvp.presenter;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.Screens;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.events.ChangeAvatarRequestEvent;
import com.wezom.ulcv2.events.CountersUpdateEvent;
import com.wezom.ulcv2.events.FollowsRequestEvent;
import com.wezom.ulcv2.events.HideDrawerEvent;
import com.wezom.ulcv2.events.ProfileImageUpdateEvent;
import com.wezom.ulcv2.events.ProfileUpdatedEvent;
import com.wezom.ulcv2.exception.ApiException;
import com.wezom.ulcv2.mvp.model.NewsFeed;
import com.wezom.ulcv2.mvp.view.NavigationDrawerView;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.models.requests.ProfileRequest;
import com.wezom.ulcv2.ui.fragment.NewsfeedFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import ru.terrakok.cicerone.Router;

/**
 * Created by Sivolotskiy.v on 25.05.2016.
 */
@InjectViewState
public class NavigationDrawerPresenter extends BasePresenter<NavigationDrawerView> {
    @Inject
    EventBus mBus;
    @Inject
    ApiManager mApiManager;
    @Inject
    Router mRouter;

    public NavigationDrawerPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void callFollows() {
        mBus.post(new FollowsRequestEvent(0, true));
    }

    public void callNews() {
        mBus.post(new HideDrawerEvent());
        mRouter.replaceScreen(Screens.NEWS_FEED_SCREEN, new NewsFeed(NewsfeedFragment.TYPE_FOLLOWING, 0, true));
    }

    public void callMessages() {
        mBus.post(new HideDrawerEvent());
        mRouter.replaceScreen(Screens.DIALOGS_SCREEN);
    }

    public void callSearch() {
        mBus.post(new HideDrawerEvent());
        mRouter.replaceScreen(Screens.SEARCH_SCREEN);
    }

    public void callProfile() {
        mBus.post(new HideDrawerEvent());
        mRouter.replaceScreen(Screens.PROFILE_SCREEN, 0);
    }

    public void callSettings() {
        mBus.post(new HideDrawerEvent());
        mRouter.replaceScreen(Screens.SETTINGS_SCREEN);
    }

    public void onClickAvatarChange(int type) {
        //0 - gallery
        //1 - make photo
        mBus.post(new ChangeAvatarRequestEvent(type));
    }


    public void loadProfile() {

        mApiManager.getProfile(new ProfileRequest(0))
                .subscribe(profileResponse -> {

                    getViewState().loadAvatarAndBackground(profileResponse.getProfile());
                }, throwable -> {
                    int errorMessageResId = R.string.error_check_your_net_and_retry;
                    if (throwable instanceof ApiException) {
                        switch (((ApiException) throwable).getCode()) {
                            case 10:
                                errorMessageResId = R.string.error_user_not_found;
                                break;
                            case 11:
                                errorMessageResId = R.string.error_account_disabled;
                                break;
                        }
                    }

                    getViewState().showMessageDialog(errorMessageResId);
                });
    }
}
