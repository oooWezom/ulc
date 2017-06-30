package com.wezom.ulcv2.ui.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.percent.PercentLayoutHelper;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.common.CustomUnityPlayer;
import com.wezom.ulcv2.common.FixedAspectRatioFrameLayout;
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
import com.wezom.ulcv2.interfaces.PlayVideoFragmentConnector;
import com.wezom.ulcv2.mvp.model.NewMessage;
import com.wezom.ulcv2.mvp.model.UserData;
import com.wezom.ulcv2.mvp.presenter.GameActivityPresenter;
import com.wezom.ulcv2.mvp.presenter.GameActivityPresenter.SessionState;
import com.wezom.ulcv2.mvp.view.GameActivityView;
import com.wezom.ulcv2.net.models.responses.websocket.ChatMessageResponse;
import com.wezom.ulcv2.ui.adapter.SessionAdapter;
import com.wezom.ulcv2.ui.adapter.decorator.ItemListDividerDecorator;
import com.wezom.ulcv2.ui.dialog.InformationDialog;
import com.wezom.ulcv2.ui.dialog.InformationDialogBuilder;
import com.wezom.ulcv2.ui.dialog.VoteRateDialog;
import com.wezom.ulcv2.ui.dialog.VoteRateDialogBuilder;
import com.wezom.ulcv2.ui.fragment.PlayVideoFragment;
import com.wezom.ulcv2.ui.fragment.PlayVideoFragmentBuilder;
import com.wezom.ulcv2.ui.fragment.VideoFragment;
import com.wezom.ulcv2.ui.view.ChatView;
import com.wezom.ulcv2.ui.views.ExecutionView;
import com.wezom.ulcv2.ui.views.GameMessageBoard;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.wezom.ulcv2.mvp.presenter.GameActivityPresenter.SessionState.STATE_CALCULATIONS;
import static com.wezom.ulcv2.mvp.presenter.GameActivityPresenter.SessionState.STATE_EXECUTION;
import static com.wezom.ulcv2.mvp.presenter.GameActivityPresenter.SessionState.STATE_GAME;
import static com.wezom.ulcv2.mvp.presenter.GameActivityPresenter.SessionState.STATE_RATING;
import static com.wezom.ulcv2.mvp.presenter.GameActivityPresenter.SessionState.STATE_SESSION_END;
import static com.wezom.ulcv2.net.models.responses.websocket.GameStateResponse.PlayerData;

/**
 * Created by kartavtsev.s on 02.02.2016.
 */
public class GameActivity extends BaseActivity implements
        GameActivityView {
    private static final String TAG = GameActivity.class.getSimpleName();

    //region constants
    public static final String EXTRA_GAME_ID = "game_id";
    public static final String EXTRA_GAME_TYPE = "game_type";
    public static final String EXTRA_IS_PLAYER = "is_player";
    public static final String EXTRA_PLAYER_ID_1 = "player_id_1";
    public static final String EXTRA_PLAYER_ID_2 = "player_id_2";
    public static final String EXTRA_PLAYER_NAME_1 = "player_name_1";
    public static final String EXTRA_PLAYER_NAME_2 = "player_name_2";
    public static final String EXTRA_PLAYER_LEVEL_1 = "player_lvl_1";
    public static final String EXTRA_PLAYER_LEVEL_2 = "player_lvl_2";
    public static final String EXTRA_PLAYER_AVATAR_1 = "player_avatar_1";
    public static final String EXTRA_PLAYER_AVATAR_2 = "player_avatar_2";
    public static final String EXTRA_WATCHERS = "game_watchers";
    public static final String VIDEO_URL = "video_url";
    public static final float TWO_THIRDS_SCREEN = 0.6f;// port - 0.66667f land - 0.6f
    public static final float ONE_THIRDS_SCREEN = 0.4f;// port - 0.3333f  land - 0.4
    public static final int DEFAULT_COUNT = 0;
    private static final long VOTE_TIME_MS = 15000;
    //endregion

    //region var

    protected CustomUnityPlayer mUnityPlayer;
    @Inject
    SessionAdapter mGameChatAdapter;
    @InjectPresenter
    GameActivityPresenter presenter;

    private boolean isPlayer;
    private boolean isControlsHidden;
    private boolean isUiOrientationSwitchAvailable = true; //from land to portrait
    private PlayVideoFragmentConnector mRightVideoFragment;
    private PlayVideoFragmentConnector mLeftVideoFragment;
    private UserData mFirstPlayerData;
    private UserData mSecondPlayerData;

    private int mFullHeight;
    private int gameType;

    //endregion

    //region views
    @BindView(R.id.activity_game_game_layout)
    RelativeLayout mGameAndControlsLayout;
    @BindView(R.id.activity_game_left_webcam_layout)
    FixedAspectRatioFrameLayout mLeftWebcamView;
    @BindView(R.id.fragment_session_right_webcam_layout)
    FixedAspectRatioFrameLayout mRightWebcamView;
    @BindView(R.id.fragment_session_webcams_layout)
    PercentRelativeLayout mWebcamPercentLayout;
    @BindView(R.id.activity_game_chat_recycler_portrait_layout)
    RelativeLayout mPortraitChatLayout;
    @BindView(R.id.game_activity_watchers_count_label)
    TextView mWatchersTextView;
    @BindView(R.id.game_activity_chat_view)
    ChatView mChatView;
    @BindView(R.id.contentView)
    RecyclerView mChatRecyclerView;
    @BindView(R.id.view_chat_message_edit_text)
    EditText mMessageEditText;
    @BindView(R.id.view_chat_send_text_view)
    TextView mSendTextView;
    @BindView(R.id.game_activity_next_text_view)
    TextView mNextTextView;
    @BindView(R.id.game_activity_leave_text_view)
    TextView mLeaveTextView;
    @BindView(R.id.activity_game_control_and_game_layout)
    RelativeLayout mGameLayout;
    @BindView(R.id.game_activity_control_stream_layout)
    ViewGroup mGamesControlLayout;
    @BindView(R.id.activity_game_right_execution_view)
    ExecutionView mRightExecutionView;
    @BindView(R.id.activity_game_left_execution_view)
    ExecutionView mLeftExecutionView;
    @BindView(R.id.activity_game_root)
    ViewGroup mRoot;
    @BindView(R.id.game_activity_chat_header)
    ViewGroup mChatHeader;
    @BindView(R.id.activity_game_sub_root)
    ViewGroup mChatRoot;
    @BindView(R.id.game_activity_real_input_layout)
    ViewGroup mRealInputLayout;
    @BindView(R.id.loadingView)
    RelativeLayout loadingView;
    @BindView(R.id.loading_view_text)
    TextView loadingTextView;
    @BindView(R.id.game_activity_real_input)
    EditText mRealInput;
    @BindView(R.id.game_activity_message_board)
    GameMessageBoard messageBoard;

    //endregion

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
        Bundle extras = getIntent().getExtras();

        //starting Unity
        setupUnity();
        //setting players and game data from profileChannel
        setPlayersCachedData(extras);
        initChat(extras.getInt(EXTRA_WATCHERS, DEFAULT_COUNT));
        //Its crunch to not push up UI when kb appears
        listenLayoutOnKeyboardAppear();

        //crunches if user rotated to landscape on start
        rotateLandscapeIfNeeded();
        //prepare data to ExecutionViewsFw
        setupExecutionViews();

        //set ready buttons
        presenter.initMessageBoard();
        messageBoard.setClickListener(() -> {
            if (isPlayer) {
                onPlayerReady(new PlayerReadyEvent(-1));
                messageBoard.setLeftButtonReady();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBus();
        if (mUnityPlayer != null) {
            mUnityPlayer.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterBus();
        if (mUnityPlayer != null) {
            mUnityPlayer.pause();
        }
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "onStop()");
        presenter.disconnectSession();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy()");
        mUnityPlayer.quit();
        super.onDestroy();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_game;
    }

    @Override
    public void switchToPlayMode() {
        Log.v(TAG, "switchToPlayMode()");
        if (isPlayer) {
            setOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mGameLayout.getLayoutParams();
            params.removeRule(RelativeLayout.BELOW);
            mGameLayout.setLayoutParams(params);
            mPortraitChatLayout.setVisibility(GONE);
            mRightExecutionView.setVisibility(GONE);
            mLeftExecutionView.setVisibility(GONE);
            mLeftWebcamView.setVisibility(GONE);
            mRightWebcamView.setVisibility(GONE);
            mGamesControlLayout.setVisibility(GONE);
            mNextTextView.setVisibility(GONE);
        }
        //  messageBoard.hideMessageBoard();
    }

    @Override
    public void switchToReadyMode() {
        Log.v(TAG, "switchToReadyMode()");
        if (isPlayer) {
            setOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mGameLayout.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.activity_game_left_execution_view);
            mGameLayout.setLayoutParams(params);
            mRightExecutionView.setVisibility(VISIBLE);
            mLeftWebcamView.setVisibility(VISIBLE);
            mRightWebcamView.setVisibility(VISIBLE);
            mGamesControlLayout.setVisibility(VISIBLE);
            mLeftExecutionView.setVisibility(VISIBLE);
            mNextTextView.setVisibility(GONE);
            mPortraitChatLayout.setVisibility(presenter.isPortrait() ? VISIBLE : GONE);
        }
    }

    @Override
    public void onRoundsEnd() {
        Log.v(TAG, "onRoundsEnd()");
        if (isPlayer) {
            setOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mGameLayout.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.activity_game_left_execution_view);
            mGameLayout.setLayoutParams(params);
            mRightExecutionView.setVisibility(VISIBLE);
            mLeftWebcamView.setVisibility(VISIBLE);
            mRightWebcamView.setVisibility(VISIBLE);
            mGamesControlLayout.setVisibility(VISIBLE);
            mLeftExecutionView.setVisibility(VISIBLE);
            mNextTextView.setVisibility(GONE);
            mPortraitChatLayout.setVisibility(presenter.isPortrait() ? VISIBLE : GONE);
            messageBoard.switchBoardAwaitingExecution();
        }
    }

    @Override
    public void setSpectators(int spectators) {
        Log.d(TAG, "setSpectators " + spectators);
        mWatchersTextView.setText(String.valueOf(spectators));
    }

    @Override
    public void switchToPortExecutionMode(boolean leftPlayerLoser, boolean isPlayerMode) {
        Log.v(TAG, "switchToPortExecutionMode()");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        mPortraitChatLayout.setVisibility(VISIBLE);


        PercentLayoutHelper.PercentLayoutParams layoutParams = (PercentLayoutHelper.PercentLayoutParams) mLeftWebcamView.getLayoutParams();
        layoutParams.getPercentLayoutInfo().restoreLayoutParams(mLeftWebcamView.getLayoutParams());
        layoutParams.getPercentLayoutInfo().widthPercent = leftPlayerLoser ? 0.666667f : 0.333333f;     //  0.666667% of width screen
        // 0.333333% of width screen
        mLeftWebcamView.setLayoutParams((ViewGroup.LayoutParams) layoutParams);
        mLeftWebcamView.requestLayout();

        layoutParams = (PercentLayoutHelper.PercentLayoutParams) mRightWebcamView.getLayoutParams();
        layoutParams.getPercentLayoutInfo().restoreLayoutParams(mRightWebcamView.getLayoutParams());
        layoutParams.getPercentLayoutInfo().widthPercent = leftPlayerLoser
                ? 0.333333f         // 0.333333% of width screen
                : 0.666667f;        //  0.666667% of width screen
        mRightWebcamView.setLayoutParams((ViewGroup.LayoutParams) layoutParams);
        mRightWebcamView.requestLayout();

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mGameLayout.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, leftPlayerLoser ?
                R.id.activity_game_left_webcam_layout :
                R.id.fragment_session_right_webcam_layout);

        mGameLayout.setLayoutParams(params);
        mGameLayout.requestLayout();
        mWebcamPercentLayout.requestLayout();

        Observable.timer(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (mLeftVideoFragment != null) {
                        mLeftVideoFragment.hideUi(true);
                    }
                    if (mRightVideoFragment != null) {
                        mRightVideoFragment.hideUi(true);
                    }
                    mLeaveTextView.setVisibility(VISIBLE);
                    isUiOrientationSwitchAvailable = false;
                    modifyExecutionView(leftPlayerLoser, isPlayerMode);

                });
    }

    @Override
    public void switchToLandExecutionMode(boolean leftPlayerLoser, boolean isPlayerMode) {
        Log.v(TAG, "switchToLandExecutionMode()");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        mPortraitChatLayout.setVisibility(GONE);


        PercentLayoutHelper.PercentLayoutParams layoutParams = (PercentLayoutHelper.PercentLayoutParams) mLeftWebcamView.getLayoutParams();
        layoutParams.getPercentLayoutInfo().restoreLayoutParams(mLeftWebcamView.getLayoutParams());
        layoutParams.getPercentLayoutInfo().widthPercent = leftPlayerLoser
                ? TWO_THIRDS_SCREEN          //  0.666667% of width screen
                : ONE_THIRDS_SCREEN;         // 0.333333% of width screen
        mLeftWebcamView.setLayoutParams((ViewGroup.LayoutParams) layoutParams);
        mLeftWebcamView.requestLayout();

        layoutParams = (PercentLayoutHelper.PercentLayoutParams) mRightWebcamView.getLayoutParams();
        layoutParams.getPercentLayoutInfo().restoreLayoutParams(mRightWebcamView.getLayoutParams());
        layoutParams.getPercentLayoutInfo().widthPercent = leftPlayerLoser
                ? ONE_THIRDS_SCREEN          // 0.333333% of width screen
                : TWO_THIRDS_SCREEN;         // 0.666667% of width screen
        mRightWebcamView.setLayoutParams((ViewGroup.LayoutParams) layoutParams);
        mRightWebcamView.requestLayout();

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mGameLayout.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, leftPlayerLoser ?
                R.id.activity_game_left_webcam_layout :
                R.id.fragment_session_right_webcam_layout);

        mGameLayout.setLayoutParams(params);
        mWebcamPercentLayout.requestLayout();

        Observable.timer(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (mLeftVideoFragment != null) {
                        mLeftVideoFragment.hideUi(true);
                    }
                    if (mRightVideoFragment != null) {
                        mRightVideoFragment.hideUi(true);
                    }
                    mLeaveTextView.setVisibility(VISIBLE);
                    isUiOrientationSwitchAvailable = false;
                    modifyExecutionView(leftPlayerLoser, isPlayerMode);
                });
    }

    @Override
    public void switchBoardToInitMode() {
        messageBoard.switchBoardToInitMode();
    }

    @Override
    public void switchBoardToEndMode() {
        messageBoard.switchBoardToEndMode();
    }

    @Override
    public void switchBoardToExecutionMode() {
        messageBoard.switchBoardToExecutionMode();
    }

    @Override
    public void switchToRatingMode() {
        Log.v(TAG, "switchToRatingMode()");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        isUiOrientationSwitchAvailable = true;
        mLeftExecutionView.setButtonVisibility(View.GONE);
        mRightExecutionView.setButtonVisibility(View.GONE);
        mRightExecutionView.setVisibility(View.VISIBLE);

        if (mLeftVideoFragment != null) {
            mLeftVideoFragment.hideUi(false);
            mRightVideoFragment.hideUi(false);
        }
    }

    @Override
    public void switchToEndMode(boolean isLeftWin, int leftPlayerExp, int rightPlayerExp) {
        Log.v(TAG, "switchBoardToEndMode()");
        isUiOrientationSwitchAvailable = true;
        if (mLeftVideoFragment != null) {
            mLeftVideoFragment.setResults(getString(R.string.xp_format_old, leftPlayerExp >= 0 ? "+" : "-", Math.abs(leftPlayerExp)));
        }
        if (mRightVideoFragment != null) {
            mRightVideoFragment.setResults(getString(R.string.xp_format_old, rightPlayerExp >= 0 ? "+" : "-", Math.abs(rightPlayerExp)));
        }
        mGameLayout.requestLayout();
    }

    @Override
    public void addMessage(ChatMessageResponse messageResponse) {
        messageResponse.setText(String.format(": %s", messageResponse.getText()));
        mGameChatAdapter.addData(messageResponse);
        mChatRecyclerView.scrollToPosition(mGameChatAdapter.getItemCount() - 1);
    }

    @Override
    public void addMessage(NewMessage messageResponse) {
        messageResponse.setText(String.format(": %s", messageResponse.getText()));
        ChatMessageResponse response = new ChatMessageResponse();
        response.setText(messageResponse.getText());
        response.setUser(new ChatMessageResponse.User(messageResponse.getUser().getId(),
                messageResponse.getUser().getName()));
        mGameChatAdapter.addData(response);
        mChatRecyclerView.scrollToPosition(mGameChatAdapter.getItemCount() - 1);
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
                presenter.reportUser(talkId, reportCategory);
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void setDataToVideoView(boolean isPlayer, int gameId, String videoUrl, PlayerData firstPlayerId, PlayerData secondPlayerId) {
        Log.v(TAG, "setDataToVideoView()");
        boolean canClick = !isPlayer;
        FragmentManager fm = ((BaseActivity) getActivity()).getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (mLeftVideoFragment == null) { //only first time
            mLeftVideoFragment = new PlayVideoFragmentBuilder(PlayVideoFragment.VideoMode.LEFT).build();
            mLeftVideoFragment.setData(firstPlayerId, gameId, videoUrl);
            mLeftVideoFragment.setIsStreamerMode(isPlayer);
            mLeftVideoFragment.setCanLikesMode(canClick);
            transaction.replace(R.id.activity_game_left_webcam_layout, (Fragment) mLeftVideoFragment, VideoFragment.TAG);
            mLeftVideoFragment.setUserData(mFirstPlayerData, firstPlayerId.getWins());
        }

        if (mRightVideoFragment == null) {
            mRightVideoFragment = new PlayVideoFragmentBuilder(PlayVideoFragment.VideoMode.RIGHT).build();
            mRightVideoFragment.setData(secondPlayerId, gameId, videoUrl);
            mRightVideoFragment.setIsStreamerMode(false);
            mRightVideoFragment.setCanLikesMode(canClick);
            transaction.replace(R.id.fragment_session_right_webcam_layout, (Fragment) mRightVideoFragment, VideoFragment.TAG);
            mRightVideoFragment.setUserData(mSecondPlayerData, secondPlayerId.getWins());
        }
        transaction.commit();
    }

    @Override
    public void setRightPlayerReady() {
        messageBoard.setRightButtonReady();
    }

    @Override
    public void setLeftPlayerReady() {
        messageBoard.setLeftButtonReady();
    }

    @Override
    public void hideMessageBoard() {
        Log.v(TAG, "hideMessageBoard()");
        messageBoard.hideMessageBoard();
    }

    @Override
    public void showMessageBoard() {
        messageBoard.showMessageBoard();
    }

    @Override
    public void showLoadingView() {
        loadingView.setVisibility(VISIBLE);
    }

    @Override
    public void hideLoadingView() {
        Observable.timer(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> {
                    Log.d(TAG, "hideLoadingView");
                    loadingView.setVisibility(GONE);
                }, throwable -> {
                    Log.e(TAG, throwable.getMessage());
                });
    }

    @Override
    public void switchNormalModeToLandscape() {  // normal it's 50%/50%  web layouts
        Log.v(TAG, "switchNormalModeToLandscape()");
        mPortraitChatLayout.setVisibility(GONE);
        mRightExecutionView.setVisibility(GONE);
        setOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        PercentLayoutHelper.PercentLayoutParams layoutParams = (PercentLayoutHelper.PercentLayoutParams) mLeftWebcamView.getLayoutParams();
        layoutParams.getPercentLayoutInfo().restoreLayoutParams(mLeftWebcamView.getLayoutParams());
        layoutParams.getPercentLayoutInfo().widthPercent = 0.5f;
        mLeftWebcamView.setLayoutParams((ViewGroup.LayoutParams) layoutParams);


        layoutParams = (PercentLayoutHelper.PercentLayoutParams) mRightWebcamView.getLayoutParams();
        layoutParams.getPercentLayoutInfo().restoreLayoutParams(mRightWebcamView.getLayoutParams());
        layoutParams.getPercentLayoutInfo().widthPercent = 0.5f;
        mRightWebcamView.setLayoutParams((ViewGroup.LayoutParams) layoutParams);

        mLeftWebcamView.requestLayout();
        mRightWebcamView.requestLayout();
        mGameAndControlsLayout.requestLayout();
    }

    @Override
    public void switchNormalModeToPortrait() { // normal it's 50%/50%  web layouts
        Log.v(TAG, "switchNormalModeToPortrait()");
        mPortraitChatLayout.setVisibility(VISIBLE);
        mRightExecutionView.setVisibility(GONE);
        setOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        PercentLayoutHelper.PercentLayoutParams layoutParams = (PercentLayoutHelper.PercentLayoutParams) mLeftWebcamView.getLayoutParams();
        layoutParams.getPercentLayoutInfo().restoreLayoutParams(mLeftWebcamView.getLayoutParams());
        layoutParams.getPercentLayoutInfo().widthPercent = 0.5f;
        mLeftWebcamView.setLayoutParams((ViewGroup.LayoutParams) layoutParams);

        layoutParams = (PercentLayoutHelper.PercentLayoutParams) mRightWebcamView.getLayoutParams();
        layoutParams.getPercentLayoutInfo().restoreLayoutParams(mRightWebcamView.getLayoutParams());
        layoutParams.getPercentLayoutInfo().widthPercent = 0.5f;
        mRightWebcamView.setLayoutParams((ViewGroup.LayoutParams) layoutParams);

        mLeftWebcamView.requestLayout();
        mRightWebcamView.requestLayout();
        mGameAndControlsLayout.requestLayout();
    }

    @Override
    public void showVoteView(UserData playerData) {
        VoteRateDialog mVoteRateDialog = new VoteRateDialogBuilder()
                .avatarUrl(playerData.getAvatar())
                .userName(playerData.getName())
                .maxProgress(VOTE_TIME_MS)
                .build();
        mVoteRateDialog.setOnDialogResult(new VoteRateDialog.OnDialogResult() {
            @Override
            public void onLike() {
                presenter.onVoteClick(true);
            }

            @Override
            public void onDislike() {
                presenter.onVoteClick(false);
            }
        });
        mVoteRateDialog.setCancelable(false);
        mVoteRateDialog.show(getSupportFragmentManager(), "");
    }

    @Override
    public void initGame() {
        Log.v(TAG, "initGame()");

    }

    @Override
    public void killView() {
        Log.v(TAG, "killView()");
        finish();
    }

    @Override
    public void setOrientation(int orientation) {
    }

    @Override
    public void addLikes(int talkId, int likes) {
        if (mFirstPlayerData != null && talkId == mFirstPlayerData.getId()) {
            int newLikes = (int) mFirstPlayerData.getLikes() + likes;
            mFirstPlayerData.setLikes(newLikes);
            mLeftVideoFragment.setLikes((int) mFirstPlayerData.getLikes());
        }
        if (mSecondPlayerData != null && talkId == mSecondPlayerData.getId()) {
            int newLikes = (int) mSecondPlayerData.getLikes() + likes;
            mSecondPlayerData.setLikes(newLikes);
            mRightVideoFragment.setLikes((int) mSecondPlayerData.getLikes());
        }
    }

    @Override
    public void showCloseSessionDialog(int title, int message) {
        InformationDialog dialog = new InformationDialogBuilder(InformationDialog.DialogType.TWO_BUTTON_MODE)
                .dialogTitle(getString(title))
                .dialogMessage(getString(message))
                .build();
        dialog.setOnDialogResult((dialogResult, result) -> {
            if (dialogResult == Constants.DialogResult.OK) {
                presenter.leaveSession();
                Log.v(TAG, "presenter.leaveSession()");
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void addLeftWinner() {
        mLeftVideoFragment.addWins();
    }

    @Override
    public void addRightWinner() {
        mRightVideoFragment.addWins();
    }

    @Override
    public void switchUiVisibility() {
        Log.v(TAG, "switchUiVisibility()");
        if (isUiOrientationSwitchAvailable) {
            mLeftVideoFragment.hideUi(!isControlsHidden);
            mRightVideoFragment.hideUi(!isControlsHidden);
            Log.v(TAG, "videoFragment.hideUi(" + !isControlsHidden + ");");
            TransitionManager.beginDelayedTransition(mGamesControlLayout); //bug in android
            mLeaveTextView.setVisibility(isControlsHidden ? VISIBLE : GONE);
            mNextTextView.setVisibility(!isPlayer && isControlsHidden ? VISIBLE : GONE);
            mGamesControlLayout.setVisibility(isControlsHidden ? VISIBLE : GONE);
            isControlsHidden = !isControlsHidden;
        }
    }

    @Override
    public void onSpectatorConnected(int spectatorsCount) {
        Log.d(TAG, "onSpectatorConnected " + spectatorsCount);
        mWatchersTextView.setText(String.valueOf(spectatorsCount));
    }

    @Override
    public void onSpectatorDisconnected(int spectatorsCount) {
        Log.d(TAG, "onSpectatorDisconnected " + spectatorsCount);
        mWatchersTextView.setText(String.valueOf(spectatorsCount));
    }

    @Override
    public void onPlayerDisconnected(int playerId) {
        if (playerId == mLeftVideoFragment.getPlayerId()) {
            mLeftVideoFragment.pause();
        } else {
            mRightVideoFragment.pause();
        }
    }

    @Override
    public void modifyUiIfPlayerOrSpectator(boolean isBlackColorTheme, boolean isShowNextButton) {
        Log.v(TAG, "modifyUiIfPlayerOrSpectator()");
        setBlackColorThemeControlButton(isBlackColorTheme);
        mNextTextView.setVisibility(isShowNextButton ? VISIBLE : GONE);
    }

    @Override
    public void hideKeyBoard() {
        Utils.hideKeyboard(this);
    }

    @Override
    public void switchBoardToGameNotSupportedMode() {
        Log.v(TAG, "switchBoardToGameNotSupportedMode()");
        messageBoard.switchBoardToGameNotSupportedMode();
    }

    @Override
    public void showSignInDialog(int title, int message) {
        InformationDialog dialog = new InformationDialogBuilder(InformationDialog.DialogType.TWO_BUTTON_MODE)
                .dialogTitle(getString(title))
                .dialogMessage(getString(message))
                .build();
        dialog.setOnDialogResult((dialogResult, result) -> {
            if (dialogResult == Constants.DialogResult.OK) {
                dialog.dismiss();
                overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
                presenter.showLoginScreen();
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void requestOrientationAutorotate() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.v(TAG, "onConfigurationChanged()");

        presenter.handleConfigurationChange(newConfig.orientation);
        if (mUnityPlayer != null) {
            mUnityPlayer.configurationChanged(newConfig);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mUnityPlayer != null) {
            mUnityPlayer.windowFocusChanged(hasFocus);
        }
    }

    @Override
    public void setMessageBoardOnState(@SessionState int gameState) {
        switch (gameState) {
            case STATE_GAME:
                break;
            case STATE_EXECUTION:
                messageBoard.switchBoardToExecutionMode();
                break;
            case STATE_CALCULATIONS:
            case STATE_RATING:
                messageBoard.switchToRatingMode();
                break;
            case STATE_SESSION_END:
                messageBoard.switchBoardToEndMode();
                break;
        }
    }

    //endregion

    //region clicks
    @OnClick(R.id.game_activity_leave_text_view)
    public void onLeaveClick() {
        Log.v(TAG, "presenter.onClickLeave()");
        presenter.onClickLeave();
    }

    @OnClick(R.id.game_activity_next_text_view)
    void nextSession() {
        Log.v(TAG, "presenter.showNextGame()");
        presenter.showNextGame();
    }

    @OnClick(R.id.game_activity_real_send_text_view)
    void onSendClick(View view) {
        if (!TextUtils.isEmpty(mRealInput.getText().toString())) {
            presenter.sendMessage(mRealInput.getText().toString());
            mMessageEditText.setText("");
            mRealInput.setText("");
        }
    }

    //endregion

    //region unity
    protected void onUnityReady() {
        Log.v(TAG, "onUnityReady()");
        presenter.onUnityReady();
    }

    protected void onUnityInitialSceneReady() {
        Log.v(TAG, "onUnityInitialSceneReady()");
        presenter.switchUnityScene(gameType);
    }

    protected void onUnityBackPressed() {
        Log.v(TAG, "onUnityBackPressed()");
        presenter.onUnityBackPressed();
    }

    protected void onUnityMessage(String message) {
        Log.v(TAG, "onUnityMessage() " + message);
        presenter.onUnityMessage(message);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mUnityPlayer != null) {
            if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
                return mUnityPlayer.injectEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        Log.v(TAG, "onBackPressed");
        onLeaveClick();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mUnityPlayer != null) {
            return mUnityPlayer.injectEvent(event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mUnityPlayer != null) {
            return mUnityPlayer.injectEvent(event);
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mUnityPlayer != null) {
            return mUnityPlayer.injectEvent(event);
        }
        return super.onTouchEvent(event);
    }

    //endregion

    //region methods

    public void requestOrientationLandscape() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void setupExecutionViews() {
        mLeftExecutionView.setInitState();
        mRightExecutionView.setInitState();

        mRightExecutionView.setClickListener(() -> {
            presenter.onPerformanceDone();
            Log.d(TAG, "OnExecutionClick  mRightExecutionLayout");
        });
    }

    private void setPlayersCachedData(Bundle extras) {
        mFirstPlayerData = new UserData();
        mSecondPlayerData = new UserData();
        int gameId = extras.getInt(EXTRA_GAME_ID, DEFAULT_COUNT);
        gameType = extras.getInt(EXTRA_GAME_TYPE, 0);
        String videoUrl = extras.getString(VIDEO_URL, "");
        isPlayer = extras.getBoolean(EXTRA_IS_PLAYER, true);

        mFirstPlayerData.setId(extras.getInt(EXTRA_PLAYER_ID_1, DEFAULT_COUNT));
        mFirstPlayerData.setName(extras.getString(EXTRA_PLAYER_NAME_1, ""));
        mFirstPlayerData.setLevel(extras.getInt(EXTRA_PLAYER_LEVEL_1, DEFAULT_COUNT));
        mFirstPlayerData.setAvatar(extras.getString(EXTRA_PLAYER_AVATAR_1, ""));

        mSecondPlayerData.setId(extras.getInt(EXTRA_PLAYER_ID_2, DEFAULT_COUNT));
        mSecondPlayerData.setName(extras.getString(EXTRA_PLAYER_NAME_2, ""));
        mSecondPlayerData.setLevel(extras.getInt(EXTRA_PLAYER_LEVEL_2, DEFAULT_COUNT));
        mSecondPlayerData.setAvatar(extras.getString(EXTRA_PLAYER_AVATAR_2, ""));

        // streamer  must be left
        if (extras.getInt(EXTRA_PLAYER_ID_1, DEFAULT_COUNT)
                != presenter.getPreferenceManager().getUserId() && isPlayer) {
            UserData transitUserData = mFirstPlayerData;
            mFirstPlayerData = mSecondPlayerData;
            mSecondPlayerData = transitUserData;
        }

        presenter.setCachedData(gameId, gameType, videoUrl, isPlayer, mFirstPlayerData, mSecondPlayerData); //when first enter game activity, data from profileChannelHandler
    }

    private void rotateLandscapeIfNeeded() {
        Configuration configuration = getResources().getConfiguration();
        int orientation = configuration.orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Observable.timer(2, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(aLong -> {
                        presenter.handleConfigurationChange(Configuration.ORIENTATION_LANDSCAPE);
                    });
        }
    }

    private void setupUnity() {
        mUnityPlayer = new CustomUnityPlayer(this);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// Clear low profile flags to apply non-fullscreen mode before splash screen
        showSystemUi();
        addUiVisibilityChangeListener();
        getWindow().setFormat(PixelFormat.RGBX_8888);
        mGameAndControlsLayout.addView(mUnityPlayer.getView());
        mUnityPlayer.windowFocusChanged(true);
        mUnityPlayer.resume();
    }

    private void setBlackColorThemeControlButton(boolean isBlackTheme) {
        if (isBlackTheme) {
            mLeaveTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_leave_black, 0, 0, 0);
            mNextTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_next_black, 0);
            mNextTextView.setTextColor(getResources().getColor(R.color.color_black));
            mLeaveTextView.setTextColor(getResources().getColor(R.color.color_black));
            mGamesControlLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        } else {
            mLeaveTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_cancel_white, 0, 0, 0);
            mNextTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_next_white, 0);
            mNextTextView.setTextColor(getResources().getColor(R.color.color_white));
            mLeaveTextView.setTextColor(getResources().getColor(R.color.color_white));
            mGamesControlLayout.setBackgroundResource(R.drawable.bg_game_control_layout_gradient);
        }
    }

    private void listenLayoutOnKeyboardAppear() {
        mRoot.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            mFullHeight = mRoot.getRootView().getHeight();
            //rect will be populated with the coordinates of your view that area still visible.
            Rect rect = new Rect();
            mRoot.getWindowVisibleDisplayFrame(rect);
            int kBHeight = mFullHeight - (rect.bottom - rect.top);

            //kbHeight - 612  mFullHeight - 1280
            PercentLayoutHelper.PercentLayoutParams layoutParams = (PercentLayoutHelper.PercentLayoutParams) mPortraitChatLayout.getLayoutParams();
            if (kBHeight > mFullHeight / 4) { // if more than 25%   pixels, its probably a keyboard...
                Log.v("RESIZE_", " keyboard visible");
                showInputLayout(true);
            } else {
                Log.v("RESIZE_", " keyboard not visible");
                showInputLayout(false);
            }
        });
    }

    private void showInputLayout(boolean isShowFakeInput) {
        TransitionManager.beginDelayedTransition(mGamesControlLayout);
        mRealInputLayout.setVisibility(isShowFakeInput ? VISIBLE : GONE);
        mRealInputLayout.requestFocus();
    }

    private void showSystemUi() {
        mUnityPlayer.setSystemUiVisibility(mUnityPlayer.getSystemUiVisibility() & ~getLowProfileFlag());
    }

    private void addUiVisibilityChangeListener() {
        mUnityPlayer.setOnSystemUiVisibilityChangeListener(visibility -> {
            // Whatever changes - force status/nav bar to be visible
            showSystemUi();
            mRoot.requestLayout();
        });
    }

    private void initChat(int watchers) {
        mGameChatAdapter = new SessionAdapter();
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mChatRecyclerView.setAdapter(mGameChatAdapter);
        mChatRecyclerView.addItemDecoration(new ItemListDividerDecorator(this, R.drawable.bg_line_divider_dialogs));
        mWatchersTextView.setText(String.valueOf(watchers));

        RxTextView.textChangeEvents(mMessageEditText)
                .subscribe(textViewTextChangeEvent -> mSendTextView.setEnabled(textViewTextChangeEvent.count() > 0));
    }

    public void unregisterBus() {
        if (bus.isRegistered(this)) {
            bus.unregister(this);
        }
    }

    public void registerBus() {
        if (!bus.isRegistered(this)) {
            bus.register(this);
        }
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mUnityPlayer != null) {
            return mUnityPlayer.injectEvent(event);
        }
        return super.onGenericMotionEvent(event);
    }

    private static int getLowProfileFlag() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                ?
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                :
                View.SYSTEM_UI_FLAG_LOW_PROFILE;
    }

    private void modifyExecutionView(boolean leftPlayerLoser, boolean isPlayerMode) {
        Log.v(TAG, "modifyExecutionView: leftPlayerLoser " + leftPlayerLoser + ", isPlayerMode:" + isPlayerMode);
        if (isPlayerMode) {
            if (leftPlayerLoser) {
                mRightExecutionView.looserLayout();
            } else {
                mLeftExecutionView.winnerLayout();
            }
        } else {
            mLeftExecutionView.setWatcherMode();
            mRightExecutionView.setWatcherMode();
        }
    }
    //endregion

    //region subscribes

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSessionState(SessionStateEvent event) {
        presenter.onSessionState(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGameState(GameStateEvent event) {
        Log.d(TAG, "onGameState ");
        presenter.onGameState(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayerReady(PlayerReadyEvent event) {
        presenter.onPlayerReady(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnityResentEvent(ResentUnityMessageEvent event) {
        presenter.onUnityResentEvent(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatMessage(NewMessageEvent event) {
        presenter.onChatMessage(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowClick(FollowClickEvent event) {
        presenter.onFollowClick(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoClick(OnVideoClickEvent event) {
        presenter.onVideoClick(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLikeClick(OnLocalLikeClickEvent event) {
        presenter.onLikeClick(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkLikeClick(OnNetworkLikeClickEvent event) {
        presenter.onNetworkLikeClick(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLikeLocalResponse(TalkLikesLocalEvent event) {
        presenter.onLikeLocalResponse(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLikesResponse(TalkLikesResponseEvent event) {
        presenter.onLikesResponse(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowResponse(FollowEvent event) {
        presenter.onFollowResponse(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnfolowResponce(UnFollowEvent event) {
        presenter.onUnfollowResponse(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewFollowerResponse(NewFollowerEvent event) {
        presenter.onNewFollowerResponse(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReportUserClick(ReportUserClickEvent event) {
        presenter.onReportUserClick(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpectatorConnection(SpectatorConnectionEvent event) {
        Log.d(TAG, "onSpectatorConnection event ");
        if (event.getUserId() == mLeftVideoFragment.getPlayerId()
                || event.getUserId() == mRightVideoFragment.getPlayerId()) {
            presenter.onPlayerConnection(event);
        } else {
            int currentUsersCount = Integer.parseInt(mWatchersTextView.getText().toString());
            presenter.onSpectatorConnection(event, currentUsersCount);
        }
    }

    @Subscribe
    public void onPlayerMove(DiskMoveEvent event) {
        presenter.onPlayerMove(event);
    }

    @Subscribe
    public void onPlayerMove(RockSpockMoveEvent event) {
        presenter.onPlayerMove(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRoundResult(RoundResultEvent event) {
        presenter.onRoundResult(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRoundStart(RoundStartEvent event) {
        presenter.onRoundStart(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExecutionBegin(BeginExecutionEvent event) {
        presenter.onExecutionBegin(event);
        // messageBoard.switchBoardToExecutionMode();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRatingStarted(onRatingStartedEvent event) {
        presenter.onRatingStarted(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionEvent(PlayerConnectionEvent event) {
        Log.d(TAG, "onConnectionEvent " + event.getType());

        if (event.getType() == Constants.CONNECTION_MODE_RECONNECT) {//player reconnected
            if (mLeftVideoFragment.getPlayerId() == event.getPlayerId()) {
                mLeftVideoFragment.resume();
            } else {
                mRightVideoFragment.resume();
            }
        }
        presenter.onConnectionEvent(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGameResult(GameResultEvent event) {
        presenter.onGameResult(event);
        // messageBoard.switchBoardToEndMode();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onError(OnErrorEvent event) {
        presenter.onError(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketException(FailureConnectionEvent event) {
        presenter.onSocketException(event);
    }

    //endregion
}