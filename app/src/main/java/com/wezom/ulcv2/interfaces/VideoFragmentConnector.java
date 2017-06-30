package com.wezom.ulcv2.interfaces;

import com.wezom.ulcv2.net.models.Talk;

/**
 * Created: Zorin A.
 * Date: 28.09.2016.
 */

public interface VideoFragmentConnector {
    void hideUi(boolean isControlsHidden);

    void setData(Talk talk);

    void showStreamerMenu();

    void showMultiWindowMenu();

    void hideMenu();

    void setChecked(boolean isChecked);

    void showSingleWindowMenu();

    void refreshDataAndMenu();

    void addLikes(int likes);

    void setLikes(int likes);

    void clearLikes();

    void setIsStreamerMode(boolean isInStreamerMode);

    void setUiVisibilityState(boolean isVisible);

    void stopContentExecution();

    void pause();

    void resume();

    Talk getCurrentTalk();
}
