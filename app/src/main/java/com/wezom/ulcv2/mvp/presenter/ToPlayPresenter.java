package com.wezom.ulcv2.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.Screens;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.events.PlaySessionEvent;
import com.wezom.ulcv2.events.SearchGameToWatchEvent;
import com.wezom.ulcv2.events.ShowNextSessionEvent;
import com.wezom.ulcv2.events.WatchGameEvent;
import com.wezom.ulcv2.mvp.view.ToPlayView;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.models.Session;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ru.terrakok.cicerone.Router;

/**
 * Created: Zorin A.
 * Date: 27.06.2016.
 */

@InjectViewState
public class ToPlayPresenter extends BasePresenter<ToPlayView> {
    @Inject
    EventBus mBus;
    @Inject
    ApiManager mApiManager;
    @Inject
    Router mRouter;

    private List<Session> mLoadedSessions;
    private int mOffset;
    private int mCount = 10;
    private int mSelectedSessionPosition;

    public ToPlayPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }
    public void searchRandomGame() {
        mBus.post(new SearchGameToWatchEvent());
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

    public void playSession() {
        mRouter.replaceScreen(Screens.GAMES_SCREEN);
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
