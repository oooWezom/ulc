package com.wezom.ulcv2.interfaces;

import com.wezom.ulcv2.mvp.model.UserData;
import com.wezom.ulcv2.net.models.Session;
import com.wezom.ulcv2.net.models.Talk;
import com.wezom.ulcv2.net.models.responses.websocket.GameStateResponse;

/**
 * Created by sivolotskiy.v on 13/10/2016.
 */

public interface PlayVideoFragmentConnector {
    void hideUi(boolean isControlsHidden);

    void setData(GameStateResponse.PlayerData session, int mGameId, String videoUrl);

    void executePlayback(boolean isInStreamerMode);

    void setResults(String result);

    void stopVideo();

    void addWins();

    void setUserData(UserData data, int wins);

    void setChecked(boolean isChecked);

    void showSingleWindowMenu();

    void refreshDataAndMenu();

    void addLikes(int likes);

    void setLikes(int likes);

    void clearLikes();

    void setIsStreamerMode(boolean isInStreamerMode);

    void setCanLikesMode(boolean can);

    void pause();

    void resume();

    int getPlayerId();
}
