package com.wezom.ulcv2.mvp.view;

import android.support.annotation.StringRes;
import android.view.SurfaceView;

import com.wezom.ulcv2.mvp.model.NewMessage;
import com.wezom.ulcv2.mvp.model.UserData;
import com.wezom.ulcv2.ui.view.PlayWebCamView;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 23.07.2016
 * Time: 21:40
 */

public interface SessionFragmentView<M extends List> extends ListLceView<M> {

    void setParticipantsData(UserData firstPlayerData, UserData secondPlayerData);

    void setSpectatorsCount(int count);

    void setLeftParticipantLikes(long likes);

    void setRightParticipantLikes(long likes);

    void setLeftWebCamSurface(SurfaceView surfaceView);

    void setRightWebCamSurface(SurfaceView surfaceView);

    void addSystemMessage(String message);

    void showPrivateMessageDialog(int userId);

    void addMessage(NewMessage newMessage);

    void showUserContextMenu(int id, boolean isLeft);

    void showToast(int resId, int duration);

    void showMessageDialog(@StringRes int title, @StringRes int message);

    void runLikesAnimation(PlayWebCamView.WebCamAlignEnum align);
}
