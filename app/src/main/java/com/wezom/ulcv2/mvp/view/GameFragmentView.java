package com.wezom.ulcv2.mvp.view;

import android.view.View;

import com.wezom.ulcv2.net.models.Profile;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 19.07.2016
 * Time: 12:04
 */

public interface GameFragmentView extends SessionFragmentView<List<Profile>> {

    void setRoundResults(int firstPlayerWinsCount, int secondPlayerWinsCount);

    void setUpUnityView(View view);

    void switchToPollMode();

    void switchToEndMode(boolean isLeftWin, int leftPlayerExp, int rightPlayerExp);

    void setLikes(long likes);
}
