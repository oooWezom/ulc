package com.wezom.ulcv2.mvp.view;

import com.wezom.ulcv2.net.models.Profile;

import java.util.ArrayList;

/**
 * Created by zorin.a on 19.09.2016.
 */
public interface TalkUserSearchFragmentView extends ListLceView<ArrayList<Profile>> {

    void setSearchState(boolean isActive);
}
