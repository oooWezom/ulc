package com.wezom.ulcv2.mvp.view;

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.wezom.ulcv2.net.models.Profile;
import com.wezom.ulcv2.net.models.requests.websocket.InviteToTalkRequest;

/**
 * Created: Zorin A.
 * Date: 31.05.2016.
 */

@StateStrategyType(SkipStrategy.class)
public interface ProfileFragmentView extends BaseFragmentView {
    void setProfileData(Profile profile);

    void switchToForeignProfile();

    void showAvatarPopupMenu();

    void enableForeignControls();

    void disableOptions();

    void showLoading();

    void hideLoading();

    void showErrorMessage(int errorMessageResId);

    void updateBlockButtonStatus(boolean isUserBlocked);

    void showMessageReadToast();

    void showInviteDialog(int expires);

    void hideReconnectDialog();

    void dismissDialog();
}
