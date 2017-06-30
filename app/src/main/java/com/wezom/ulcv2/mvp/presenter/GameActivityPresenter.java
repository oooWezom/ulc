package com.wezom.ulcv2.mvp.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.common.ScreenOrientation;
import com.wezom.ulcv2.common.Utils;
import com.wezom.ulcv2.events.FailureConnectionEvent;
import com.wezom.ulcv2.events.FollowClickEvent;
import com.wezom.ulcv2.events.FollowEvent;
import com.wezom.ulcv2.events.NewFollowerEvent;
import com.wezom.ulcv2.events.OnErrorEvent;
import com.wezom.ulcv2.events.OnLocalLikeClickEvent;
import com.wezom.ulcv2.events.OnNetworkLikeClickEvent;
import com.wezom.ulcv2.events.OnVideoClickEvent;
import com.wezom.ulcv2.events.ReportUserClickEvent;
import com.wezom.ulcv2.events.TalkLikesLocalEvent;
import com.wezom.ulcv2.events.TalkLikesResponseEvent;
import com.wezom.ulcv2.events.UnFollowEvent;
import com.wezom.ulcv2.events.game.BeginExecutionEvent;
import com.wezom.ulcv2.events.game.GameResultEvent;
import com.wezom.ulcv2.events.game.GameStateEvent;
import com.wezom.ulcv2.events.game.NewMessageEvent;
import com.wezom.ulcv2.events.game.PlayerConnectionEvent;
import com.wezom.ulcv2.events.game.DiskMoveEvent;
import com.wezom.ulcv2.events.game.PlayerReadyEvent;
import com.wezom.ulcv2.events.game.ResentUnityMessageEvent;
import com.wezom.ulcv2.events.game.RockSpockMoveEvent;
import com.wezom.ulcv2.events.game.RoundResultEvent;
import com.wezom.ulcv2.events.game.RoundStartEvent;
import com.wezom.ulcv2.events.game.SessionStateEvent;
import com.wezom.ulcv2.events.game.SpectatorConnectionEvent;
import com.wezom.ulcv2.events.game.onRatingStartedEvent;
import com.wezom.ulcv2.exception.WebsocketException;
import com.wezom.ulcv2.interfaces.UnityActionsManager;
import com.wezom.ulcv2.managers.NotImplementedUnityManager;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.managers.RockSpockUnityManager;
import com.wezom.ulcv2.managers.SpinTheDisksUnityManager;
import com.wezom.ulcv2.managers.UnityManager;
import com.wezom.ulcv2.mvp.model.UserData;
import com.wezom.ulcv2.mvp.view.GameActivityView;
import com.wezom.ulcv2.net.SessionChannelHandler;
import com.wezom.ulcv2.net.models.responses.websocket.ChatMessageResponse;
import com.wezom.ulcv2.net.models.responses.websocket.GameResultResponse;
import com.wezom.ulcv2.net.models.responses.websocket.GameStateResponse.GameState;
import com.wezom.ulcv2.net.models.responses.websocket.GameStateResponse.PlayerData;
import com.wezom.ulcv2.ui.activity.HomeActivity;

import org.greenrobot.eventbus.EventBus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import lombok.Data;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.wezom.ulcv2.common.Constants.CONNECTION_MODE_CONNECT;
import static com.wezom.ulcv2.common.Constants.CONNECTION_MODE_DISCONNECT;
import static com.wezom.ulcv2.mvp.presenter.GameActivityPresenter.SessionState.STATE_CALCULATIONS;
import static com.wezom.ulcv2.mvp.presenter.GameActivityPresenter.SessionState.STATE_EXECUTION;
import static com.wezom.ulcv2.mvp.presenter.GameActivityPresenter.SessionState.STATE_GAME;
import static com.wezom.ulcv2.mvp.presenter.GameActivityPresenter.SessionState.STATE_RATING;
import static com.wezom.ulcv2.mvp.presenter.GameActivityPresenter.SessionState.STATE_SESSION_END;


@Data
@InjectViewState
public class GameActivityPresenter extends BasePresenter<GameActivityView> {
    private static final String TAG = GameActivityPresenter.class.getSimpleName();

    @IntDef({STATE_GAME, STATE_EXECUTION, STATE_RATING, STATE_CALCULATIONS, STATE_SESSION_END})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SessionState {
        int STATE_GAME = 1;
        int STATE_EXECUTION = 2;
        int STATE_RATING = 3;
        int STATE_CALCULATIONS = 4;
        int STATE_SESSION_END = 5;
    }

    CompositeSubscription compositeSubscription;

    private static final int NOT_PLAYER_ID = -1;
    private static final int GAME_STATE_READY = 1;
    private static final int GAME_STATE_PLAYING = 2;

    @Inject
    EventBus bus;
    @Inject
    SessionChannelHandler sessionChannelHandler;
    @Inject
    PreferenceManager preferenceManager;
    @Inject
    Context context;

    UnityActionsManager unityManager;

    private int currentOrientation = ScreenOrientation.PORTRAIT;
    private int gameId;
    private int gameType;
    private int loserId;
    private int voteCount;
    private int sessionState;
    private int leftPlayerId;

    private static boolean isGameSupported;
    private boolean isGameSessionOver;
    private boolean isPlayerMode;

    private PlayerData leftGameStatePlayerData;
    private PlayerData rightGameStatePlayerData;

    private UserData leftCachedPlayerData;
    private UserData rightCachedPlayerData;

    private String unityCachedGameState;
    private String videoUrl;
    private GameState fullGameState;

    public GameActivityPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    /***
     * When UnityEngine done loading scene, it calls onUnityReady
     */
    public void onUnityReady() {
        Log.v(TAG, "onUnityReady");
        if (unityCachedGameState == null) {
            return;
        }
        initGameData();
    }

    public void onUnityMessage(String message) {
        Log.v(TAG, "onUnityMessage: " + message);
        unityManager.onMessage(message);
    }

    public void onUnityResentEvent(ResentUnityMessageEvent event) {
        Log.v(TAG, "onUnityResentEvent: " + event.getMessage());
        unityManager.sendMessage(event.getMessage());
    }

    public void onSessionState(SessionStateEvent event) {
        Log.v(TAG, "onSessionState");

        //if u r player - u should be left always
        isGameSupported = Utils.isGameSupported(gameType);
        sessionState = event.getState();

        if (gameType != event.getGame()) {
            gameType = event.getGame();
            initGameRouter(gameType);
        }
        getViewState().setSpectators(event.getSpectators());

        getViewState().hideLoadingView();

        switch (event.getState()) {
            case STATE_GAME: //session state game
                if (isGameSupported) {
                    getViewState().setMessageBoardOnState(STATE_GAME);
                } else {
                    getViewState().switchBoardToGameNotSupportedMode();
                    unityManager.sendSystemMessage(new UnityManager.OrientationMessage(UnityManager.MESSAGE_TYPE_ORIENTATION_CHANGE,
                            UnityManager.ORIENTATION_AUTOROTATE));
                    getViewState().setOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                }
                break;
            case STATE_EXECUTION: // session state execution
                getViewState().setMessageBoardOnState(STATE_EXECUTION);
                sessionState = STATE_EXECUTION;
                loserId = event.getLoserId();
                leftPlayerId = event.getPlayers().get(0).getId();

                if (isPlayerMode) {
                    if (isPortrait()) {
                        getViewState().switchToPortExecutionMode(loserId == preferenceManager.getUserId(), isPlayerMode);//is player(left one) looser
                    } else {
                        getViewState().switchToLandExecutionMode(loserId == preferenceManager.getUserId(), isPlayerMode);
                    }
                } else {
                    if (isPortrait()) {
                        getViewState().switchToPortExecutionMode(loserId == leftPlayerId, isPlayerMode); //is left player looser
                    } else {
                        getViewState().switchToLandExecutionMode(loserId == leftPlayerId, isPlayerMode);//is left player looser
                    }
                }
                break;
            case STATE_CALCULATIONS:
                // getViewState().setMessageBoardOnState(STATE_CALCULATIONS); not using right now
            case STATE_RATING:
                getViewState().setMessageBoardOnState(STATE_RATING);
                sessionState = STATE_RATING;
                isGameSessionOver = true;
                if (isPortrait()) {
                    getViewState().switchNormalModeToPortrait();
                } else {
                    getViewState().switchNormalModeToLandscape();
                }
                getViewState().switchToRatingMode();
                break;
            case STATE_SESSION_END:
                getViewState().setMessageBoardOnState(STATE_SESSION_END);
                sessionState = STATE_SESSION_END;
                if (isPortrait()) {
                    getViewState().switchNormalModeToPortrait();
                } else {
                    getViewState().switchNormalModeToLandscape();
                }

                boolean isLeftWin = event.getLoserId() == event.getPlayers().get(1).getId();
                int leftExp = event.getPlayers().get(0).getExp();
                int rightExp = event.getPlayers().get(1).getExp();

                getViewState().switchToEndMode(isLeftWin, leftExp, rightExp);
                break;
        }
    }

    public void onGameState(GameStateEvent event) {
        Log.v(TAG, "onGameState");
        fullGameState = event.getGameState();
        unityCachedGameState = event.getSelf(); //throws this string to UnityEngine
        initPlayersData(fullGameState.getPlayersData());

        checkPlayersReady();

        getViewState().setDataToVideoView(isPlayerMode, gameId, videoUrl, leftGameStatePlayerData, rightGameStatePlayerData);
        if (isPlayerMode) { // if player - game should rotate landscape on PLAYING state
            switchOrientationAndModeOnGameState();
        }
    }

    public void initMessageBoard() {
        if (isGameSupported) {
            getViewState().switchBoardToInitMode();
        } else {
            getViewState().switchBoardToGameNotSupportedMode();
        }
    }

    private void checkPlayersReady() {
        Log.v(TAG, "checkPlayersReady");
        if (leftGameStatePlayerData.isReady()) {
            getViewState().setLeftPlayerReady();
        }
        if (rightGameStatePlayerData.isReady()) {
            getViewState().setRightPlayerReady();
        }

        if (leftGameStatePlayerData.isReady() && rightGameStatePlayerData.isReady()) { //if both playing
            Log.v(TAG, "two players ready");
            Log.v(TAG, "isGameSupported" + isGameSupported);
            if (isGameSupported) {
                Log.v(TAG, "hideMessageBoard()");
                getViewState().hideMessageBoard();

                if (isPlayerMode) {
                    compositeSubscription.add(Observable.timer(500, TimeUnit.MILLISECONDS)
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(aLong -> {
                                unityManager.sendSystemMessage(new UnityManager.OrientationMessage(UnityManager.MESSAGE_TYPE_ORIENTATION_CHANGE, UnityManager.ORIENTATION_LANDSCAPE));
                            }));
                }
            } else {
                Log.v(TAG, "switchBoardToGameNotSupportedMode()");
                getViewState().switchBoardToGameNotSupportedMode();
            }
        }
    }

    private void initPlayersData(ArrayList<PlayerData> playersData) {
        Log.v(TAG, "initPlayersData");
        if (isPlayerMode) {
            if (playersData.get(0).getId() == preferenceManager.getUserId()) {
                leftGameStatePlayerData = playersData.get(0);
                rightGameStatePlayerData = playersData.get(1);
            } else {
                leftGameStatePlayerData = playersData.get(1);
                rightGameStatePlayerData = playersData.get(0);
            }

        } else {
            if (playersData.get(0).getId() == leftCachedPlayerData.getId()) {
                leftGameStatePlayerData = playersData.get(0);
                rightGameStatePlayerData = playersData.get(1);
            } else {
                leftGameStatePlayerData = playersData.get(1);
                rightGameStatePlayerData = playersData.get(0);
            }
        }
    }

    public void onSpectatorConnection(SpectatorConnectionEvent event, int currentUsersCount) {
        Log.v(TAG, "onSpectatorConnection");
        if (event.getType() == CONNECTION_MODE_CONNECT) {
            currentUsersCount++;
            getViewState().onSpectatorConnected(currentUsersCount);
        }
        if (event.getType() == CONNECTION_MODE_DISCONNECT) {
            currentUsersCount--;
            currentUsersCount = currentUsersCount < 0 ? 0 : currentUsersCount; //crunches
            getViewState().onSpectatorDisconnected(currentUsersCount);
        }
    }

    public void onPlayerConnection(SpectatorConnectionEvent event) {
        Log.v(TAG, "onPlayerConnection");
        if (event.getType() == CONNECTION_MODE_DISCONNECT) {
            getViewState().onPlayerDisconnected(event.getUserId());
        }
    }

    public void onPlayerReady(PlayerReadyEvent event) {
        int id = event.getPlayerId();
        Log.v(TAG, "onPlayerReady id " + id);
        checkPlayersReady(id);
    }

    private void checkPlayersReady(int playerId) {
        Log.v(TAG, "checkPlayersReady");
        if (isPlayerMode) {
            if (playerId == -1) { // if self
                try {
                    sessionChannelHandler.ready();
                } catch (WebsocketException e) {

                    getViewState().showToast(R.string.failed_to_send_ready);
                }
            } else {
                getViewState().setRightPlayerReady();
            }
        } else {
            //who click ready
            if (playerId == leftGameStatePlayerData.getId()) {
                getViewState().setLeftPlayerReady();
            } else {
                getViewState().setRightPlayerReady();
            }
        }
    }

    public void onChatMessage(NewMessageEvent event) {
        getViewState().addMessage(event.getNewMessage());
    }

    public void onFollowClick(FollowClickEvent event) {
        if (Utils.isUserLoggedIn(context)) {
            sessionChannelHandler.follow(event.getId());
        } else {
            getViewState().showSignInDialog(R.string.not_autorized_title, R.string.not_autorized_message);
        }
    }

    public void onVideoClick(OnVideoClickEvent event) {
        Log.v(TAG, "onVideoClick");
        getViewState().switchUiVisibility();
    }

    public void onLikeClick(OnLocalLikeClickEvent event) {
//        Log.v("SOCKET like send", "Talk: " + event.getTalkId() + " likes:" + event.getCount());
//        int talkId = event.getTalkId();
//        int likeCount = event.getCount();
//        sessionChannelHandler.sendLocalLike(talkId, likeCount);

        if (Utils.isUserLoggedIn(context)) {

        } else {
            getViewState().showSignInDialog(R.string.not_autorized_title, R.string.not_autorized_message);
        }
    }

    public void onNetworkLikeClick(OnNetworkLikeClickEvent event) {
//        Log.v("SOCKET like send", "Talk: " + event.getTalkId() + " likes:" + event.getCount());
//        int talkId = event.getTalkId();
//        int likeCount = event.getCount();
//        sessionChannelHandler.sendNetworkLike(talkId, likeCount);
    }

    public void onLikeLocalResponse(TalkLikesLocalEvent event) {
        int talkId = event.getResponse().getTalkId();
        int likes = event.getResponse().getLikes();
        Log.v("SOCKET like receive", "local like receive Talk: " + talkId + " likes:" + likes);
        getViewState().addLikes(talkId, likes);
    }

    public void onLikesResponse(TalkLikesResponseEvent event) {
        getViewState().addLikes(event.getResponse().getTalkId(),
                event.getResponse().getLikes());
    }

    public void onFollowResponse(FollowEvent event) {
        getViewState().showToast(String.format(context.getString(R.string.you_are_following), event.getUser().getName()));
    }

    public void onUnfollowResponse(UnFollowEvent event) {
        getViewState().showToast(R.string.you_are_unfollowing);
    }

    public void onNewFollowerResponse(NewFollowerEvent event) {
        getViewState().showToast(R.string.you_have_new_follower);
    }

    public void onReportUserClick(ReportUserClickEvent event) {
        getViewState().showReportDialog(R.string.report, event.getTalkId());
    }

    public void onPlayerMove(DiskMoveEvent event) {
        Log.v(TAG, "onPlayerMove() angle: " + String.valueOf(event.getAngle() + " disk: " + String.valueOf(event.getDisk())));
        try {
            sessionChannelHandler.moveDisk(event.getAngle(), event.getDisk());
        } catch (WebsocketException e) {
            // do nothing, C`est la vie
        }
    }

    public void onPlayerMove(RockSpockMoveEvent event) {
        Log.v(TAG, "onPlayerMove() type: " + String.valueOf(event.getMoveId()));
        try {
            sessionChannelHandler.setRockSpockMove(event.getMoveId());
        } catch (WebsocketException e) {
            // do nothing, C`est la vie
        }
    }

    public void onRoundResult(RoundResultEvent event) {
        Log.v(TAG, "onRoundResult");
        if (isGameSupported) {
            getViewState().switchBoardToInitMode();
        } else {
            getViewState().switchBoardToGameNotSupportedMode();
        }
        unityManager.sendSystemMessage(new UnityManager.OrientationMessage(UnityManager.MESSAGE_TYPE_ORIENTATION_CHANGE,
                UnityManager.ORIENTATION_AUTOROTATE));
        if (leftGameStatePlayerData.getId() == event.getWinner()) {
            getViewState().addLeftWinner();
        }

        if (rightGameStatePlayerData.getId() == event.getWinner()) {
            getViewState().addRightWinner();
        }

        if (event.isFinalRound()) {
            getViewState().switchToReadyMode();
            onExecutionBegin(null);

        } else {
            getViewState().switchToReadyMode();
        }

//        if (event.getWinner() == 0) {
//             draw! do nothing
//        }
    }

    public void onRoundStart(@Nullable RoundStartEvent event) {
        Log.v(TAG, "onRoundStart");
        if (isPlayerMode) {
            getViewState().switchToPlayMode();
            unityManager.sendSystemMessage(new UnityManager.OrientationMessage(UnityManager.MESSAGE_TYPE_ORIENTATION_CHANGE, UnityManager.ORIENTATION_LANDSCAPE));
        }
        if (isGameSupported) {
            getViewState().hideMessageBoard();
        }
    }

    public void onExecutionBegin(BeginExecutionEvent event) {
        Log.v(TAG, "onExecutionBegin");

        if (event != null) {
            loserId = event.getLoserId();
            sessionState = STATE_EXECUTION;
        }

        getViewState().switchBoardToExecutionMode();
        unityManager.sendSystemMessage(new UnityManager.OrientationMessage(UnityManager.MESSAGE_TYPE_ORIENTATION_CHANGE, UnityManager.ORIENTATION_AUTOROTATE));

        if (isPlayerMode) {
            if (isPortrait()) {
                getViewState().switchToPortExecutionMode(loserId == preferenceManager.getUserId(), isPlayerMode);
            } else {
                getViewState().switchToLandExecutionMode(loserId == preferenceManager.getUserId(), isPlayerMode);
            }
        } else {
            if (isPortrait()) {
                getViewState().switchToPortExecutionMode(loserId == leftGameStatePlayerData.getId(), isPlayerMode);
            } else {
                getViewState().switchToLandExecutionMode(loserId == leftGameStatePlayerData.getId(), isPlayerMode);
            }
        }
    }

    public void onRatingStarted(onRatingStartedEvent event) {
        getViewState().setMessageBoardOnState(STATE_RATING);
        Log.v(TAG, "onRatingStarted");
        sessionState = STATE_RATING;
        getViewState().switchToRatingMode();
        unityManager.sendSystemMessage(new UnityManager.OrientationMessage(UnityManager.MESSAGE_TYPE_ORIENTATION_CHANGE, UnityManager.ORIENTATION_AUTOROTATE));
        if (isPortrait()) {
            getViewState().switchNormalModeToPortrait();
        } else {
            getViewState().switchNormalModeToLandscape();
        }
        isGameSessionOver = true;

        if (!isPlayerMode && Utils.isUserLoggedIn(context)) {
            if (voteCount == 0) {
                getViewState().showVoteView(leftCachedPlayerData);
            } else {
                getViewState().showVoteView(rightCachedPlayerData);
            }
        }
    }

    public void onConnectionEvent(PlayerConnectionEvent event) {
        Log.v(TAG, "onConnectionEvent");
        switch (event.getType()) {
            case CONNECTION_MODE_CONNECT:
                break;
            case Constants.CONNECTION_MODE_DISCONNECT:
                break;
            case Constants.CONNECTION_MODE_RECONNECT:
                break;
        }
    }

    public void onGameResult(GameResultEvent event) {
        Log.v(TAG, "onGameResult");
        sessionState = STATE_SESSION_END;

        boolean isLeftWin;
        int maxExp;
        int minExp;

        GameResultResponse.SessionState sessionState = event.getGameResultResponse().getSessionState();
        isGameSessionOver = true;

        getViewState().switchToReadyMode();

        unityManager.sendSystemMessage(new UnityManager.OrientationMessage(UnityManager.MESSAGE_TYPE_ORIENTATION_CHANGE,
                UnityManager.ORIENTATION_AUTOROTATE));
        maxExp = Math.max(sessionState.getPlayerStats().get(0).getExp(), sessionState.getPlayerStats().get(1).getExp());
        minExp = Math.min(sessionState.getPlayerStats().get(0).getExp(), sessionState.getPlayerStats().get(1).getExp());

        if (isPlayerMode) {
            isLeftWin = sessionState.getLoserId() != preferenceManager.getUserId();
            if (isLeftWin) {
                getViewState().switchToEndMode(isLeftWin, maxExp, minExp);
            } else {
                getViewState().switchToEndMode(isLeftWin, minExp, maxExp);
            }
        } else {
            isLeftWin = sessionState.getLoserId() == rightCachedPlayerData.getId();
            if (isLeftWin) {
                getViewState().switchToEndMode(isLeftWin, maxExp, minExp);
            } else {
                getViewState().switchToEndMode(isLeftWin, minExp, maxExp);
            }
        }
        getViewState().switchBoardToEndMode();
    }

    public void onError(OnErrorEvent event) {
        String message = event.getResponse().getMessage();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        getViewState().hideLoadingView();
        if (message.equals("Session not found")) {
            getViewState().showCloseSessionDialog(R.string.session_is_over, R.string.session_is_over);
        }
    }

    public void onPerformanceDone() {
        Log.v(TAG, "onPerformanceDone");
        if (isPlayerMode && loserId == preferenceManager.getUserId()) {
            try {
                sessionChannelHandler.performanceDone();
            } catch (WebsocketException e) {
                getViewState().showToast(R.string.failed_to_send_done);
            }
        }
    }

    public void setCachedData(int gameId, int gameType, String videoUrl, boolean isPlayer, UserData firstPlayerData, UserData secondPlayerData) {
        Log.v(TAG, "setCachedData");
        this.gameId = gameId;
        this.gameType = gameType;
        this.videoUrl = videoUrl;
        this.isPlayerMode = isPlayer;
        leftCachedPlayerData = firstPlayerData;
        rightCachedPlayerData = secondPlayerData;
        isGameSupported = Utils.isGameSupported(gameType);
        initGameRouter(gameType);
        getViewState().showLoadingView();
        sessionChannelHandler.connect(preferenceManager.getAuthToken(), gameId);

        boolean isBlackColorTheme = isPlayer;
        boolean isShowNextButton = !isPlayer;
        getViewState().modifyUiIfPlayerOrSpectator(isBlackColorTheme, isShowNextButton);
    }

    private void initGameRouter(int gameType) {
        Log.v(TAG, "initGameRouter gameType = " + gameType);
        if (gameType == Constants.Games.SPIN_THE_DISKS.getId()) {
            unityManager = new SpinTheDisksUnityManager(bus);
        } else if (gameType == Constants.Games.ROCK_SPOCK.getId()) {
            unityManager = new RockSpockUnityManager(bus);
        } else {
            unityManager = new NotImplementedUnityManager(null);
        }
    }

    public void handleConfigurationChange(int orientation) {
        Log.v(TAG, "handleConfigurationChange " + orientation);
        currentOrientation = orientation;
        switch (orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getViewState().hideKeyBoard();
                if (sessionState == STATE_GAME || sessionState == STATE_RATING || sessionState == STATE_SESSION_END) {
                    getViewState().switchNormalModeToLandscape();
                } else {
                    if (isPlayerMode) {
                        getViewState().switchToLandExecutionMode(loserId == preferenceManager.getUserId(), isPlayerMode);
                    } else {
                        getViewState().switchToLandExecutionMode(loserId == leftGameStatePlayerData.getId(), isPlayerMode);
                    }
                }
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                if (sessionState == STATE_GAME || sessionState == STATE_RATING || sessionState == STATE_SESSION_END) {
                    getViewState().switchNormalModeToPortrait();
                } else {
                    if (isPlayerMode) {
                        getViewState().switchToPortExecutionMode(loserId == preferenceManager.getUserId(), isPlayerMode);//is player(left one) looser
                    } else {
                        getViewState().switchToPortExecutionMode(loserId == leftCachedPlayerData.getId(), isPlayerMode);
                    }
                }
                break;
        }
    }

    public void sendMessage(String message) {

        if (Utils.isUserLoggedIn(context)) {
            try {
                sessionChannelHandler.sendMessage(message);
                ChatMessageResponse chatMessageResponse = new ChatMessageResponse();
                chatMessageResponse.setText(message);
                ChatMessageResponse.User user = new ChatMessageResponse.User();
                user.setId(preferenceManager.getUserId());
                user.setName(preferenceManager.getProfileData().getName());
                chatMessageResponse.setUser(user);
                getViewState().addMessage(chatMessageResponse);

            } catch (WebsocketException e) {
                getViewState().showToast(R.string.failed_to_send_message);
            }
        } else {
            getViewState().showSignInDialog(R.string.not_autorized_title, R.string.not_autorized_message);
        }

    }

    public void showNextGame() {
        if (Utils.isUserLoggedIn(context)) {
            getViewState().showToast(context.getString(R.string.next_session_eror));
        } else {
            getViewState().showSignInDialog(R.string.not_autorized_title, R.string.not_autorized_message);
        }
        getViewState().showToast(context.getString(R.string.next_session_eror));

    }

    public void onVoteClick(boolean isPositive) {
        int playerVoteId = (voteCount == 0) ? leftGameStatePlayerData.getId() : rightGameStatePlayerData.getId();
        try {
            if (isPositive) {
                sessionChannelHandler.votePlus(playerVoteId);
            } else {
                sessionChannelHandler.voteMinus(playerVoteId);
            }
        } catch (WebsocketException e) {
            getViewState().showToast(R.string.failed_to_send_vote);
        }

        if (voteCount == 0) {
            getViewState().showVoteView(rightCachedPlayerData);
        }
        voteCount++;
    }

    public void onUnityBackPressed() {
        Log.v(TAG, "onUnityBackPressed");
        onClickLeave();
    }

    public void onClickLeave() {
        Log.v(TAG, "onClickLeave");
        if (isPlayerMode && !isGameSessionOver) {
            compositeSubscription.add(Observable.timer(0, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(aLong -> {
                        getViewState().showCloseSessionDialog(R.string.leave_title, R.string.leave_message);
                    }));
        } else {
            leaveSession();
        }
    }

    public void leaveSession() {
        Log.v(TAG, "leave session() isPlayerMode == " + isPlayerMode);
        if (isPlayerMode) {
            sessionChannelHandler.leave();
            Log.v(TAG, "sessionChannelHandler.leave()");
        }
        sessionChannelHandler.disconnect();
        Log.v(TAG, "sessionChannelHandler.disconnect()");
        getViewState().killView();
    }

    public void disconnectSession() {
        sessionChannelHandler.disconnect();
        Log.v(TAG, "sessionChannelHandler.disconnect()");
        getViewState().killView();
    }

    public void followUserClick(int id) {
        sessionChannelHandler.follow(id);
    }

    public void sendPrivateMessage(int id, String message) {
        sessionChannelHandler.sendPrivateMessage(message, id);
    }

    public void reportUser(int talkId, int reportCategory) {
        sessionChannelHandler.reportUser(talkId, reportCategory);
    }

    private void switchOrientationAndModeOnGameState() {
        switch (fullGameState.getState()) {
            case GAME_STATE_READY: // ready
                getViewState().switchToReadyMode();
                unityManager.sendSystemMessage(new UnityManager.OrientationMessage(UnityManager.MESSAGE_TYPE_ORIENTATION_CHANGE,
                        UnityManager.ORIENTATION_AUTOROTATE));
                getViewState().requestOrientationAutorotate();
                break;
            case GAME_STATE_PLAYING: // playing
                compositeSubscription.add(
                        Observable.timer(1000, TimeUnit.MILLISECONDS) //if no delay - orientation landscape wont work
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(t -> onRoundStart(null)));

                break;
            default:
                unityManager.sendSystemMessage(new UnityManager.OrientationMessage(UnityManager.MESSAGE_TYPE_ORIENTATION_CHANGE,
                        UnityManager.ORIENTATION_AUTOROTATE));
                getViewState().setOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                getViewState().requestOrientationAutorotate();
                break;
        }
    }

    private void initGameData() {
        Log.v(TAG, "initGameData");
        if (isPlayerMode) {
            unityManager.sendSystemMessage(
                    new UnityManager.InitMessage(
                            UnityManager.MESSAGE_TYPE_INIT,
                            preferenceManager.getUserId(),
                            gameId,
                            leftCachedPlayerData.getId(),
                            Constants.GAMES_IMAGES_URL
                    ));
        } else {
            unityManager.sendSystemMessage(
                    new UnityManager.InitMessage(
                            UnityManager.MESSAGE_TYPE_INIT, NOT_PLAYER_ID,
                            gameId,
                            leftCachedPlayerData.getId(),
                            Constants.GAMES_IMAGES_URL
                    ));
        }
        Log.d(TAG, "initGameData unityCachedGameState " + unityCachedGameState);
        unityManager.sendMessage(unityCachedGameState);

    }

    public boolean isPortrait() {
        Log.v(TAG, "isPortrait");
        if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                || currentOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                || currentOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                || currentOrientation == ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT) {
            return true;
        } else {
            return false;
        }
    }

    public void showLoginScreen() {
        Log.v("SOCKET_GA", "leave session() isPlayerMode == " + isPlayerMode);
        if (isPlayerMode) {
            sessionChannelHandler.leave();
            Log.v("SOCKET_GA", "sessionChannelHandler.leave()");
        }
        sessionChannelHandler.disconnect();

        Log.v("SOCKET_GA", "sessionChannelHandler.disconnect()");
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(HomeActivity.ACTION_SHOW_LOGIN);
        context.startActivity(intent);
    }

    public void switchUnityScene(int sceneId) {
        Log.v(TAG, "switchUnityScene" + sceneId);
        unityManager.switchScene(sceneId);
    }

    public void onSocketException(FailureConnectionEvent event) {
        Log.v("SOCKET_GA", "onSocketException()");
        getViewState().showToast(R.string.lost_connection);
        getViewState().killView();
    }

    @Override
    public void attachView(GameActivityView view) {
        super.attachView(view);
        compositeSubscription = new CompositeSubscription();
    }

    @Override
    public void detachView(GameActivityView view) {
        super.detachView(view);
        compositeSubscription.clear();
        compositeSubscription = null;
    }
}