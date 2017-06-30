package com.wezom.ulcv2.mvp.view;

import com.wezom.ulcv2.net.models.Profile;

import java.util.ArrayList;

/**
 * Created by Zorin.a on 16.06.2016.
 */
public interface SearchView extends BaseFragmentView {
    void showLoading(boolean b);

    void showError(Throwable throwable);

    void setData(ArrayList<Profile> response);

    void addData(ArrayList<Profile> response);

    void showContent();
}
