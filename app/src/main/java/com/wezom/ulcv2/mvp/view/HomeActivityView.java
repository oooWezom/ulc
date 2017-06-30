package com.wezom.ulcv2.mvp.view;

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.wezom.ulcv2.common.Constants.LayoutMode;
import com.wezom.ulcv2.mvp.model.UserData;
import com.wezom.ulcv2.net.models.requests.websocket.InviteToTalkRequest;
import com.wezom.ulcv2.net.models.responses.websocket.NotificationResponse;

import java.util.ArrayList;

/**
 * Created: Zorin A.
 * Date: 23.05.2016.
 */

public interface HomeActivityView extends BaseActivityView {
    void setLayoutMode(LayoutMode mode);

    void showLoadingEvent(boolean show);

    void showFbLogin(ArrayList<String> scope);

    void showVkLogin(String email);

    void dispatchPickPictureIntent(boolean isAvatar);

    void dispatchTakePictureIntent();

    void animateRevealOut();

    void setToolbarTitle(String title);

    void dispatchStartGameIntent(int sessionId, int gameType,String videoUrl, int watchers, boolean isPlayer, UserData firstUser, UserData secondUser);

//    void dispatchStartTalkIntent(int sessionId, String categoryTitle, String categoryIconUrl, boolean isParticipant);

    void showGameSearch(boolean show);

    void setGameSearchCountdown(int time);

    void hideDrawer();

    void showDrawer(boolean show);

    void showMessageDialog(int messageResId);

    @StateStrategyType(SkipStrategy.class)
    void showAcceptDialog(InviteToTalkRequest inviteToTalkRequest);

    void startVideoPreview();

    void stopVideoPreview();

    void openDrawer();

    void setNewNotification(NotificationResponse notification);

    void hideKeyboard();

    void hideTintLayer(boolean hide);

    void allowVideoPreview(boolean isAllowed);

    void confirmRegister(String token);

    void restart();

    @StateStrategyType(SkipStrategy.class)
    void showReconnectDialog();
}
