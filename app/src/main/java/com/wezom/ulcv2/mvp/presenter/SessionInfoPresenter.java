package com.wezom.ulcv2.mvp.presenter;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.Screens;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.events.BackEvents;
import com.wezom.ulcv2.events.OnShowDrawerEvent;
import com.wezom.ulcv2.injection.qualifier.ApplicationContext;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.mvp.model.Game;
import com.wezom.ulcv2.mvp.view.SessionInfoView;
import com.wezom.ulcv2.net.ApiManager;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

import lombok.Getter;
import lombok.experimental.Accessors;
import ru.terrakok.cicerone.Router;

import static com.wezom.ulcv2.common.Constants.Games.HELICOPTER;
import static com.wezom.ulcv2.common.Constants.Games.MEMORIZE_HIDES;
import static com.wezom.ulcv2.common.Constants.Games.RANDOM;
import static com.wezom.ulcv2.common.Constants.Games.ROCK_SPOCK;
import static com.wezom.ulcv2.common.Constants.Games.SPIN_THE_DISKS;
import static com.wezom.ulcv2.common.Constants.Games.UFO;

/**
 * Created by sivolotskiy.v on 14.07.2016.
 */

@InjectViewState
public class SessionInfoPresenter extends BasePresenter<SessionInfoView> {

    @Inject
    @Getter
    @Accessors(prefix = "m")
    EventBus mBus;
    @Inject
    @Getter
    @Accessors(prefix = "m")
    PreferenceManager mPreferenceManager;

    @Inject
    @ApplicationContext
    Context mContext;
    @Inject
    ApiManager mApiManager;
    @Inject
    Router mRouter;


    public SessionInfoPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void onBackPressed() {
        mBus.post(new BackEvents());
    }

    public Category getCategoriesById(int id) {
        return Category.getCategoryById(id);
    }

    public Game getGameById(long gameId) {
        Game game = null;
        List<Game> gameList = Constants.getGamesList(mContext);
        for (Game g : gameList) {
            if (gameId == g.getGameId()) {
                game = g;
                break;
            }
        }
        return game;
    }

    public int getGameIconRes(int gameId) {
        int resId = 0;
        if (checkGameId(HELICOPTER, gameId)) {
            resId = R.drawable.ic_choose_land_it;
        }
        if (checkGameId(RANDOM, gameId)) {
            resId = R.drawable.ic_choose_random;
        }
        if (checkGameId(ROCK_SPOCK, gameId)) {
            resId = R.drawable.ic_choose_rock_spok;
        }
        if (checkGameId(SPIN_THE_DISKS, gameId)) {
            resId = R.drawable.ic_choose_spin;
        }
        if (checkGameId(MEMORIZE_HIDES, gameId)) {
            resId = R.drawable.ic_choose_math2;
        }
        if (checkGameId(UFO, gameId)) {
            resId = R.drawable.ic_choose_x_cows;
        }
        return resId;
    }

    private boolean checkGameId(Constants.Games game, int gameID) {
        return gameID == game.getId();
    }

    public void getDetailedEventById(int id) {
        mApiManager.getEventById(id).subscribe(event -> {
            getViewState().setDetailedEvent(event.getEvent());
        }, throwable -> getViewState().showError(throwable, false));
    }

    public void isShowDrawer(boolean isShowDrawer) {
        mBus.post(new OnShowDrawerEvent(isShowDrawer));
    }
}
