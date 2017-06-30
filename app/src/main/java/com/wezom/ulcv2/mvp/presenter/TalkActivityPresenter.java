package com.wezom.ulcv2.mvp.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.ULCApplication;
import com.wezom.ulcv2.common.Utils;
import com.wezom.ulcv2.events.AddUserToTalkEvent;
import com.wezom.ulcv2.events.CancelFindTalkPartnerEvent;
import com.wezom.ulcv2.events.DonateMessageEvent;
import com.wezom.ulcv2.events.FailureConnectionEvent;
import com.wezom.ulcv2.events.FindTalkPartnerClickEvent;
import com.wezom.ulcv2.events.FindTalkPartnerEvent;
import com.wezom.ulcv2.events.FollowClickEvent;
import com.wezom.ulcv2.events.FollowEvent;
import com.wezom.ulcv2.events.InviteToTalkRequestEvent;
import com.wezom.ulcv2.events.InviteToTalkResponseEvent;
import com.wezom.ulcv2.events.InviteToTalkSentResultEvent;
import com.wezom.ulcv2.events.NewFollowerEvent;
import com.wezom.ulcv2.events.OnCancelUserSearchClickEvent;
import com.wezom.ulcv2.events.OnCloseSearchPanelEvent;
import com.wezom.ulcv2.events.OnDetachTalkClickEvent;
import com.wezom.ulcv2.events.OnErrorEvent;
import com.wezom.ulcv2.events.OnFindTalkResponseEvent;
import com.wezom.ulcv2.events.OnLocalLikeClickEvent;
import com.wezom.ulcv2.events.OnNetworkLikeClickEvent;
import com.wezom.ulcv2.events.OnShowToastEvent;
import com.wezom.ulcv2.events.OnVideoClickEvent;
import com.wezom.ulcv2.events.OnVideoStreamDisconnectedEvent;
import com.wezom.ulcv2.events.ReconnectTalkChannelEvent;
import com.wezom.ulcv2.events.RectonnectChannelEvent;
import com.wezom.ulcv2.events.ReportUserClickEvent;
import com.wezom.ulcv2.events.SendDonateMessageEvent;
import com.wezom.ulcv2.events.SendTalkMessageEvent;
import com.wezom.ulcv2.events.StreamClosedByReportsEvent;
import com.wezom.ulcv2.events.SwitchTalkResponseEvent;
import com.wezom.ulcv2.events.TalkAddedEvent;
import com.wezom.ulcv2.events.TalkClosedEvent;
import com.wezom.ulcv2.events.TalkLikesLocalEvent;
import com.wezom.ulcv2.events.TalkLikesResponseEvent;
import com.wezom.ulcv2.events.TalkRemovedEvent;
import com.wezom.ulcv2.events.TalkSpectatorConnectedEvent;
import com.wezom.ulcv2.events.TalkSpectatorDisconnectedEvent;
import com.wezom.ulcv2.events.TalkStateEvent;
import com.wezom.ulcv2.events.UnFollowEvent;
import com.wezom.ulcv2.events.UpdateTalkDateEvent;
import com.wezom.ulcv2.events.game.NewMessageEvent;
import com.wezom.ulcv2.events.onSwitchTalkClickEvent;
import com.wezom.ulcv2.interfaces.ChatConnector;
import com.wezom.ulcv2.interfaces.TalkUserSearchFragmentConnector;
import com.wezom.ulcv2.interfaces.VideoFragmentConnector;
import com.wezom.ulcv2.managers.PreferenceManager;
import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.mvp.view.TalkActivityView;
import com.wezom.ulcv2.net.ApiManager;
import com.wezom.ulcv2.net.ProfileChannelHandlerInTalk;
import com.wezom.ulcv2.net.TalkChannelHandler;
import com.wezom.ulcv2.net.models.Talk;
import com.wezom.ulcv2.net.models.User;
import com.wezom.ulcv2.net.models.responses.websocket.ErrorResponse;
import com.wezom.ulcv2.net.models.responses.websocket.SwitchTalkResponse;
import com.wezom.ulcv2.ui.activity.BaseActivity;
import com.wezom.ulcv2.ui.activity.HomeActivity;
import com.wezom.ulcv2.ui.activity.TalkActivity;
import com.wezom.ulcv2.ui.fragment.ChatFragment;
import com.wezom.ulcv2.ui.fragment.TalkUserSearchFragment;
import com.wezom.ulcv2.ui.fragment.VideoFragment;
import com.wezom.ulcv2.ui.fragment.VideoFragmentBuilder;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import lombok.experimental.Accessors;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 23.07.2016
 * Time: 20:41
 */
@Accessors(prefix = "m")
@InjectViewState
public class TalkActivityPresenter extends BasePresenter<TalkActivityView> {


    public static final int SINGLE_LEFT_WINDOW_MODE = 1;
    public static final int SINGLE_RIGHT_WINDOW_MODE = 2;
    public static final int MULTI_WINDOW_MODE = 3;

    private static final String TAG = TalkActivityPresenter.class.getName();

    @Inject
    ApiManager mApiManager;
    @Inject
    PreferenceManager mPreferenceManager;
    @Inject
    Context mContext;

    @Inject
    TalkChannelHandler talkChannelHandler;
    @Inject
    ProfileChannelHandlerInTalk profileChannelHandlerInTalk;

    private Talk primaryTalk;
    private Talk secondaryTalk;
    private ChatConnector mChatConnector;
    private VideoFragmentConnector mRightVideoFragment;
    private VideoFragmentConnector mLeftVideoFragment;
    private TalkUserSearchFragmentConnector mSearchFragmentConnector;
    private boolean mIsInStreamerMode;

    public TalkActivityPresenter() {
        ULCApplication.mApplicationComponent.inject(this);
    }

    public void onReconnectSocket(ReconnectTalkChannelEvent event) {
        Log.v("SOCKET", "onReconnectSocket");
        if (!talkChannelHandler.isConnected()) {
            int talkId = 0;
            if (primaryTalk != null && primaryTalk.isChosen()) {
                talkId = primaryTalk.getId();
            }
            if (secondaryTalk != null && secondaryTalk.isChosen()) {
                talkId = secondaryTalk.getId();
            }
            if (talkId > 0) {
                connectTalk(primaryTalk.getId());
                getViewState().showToast(R.string.connection_lost_reconnecting);
            }
        }
    }

    public void onFailureConnection(FailureConnectionEvent event) {
        Log.v(TAG, "onFailureConnection, exitTalks");
        exitTalks();
    }

    public void onAddUserToTalkClick(AddUserToTalkEvent event) {
        talkChannelHandler.inviteToTalk(event.getUser().getId(), primaryTalk.getCategory());
    }

    public void onRandomUserSearchClick(FindTalkPartnerClickEvent event) {
        talkChannelHandler.findTalkPartner();
        mSearchFragmentConnector.onRandomSearchStateActive(true);
    }

    public void onFindTalkPartner(FindTalkPartnerEvent event) {
        switch (event.getResponse().getResult()) {
            case FindTalkPartnerEvent.OK:
                getViewState().showToast(R.string.successfully_added_to_search_queue);
                mSearchFragmentConnector.onRandomSearchStateActive(true);
                break;
            case FindTalkPartnerEvent.WRONG_TALK_STATE:
                getViewState().showToast(R.string.you_already_in_queue);
                break;
            case FindTalkPartnerEvent.MAX_PARTNERS:
                getViewState().showToast(R.string.max_partners);
                break;
            case FindTalkPartnerEvent.UNKNOWN_ERROR:
                getViewState().showToast(R.string.unknown_error);
                break;
        }
    }

    public void onCancelUserSearch(OnCancelUserSearchClickEvent event) {
        talkChannelHandler.cancelFindTalkPartner();
        mSearchFragmentConnector.onRandomSearchStateActive(false);
    }

    public void onCancelUserSearchResponse(CancelFindTalkPartnerEvent event) {
        switch (event.getCancelFindTalkPartner().getResult()) {
            case CancelFindTalkPartnerEvent.OK:
                getViewState().showToast(R.string.user_search_canceled);
                break;
            case CancelFindTalkPartnerEvent.NOT_IN_QUEUE:
                getViewState().showToast(R.string.not_in_queue);
                break;
        }
    }

    public void onCloseSearchPanel(OnCloseSearchPanelEvent event) {
        getViewState().closeSearchPanel();
    }

    public void onSendInviteToTalkResult(InviteToTalkSentResultEvent event) { //we receive result is our request sent successfully
        int result = event.getInviteToTalk().getResult();
        switch (result) {
            case InviteToTalkSentResultEvent.OK:
                getViewState().showToast(R.string.invite_to_talk_sent);
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
                int minutes = Math.round(event.getResponse().getTimeDoNotDisturb() / 60);
                if (minutes > 0)
                    getViewState().showToast(mContext.getString(R.string.do_not_disturb, minutes));
                else
                    getViewState().showToast(mContext.getString(R.string.do_not_disturb_seconds, event.getResponse().getTimeDoNotDisturb()));
                break;
            case InviteToTalkResponseEvent.DENY:
                getViewState().showToast(R.string.user_deny_request);
                break;
        }
    }

    public void onReceiveInviteToTalk(InviteToTalkRequestEvent event) {
        getViewState().showAcceptDialog(event.getInviteToTalkRequest());
    }

    public void onDonateMessage(SendDonateMessageEvent event) {
        int result = event.getResponse().getResult();
        if (result == SendDonateMessageEvent.RESPONSE_OK) {
            getViewState().showToast(R.string.donate_message_added_successfully);
        }
    }

    public void onShowDonateMessage(DonateMessageEvent event) {
        String message = event.getResponse().getMessage().getText();
        if (!TextUtils.isEmpty(message)) {
            getViewState().setDonateMessage(message);
        }
    }

    public void onTalkState(TalkStateEvent event) {
        Log.v("TAP", "onTalkState()");
        Talk tmp = event.getTalk();
        initTalk(tmp);
        prepareGlobalUi(primaryTalk, secondaryTalk);
    }

    public void onTalkClosed(TalkClosedEvent event) {
        Talk closedTalk = event.getResponse().getTalk();

        if (primaryTalk != null && closedTalk.getId() == primaryTalk.getId() && primaryTalk.isChosen()) {
            getViewState().showTalkClosedMessage(R.string.session_info, R.string.streamer_has_left);

        }
        if (secondaryTalk != null && closedTalk.getId() == secondaryTalk.getId() && secondaryTalk.isChosen()) {
            getViewState().showTalkClosedMessage(R.string.session_info, R.string.streamer_has_left);
        }

        if (primaryTalk != null && closedTalk.getId() == primaryTalk.getId() && !primaryTalk.isChosen()) {
            closePrimaryTalk();
        }
        if (secondaryTalk != null && closedTalk.getId() == secondaryTalk.getId() && !secondaryTalk.isChosen()) {
            closeSecondaryTalk();
        }
    }

    public void onTalkAdded(TalkAddedEvent event) {
        Talk addedTalk = event.getResponse().getTalk();
        int newStreamerId = addedTalk.getStreamer().getId();
        if (newStreamerId == mPreferenceManager.getUserId()) { //if You are the invited user, now you are streamer
            getViewState().setLoadingViewVisible(true);
            closePrimaryTalk();
            leaveChannel();

            Log.v(TAG, "onTalkAdded closePrimaryTalk()");
            Observable.timer(1, TimeUnit.SECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        Log.v(TAG, "onTalkAdded subscribe");
                        connectTalk(addedTalk.getId());
                        initTalk(addedTalk);
                        Log.v(TAG, "onTalkAdded primaryTalk = " + primaryTalk.getId() + ", secondaryTalk = " + secondaryTalk.getId());
                        getViewState().setLoadingViewVisible(false);
                        Log.v(TAG, "onTalkAdded hideLoadingVIew");
                        mSearchFragmentConnector.onRandomSearchStateActive(false);
                        prepareGlobalUi(primaryTalk, secondaryTalk);
                    });


        } else {//if invited user is someone else

            if (primaryTalk == null && secondaryTalk != null) {
                primaryTalk = addedTalk;
            }
            if (secondaryTalk == null && primaryTalk != null) {
                secondaryTalk = addedTalk;
            }
            mSearchFragmentConnector.onRandomSearchStateActive(false);
            prepareGlobalUi(primaryTalk, secondaryTalk);
        }

    }

    public void onSpectatorConnected(TalkSpectatorConnectedEvent event) {
        if (primaryTalk != null && event.getResponse().getTalkId() == primaryTalk.getId()) {
            int all = primaryTalk.getSpectators();
            int newVal = all + 1;
            primaryTalk.setSpectators(newVal);
            if (primaryTalk.isChosen()) {
                mChatConnector.setSpectators(primaryTalk.getSpectators());
            }
        } else {
            if (secondaryTalk != null) {
                int all = secondaryTalk.getSpectators();
                int newVal = all + 1;
                secondaryTalk.setSpectators(newVal);
                if (secondaryTalk.isChosen()) {
                    mChatConnector.setSpectators(secondaryTalk.getSpectators());
                }
            }
        }
        Log.v(TAG, "onSpectatorConnected " + primaryTalk.getSpectators());
    }

    public void onSpectatorDisconnected(TalkSpectatorDisconnectedEvent event) {
        if (primaryTalk != null && event.getResponse().getTalkId() == primaryTalk.getId()) {
            primaryTalk.setSpectators(primaryTalk.getSpectators() - 1);
            if (primaryTalk.isChosen()) {
                mChatConnector.setSpectators(primaryTalk.getSpectators());
            }

        } else {
            if (secondaryTalk != null) {
                secondaryTalk.setSpectators(secondaryTalk.getSpectators() - 1);
                if (secondaryTalk.isChosen()) {
                    mChatConnector.setSpectators(secondaryTalk.getSpectators());
                }
            }
        }
    }

    public void onError(OnErrorEvent event) {
        ErrorResponse response = event.getResponse();
        Log.v("SOCKET_ON_ERROR", "error: " + response.getError() + "message: " + response.getMessage());
        switch (response.getError()) {
            case 32://Error: no such talk
                getViewState().showTalkClosedMessage(R.string.session_info, R.string.streamer_closed_session);
                break;
            case 21: //duplicate socket connection
                Log.v("SOCKET_ON_ERROR", "showTalkClosedMessage()");
                leaveChannel();
                talkChannelHandler.destroySocket();
                getViewState().showTalkClosedMessage(R.string.session_info, R.string.lost_game_connection);
                break;
        }
    }

    public void onUpdateTalk(UpdateTalkDateEvent event) {
        String talkName = event.getResponse().getNewTalkName();
        if (event.getResponse().getTalkId() == primaryTalk.getId()) {
            getViewState().updateTalkTitle(talkName);
        }
    }

    public void onChatMessageResponse(NewMessageEvent event) {
        mChatConnector.sendNewMessage(event.getNewMessage());
    }

    public void onChatMessageSent(SendTalkMessageEvent event) {
        talkChannelHandler.sendMessage(event.getMessage());
    }

    public void onVideoClick(OnVideoClickEvent event) {
        getViewState().switchUiVisibility();
    }

    public void onShowToast(OnShowToastEvent event) {
        getViewState().showToast(event.getMessageRes());
    }

    public void onDetachTalkClick(OnDetachTalkClickEvent event) {
        talkChannelHandler.detachTalk();
    }

    public void onTalkRemovedResponse(TalkRemovedEvent event) {
        if (mIsInStreamerMode) {
            if (event.getResponse().getTalk().getId() == secondaryTalk.getId()) {
                closeSecondaryTalk();
            }
        } else {
            if (event.getResponse().getTalk().getId() == primaryTalk.getId()) {
                closePrimaryTalk();
            } else {
                closeSecondaryTalk();
            }
        }
    }

    public void onSwitchTalkResponse(SwitchTalkResponseEvent event) {
        SwitchTalkResponse response = event.getResponse();
        int myId = mPreferenceManager.getUserId();
        int userId = event.getResponse().getUserId();
        int toSession = response.getToSession();

        if (myId == userId) { //its event only for me
            if (toSession == primaryTalk.getId()) { // I switched on left window
                primaryTalk.setChosen(true);
                secondaryTalk.setChosen(false);

                mRightVideoFragment.setChecked(false);
                mRightVideoFragment.refreshDataAndMenu();

                mLeftVideoFragment.setChecked(true);
                mLeftVideoFragment.refreshDataAndMenu();

                primaryTalk.setSpectators(primaryTalk.getSpectators() + 1);
                secondaryTalk.setSpectators(secondaryTalk.getSpectators() - 1);

                initData(primaryTalk);
                getViewState().showToast(String.format(mContext.getString(R.string.talk_switched_to), primaryTalk.getStreamer().getName()));

            } else { //i switched to right window
                secondaryTalk.setChosen(true);
                primaryTalk.setChosen(false);

                mLeftVideoFragment.setChecked(false);
                mLeftVideoFragment.refreshDataAndMenu();

                mRightVideoFragment.setChecked(true);
                mRightVideoFragment.refreshDataAndMenu();

                secondaryTalk.setSpectators(secondaryTalk.getSpectators() + 1);
                primaryTalk.setSpectators(primaryTalk.getSpectators() - 1);

                initData(secondaryTalk);
                getViewState().showToast(String.format(mContext.getString(R.string.talk_switched_to), secondaryTalk.getStreamer().getName()));

            }
        } else { // for everyone
            if (toSession == primaryTalk.getId()) {
                primaryTalk.setSpectators(primaryTalk.getSpectators() + 1);
                secondaryTalk.setSpectators(secondaryTalk.getSpectators() - 1);
                mChatConnector.setSpectators(primaryTalk.getSpectators());
            } else {
                secondaryTalk.setSpectators(secondaryTalk.getSpectators() + 1);
                primaryTalk.setSpectators(primaryTalk.getSpectators() - 1);
                mChatConnector.setSpectators(secondaryTalk.getSpectators());
            }
        }
    }

    public void onSwitchTalkClick(onSwitchTalkClickEvent event) {
        talkChannelHandler.switchTalk(event.getTalk().getId());
    }

    public void onFindTalkResponse(OnFindTalkResponseEvent event) { //next talk
        Talk talk = event.getResponse().getTalk();
        if (talk == null) {
            getViewState().showToast(R.string.no_more_talks);
            unblockUiByTimer(0);
            return;
        } else {
            unblockUiByTimer(3000); //time to ui setCachedData
            getViewState().setDonateMessage(null);

            leaveChannel();
            mChatConnector.clearChat();

            FragmentManager fm = ((BaseActivity) mContext).getSupportFragmentManager();
            FragmentTransaction t = fm.beginTransaction();
            if (mLeftVideoFragment != null) {
                t.remove((Fragment) mLeftVideoFragment);
            }
            if (mRightVideoFragment != null) {
                t.remove((Fragment) mRightVideoFragment);
            }
            t.commit();

            mLeftVideoFragment = null;
            mRightVideoFragment = null;

            Observable.timer(800, TimeUnit.MILLISECONDS) //delay to socket properly disconnect
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        connectTalk(talk.getId());
                    });
        }
    }

    public void onFollowClick(FollowClickEvent event) {
        talkChannelHandler.follow(event.getId());
    }

    public void onLikeClick(OnLocalLikeClickEvent event) {
        if (Utils.isUserLoggedIn(mContext)) {
            Log.v("SOCKET like send", "Talk: " + event.getTalkId() + " likes:" + event.getCount());
            int talkId = event.getTalkId();
            int likeCount = event.getCount();
            talkChannelHandler.sendLocalLike(talkId, likeCount);
        } else {
            getViewState().showSignInDialog(R.string.not_autorized_title, R.string.not_autorized_message);
        }
    }

    public void onNetworkLikeClick(OnNetworkLikeClickEvent event) {
        if (Utils.isUserLoggedIn(mContext)) {
            Log.v("SOCKET like send", "Talk: " + event.getTalkId() + " likes:" + event.getCount());
            int talkId = event.getTalkId();
            int likeCount = event.getCount();
            talkChannelHandler.sendNetworkLike(talkId, likeCount);
        }
    }

    public void onLikeLocalResponse(TalkLikesLocalEvent event) {
        int talkId = event.getResponse().getTalkId();
        int likes = event.getResponse().getLikes();
        Log.v("SOCKET like receive", "local like receive Talk: " + talkId + " likes:" + likes);
        addLikes(talkId, likes);

    }

    public void onLikesResponse(TalkLikesResponseEvent event) {
        int talkId = event.getResponse().getTalkId();
        int likes = event.getResponse().getLikes();
        Log.v("SOCKET like receive", "Talk: " + talkId + " likes:" + likes);
        addLikes(talkId, likes);
    }

    private void addLikes(int talkId, int likes) {
        if (primaryTalk != null && talkId == primaryTalk.getId()) {
            int newLikes = primaryTalk.getLikes() + likes;
            primaryTalk.setLikes(newLikes);
            if (mLeftVideoFragment != null) {
                mLeftVideoFragment.setLikes(primaryTalk.getLikes());
            }
        }
        if (secondaryTalk != null && talkId == secondaryTalk.getId()) {
            int newLikes = secondaryTalk.getLikes() + likes;
            secondaryTalk.setLikes(newLikes);
            if (mRightVideoFragment != null) {
                mRightVideoFragment.setLikes(secondaryTalk.getLikes());
            }
        }
    }

    public void onFollowResponse(FollowEvent event) {
        getViewState().showToast(String.format(mContext.getString(R.string.you_are_following), event.getUser().getName()));
    }

    public void onUnfolowResponce(UnFollowEvent event) {
        getViewState().showToast(R.string.you_are_unfollowing);
    }

    public void onNewFollowerResponse(NewFollowerEvent event) {
        getViewState().showToast(R.string.you_have_new_follower);
    }

    public void onReportUserClick(ReportUserClickEvent event) {
        getViewState().showReportDialog(R.string.report, event.getTalkId());
    }

    public void onNotifyYouAreDisconnected(StreamClosedByReportsEvent event) {
        getViewState().showTalkClosedMessage(R.string.report, R.string.you_where_disconnected);
    }

    public void connectTalk(int sessionId) {
        talkChannelHandler.connect(mPreferenceManager.getAuthToken(), sessionId);
        profileChannelHandlerInTalk.connect(mPreferenceManager.getAuthToken());
        Log.v("T_PRESENTER", "connectTalk");
    }

    public void onVideoStreamDisconnected(OnVideoStreamDisconnectedEvent event) {
        getViewState().showConnectionLostMessage(R.string.session_info, R.string.connection_status_fail);
    }

    public void onRectonnectChannel(RectonnectChannelEvent event) {
        Log.v("SOCKET_TA", "RECONNECTING");
        getViewState().showToast(R.string.connection_lost_reconnecting);
        talkChannelHandler.connect(mPreferenceManager.getAuthToken());
    }

    public void initContext(Context context) {
        this.mContext = context;
    }

    public void leaveChannel() {
        if (mIsInStreamerMode) {
            if (talkChannelHandler.isConnected()) {
                Log.v("SOCKET_TAP", "talkChannelHandler.leave();");
                Log.v("SOCKET_TAP", "talkChannelHandler.disconnect();");
                talkChannelHandler.leave();
                talkChannelHandler.disconnect();
            }
        } else {
            if (talkChannelHandler.isConnected()) {
                Log.v("SOCKET_TAP", "talkChannelHandler.disconnect()");
                talkChannelHandler.disconnect();
            }
        }
        profileChannelHandlerInTalk.disconnect();
    }

    public void showCloseSessionDialog() {
        getViewState().showCloseSessionDialog(R.string.leave_title, R.string.leave_talk_message);
    }

    public void onNextTalkClick() {
        if (Utils.isUserLoggedIn(mContext)) {
            if (primaryTalk != null && primaryTalk.isChosen()) {
                talkChannelHandler.findTalk(-1, primaryTalk.getId());
            }
            if (secondaryTalk != null && secondaryTalk.isChosen()) {
                talkChannelHandler.findTalk(-1, secondaryTalk.getId());
            }
            getViewState().setUiBlocked(true);
            getViewState().showNextButtonLoading(true);
        } else {
            getViewState().showSignInDialog(R.string.not_autorized_title, R.string.not_autorized_message);
        }
    }

    private void unblockUiByTimer(int time) {
        Observable.timer(time, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    getViewState().setUiBlocked(false);
                    getViewState().showNextButtonLoading(false);
                });
    }

    public void showRenameSessionDialog() {
        if (Utils.isUserLoggedIn(mContext)) {
            getViewState().showRenameTalkDialog();
        } else {
            getViewState().showSignInDialog(R.string.not_autorized_title, R.string.not_autorized_message);
        }
    }

    public void setTalkNewTitle(String newTitle) {
        if (Utils.isUserLoggedIn(mContext)) {
            talkChannelHandler.updateTalkData(newTitle);
            getViewState().setTalkTitle(newTitle);
        } else {
            getViewState().showSignInDialog(R.string.not_autorized_title, R.string.not_autorized_message);
        }
        talkChannelHandler.updateTalkData(newTitle);
        getViewState().setTalkTitle(newTitle);
    }

    public void showDonateMessageDialog() {
        if (Utils.isUserLoggedIn(mContext)) {
            getViewState().showDonateMessageDialog();
        } else {
            getViewState().showSignInDialog(R.string.not_autorized_title, R.string.not_autorized_message);
        }
    }

    public void sendDonateMessage(String message) {
        if (Utils.isUserLoggedIn(mContext)) {
            talkChannelHandler.sendDonateMessage(message);
        } else {
            getViewState().showSignInDialog(R.string.not_autorized_title, R.string.not_autorized_message);
        }
    }

    public void setSearchPanel() {
        FragmentManager fm = ((BaseActivity) mContext).getSupportFragmentManager();
        mSearchFragmentConnector = (TalkUserSearchFragment) fm.findFragmentByTag(TalkUserSearchFragment.TAG);
        if (mSearchFragmentConnector == null) {
            mSearchFragmentConnector = new TalkUserSearchFragment();
        }
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.talk_activity_sliding_layer, (TalkUserSearchFragment) mSearchFragmentConnector, TalkUserSearchFragment.TAG);
        transaction.commit();
        getViewState().onSearchFragmentSet(mSearchFragmentConnector);
    }

    public void setChat() {
        FragmentManager fm = ((BaseActivity) mContext).getSupportFragmentManager();
        mChatConnector = (ChatFragment) fm.findFragmentByTag(ChatFragment.TAG);
        if (mChatConnector == null) {
            mChatConnector = new ChatFragment();
        }
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.talk_activity_chat_placeholder, (ChatFragment) mChatConnector, ChatFragment.TAG);
        transaction.commit();
        getViewState().onChatSet((ChatFragment) mChatConnector);
    }

    public void sendInviteResponse(int userId, int responseResult, int delay) {
        if (Utils.isUserLoggedIn(mContext)) {
            profileChannelHandlerInTalk.sendInvite2TalkResponse(userId, responseResult, delay);
        } else {
            getViewState().showSignInDialog(R.string.not_autorized_title, R.string.not_autorized_message);
        }
    }

    public void showFullDonateMessage() {
        getViewState().onShowFullDonateMessage();
    }

    private Talk getSecondaryTalk(Talk talk) {
        if (talk.getLinked() != null && talk.getLinked().size() > 0) {
            return talk.getLinked().get(0);
        }
        return null;
    }

    private Category getCategoryName(int category) {
        return Category.getCategoryById(category);

    }

    private boolean heyIsItMeWhoStreamer(User streamer) {
        int talkUserId = streamer.getId();
        int myId = mPreferenceManager.getUserId();
        boolean isStreamer = talkUserId == myId;
        return isStreamer;
    }

    public void exitTalks() {
        Log.v("SOCKET_TA", "exitTalks()");
        if (mLeftVideoFragment != null) {
            mLeftVideoFragment.stopContentExecution();
        }
        if (mRightVideoFragment != null) {
            mRightVideoFragment.stopContentExecution();
        }

        getViewState().closeSession();

    }

    public void reportUser(int talkId, int reportCategory) {
        talkChannelHandler.reportUser(talkId, reportCategory);
    }

    private void initTalk(Talk tmp) {
        if (primaryTalk != tmp) {
            primaryTalk = tmp;
            primaryTalk.setChosen(true);
        }
        tmp = getSecondaryTalk(tmp);
        if (secondaryTalk != tmp) {
            secondaryTalk = tmp;
        }
    }

    private void prepareGlobalUi(Talk talk, Talk guestTalk) {
        Log.v(TAG, "prepareGlobalUi");
        int windowMode = guestTalk == null ? SINGLE_LEFT_WINDOW_MODE : MULTI_WINDOW_MODE;

        mIsInStreamerMode = heyIsItMeWhoStreamer(talk.getStreamer());

        getViewState().setWindowMode(windowMode);
        getViewState().modifyLayout(windowMode, mIsInStreamerMode); //prepare UI
        getViewState().prepareUiVisibility(windowMode, mIsInStreamerMode);

        initVideoFragments(windowMode, mIsInStreamerMode);
        Log.v(TAG, "prepareGlobalUi " + talk.getSpectators());
        initData(talk);
    }

    private void initVideoFragments(int windowMode, boolean isInStreamerMode) {
        Log.v(TAG, "initVideoFragments");
        FragmentManager fm = ((BaseActivity) mContext).getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        if (mLeftVideoFragment == null) { //only first time
            mLeftVideoFragment = new VideoFragmentBuilder(VideoFragment.VideoMode.LEFT).build();
            mLeftVideoFragment.setData(primaryTalk);
            transaction.replace(R.id.talk_activity_main_talk_holder, (Fragment) mLeftVideoFragment, VideoFragment.TAG);
        }

        if (windowMode == MULTI_WINDOW_MODE) { //add second user
            if (mRightVideoFragment == null) {
                mRightVideoFragment = new VideoFragmentBuilder(VideoFragment.VideoMode.RIGHT).build();
                mRightVideoFragment.setData(secondaryTalk);
                mRightVideoFragment.setUiVisibilityState(TalkActivity.isControlsHidden());
                transaction.replace(R.id.talk_activity_guest_talk_holder, (Fragment) mRightVideoFragment, VideoFragment.TAG);
            }
        }

        if (windowMode == SINGLE_LEFT_WINDOW_MODE && isInStreamerMode) {
            mLeftVideoFragment.setIsStreamerMode(true);
            mLeftVideoFragment.setChecked(false);
            mLeftVideoFragment.hideMenu();
        }
        if (windowMode == SINGLE_LEFT_WINDOW_MODE && !isInStreamerMode) {
            mLeftVideoFragment.setChecked(false);
            mLeftVideoFragment.showSingleWindowMenu();
        }

        if (windowMode == MULTI_WINDOW_MODE && isInStreamerMode) {
            mLeftVideoFragment.setChecked(false);
            mLeftVideoFragment.hideMenu();
            mLeftVideoFragment.setIsStreamerMode(true);
            mRightVideoFragment.setChecked(false);
            mRightVideoFragment.showStreamerMenu();
        }
        if (windowMode == MULTI_WINDOW_MODE && !isInStreamerMode) {
            mLeftVideoFragment.showMultiWindowMenu();
            mRightVideoFragment.showMultiWindowMenu();

            if (primaryTalk.isChosen()) {
                mLeftVideoFragment.setChecked(true);
                mRightVideoFragment.setChecked(false);
            }
            if (secondaryTalk.isChosen()) {
                mLeftVideoFragment.setChecked(false);
                mRightVideoFragment.setChecked(true);
            }
        }
        transaction.commit();

        mLeftVideoFragment.refreshDataAndMenu();
        mLeftVideoFragment.setIsStreamerMode(isInStreamerMode);
        if (mRightVideoFragment != null) {
            mRightVideoFragment.refreshDataAndMenu();
        }
    }

    private void initData(Talk talk) {
        Category category = getCategoryName(talk.getCategory());
        getViewState().setTalkTitle(TextUtils.isEmpty(talk.getName()) ? category.getName() : talk.getName());
        mChatConnector.setSpectators(talk.getSpectators());
        Log.v(TAG, "initData " + talk.getSpectators());
        getViewState().setTalkIcon(Utils.getCorrectCategoryIconSizeURL(mContext, true, true) + category.getIcon());
    }

    private void closeSecondaryTalk() {
        FragmentManager fm = ((BaseActivity) mContext).getSupportFragmentManager();

        FragmentTransaction t = fm.beginTransaction();
        t.remove((Fragment) mRightVideoFragment);
        t.commit();
        mRightVideoFragment = null;

        if (secondaryTalk != null) {
            secondaryTalk = null;
        }
        getViewState().modifyLayout(SINGLE_LEFT_WINDOW_MODE, mIsInStreamerMode);
    }

    private void closePrimaryTalk() {
        FragmentManager fm = ((BaseActivity) mContext).getSupportFragmentManager();
        FragmentTransaction t = fm.beginTransaction();
        t.remove((Fragment) mLeftVideoFragment);
        t.commit();
        mLeftVideoFragment = null;
        if (primaryTalk != null) {
            primaryTalk = null;
        }
        getViewState().modifyLayout(SINGLE_RIGHT_WINDOW_MODE, mIsInStreamerMode);
    }

    public void switchFragmentsUiVisibility(boolean isHideUi) {
        if (mLeftVideoFragment != null) {
            mLeftVideoFragment.hideUi(isHideUi);
        }

        if (mRightVideoFragment != null) {
            mRightVideoFragment.hideUi(isHideUi);
        }
    }

    public void switchToMainScreen() {
        Intent intent = new Intent(mContext, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
    }

    public void showLoginScreen() {
        Intent intent = new Intent(mContext, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(HomeActivity.ACTION_SHOW_LOGIN);
        mContext.startActivity(intent);
    }

    public void disconnectSockets() {
        talkChannelHandler.disconnect();
        profileChannelHandlerInTalk.disconnect();
        getViewState().killView();
    }

    public void onStreamerDisconnected(int talkId, int userId) {
        if (mLeftVideoFragment != null && talkId == mLeftVideoFragment.getCurrentTalk().getId()) {
            mLeftVideoFragment.pause();
        } else {
            if (mRightVideoFragment != null) {
                mRightVideoFragment.pause();
            }
        }
    }

    public void onStreamerReconnected(int talkId, int userId) {
        if (mLeftVideoFragment != null && talkId == mLeftVideoFragment.getCurrentTalk().getId()) {
            mLeftVideoFragment.resume();
        } else {
            if (mRightVideoFragment != null) {
                mRightVideoFragment.resume();
            }
        }
    }
}
