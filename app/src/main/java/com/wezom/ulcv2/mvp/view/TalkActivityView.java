package com.wezom.ulcv2.mvp.view;

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.wezom.ulcv2.interfaces.TalkUserSearchFragmentConnector;
import com.wezom.ulcv2.net.models.requests.websocket.InviteToTalkRequest;
import com.wezom.ulcv2.ui.fragment.ChatFragment;

/**
 * Created by kartavtsev.s on 02.02.2016.
 */
public interface TalkActivityView extends BaseActivityView {
    void updateParticipantsData();

    void showCloseSessionDialog(int title, int message);

    void showReportDialog(int title, int talkId);

    void closeSession();

    void showRenameTalkDialog();

    void setTalkTitle(String newTitle);

    void showSignInDialog(int title, int message);

    void showDonateMessageDialog();

    void setDonateMessage(String message);

    void prepareUiVisibility(int windowMode, boolean isParticipant);

    void onSearchFragmentSet(TalkUserSearchFragmentConnector fragment);

    @StateStrategyType(SkipStrategy.class)
    void showAcceptDialog(InviteToTalkRequest inviteToTalkRequest);

    void setTalkIcon(String categoryIconUrl);

    void onShowFullDonateMessage();

    void exitTalkActivity();

    void addLikesToLeftTalk(int likes);

    void addLikesToRightTalk(int likes);

    void showTalkClosedMessage(int title, int message);

    void modifyLayout(int windowMode, boolean isStreamer);

    void updateTalkTitle(String talkName);

    void onChatSet(ChatFragment fragment);

    void switchUiVisibility();

    void setWindowMode(int windowMode);

    void setUiBlocked(boolean isBlock);

    void closeSearchPanel();

    void showNextButtonLoading(boolean isVisible);

    void showConnectionLostMessage(int title, int message);

    void killView();

    void setLoadingViewVisible(boolean isVisible);
}
