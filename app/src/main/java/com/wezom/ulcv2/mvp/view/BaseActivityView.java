package com.wezom.ulcv2.mvp.view;


import com.arellomobile.mvp.MvpView;

/**
 * Created: Zorin A.
 * Date: 23.05.2016.
 */
public interface BaseActivityView extends MvpView {
    void showMessageDialog(int title, int message);

    void showMessageDialog(int title, String message);

    void showMessageDialog(int message);

    void showMessageDialog(String message);

    void showUpdateDialog();

    void showToast(int message);

    void showToast(String message);
}
