package com.wezom.ulcv2.mvp.view;

import com.wezom.ulcv2.net.models.Profile;

import java.util.ArrayList;

/**
 * Created by kartavtsev.s on 18.01.2016.
 */
public interface SearchFragmentView extends ListLceView<ArrayList<Profile>> {
    void setLanguages(ArrayList<String> languageNames);

    void prepareLevelRangeBar(int level);

    void showKeyboard();

}
