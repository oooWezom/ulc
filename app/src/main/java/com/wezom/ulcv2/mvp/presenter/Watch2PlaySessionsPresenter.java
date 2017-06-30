package com.wezom.ulcv2.mvp.presenter;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.events.ShowNextSessionEvent;
import com.wezom.ulcv2.events.WatchGameEvent;
import com.wezom.ulcv2.injection.qualifier.ApplicationContext;
import com.wezom.ulcv2.mvp.view.Watch2PlayFragmentView;
import com.wezom.ulcv2.mvp.view.Watch2TalkFragmentView;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.models.Session;
import com.wezom.ulcv2.net.models.Talk;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ru.terrakok.cicerone.Router;

/**
 * Created: Zorin A.
 * Date: 24.02.2017.
 */

@InjectViewState
public class Watch2PlaySessionsPresenter extends BasePresenter<Watch2PlayFragmentView> {
    @Inject
    EventBus mBus;
    @Inject
    ApiManager mApiManager;
    @Inject
    @ApplicationContext
    Context mContext;
    @Inject
    Router mRouter;

    private List<Session> mLoadedSessions;
    private int mSelectedSessionPosition;
    private int mCount = 10;
    private int mOffset;
    private int mSelectedTalkPosition;

    public Watch2PlaySessionsPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void getActiveGames(@Constants.LoadingMode int loadMode) {
        if (loadMode == Constants.LOADING_MODE_INITIAL || loadMode == Constants.LOADING_MODE_REFRESH) {
            mOffset = 0;
        }
        mApiManager.getActiveGames().subscribe(activeGamesResponse -> {

            ArrayList<Session> sessions = activeGamesResponse.getSession();
            mLoadedSessions = sessions;
            getViewState().setData(sessions);
            getViewState().showContent();
        }, throwable -> {
            getViewState().showError(throwable, false);
        });
        mOffset += mCount;
    }

    public void watchGame(Session session, int position) {
        mSelectedSessionPosition = position;
        mBus.post(new WatchGameEvent(session));
    }

    @Subscribe
    public void onShowNextSession(ShowNextSessionEvent event) {
        int nextPosition = mSelectedSessionPosition + 1;
        if (mLoadedSessions.size() - 1 >= nextPosition) {
            Session nextGame = mLoadedSessions.get(nextPosition);
            watchGame(nextGame, nextPosition);
        }
    }
}
