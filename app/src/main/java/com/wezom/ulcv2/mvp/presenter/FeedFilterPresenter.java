package com.wezom.ulcv2.mvp.presenter;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.events.BackEvents;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.mvp.view.FeedFilterView;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created by Sivolotskiy.v on 08.06.2016.
 */

@InjectViewState
public class FeedFilterPresenter extends BasePresenter<FeedFilterView> {
    @Inject
    EventBus mBus;
    @Inject
     PreferenceManager mPreferenceManager;


    public FeedFilterPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void loadFilterSettings() {
        PreferenceManager.FilterPrefs filterPrefs = mPreferenceManager.getFilterPrefs();
            getViewState().setData(filterPrefs);

    }

    public void switchPrefs(boolean games, boolean talks, boolean follows) {
        PreferenceManager.FilterPrefs filterPrefs = mPreferenceManager.getFilterPrefs();
        filterPrefs.setGames(games);
        filterPrefs.setFollows(follows);
        filterPrefs.setTalkss(talks);
        mPreferenceManager.setFilterPrefs(filterPrefs);
    }

    public void onBack() {
        mBus.post(new BackEvents());
    }
}
