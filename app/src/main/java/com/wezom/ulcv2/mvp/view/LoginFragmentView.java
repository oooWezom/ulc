package com.wezom.ulcv2.mvp.view;

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created: Zorin A.
 * Date: 23.05.2016.
 */

@StateStrategyType(SkipStrategy.class)
public interface LoginFragmentView extends BaseFragmentView {
    void animateLoginSuccess();

    void showLoading();

    void hideLoading();

//    void setLanguages(String[] languages);

    void showAppLanguagesDialog();

    void setUiValues();
}
