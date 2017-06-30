package com.wezom.ulcv2.mvp.view;

/**
 * Created by Sivolotskiy.v on 27.05.2016.
 */
public interface PasswordRecoveryView extends BaseFragmentView {

    void showLocalLoading();

    void hideLocalLoading();

    void showSuccessRestoreText();

    void showErrorText(int error);
}
