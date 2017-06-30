package com.wezom.ulcv2.mvp.view;

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.wezom.ulcv2.mvp.model.Language;
import com.wezom.ulcv2.net.models.Profile;

import java.util.List;

/**
 * Created by Sivolotskiy.v on 09.06.2016.
 */

public interface SettingsView extends BaseFragmentView {

    void setData(Profile data, boolean pushEnabled);

    void setLogin(String login);

    @StateStrategyType(SkipStrategy.class)
    void setLanguages(List<Language> response);

    void showAvatarPopupMenu();

    void loadAvatarAndBackground(Profile profile);
}
