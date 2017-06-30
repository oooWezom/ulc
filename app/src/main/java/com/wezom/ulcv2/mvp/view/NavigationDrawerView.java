package com.wezom.ulcv2.mvp.view;

import com.wezom.ulcv2.net.models.Profile;

/**
 * Created by Sivolotskiy.v on 25.05.2016.
 */

public interface NavigationDrawerView extends BaseFragmentView {

    void countersUpdate(int warning,int follows,int messages);

    void profileUpdate(String username,String photo);
    void showAvatarPopupMenu();

    void loadAvatarAndBackground(Profile profile);
}
