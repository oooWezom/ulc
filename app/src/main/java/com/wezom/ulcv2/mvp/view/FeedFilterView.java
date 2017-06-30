package com.wezom.ulcv2.mvp.view;

import com.wezom.ulcv2.managers.PreferenceManager;

/**
 * Created by Sivolotskiy.v on 08.06.2016.
 */
public interface FeedFilterView extends BaseFragmentView {
    void setData(PreferenceManager.FilterPrefs prefs);
}
