package com.wezom.ulcv2.mvp.view;

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.wezom.ulcv2.mvp.model.Language;

import java.util.List;

/**
 * Created by Sivolotskiy.v on 26.05.2016.
 */

@StateStrategyType(SkipStrategy.class)
public interface SignupView extends BaseFragmentView {

    void showEmailConfirmView();

    void showLocalLoading();

    void hideLocalLoading();

    void showError(String error);

    void setLanguages(List<Language> response);
}
