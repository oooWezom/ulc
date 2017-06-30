package com.wezom.ulcv2.mvp.view;

/**
 * Created: Zorin A.
 * Date: 26.09.2016.
 */

public interface VideoFragmentView extends BaseFragmentView {
    void setIsFollower(int link);

    void showCloseSessionDialog(int not_autorized_title, int not_autorized_message);
}
