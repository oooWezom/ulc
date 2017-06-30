package com.wezom.ulcv2.mvp.view;


import com.arellomobile.mvp.MvpView;

/**
 * Created by kartavtsev.s on 02.02.2016.
 */
public interface SessionActivityView extends MvpView {

    void initCamera(boolean isPlayer, int leftSessionId, int rightSessionId, int firstPlayerId, int secondPlayerId);

    void closeSession();

    void showLeaveConfirmationMessage();

    void showToast(int resId, int duration);

    void setLandscapeOrientation();

    void setPortraitOrientation();

    void showMessageDialog(int messageResId);

}
