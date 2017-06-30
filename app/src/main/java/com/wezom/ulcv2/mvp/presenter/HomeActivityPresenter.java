package com.wezom.ulcv2.mvp.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.birbit.android.jobqueue.JobManager;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.Screens;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.common.LocaleUtils;
import com.wezom.ulcv2.common.Utils;
import com.wezom.ulcv2.database.DatabaseManager;
import com.wezom.ulcv2.events.AvatarCropEvent;
import com.wezom.ulcv2.events.BackEvents;
import com.wezom.ulcv2.events.BackgroundCropEvent;
import com.wezom.ulcv2.events.ChangeAvatarRequestEvent;
import com.wezom.ulcv2.events.ChangeBackgroundRequestEvent;
import com.wezom.ulcv2.events.ChangeEvents;
import com.wezom.ulcv2.events.ConfirmChangePassEvent;
import com.wezom.ulcv2.events.ConnectionRecoveredEvent;
import com.wezom.ulcv2.events.CountersUpdateEvent;
import com.wezom.ulcv2.events.CreatePlayEvent;
import com.wezom.ulcv2.events.CreateTalkClickEvent;
import com.wezom.ulcv2.events.CreateTalkEvent;
import com.wezom.ulcv2.events.DialogSelectedEvent;
import com.wezom.ulcv2.events.ErrorMessageEvent;
import com.wezom.ulcv2.events.FailureConnectionEvent;
import com.wezom.ulcv2.events.FindTalkPartnerEvent;
import com.wezom.ulcv2.events.FollowClickEvent;
import com.wezom.ulcv2.events.FollowersShowEvent;
import com.wezom.ulcv2.events.FollowsRequestEvent;
import com.wezom.ulcv2.events.GameCreatedEvent;
import com.wezom.ulcv2.events.GameFoundEvent;
import com.wezom.ulcv2.events.HideDrawerEvent;
import com.wezom.ulcv2.events.InviteToTalkRequestEvent;
import com.wezom.ulcv2.events.InviteToTalkResponseEvent;
import com.wezom.ulcv2.events.InviteToTalkSentResultEvent;
import com.wezom.ulcv2.events.LanguageSelectedEvent;
import com.wezom.ulcv2.events.LanguagesFetchEvent;
import com.wezom.ulcv2.events.LanguagesSelectedWithDelay;
import com.wezom.ulcv2.events.LoginEvent;
import com.wezom.ulcv2.events.LogoutRequestEvent;
import com.wezom.ulcv2.events.LostConnectionEvent;
import com.wezom.ulcv2.events.NewFollowerEvent;
import com.wezom.ulcv2.events.NewMessageEvent;
import com.wezom.ulcv2.events.NewNotificationEvent;
import com.wezom.ulcv2.events.On2PlayClickEvent;
import com.wezom.ulcv2.events.On2TalkClickEvent;
import com.wezom.ulcv2.events.OnDrawerOpenEvent;
import com.wezom.ulcv2.events.OnNetworkStateEvent;
import com.wezom.ulcv2.events.OnShowDrawerEvent;
import com.wezom.ulcv2.events.OnShowMessageDialogEvent;
import com.wezom.ulcv2.events.ProfileClickedEvent;
import com.wezom.ulcv2.events.ProfileImageUpdateEvent;
import com.wezom.ulcv2.events.ProfileUpdatedEvent;
import com.wezom.ulcv2.events.RectonnectChannelEvent;
import com.wezom.ulcv2.events.SearchGameEvent;
import com.wezom.ulcv2.events.SessionInfoEvent;
import com.wezom.ulcv2.events.ShowGlobalLoadingEvent;
import com.wezom.ulcv2.events.ShowTalkEvent;
import com.wezom.ulcv2.events.SocialLoginRequestEvent;
import com.wezom.ulcv2.events.UpdateDateRequestEvent;
import com.wezom.ulcv2.events.VideoPreviewEvent;
import com.wezom.ulcv2.events.WatchGameEvent;
import com.wezom.ulcv2.exception.ApiException;
import com.wezom.ulcv2.exception.WebsocketException;
import com.wezom.ulcv2.injection.scope.ApplicationScope;
import com.wezom.ulcv2.jobs.FetchLanguageJob;
import com.wezom.ulcv2.managers.ConnectionManager;
import com.wezom.ulcv2.managers.NotificationManager;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.mvp.model.Avatar;
import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.mvp.model.Conversation;
import com.wezom.ulcv2.mvp.model.Dialog;
import com.wezom.ulcv2.mvp.model.Follows;
import com.wezom.ulcv2.mvp.model.Game;
import com.wezom.ulcv2.mvp.model.Language;
import com.wezom.ulcv2.mvp.model.Message;
import com.wezom.ulcv2.mvp.model.UserData;
import com.wezom.ulcv2.mvp.view.HomeActivityView;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.ProfileChannelHandler;
import com.wezom.ulcv2.net.models.Session;
import com.wezom.ulcv2.net.models.Talk;
import com.wezom.ulcv2.net.models.User;
import com.wezom.ulcv2.net.models.requests.ProfileRequest;
import com.wezom.ulcv2.net.models.requests.ProfileUpdateRequest;
import com.wezom.ulcv2.net.models.responses.CountersResponse;
import com.wezom.ulcv2.net.models.responses.websocket.NotificationResponse;
import com.wezom.ulcv2.net.models.responses.websocket.SearchGamesResponse;
import com.wezom.ulcv2.ui.activity.GameActivity;
import com.wezom.ulcv2.ui.activity.TalkActivity;
import com.wezom.ulcv2.ui.fragment.AvatarFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import ru.terrakok.cicerone.Router;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.wezom.ulcv2.Screens.CONFIRM_RESTORE_PASS_FRAGMENT;
import static com.wezom.ulcv2.common.Constants.GAME_COUNTDOWN_NUMBER;
import static com.wezom.ulcv2.ui.activity.GameActivity.EXTRA_GAME_ID;
import static com.wezom.ulcv2.ui.activity.GameActivity.EXTRA_GAME_TYPE;
import static com.wezom.ulcv2.ui.activity.GameActivity.EXTRA_IS_PLAYER;
import static com.wezom.ulcv2.ui.activity.GameActivity.EXTRA_PLAYER_AVATAR_1;
import static com.wezom.ulcv2.ui.activity.GameActivity.EXTRA_PLAYER_AVATAR_2;
import static com.wezom.ulcv2.ui.activity.GameActivity.EXTRA_PLAYER_ID_1;
import static com.wezom.ulcv2.ui.activity.GameActivity.EXTRA_PLAYER_ID_2;
import static com.wezom.ulcv2.ui.activity.GameActivity.EXTRA_PLAYER_LEVEL_1;
import static com.wezom.ulcv2.ui.activity.GameActivity.EXTRA_PLAYER_LEVEL_2;
import static com.wezom.ulcv2.ui.activity.GameActivity.EXTRA_PLAYER_NAME_1;
import static com.wezom.ulcv2.ui.activity.GameActivity.EXTRA_PLAYER_NAME_2;
import static com.wezom.ulcv2.ui.activity.GameActivity.EXTRA_WATCHERS;
import static com.wezom.ulcv2.ui.activity.GameActivity.VIDEO_URL;
import static com.wezom.ulcv2.ui.activity.HomeActivity.AVATAR_KEY;
import static com.wezom.ulcv2.ui.activity.HomeActivity.DIALOG_ID_KEY;
import static com.wezom.ulcv2.ui.activity.HomeActivity.NAME_KEY;
import static com.wezom.ulcv2.ui.fragment.FollowsFragment.FOLLOWERS_TAB_MODE;

/**
 * Created: Zorin A.
 * Date: 23.05.2016.
 */

@InjectViewState
public class HomeActivityPresenter extends MvpPresenter<HomeActivityView> implements FacebookCallback<LoginResult>,
        VKCallback<VKAccessToken> {

    private static final String TAG = HomeActivityPresenter.class.getSimpleName();
    private static final String INTENT_ACTION_KEY = "key";
    private static final String INTENT_ACTION_NEW_ACCOUNT = "new-account";
    private static final String INTENT_ACTION_EVENT = "event";
    private static final String INTENT_RESTORE_PASS = "restore-password";

    //region injects
    @Inject
    PreferenceManager mPreferenceManager;
    @Inject
    DatabaseManager mDatabaseManager;
    @Inject
    ProfileChannelHandler mProfileChannelHandler;
    @Inject
    NotificationManager mNotificationManager;
    @Inject
    EventBus mBus;
    @Inject
    ApiManager mApiManager;
    @Inject
    JobManager mJobManager;
    @Inject
    @ApplicationScope
    Context mContext;
    @Inject
    Router mRouter;
    @Inject
    LocaleUtils localeUtils;
    //endregion
    //region var

    private CallbackManager mCallbackManager = CallbackManager.Factory.create();
    private int mProfilesStackCount;
    private Session lostGame;
    private Talk lostTalk;
    //endregion


    public HomeActivityPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }


    //region overrides

    @Override
    public void onSuccess(LoginResult loginResult) {

    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException error) {

    }

    @Override
    public void onResult(VKAccessToken res) {

    }

    @Override
    public void onError(VKError error) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKSdk.onActivityResult(requestCode, resultCode, data, this);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    //endregion

    //region subscribes


    public void onHideDrawer(HideDrawerEvent event) {
        getViewState().hideDrawer();
        getViewState().hideKeyboard();
    }

    public void onReconnectSocket(RectonnectChannelEvent event) {
        Log.v("SOCKET_HA", "RECONNECTING");
        getViewState().showToast(R.string.connection_lost_reconnecting);
        mProfileChannelHandler.connect(mPreferenceManager.getAuthToken());
    }


    public void onCreateTalkClickEvent(CreateTalkClickEvent event) {
        Log.v("SOCKET_HA", "onCreateTalkClickEvent");
        mProfileChannelHandler.createTalk(event.getCategoryId(), event.getCategoryName());
    }


    public void onProfileClickedEvent(ProfileClickedEvent event) {
        getViewState().hideKeyboard();
        // mNavigationHandler.switchFragment(ProfileFragment.getNewInstance(event.getUserId()), NavigationHandler.SwitchMethod.REPLACE, true);
        mRouter.navigateTo(Screens.PROFILE_SCREEN, event.getUserId());
    }


    public void onFollowsRequest(FollowsRequestEvent event) {
        Follows follows;

        if (event.isCalledFromDrawer()) {
            getViewState().hideDrawer();
            getViewState().hideKeyboard();

            follows = new Follows(FOLLOWERS_TAB_MODE, 0);
            mRouter.replaceScreen(Screens.FOLLOWS_SCREEN, follows);
        } else {
            follows = new Follows(FOLLOWERS_TAB_MODE, event.getId());
            mRouter.navigateTo(Screens.FOLLOWS_SCREEN, follows);
        }
    }


    public void onBackEvents(BackEvents event) {
        getViewState().hideKeyboard();
        mRouter.exit();
    }


    public void onLogin(LoginEvent event) {
        saveAuthToken(event.getToken());
        getGlobalMaxUserLevelOnServer();
        syncData();

        getViewState().showDrawer(true);
        getViewState().hideTintLayer(true);
        getViewState().hideKeyboard();
        if (!event.getLogin().isEmpty()) { // if login event caused by login button
            getViewState().animateRevealOut();
        }
        getViewState().stopVideoPreview();
        getViewState().allowVideoPreview(false);
        mRouter.newRootScreen(Screens.PROFILE_SCREEN, 0);

        connectSocket();
    }

    public void onRegisterConfirm(ConfirmChangePassEvent event) {
        saveAuthToken(event.getToken());
        getGlobalMaxUserLevelOnServer();
        syncData();

        getViewState().showDrawer(true);
        getViewState().hideTintLayer(true);
        getViewState().hideKeyboard();
        getViewState().stopVideoPreview();
        getViewState().allowVideoPreview(false);
        mRouter.newRootScreen(Screens.PROFILE_SCREEN, 0);

        connectSocket();
    }


    public void onAvatarCrop(AvatarCropEvent event) {
        //   mNavigationHandler.switchBack();
        mRouter.exit();
        String image = Utils.encodeToBase64(event.getImage());
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setAvatar(image);
        mApiManager.updateProfile(request)
                .subscribe(response -> {
                    mBus.post(new ProfileImageUpdateEvent());
                }, throwable -> {

                    getViewState().showMessageDialog(R.string.failed_to_update_avatar);
                });
    }


    public void onBackgroundCrop(BackgroundCropEvent event) {
        mRouter.exit();
        String image = Utils.encodeToBase64(event.getImage());
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setBackground(image);
        mApiManager.updateProfile(request)
                .subscribe(response -> {
                    mBus.post(new ProfileImageUpdateEvent());
                }, throwable -> {
                    getViewState().showMessageDialog(R.string.failed_to_update_background);

                });
    }


    public void onAvatarChange(ChangeAvatarRequestEvent event) {
        switch (event.getType()) {
            case ChangeAvatarRequestEvent.GALLERY:
                getViewState().dispatchPickPictureIntent(true);
                break;
            case ChangeAvatarRequestEvent.PHOTO:
                getViewState().dispatchTakePictureIntent();

                break;
        }
    }


    public void onBackgroundChange(ChangeBackgroundRequestEvent event) {
        getViewState().dispatchPickPictureIntent(false);
    }


    public void onLogoutRequest(LogoutRequestEvent event) {
        getViewState().hideKeyboard();
        mProfileChannelHandler.disconnect();
        mRouter.newRootScreen(Screens.LOGIN_SCREEN);
        getViewState().allowVideoPreview(true);
        getViewState().startVideoPreview();
        getViewState().showDrawer(false);
        //// TODO: 04.04.2017 Remove this shit in release
        if (event.isRestart()) {
            clearToken();
        } else {
            removeUserData();
        }
        getViewState().hideTintLayer(false);
        getLanguages();
        Log.d("Log_ LogOut ", String.valueOf(mPreferenceManager.isDevServ()) + mPreferenceManager.getBaseUrl(mContext.getApplicationContext()).getSocketUrl() + "  " + mPreferenceManager.getBaseUrl(mContext.getApplicationContext()).getRestUrl());
    }


    public void onVideoPreview(VideoPreviewEvent event) {
        switch (event.getVideoPreviewState()) {
            case VideoPreviewEvent.START:
                getViewState().startVideoPreview();
                break;
            case VideoPreviewEvent.STOP:
                getViewState().stopVideoPreview();
                break;
        }
    }

    public void onDrawerOpen(OnDrawerOpenEvent event) {

        if (event.isOpen()) {
            getViewState().openDrawer();
        } else {
            getViewState().hideDrawer();
        }
    }


    public void onChangedLogin(ChangeEvents.ChangedLoginEvent event) {
        getViewState().hideKeyboard();
        syncData();
    }


    public void onLanguagesFetch(LanguagesFetchEvent event) {
        Language.updateLanguages(event.getLanguages());
    }


    public void onUserSelectedLanguages(LanguageSelectedEvent event) {
        getViewState().hideKeyboard();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            mBus.post(new LanguagesSelectedWithDelay(event.getLanguageList()));
        }, 500);
    }


    public void onSocialLogin(SocialLoginRequestEvent event) {
        switch (event.getSocialType()) {
            case 0: // fb
                ArrayList<String> scope = new ArrayList<>();
                scope.add("email");
                scope.add("public_profile");
                LoginManager.getInstance().registerCallback(mCallbackManager, this);

                getViewState().showFbLogin(scope);
                break;
            case 1: // vk
                getViewState().showVkLogin("email");
                break;
        }
    }


    public void onShowGlobalLoading(ShowGlobalLoadingEvent event) {
        getViewState().showLoadingEvent(event.isShow());
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 2)
    public void onNewMessage(NewMessageEvent event) {
        refreshCounters();
        Dialog.updateDialog(event.getMessage().getMessage());
        Message.updateMessage(event.getMessage().getMessage());
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 0)
    public void onNewMessageNotify(NewMessageEvent event) {
        mNotificationManager.newMessageNotification(event.getMessage().getMessage().getMessage(),
                event.getMessage().getMessage().getSender().getId(), event.getMessage().getMessage().getSender().getName(),
                event.getMessage().getMessage().getSender().getAvatar());
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onNewFollowerNotify(NewFollowerEvent event) {
        refreshCounters();
        mNotificationManager.newFollowerNotification(event.getUser().getName(), event.getUser().getId());
    }


    public void onProfileConnectionLost(LostConnectionEvent event) { //socket connection lost
    }


    public void onProfileConnectionRecovered(ConnectionRecoveredEvent event) { //deprecated

    }


    public void onFollowClick(FollowClickEvent event) {
        if (event.isFollow()) {
            try {
                mProfileChannelHandler.follow(event.getId());
            } catch (WebsocketException e) {
                getViewState().showMessageDialog(R.string.failed_to_follow);
            }
        } else {
            try {
                mProfileChannelHandler.unFollow(event.getId());
            } catch (WebsocketException e) {
                getViewState().showMessageDialog(R.string.failed_to_unfollow);
            }
        }
    }


    public void onDialogSelected(DialogSelectedEvent event) {
      /*  mNavigationHandler.switchFragment(ConversationFragment.getNewInstance(event.getName(),
                event.getAvatar(), event.getDialogId()), NavigationHandler.SwitchMethod.REPLACE, true);*/
        mRouter.navigateTo(Screens.CONVERSATION_SCREEN, new Conversation(event.getName(),
                event.getAvatar(), event.getDialogId()));
    }


    public void onRefreshRequestEvent(UpdateDateRequestEvent event) {
        syncData();
    }


    public void on2PlayClick(On2PlayClickEvent event) {
        //  mNavigationHandler.switchFragment(new ToPlayFragment(), NavigationHandler.SwitchMethod.REPLACE, true);
        mRouter.navigateTo(Screens.TO_PLAY_SCREEN);

    }

    public void onSessionInfoEvent(SessionInfoEvent event) {
        mRouter.navigateTo(Screens.SESSION_INFO_SCREEN, event.getEvent());
    }

    private void getEventById(int id) {
        mApiManager.getEventById(id)
                .subscribe(event -> {
                            onSessionInfoEvent(new SessionInfoEvent(event.getEvent()));
                        },
                        throwable -> getViewState().showMessageDialog(throwable.getMessage()));
    }

    private void confirmRegister(String key) {
        mApiManager.confirmRegister(key)
                .subscribe(response -> {
                    getViewState().confirmRegister(response.getToken());
                }, throwable -> {
                    getViewState().showMessageDialog(throwable.getMessage());
                });
    }

    public void isShowDrawer(OnShowDrawerEvent event) {
        getViewState().showDrawer(event.isShow());
    }


    public void on2TalkClick(On2TalkClickEvent event) {
        // mNavigationHandler.switchFragment(new ToTalkFragment(), NavigationHandler.SwitchMethod.REPLACE, true);
        mRouter.navigateTo(Screens.TO_TALK_SCREEN);

    }


    public void onGameSearchResult(SearchGameEvent event) {
        SearchGamesResponse searchResponse = event.getSearchResponse();

        switch (searchResponse.getResult()) {
            case ADDED_TO_QUEUE:
                getViewState().showGameSearch(true);
                break;
            case PLAYER_DISCONNECTED: //disconnected, connect
                Session session = event.getSearchResponse().getSession();
                if (session == null) {
                    return;
                }
                User firstPlayer = session.getPlayers().get(0);
                User secondPlayer = session.getPlayers().get(1);
                getViewState().dispatchStartGameIntent(
                        session.getId(),
                        session.getGameId(),
                        session.getVideoUrl(),
                        session.getViewers(),
                        true,
                        new UserData(firstPlayer.getId(),
                                firstPlayer.getName(),
                                firstPlayer.getLevel(),
                                firstPlayer.getAvatar(),
                                firstPlayer.getLikes()),
                        new UserData(secondPlayer.getId(),
                                secondPlayer.getName(),
                                secondPlayer.getLevel(),
                                secondPlayer.getAvatar(),
                                secondPlayer.getLikes())
                );
                break;
        }
    }


    public void onWatchGameRequest(WatchGameEvent event) {
        Session session = event.getSession();
        watchGame(session);
    }

    private void watchGame(Session session) {
        User firstPlayer = session.getPlayers().get(0);
        User secondPlayer = session.getPlayers().get(1);
        boolean isPlayer = (firstPlayer.getId() == mPreferenceManager.getUserId())
                || (secondPlayer.getId() == mPreferenceManager.getUserId());
        getViewState().dispatchStartGameIntent(
                session.getId(),
                session.getGameId(),
                session.getVideoUrl(),
                session.getViewers(),
                isPlayer,
                new UserData(firstPlayer.getId(),
                        firstPlayer.getName(),
                        firstPlayer.getLevel(),
                        firstPlayer.getAvatar(),
                        firstPlayer.getLikes()),
                new UserData(secondPlayer.getId(),
                        secondPlayer.getName(),
                        secondPlayer.getLevel(),
                        secondPlayer.getAvatar(),
                        secondPlayer.getLikes())
        );
    }


    public void onGameCreated(GameCreatedEvent event) {
        Observable.interval(1, TimeUnit.SECONDS)
                .takeUntil(count -> GAME_COUNTDOWN_NUMBER + 1 - count == 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(count -> {
                    getViewState().setGameSearchCountdown((int) (GAME_COUNTDOWN_NUMBER + 1 - count));
                }, throwable -> {
                    Log.e(TAG, throwable.getMessage());
                }, () -> {
                    getViewState().showGameSearch(false);
                    Session session = event.getSession();
                    User firstPlayer = session.getPlayers().get(0);
                    User secondPlayer = session.getPlayers().get(1);
                    getViewState().dispatchStartGameIntent(
                            session.getId(),
                            session.getGameId(),
                            session.getVideoUrl(),
                            session.getViewers(),
                            true,
                            new UserData(firstPlayer.getId(),
                                    firstPlayer.getName(),
                                    firstPlayer.getLevel(),
                                    firstPlayer.getAvatar(),
                                    firstPlayer.getLikes()),
                            new UserData(secondPlayer.getId(),
                                    secondPlayer.getName(),
                                    secondPlayer.getLevel(),
                                    secondPlayer.getAvatar(),
                                    secondPlayer.getLikes())
                    );
                });
    }


    public void onGameFound(GameFoundEvent event) {
        getViewState().showGameSearch(false);
        Session session = event.getSession();
        User firstPlayer = session.getPlayers().get(0);
        User secondPlayer = session.getPlayers().get(1);
        boolean isPlayer = (firstPlayer.getId() == mPreferenceManager.getUserId())
                || (secondPlayer.getId() == mPreferenceManager.getUserId());

        getViewState().dispatchStartGameIntent(
                session.getId(),
                session.getGameId(),
                session.getVideoUrl(),
                session.getViewers(),
                isPlayer,
                new UserData(firstPlayer.getId(),
                        firstPlayer.getName(),
                        firstPlayer.getLevel(),
                        firstPlayer.getAvatar(),
                        firstPlayer.getLikes()),
                new UserData(secondPlayer.getId(),
                        secondPlayer.getName(),
                        secondPlayer.getLevel(),
                        secondPlayer.getAvatar(),
                        secondPlayer.getLikes())
        );
    }

    public void onNewNotification(NewNotificationEvent event) {
        if (checkYouArePraticipant(event.getResponse())) {
            return;
        }
        getViewState().setNewNotification(event.getResponse());
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1)
    public void onSendInviteToTalkResult(InviteToTalkSentResultEvent event) { //we receive result is our request sent successfully
        int result = event.getInviteToTalk().getResult();
        switch (result) {
            case InviteToTalkSentResultEvent.OK:
                break;
            case InviteToTalkSentResultEvent.WRONG_TALK_STATE:
                getViewState().showToast(R.string.cant_send_invite);
                break;
            case InviteToTalkSentResultEvent.MAX_PARTNERS:
                getViewState().showToast(R.string.max_partners);
                break;
            case InviteToTalkSentResultEvent.USER_NOT_READY:
                getViewState().showToast(R.string.user_not_ready);
                break;
            case InviteToTalkSentResultEvent.DO_NOT_DISTURB:
                int minutes = Math.round(event.getInviteToTalk().getTime() / 60);
                if (minutes > 0)
                    getViewState().showToast(mContext.getString(R.string.do_not_disturb, minutes));
                else
                    getViewState().showToast(mContext.getString(R.string.do_not_disturb_seconds, event.getInviteToTalk().getTime()));
                break;
            case InviteToTalkSentResultEvent.INVITATION_EXISTS:
                getViewState().showToast(R.string.invitation_sent);
                break;
        }
    }


    public void onInviteToTalkResponse(InviteToTalkResponseEvent event) {
        switch (event.getResponse().getResult()) {
            case InviteToTalkResponseEvent.DO_NOT_DISTURB:
                getViewState().showToast(R.string.do_not_disturb);

                break;
            case InviteToTalkResponseEvent.DENY:
                getViewState().showToast(R.string.user_deny_request);
                break;
        }
    }

    public void onReceiveInviteToTalk(InviteToTalkRequestEvent event) {
        getViewState().showAcceptDialog(event.getInviteToTalkRequest());
    }

    public void onCreateTalk(CreateTalkEvent event) {
        if (event.getTalk() == null) {
            getViewState().showMessageDialog(event.getMessage());
        } else {
            createTalk(event.getTalk());
        }
    }

    public void onCreateTalk(ShowTalkEvent event) {
        createTalk(event.getTalk());
    }

//    public void onCreateGame(CreatePlayEvent event) {
//        onPlayIntent(event.getGameId(),event.getGameType(), event.getVideoUrl(), event.getMainUser(), event.getLinkedUser());
//    }

    public void onFindTalkPartnerEvent(FindTalkPartnerEvent event) {
        switch (event.getResponse().getResult()) {
            case FindTalkPartnerEvent.OK:
                //handle find user event
                break;
            case FindTalkPartnerEvent.WRONG_TALK_STATE:
                getViewState().showToast(R.string.cant_send_invite);
                break;
            case FindTalkPartnerEvent.MAX_PARTNERS:
                getViewState().showToast(R.string.max_partners);
                break;
            case FindTalkPartnerEvent.UNKNOWN_ERROR:
                getViewState().showToast(R.string.unknown_error);
                break;
        }
    }

    public void onFollowsShow(FollowersShowEvent event) {
        try {
            mProfileChannelHandler.acknowledgeFollowers(event.getFollowers());
        } catch (WebsocketException e) {
            // empty
        }
    }

    public void onNetworkState(OnNetworkStateEvent event) {
        if (event.isConnected()) {
            connectSocket();
        } else {
            getViewState().showToast(R.string.lost_connection);
        }
    }

    public void onShowMessageDialog(OnShowMessageDialogEvent event) {
        getViewState().showMessageDialog(event.getMessageId());
    }

    public void onFailureConnection(FailureConnectionEvent event) {
        Log.v("SOCKET_HA", "FailureConnectionEvent");
        Log.v("SOCKET_HA", "mProfileChannelHandler.disconnect()");
        mProfileChannelHandler.disconnect();
        Observable.timer(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    Log.v("SOCKET_HA", "connectSocket()");
                    connectSocket();
                }, throwable -> {
                    Log.v("SOCKET_HA", "connectSocket() exception: " + throwable.getMessage());
                });
    }

    public void onError(ErrorMessageEvent event) {
        getViewState().showToast(event.getMessageResId());
    }
    //endregion

    public void connectSocket() {
        String token = mPreferenceManager.getAuthToken();
        if (!TextUtils.isEmpty(token)) {
            if (!mProfileChannelHandler.isConnected()) {
                mProfileChannelHandler.connect(token);
            }
        }
    }

    private boolean checkYouArePraticipant(NotificationResponse response) {
        Game game = response.getGame();
        if (game != null) {
            int profileId = mPreferenceManager.getUserId();
            int firstPaticipant = game.getUsers().get(0).getId();
            int secondPaticipant = game.getUsers().get(1).getId();
            return profileId == firstPaticipant || profileId == secondPaticipant;
        }
        return false;
    }

    public void switchToInitialScreen(Bundle extras) {

        getViewState().showDrawer(false);
        if (!isLoggedIn()) {
            //   mNavigationHandler.switchFragment(new LoginFragment(), NavigationHandler.SwitchMethod.REPLACE, true);
            mRouter.replaceScreen(Screens.LOGIN_SCREEN);
        } else {
            if (extras != null) {
                if (extras.containsKey(DIALOG_ID_KEY)) {
                    onDialogSelected(new DialogSelectedEvent(extras.getInt(DIALOG_ID_KEY),
                            extras.getString(NAME_KEY), extras.getString(AVATAR_KEY)));
                }
            } else {
                //mNavigationHandler.switchFragment(ProfileFragment.getNewInstance(0), NavigationHandler.SwitchMethod.REPLACE, false);
                mRouter.replaceScreen(Screens.PROFILE_SCREEN, 0);

            }
            onLogin(new LoginEvent("", "", mPreferenceManager.getAuthToken()));
        }
    }

    public void changeAvatar(Uri uri) {
       /* mNavigationHandler.switchFragment(AvatarFragment.getNewInstance(new Avatar(uri, AvatarFragment.CROP_MODE_AVATAR)),
                NavigationHandler.SwitchMethod.REPLACE, true);*/
        mRouter.navigateTo(Screens.AVATAR_SCREEN, new Avatar(uri, AvatarFragment.CROP_MODE_AVATAR));
    }

    public void changeBackground(Uri uri) {
        /*mNavigationHandler.switchFragment(AvatarFragment.getNewInstance(new Avatar(uri, AvatarFragment.CROP_MODE_BACKGROUND)),
                NavigationHandler.SwitchMethod.REPLACE, true);
*/
        mRouter.navigateTo(Screens.AVATAR_SCREEN, new Avatar(uri, AvatarFragment.CROP_MODE_BACKGROUND));
    }

    public void onBackPressed() {
        getViewState().hideKeyboard();
        // mNavigationHandler.handleBackButtonPress();
        mRouter.exit();
    }

    public void getLanguages() {
        mJobManager.addJobInBackground(new FetchLanguageJob());
    }

    public void actionIntentEvent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {

            Uri url = Uri.parse(intent.getDataString());
            final List<String> segments = intent.getData().getPathSegments();

            if (segments.size() > 1 && TextUtils.equals(segments.get(0), INTENT_ACTION_EVENT)) {
                getEventById(Integer.parseInt(segments.get(1)));
            } else if (segments.size() > 1 && TextUtils.equals(segments.get(1), INTENT_ACTION_NEW_ACCOUNT)) {
                confirmRegister(url.getQueryParameter(INTENT_ACTION_KEY));
            } else if (segments.size() > 1 && TextUtils.equals(segments.get(1), INTENT_RESTORE_PASS)) {
                mRouter.navigateTo(CONFIRM_RESTORE_PASS_FRAGMENT, url.getQueryParameter(INTENT_ACTION_KEY));
            }
        }
    }

    public void getGlobalMaxUserLevelOnServer() {
        mApiManager.getMaxLevel().subscribe(levelResponse -> {
            int newMaxLevel = levelResponse.getResponse().getMaxLevel();
            int maxLevel = mPreferenceManager.getMaxUserLevel();
            if (newMaxLevel > maxLevel) {
                mPreferenceManager.setMaxUserLevel(newMaxLevel);
            }
        }, throwable -> {
            //don't do anything. Level will be get from cache.

        });
    }

    public void createTalk(Talk talk) {
        Intent intent = new Intent(mContext, TalkActivity.class);
        intent.putExtra(TalkActivity.EXTRA_SESSION_ID, talk.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void watchTalk(int talkId) {
        Intent intent = new Intent(mContext, TalkActivity.class);
        intent.putExtra(TalkActivity.EXTRA_SESSION_ID, talkId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void onPlayIntent(int sessionId, int gameType, String videoUrl, int watchers, boolean isParticipant, UserData firstUser, UserData secondUser) {
        Intent intent = new Intent(mContext, GameActivity.class);
        intent.putExtra(EXTRA_GAME_TYPE, gameType);
        intent.putExtra(EXTRA_GAME_ID, sessionId);
        intent.putExtra(EXTRA_IS_PLAYER, isParticipant);
        intent.putExtra(EXTRA_WATCHERS, watchers);
        intent.putExtra(VIDEO_URL, videoUrl);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (firstUser != null) {
            intent.putExtra(EXTRA_PLAYER_ID_1, firstUser.getId());
            intent.putExtra(EXTRA_PLAYER_NAME_1, firstUser.getName());
            intent.putExtra(EXTRA_PLAYER_LEVEL_1, firstUser.getLevel());
            intent.putExtra(EXTRA_PLAYER_AVATAR_1, firstUser.getAvatar());
        }

        if (secondUser != null) {
            intent.putExtra(EXTRA_PLAYER_ID_2, secondUser.getId());
            intent.putExtra(EXTRA_PLAYER_NAME_2, secondUser.getName());
            intent.putExtra(EXTRA_PLAYER_LEVEL_2, secondUser.getLevel());
            intent.putExtra(EXTRA_PLAYER_AVATAR_2, secondUser.getAvatar());
        }
        mContext.startActivity(intent);
    }

    public void onPlayIntent(int sessionId, int gameType, String videoUrl, User firstUser, User secondUser) {
        Intent intent = new Intent(mContext, GameActivity.class);
        intent.putExtra(EXTRA_GAME_ID, sessionId);
        intent.putExtra(EXTRA_GAME_TYPE, gameType);
        intent.putExtra(EXTRA_IS_PLAYER, false);
        intent.putExtra(VIDEO_URL, videoUrl);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (firstUser != null) {
            intent.putExtra(EXTRA_PLAYER_ID_1, firstUser.getId());
            intent.putExtra(EXTRA_PLAYER_NAME_1, firstUser.getName());
            intent.putExtra(EXTRA_PLAYER_LEVEL_1, firstUser.getLevel());
            intent.putExtra(EXTRA_PLAYER_AVATAR_1, firstUser.getAvatar());
        }

        if (secondUser != null) {
            intent.putExtra(EXTRA_PLAYER_ID_2, secondUser.getId());
            intent.putExtra(EXTRA_PLAYER_NAME_2, secondUser.getName());
            intent.putExtra(EXTRA_PLAYER_LEVEL_2, secondUser.getLevel());
            intent.putExtra(EXTRA_PLAYER_AVATAR_2, secondUser.getAvatar());
        }
        mContext.startActivity(intent);
    }

    public void disconnectSocket() {
        Log.v("HAP", "disconnectSocket()");
        if (mProfileChannelHandler.isConnected()) {
            mProfileChannelHandler.disconnect();
        }
    }

    public void onCancelSearch() {
        mProfileChannelHandler.cancelSearch();
        getViewState().showGameSearch(false);
    }

    private void saveMaxUserLevel(int maxLevel) {
        mPreferenceManager.setMaxUserLevel(maxLevel);
    }


    private void syncData() {
        Log.d("Log_ syncData", "sync");
        mApiManager.getProfile(new ProfileRequest(0))
                .subscribe(profileResponse -> {
                    Log.d("Log_ syncData", profileResponse.getProfile().getName());
                    mBus.post(new ProfileUpdatedEvent(profileResponse.getProfile()));
                    mPreferenceManager.setUserAvatar(profileResponse.getProfile().getAvatar());

                }, throwable -> {
                    int errorMessageResId = R.string.error_check_your_net_and_retry;
                    if (throwable instanceof ApiException) {
                        switch (((ApiException) throwable).getCode()) {
                            case 10:
                                errorMessageResId = R.string.error_user_not_found;
                                break;
                            case 11:
                                errorMessageResId = R.string.error_account_disabled;
                                break;
                            case 12:
                                errorMessageResId = R.string.error_account_private;
                                break;
                            case 13:
                                errorMessageResId = R.string.error_blacklisted;
                                break;
                        }
                    }
                    Log.d("Log_ throwable", mContext.getString(errorMessageResId));
                });

        mApiManager.getCounters()
                .subscribe(countersResponse -> {
                    CountersResponse.Response response = countersResponse.getResponse();
                    mBus.post(new CountersUpdateEvent(response.getFollowers(), response.getMessages(),
                            response.getWarnings()));
                }, throwable -> {
                    Log.d("Log_ error sync", throwable.toString());
                });

        getCategoriesFromBackend();
    }

    private void getCategoriesFromBackend() {
        mApiManager.getCategories()
                .subscribe(categoryResponse -> {
                    if (categoryResponse.getResponse() == null || categoryResponse.getResponse().size() < 1) {
                        getCategoriesFromBackend();
                    } else {
                        mDatabaseManager.clearCategories();
                        Category.updateCategories(categoryResponse.getResponse());
                    }

                }, throwable -> {
                    //don't do anything
                });
    }

    private void refreshCounters() {
        mApiManager.getCounters()
                .subscribe(countersResponse -> {
                    CountersResponse.Response response = countersResponse.getResponse();
                    mBus.post(new CountersUpdateEvent(response.getFollowers(), response.getMessages(),
                            response.getWarnings()));
                }, throwable -> {
                    //don't do anything
                });
    }

    private void removeUserData() {
        mDatabaseManager.clearDatabase();
        mPreferenceManager.clearData();
    }

    //// TODO: 04.04.2017 Remove this shit in release
    private void clearToken() {
        mPreferenceManager.clearToken();
        mDatabaseManager.clearDatabase();
        getViewState().restart();
    }

    private void saveAuthToken(String token) {
        mPreferenceManager.setAuthToken(token);
    }

    public boolean isLoggedIn() {
        return !mPreferenceManager.getAuthToken().isEmpty();
    }

    public void sendInviteResponse(int sender, int result, int delay) {
        mProfileChannelHandler.sendInvite2TalkResponse(sender, result, delay);
    }

    public void showPermissionDeniedMessage() {
        getViewState().showMessageDialog(R.string.to_enable_permission);
    }

    public void openNotificationDialog(int dialogId, String name, String avatar) {
        mBus.post(new DialogSelectedEvent(dialogId, name, avatar));
    }

    public void onNewFollowerNotificationClick(int userId) {
        mBus.post(new ProfileClickedEvent(userId));
    }

    public void checkConnection() {
        if (!ConnectionManager.isWiFiEnabled(mContext)) {
            getViewState().showToast(R.string.lost_connection);
        }
    }

    public void initApplicationLocale() {
        localeUtils.init();
    }

    public void checkIskNeedReconnectSession() {
        lostGame = null;
        lostTalk = null;
        mApiManager.getActiveSession(0)
                .subscribe(activeSessionResponse -> {
                    lostGame = activeSessionResponse.getGame();
                    lostTalk = activeSessionResponse.getTalk();

                    if (lostGame != null || lostTalk != null) {
                        getViewState().showReconnectDialog();
                    }

                }, throwable -> {
                    // error network
                });
    }

    public void performReconnect() {
        if (lostGame != null) {
            watchGame(lostGame);
        }
        if (lostTalk != null) {
            createTalk(lostTalk);
        }
    }
}
