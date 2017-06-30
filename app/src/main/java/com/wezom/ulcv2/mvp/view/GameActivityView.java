package com.wezom.ulcv2.mvp.view;

import com.wezom.ulcv2.interfaces.MessageBoardActions;
import com.wezom.ulcv2.mvp.model.NewMessage;
import com.wezom.ulcv2.mvp.model.UserData;
import com.wezom.ulcv2.mvp.presenter.GameActivityPresenter;
import com.wezom.ulcv2.net.models.responses.websocket.ChatMessageResponse;
import com.wezom.ulcv2.net.models.responses.websocket.GameStateResponse;

/**
 * Created by kartavtsev.s on 02.02.2016.
 */
public interface GameActivityView extends BaseActivityView, MessageBoardActions {

    void switchToPlayMode();

    void switchToReadyMode();

    void setSpectators(int spectators);

    void switchToPortExecutionMode(boolean leftPlayerLoser, boolean isPlayerMode);

    void switchToLandExecutionMode(boolean leftPlayerLoser, boolean isPlayerMode);

    void switchToRatingMode();

    void switchToEndMode(boolean isLeftWin, int leftPlayerExp, int rightPlayerExp);

    void addMessage(ChatMessageResponse messageResponse);

    void addMessage(NewMessage messageResponse);

    void showReportDialog(int title, int talkId);

    void setDataToVideoView(boolean isPlayer, int gameId, String videoUrl, GameStateResponse.PlayerData firstPlayer, GameStateResponse.PlayerData secondPlayer);

    void switchNormalModeToLandscape();

    void switchNormalModeToPortrait();

    void requestOrientationLandscape();

    void requestOrientationAutorotate();

    void showVoteView(UserData playerData);

    void initGame();

    void killView();

    void showToast(int message);

    void showToast(String text);

    void setOrientation(int orientationSensorLandscape);

    void addLikes(int talkId, int likes);

    void showCloseSessionDialog(int title, int message);

    void addLeftWinner();

    void addRightWinner();

    void switchUiVisibility();

    void onSpectatorConnected(int spectatorsCount);

    void onSpectatorDisconnected(int spectatorsCount);

    void onPlayerDisconnected(int playerId);

    void modifyUiIfPlayerOrSpectator(boolean isBlackColorTheme, boolean isShowNextButton);

    void hideKeyBoard();

    void showSignInDialog(int title, int message);

    void setRightPlayerReady();

    void setLeftPlayerReady();

    void showLoadingView();

    void hideLoadingView();

    void setMessageBoardOnState(@GameActivityPresenter.SessionState int gameState);

    void onRoundsEnd();
}
