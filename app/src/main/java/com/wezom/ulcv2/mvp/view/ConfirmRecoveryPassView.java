package com.wezom.ulcv2.mvp.view;

/**
 * Created by Android 3 on 24.03.2017.
 */

public interface ConfirmRecoveryPassView extends BaseFragmentView {
    void showLocalLoading();

    void hideLocalLoading();

    void showErrorText(int error);
}
