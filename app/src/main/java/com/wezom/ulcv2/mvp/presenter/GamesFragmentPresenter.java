package com.wezom.ulcv2.mvp.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.events.BackEvents;
import com.wezom.ulcv2.exception.WebsocketException;
import com.wezom.ulcv2.injection.qualifier.ApplicationContext;
import com.wezom.ulcv2.mvp.model.Game;
import com.wezom.ulcv2.mvp.view.GamesFragmentView;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.ProfileChannelHandler;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.wezom.ulcv2.common.Constants.SUPPORTED_GAMES;

/**
 * Created: Zorin A.
 * Date: 27.06.2016.
 */

@InjectViewState
public class GamesFragmentPresenter extends BasePresenter<GamesFragmentView> {
    @Inject
    ApiManager mApiManager;
    @Inject
    EventBus mBus;
    @Inject
    @ApplicationContext
    Context mContext;
    @Inject
    ProfileChannelHandler mWebSocketManager;


    public GamesFragmentPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void onBackPressed() {
        mBus.post(new BackEvents());
    }

    public void filterGames(CharSequence charSequence, List<Game> gameList) {
        if (TextUtils.isEmpty(charSequence)) {

            getViewState().setFilteredGames(gameList);
            getViewState().showEmptyView(false);
            return;
        }

        List<Game> filteredGames = new ArrayList<>();
        for (Game game : gameList) {
            if (game.getTitle().toLowerCase().startsWith(String.valueOf(charSequence).toLowerCase())) {
                filteredGames.add(game);
            }
        }
        if (filteredGames.size() > 0) {
            getViewState().showEmptyView(false);
        } else {
            getViewState().showEmptyView(true);
        }
        getViewState().setFilteredGames(filteredGames);
    }


    public void getNewGamesList() {
        List<Game> games = Constants.getGamesList(mContext);
        /*
        from Constants:
        RANDOM(0,R.string.random_game),
        HELICOPTER(5, R.string.helicopter),
        ROCK_SPOCK(6, R.string.rock_spock),
        SPIN_THE_DISKS(7, R.string.spin_the_disks),
        MEMORIZE_HIDES(8, R.string.memorize_hides),
        UFO(9, R.string.ufo);*/

        getViewState().setAllGames(games);
    }

    public void searchGame(List<Integer> games) {
        if (games.size() == 0) {//no games selected
            getViewState().showMessageDialog(R.string.select_games);
            return;
        }
        try {
            if (games.get(0) == 0) { //user choose "Random Game"
                List<Integer> randomGame = new ArrayList<>();
                randomGame.add(SUPPORTED_GAMES[0]);
                randomGame.add(SUPPORTED_GAMES[1]);
                mWebSocketManager.searchGame(randomGame);
            } else {
                if (games.size() > 0) {
                    List<Integer> filteredGames = new ArrayList<>();
                    for (int i = 0; i < games.size(); i++) {
                        for (int j = 0; j < SUPPORTED_GAMES.length; j++) {
                            if (games.get(i) == SUPPORTED_GAMES[j]) {
                                filteredGames.add(games.get(i));
                            }
                        }
                    }
                    if (filteredGames.size() > 0) {
                        mWebSocketManager.searchGame(filteredGames);
                    }else {
                        getViewState().showMessageDialog(R.string.select_games);
                    }
                }
            }
        } catch (WebsocketException e) {
            getViewState().showMessageDialog(R.string.failed_to_search_game);
        }
    }
}
