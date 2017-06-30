package com.wezom.ulcv2.mvp.view;

import com.arellomobile.mvp.MvpView;
import com.wezom.ulcv2.common.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kartavtsev.s on 08.02.2016.
 */
public interface ListLceView<M extends List> extends MvpView {
    void loadData(@Constants.LoadingMode int loadMode);

    void loadData(boolean pullToRefresh);

    void showLoading(@Constants.LoadingMode int loadMode);

    void showLoading(boolean showLoading);

    void showContent();

    void setData(M data);

    void addData(M data);

    void showError(Throwable error, boolean b);

    void showMessageDialog(int title, int message);

    void showMessageDialog(int title, String message);

    void showMessageDialog(int message);

    void showMessageDialog(String message);

    void showGlobalLoading(boolean show);

}
