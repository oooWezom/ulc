package com.wezom.ulcv2.ui.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.squareup.picasso.Picasso;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.common.ScreenOrientation;
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
import com.wezom.ulcv2.events.TalkStreamerDisconnectedEvent;
import com.wezom.ulcv2.events.TalkStreamerReconnectedEvent;
import com.wezom.ulcv2.events.UnFollowEvent;
import com.wezom.ulcv2.events.UpdateTalkDateEvent;
import com.wezom.ulcv2.events.game.NewMessageEvent;
import com.wezom.ulcv2.events.onSwitchTalkClickEvent;
import com.wezom.ulcv2.interfaces.TalkUserSearchFragmentConnector;
import com.wezom.ulcv2.interfaces.ToolbarActions;
import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.mvp.presenter.TalkActivityPresenter;
import com.wezom.ulcv2.mvp.view.TalkActivityView;
import com.wezom.ulcv2.net.models.requests.websocket.InviteToTalkRequest;
import com.wezom.ulcv2.ui.dialog.InformationDialog;
import com.wezom.ulcv2.ui.dialog.InformationDialogBuilder;
import com.wezom.ulcv2.ui.dialog.InviteDialog;
import com.wezom.ulcv2.ui.dialog.InviteDialogBuilder;
import com.wezom.ulcv2.ui.fragment.ChatFragment;
import com.wezom.ulcv2.ui.view.ChatView;
import com.wunderlist.slidinglayer.SlidingLayer;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.experimental.Accessors;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.wezom.ulcv2.common.Constants.DialogResult.OK;
import static com.wezom.ulcv2.ui.dialog.InformationDialog.DialogType.OK_BUTTON_MODE;

/**
 * Created with IntelliJ IDEA.
 * User: zorin.a
 * Date: 15.09.2016
 * Time: 10:30
 */

public class TalkActivity extends BaseActivity
        implements TalkActivityView, ToolbarActions {
    private static final String TAG = TalkActivity.class.getSimpleName();

    //region extras
    public static final String EXTRA_SESSION_ID = "session_id";
    //endregion

    //region var
    @Getter
    @Accessors(prefix = "m")
    public static boolean mIsControlsHidden;
    boolean mIsUserParticipant;
    int mWindowMode;
    //endregion

    //region views
    ChatView mChatView;
    TextView mTitleTextView;
    TextView mDonateMessageTextView;

    View mAddTalkButton;
    View mDonateMessageButton;
    View mEditSessionNameIcon;
    View mNextLoadingView;

    ViewGroup mLeaveLayer;
    ViewGroup mNextLayer;
    ViewGroup mVideoHolder;
    ViewGroup mControlsLayout;
    ViewGroup mChatLayout;
    ViewGroup mTitleLayout;
    ViewGroup mControlsHolder;
    ViewGroup mMainTalkPlaceHolder;
    ViewGroup mGuestTalkPlaceHolder;
    ViewGroup mNextLabelLayout;
    ViewGroup mLoadingView;

    ImageView mSessionIcon;
    SlidingLayer mSlidingLayer;
    //endregion

    @InjectPresenter
    TalkActivityPresenter mPresenter;

    //region overrides
    @Override
    public void injectDependencies() {
        getActivityComponent().inject(this);
    }

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
        Log.v("TA", "onCreate()");
        handleViewsInitialization();
        createTalk(getIntent().getExtras());
        mPresenter.initContext(this);
        mPresenter.setSearchPanel();
        mPresenter.setChat();
        setScreenOrientation(ScreenOrientation.UNSPECIFIED);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterBus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBus();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_talk;
    }

    @Override
    public void onBackPressed() {
        if (mSlidingLayer.isOpened()) { //if search slider opened
            closeSearchPanel();
        } else {
            mPresenter.showCloseSessionDialog();
        }
    }

    @Override
    public void updateParticipantsData() {

    }

    @Override
    public void showCloseSessionDialog(int title, int message) {
        if (!mIsUserParticipant) {
            leaveChannel();
            return;
        }

        InformationDialog dialog = new InformationDialogBuilder(InformationDialog.DialogType.TWO_BUTTON_MODE)
                .dialogTitle(getString(title))
                .dialogMessage(getString(message))
                .build();
        dialog.setOnDialogResult((dialogResult, result) -> {
            if (dialogResult == Constants.DialogResult.OK) {
                leaveChannel();
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void showReportDialog(int title, int talkId) {
        InformationDialog dialog = new InformationDialogBuilder(InformationDialog.DialogType.REPORT_MODE)
                .dialogTitle(getString(title))
                .viewRes(R.layout.view_report)
                .build();
        dialog.setOnDialogResult((dialogResult, result) -> {
            if (dialogResult == Constants.DialogResult.OK) {
                int reportCategory = (int) result[0];

                mPresenter.reportUser(talkId, reportCategory);

            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void closeSession() {
        mPresenter.leaveChannel();
        finish();
    }

    @Override
    public void showRenameTalkDialog() {
        InformationDialog dialog = new InformationDialogBuilder(InformationDialog.DialogType.INPUT_MODE)
                .dialogTitleRes(R.string.session_name)
                .dialogMessageRes(R.string.session_name_set)
                .hint(R.string.type_new_name)
                .build();
        dialog.setOnDialogResult((dialogResult, result) -> {
            if (dialogResult == OK) {
                String title = (String) result[0];
                if (!TextUtils.isEmpty(title)) {
                    mPresenter.setTalkNewTitle(title);
                }
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void setTalkTitle(String newTitle) {
        mTitleTextView.setText(newTitle);
    }

    @Override
    public void showSignInDialog(int title, int message) {
        InformationDialog dialog = new InformationDialogBuilder(InformationDialog.DialogType.TWO_BUTTON_MODE)
                .dialogTitle(getString(title))
                .dialogMessage(getString(message))
                .build();
        dialog.setOnDialogResult((dialogResult, result) -> {
            if (dialogResult == Constants.DialogResult.OK) {
                mPresenter.leaveChannel();
                dialog.dismiss();
                mPresenter.showLoginScreen();
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void showDonateMessageDialog() {
        InformationDialog dialog = new InformationDialogBuilder(InformationDialog.DialogType.INPUT_MODE)
                .dialogTitle(getString(R.string.ask))
                .build();
        dialog.setOnDialogResult((dialogResult, result) -> {
            if (dialogResult == Constants.DialogResult.OK) {
                String message = result[0].toString();
                if (!TextUtils.isEmpty(message)) {
                    mPresenter.sendDonateMessage(message);
                }
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void setDonateMessage(String message) {
        if (Utils.checkAndroidIsKitKatOrLater()) {
            TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.talk_activity_messages_holder));
            mDonateMessageTextView.setText(message);

        }
    }

    @Override
    public void prepareUiVisibility(int windowMode, boolean isStreamer) {
        mIsUserParticipant = isStreamer;
        Log.v(TAG, "prepareUiVisibility, isStreamer = " + isStreamer + " windowMode = " + windowMode);

        mLeaveLayer.setVisibility(View.VISIBLE);
        mTitleLayout.setVisibility(mIsControlsHidden ? View.GONE : View.VISIBLE);

        if (mIsUserParticipant) { //participant can rename session, invite to talk
            mTitleLayout.setOnClickListener(v -> mPresenter.showRenameSessionDialog());
            mNextLayer.setVisibility(View.GONE);
            mDonateMessageButton.setVisibility(View.GONE);
            mEditSessionNameIcon.setVisibility(View.VISIBLE);
            mNextLayer.setOnClickListener(null);
            mDonateMessageButton.setOnClickListener(null);

            if (windowMode == TalkActivityPresenter.MULTI_WINDOW_MODE) { //watcher cant add talks, or if two streamers
                mAddTalkButton.setVisibility(View.GONE);
            } else {
                mAddTalkButton.setVisibility(View.VISIBLE);
            }
        } else { //watchers can send donate messages
            mNextLayer.setOnClickListener(v -> mPresenter.onNextTalkClick());
            mDonateMessageButton.setOnClickListener(v -> mPresenter.showDonateMessageDialog());
            mAddTalkButton.setVisibility(View.GONE);
            mNextLayer.setVisibility(View.VISIBLE);
            mDonateMessageButton.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onSearchFragmentSet(TalkUserSearchFragmentConnector fragment) {
        //callback when search fragment sets in right panel
    }

    @Override
    public void showAcceptDialog(InviteToTalkRequest inviteToTalkRequest) {
        int expireTime = inviteToTalkRequest.getTimeToExpire();
        int categoryId = inviteToTalkRequest.getCategory();
        int userId = inviteToTalkRequest.getUser().getId();
        String userName = inviteToTalkRequest.getUser().getName() + " ";
        String avatar = inviteToTalkRequest.getUser().getAvatar();

        String titlePart1 = getString(R.string.invite_to_talk_part_1);
        String titlePart2 = " " + getString(R.string.invite_to_talk_part_2);
        Category category = Category.getCategoryById(categoryId);

        InviteDialog mInviteDialog = new InviteDialogBuilder(Constants.InviteDialogType.DELAY_MODE)
                .dialogMessage(titlePart1)
                .dialogMessage2(titlePart2)
                .userName(userName)
                .dialogUserUrl(avatar)
                .dialogCategoryUrl(Utils.getCorrectCategoryIconSizeURL(this, true, false) + category.getIcon())
                .dialogTime(expireTime)
                .build();
        mInviteDialog.setOnDialogResult((dialogResult, result) -> {
            int responseResult = -1;
            int delay = -1;
            if (dialogResult == Constants.DialogResult.OK) {
                responseResult = InviteToTalkResponseEvent.ACCEPT;//accept
            }
            if (dialogResult == Constants.DialogResult.CANCEL) {
                responseResult = InviteToTalkResponseEvent.DENY;// deny
            }
            if (dialogResult == Constants.DialogResult.DELAY) {
                responseResult = InviteToTalkResponseEvent.DO_NOT_DISTURB; //do not disturb
                delay = (int) result[0];
            }
            if (dialogResult == Constants.DialogResult.TIME_IS_OVER) {

            }
            mPresenter.sendInviteResponse(userId, responseResult, delay);
        });
        mInviteDialog.show(getSupportFragmentManager(), "");
    }

    @Override
    public void setTalkIcon(String categoryIconUrl) {
        Picasso.with(this)
                .load(categoryIconUrl)
                .into(mSessionIcon);
    }

    @Override
    public void onShowFullDonateMessage() {
        showMessageDialog(mDonateMessageTextView.getText().toString());
    }

    @Override
    public void exitTalkActivity() {
        finish();
    }

    @Override
    public void addLikesToLeftTalk(int likes) {
//TODO:implement add likes
    }

    @Override
    public void addLikesToRightTalk(int likes) {
//TODO:implement add likes
    }

    @Override
    public void showTalkClosedMessage(int title, int message) {
        InformationDialog dialog = new InformationDialogBuilder(OK_BUTTON_MODE)
                .dialogTitle(getString(title))
                .dialogMessage(getString(message)).build();
        dialog.setCancelable(false);
        dialog.setOnDialogResult((dialogResult, result) -> {
            if (dialogResult == OK) {
                closeSession();
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void modifyLayout(int mode, boolean isStreamer) {
        Log.v(TAG, "modifyLayout isStreamer = " + isStreamer);
        if (Utils.checkAndroidIsKitKatOrLater()) {
            TransitionManager.beginDelayedTransition(mVideoHolder); //animate video appear
        }

        if (mSlidingLayer.isOpened()) {
            closeSearchPanel();
        }

        if (mode == TalkActivityPresenter.SINGLE_LEFT_WINDOW_MODE) {
            Log.v(TAG, "SINGLE_LEFT_WINDOW_MODE");
            if (mGuestTalkPlaceHolder.getVisibility() == View.VISIBLE) {
                mGuestTalkPlaceHolder.setVisibility(View.GONE);
            }

            if (mMainTalkPlaceHolder.getVisibility() == View.GONE) {
                mMainTalkPlaceHolder.setVisibility(View.VISIBLE);
            }
        }

        if (mode == TalkActivityPresenter.SINGLE_RIGHT_WINDOW_MODE) {
            Log.v(TAG, "SINGLE_RIGHT_WINDOW_MODE");
            if (mMainTalkPlaceHolder.getVisibility() == View.VISIBLE) {
                mMainTalkPlaceHolder.setVisibility(View.GONE);
            }

            if (mGuestTalkPlaceHolder.getVisibility() == View.GONE) {
                mGuestTalkPlaceHolder.setVisibility(View.VISIBLE);
            }
        }
        if (mode == TalkActivityPresenter.MULTI_WINDOW_MODE) {
            Log.v(TAG, "MULTI_WINDOW_MODE");
            if (mGuestTalkPlaceHolder.getVisibility() == View.GONE) {
                mGuestTalkPlaceHolder.setVisibility(View.VISIBLE);
            }

            if (mMainTalkPlaceHolder.getVisibility() == View.GONE) {
                mMainTalkPlaceHolder.setVisibility(View.VISIBLE);
            }
        }
        mAddTalkButton.setVisibility(isStreamer ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateTalkTitle(String title) {
        if (Utils.checkAndroidIsKitKatOrLater()) {
            TransitionManager.beginDelayedTransition(mTitleLayout);
        }
        mTitleTextView.setText(title);
    }

    @Override
    public void onChatSet(ChatFragment fragment) {
        // just callback of chat fragment
    }

    @Override
    public void switchUiVisibility() {

        if (Utils.checkAndroidIsKitKatOrLater()) {
            TransitionManager.beginDelayedTransition(mControlsHolder);
        }
        mControlsLayout.setVisibility(mIsControlsHidden ? View.VISIBLE : View.GONE);
        mTitleLayout.setVisibility(mIsControlsHidden ? View.VISIBLE : View.GONE);

        if (!mIsUserParticipant) { //show only when user watcher
            mDonateMessageButton.setVisibility(mIsControlsHidden ? View.VISIBLE : View.GONE);
        } else { //if participant

            if (mWindowMode == TalkActivityPresenter.SINGLE_LEFT_WINDOW_MODE) {
                mAddTalkButton.setVisibility(mIsControlsHidden ? View.VISIBLE : View.GONE);
            }
        }
        mIsControlsHidden = !mIsControlsHidden;
        mPresenter.switchFragmentsUiVisibility(mIsControlsHidden);
    }

    @Override
    public void setWindowMode(int windowMode) {
        mWindowMode = windowMode;
    }

    @Override
    public void setUiBlocked(boolean isBlock) {
        mNextLayer.setClickable(!isBlock);
    }

    @Override
    public void closeSearchPanel() {
        mSlidingLayer.closeLayer(true);//close search layer
    }

    @Override
    public void showNextButtonLoading(boolean isVisible) {
        mNextLoadingView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mNextLabelLayout.setVisibility(isVisible ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void showConnectionLostMessage(int title, int message) {
        InformationDialog dialog = new InformationDialogBuilder(OK_BUTTON_MODE)
                .dialogTitle(getString(title))
                .dialogMessage(getString(message)).build();
        dialog.setCancelable(true);
        dialog.setOnDialogResult((dialogResult, result) -> {
            if (dialogResult == OK) {
                mPresenter.switchToMainScreen();
            }
        });
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void killView() {
        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            handleOnConfigurationViewsVisibility(true);
        } else {
            handleOnConfigurationViewsVisibility(false);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        mPresenter.disconnectSockets();
        super.onStop();
    }

    @Override
    public void setLoadingViewVisible(boolean isVisible) {
        mLoadingView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
    //endregion

    //region methods
    private void createTalk(Bundle extras) {
        if (extras == null) {
            return;
        }
        int sessionId = extras.getInt(EXTRA_SESSION_ID);
        mPresenter.connectTalk(sessionId);
    }

    private void leaveChannel() {
        mPresenter.leaveChannel();
        Observable.timer(500, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value -> finish());
    }

    private void handleViewsInitialization() {
        //setCachedData views
        mChatView = (ChatView) findViewById(R.id.talk_activity_chat_view);
        mAddTalkButton = findViewById(R.id.talk_activity_add_talk_button);
        mSlidingLayer = (SlidingLayer) findViewById(R.id.talk_activity_sliding_layer);
        mLeaveLayer = (ViewGroup) findViewById(R.id.talk_activity_leave_layer);
        mNextLayer = (ViewGroup) findViewById(R.id.talk_activity_next_layer);
        mControlsLayout = (ViewGroup) findViewById(R.id.talk_activity_controls_layout);
        mChatLayout = (ViewGroup) findViewById(R.id.talk_activity_chat_layout);
        mTitleLayout = (ViewGroup) findViewById(R.id.talk_activity_edit_title_layer);
        mDonateMessageButton = findViewById(R.id.activity_talk_donate_message_layer);
        mDonateMessageTextView = (TextView) findViewById(R.id.talk_activity_donate_message_label);
        mTitleTextView = (TextView) findViewById(R.id.talk_activity_talk_title);
        mSessionIcon = (ImageView) findViewById(R.id.talk_activity_session_category_icon);
        mEditSessionNameIcon = findViewById(R.id.talk_activity_edit_session_name_icon);
        mControlsHolder = (ViewGroup) findViewById(R.id.talk_activity_controls_holder);
        mVideoHolder = (ViewGroup) findViewById(R.id.talk_activity_video_holder);
        mMainTalkPlaceHolder = (ViewGroup) findViewById(R.id.talk_activity_main_talk_holder);
        mGuestTalkPlaceHolder = (ViewGroup) findViewById(R.id.talk_activity_guest_talk_holder);
        mNextLoadingView = findViewById(R.id.talk_activity_next_button_loading);
        mNextLabelLayout = (ViewGroup) findViewById(R.id.talk_activity_next_button_layout);
        mLoadingView = (ViewGroup) findViewById(R.id.talk_activity_loading_view);

        mSlidingLayer.setClickable(true);
        mSlidingLayer.setSlidingEnabled(false);

        //listeners
        mAddTalkButton.setOnClickListener(v -> mSlidingLayer.openLayer(true));
        mLeaveLayer.setOnClickListener(v -> mPresenter.showCloseSessionDialog());
        mDonateMessageTextView.setOnClickListener(v -> mPresenter.showFullDonateMessage());
    }

    private void handleOnConfigurationViewsVisibility(boolean isLandscape) {
        if (isLandscape) {
            mChatLayout.setVisibility(View.GONE);
            Utils.hideKeyboard(this);
        } else {
            mChatLayout.setVisibility(View.VISIBLE);
        }
    }

    public void registerBus() {
        if (!bus.isRegistered(this)) {
            Log.v("TA", "registerBus()");
            bus.register(this);
        }
    }

    public void unregisterBus() {
        if (bus.isRegistered(this)) {
            Log.v("TA", "unregisterBus()");
            bus.unregister(this);
        }
    }
    //endregion

    //region Subscribes
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReconnectSocket(ReconnectTalkChannelEvent event) {
        mPresenter.onReconnectSocket(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFailureConnection(FailureConnectionEvent event) {
        mPresenter.onFailureConnection(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddUserToTalkClick(AddUserToTalkEvent event) {
        mPresenter.onAddUserToTalkClick(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRandomUserSearchClick(FindTalkPartnerClickEvent event) {
        mPresenter.onRandomUserSearchClick(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFindTalkPartner(FindTalkPartnerEvent event) {
        mPresenter.onFindTalkPartner(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCancelUserSearch(OnCancelUserSearchClickEvent event) {
        mPresenter.onCancelUserSearch(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCancelUserSearchResponse(CancelFindTalkPartnerEvent event) {
        mPresenter.onCancelUserSearchResponse(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseSearchPanel(OnCloseSearchPanelEvent event) {
        mPresenter.onCloseSearchPanel(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendInviteToTalkResult(InviteToTalkSentResultEvent event) { //we receive result is our request sent successfully
        mPresenter.onSendInviteToTalkResult(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInviteToTalkResponse(InviteToTalkResponseEvent event) {
        mPresenter.onInviteToTalkResponse(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveInviteToTalk(InviteToTalkRequestEvent event) {
        mPresenter.onReceiveInviteToTalk(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDonateMessage(SendDonateMessageEvent event) {
        mPresenter.onDonateMessage(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowDonateMessage(DonateMessageEvent event) {
        mPresenter.onShowDonateMessage(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTalkState(TalkStateEvent event) {
        mPresenter.onTalkState(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTalkClosed(TalkClosedEvent event) {
        mPresenter.onTalkClosed(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTalkAdded(TalkAddedEvent event) {
        mPresenter.onTalkAdded(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpectatorConnected(TalkSpectatorConnectedEvent event) {
        mPresenter.onSpectatorConnected(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 1)
    public void onSpectatorDisconnected(TalkSpectatorDisconnectedEvent event) {
        mPresenter.onSpectatorDisconnected(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onError(OnErrorEvent event) {
        mPresenter.onError(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateTalk(UpdateTalkDateEvent event) {
        mPresenter.onUpdateTalk(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatMessageResponse(NewMessageEvent event) {
        mPresenter.onChatMessageResponse(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatMessageSent(SendTalkMessageEvent event) {
        mPresenter.onChatMessageSent(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoClick(OnVideoClickEvent event) {
        mPresenter.onVideoClick(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowToast(OnShowToastEvent event) {
        mPresenter.onShowToast(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDetachTalkClick(OnDetachTalkClickEvent event) {
        mPresenter.onDetachTalkClick(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTalkRemovedResponse(TalkRemovedEvent event) {
        mPresenter.onTalkRemovedResponse(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSwitchTalkResponse(SwitchTalkResponseEvent event) {
        mPresenter.onSwitchTalkResponse(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSwitchTalkClick(onSwitchTalkClickEvent event) {
        mPresenter.onSwitchTalkClick(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFindTalkResponse(OnFindTalkResponseEvent event) { //next talk
        mPresenter.onFindTalkResponse(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowClick(FollowClickEvent event) {
        mPresenter.onFollowClick(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLikeClick(OnLocalLikeClickEvent event) {
        mPresenter.onLikeClick(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkLikeClick(OnNetworkLikeClickEvent event) {
        mPresenter.onNetworkLikeClick(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLikeLocalResponse(TalkLikesLocalEvent event) {
        mPresenter.onLikeLocalResponse(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLikesResponse(TalkLikesResponseEvent event) {
        mPresenter.onLikesResponse(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowResponse(FollowEvent event) {
        mPresenter.onFollowResponse(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnfolowResponce(UnFollowEvent event) {
        mPresenter.onUnfolowResponce(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewFollowerResponse(NewFollowerEvent event) {
        mPresenter.onNewFollowerResponse(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReportUserClick(ReportUserClickEvent event) {
        mPresenter.onReportUserClick(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotifyYouAreDisconnected(StreamClosedByReportsEvent event) {
        mPresenter.onNotifyYouAreDisconnected(event);
    }

    @Subscribe
    public void connectTalk(int sessionId) {
        mPresenter.connectTalk(sessionId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoStreamDisconnected(OnVideoStreamDisconnectedEvent event) {
        mPresenter.onVideoStreamDisconnected(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRectonnectChannel(RectonnectChannelEvent event) {
        mPresenter.onRectonnectChannel(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStreamerDisconnected(TalkStreamerDisconnectedEvent event) {
        mPresenter.onStreamerDisconnected(event.getResponse().getTalkId(), event.getResponse().getUserId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStreamerReconnected(TalkStreamerReconnectedEvent event) {
        mPresenter.onStreamerReconnected(event.getResponse().getTalk(), event.getResponse().getUser());
    }

    //endregion
}