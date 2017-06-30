package com.wezom.ulcv2.ui.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Utils;
import com.wezom.ulcv2.interfaces.OnLikeClickListener;
import com.wezom.ulcv2.interfaces.PlayVideoFragmentConnector;
import com.wezom.ulcv2.media.BroadcasterCallback;
import com.wezom.ulcv2.media.MediaPlayerCallback;
import com.wezom.ulcv2.mvp.model.UserData;
import com.wezom.ulcv2.mvp.presenter.VideoFragmentPresenter;
import com.wezom.ulcv2.mvp.view.GameActivityView;
import com.wezom.ulcv2.mvp.view.VideoFragmentView;
import com.wezom.ulcv2.net.models.responses.websocket.GameStateResponse;
import com.wezom.ulcv2.ui.view.PlayWebCamView;
import com.wezom.ulcv2.ui.view.TalkWebCamView;
import com.wezom.ulcv2.ui.views.LikeLayout;

import butterknife.OnClick;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.wezom.ulcv2.ui.views.LikeLayout.LEFT_POSITION;
import static com.wezom.ulcv2.ui.views.LikeLayout.RIGHT_POSITION;

/**
 * Created by sivolotskiy.v on 12/10/2016.
 */

@FragmentWithArgs
public class PlayVideoFragment extends BaseFragment
        implements VideoFragmentView, PlayVideoFragmentConnector, TalkWebCamView.WebcamClickListener, OnLikeClickListener, PlayWebCamView.Callback {
    public static final String TAG = VideoFragment.class.getSimpleName();
    private static final int MESSAGE_ID_RECONNECTING = 0x01;
    @Arg
    PlayVideoFragment.VideoMode mVideoMode;
    //region views
    ViewGroup mUiHolder;
    ViewGroup mRoot;
    View mLoadingView;
    PlayWebCamView mTalkWebCamView;
    LikeLayout mLikeLayout;

    //endregion

    @InjectPresenter
    VideoFragmentPresenter mPresenter;

    GameStateResponse.PlayerData mPlayerData;
    ViewGroup mVideoHolder;
    int mMenuMode;
    boolean mIsChecked;
    boolean mIsInStreamerMode;
    int mLikesCount;
    String mRtmpUrl;
    boolean isUiGone;
    IjkMediaPlayer mPlayer;
    SurfaceView mSurfaceView;
    int mGameId;
    UserData mUserData;
    BroadcasterCallback mBroadcasterCallback;
    private MediaPlayerCallback mMediaPlayerCallback;
    private String mVideoUrl;
    private int mWins;
    private boolean mIsVisible = true;
    private boolean mCanLikes = true;

    private int likesDiffCount = 0;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_video;
    }

    @Override
    public void injectDependencies() {
        getFragmentComponent().inject(this);
    }

    @Override
    public void onDestroy() {
        onVideoDestroy();
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int mHeardMode = mVideoMode == PlayVideoFragment.VideoMode.LEFT ? LEFT_POSITION : RIGHT_POSITION;
        mTalkWebCamView = new PlayWebCamView(getActivity(), mVideoMode, mIsInStreamerMode);
        mLikeLayout = new LikeLayout(getContext(), mHeardMode);

        initViews(view);
        prepareLikeLayout();
        refreshDataAndMenu();
        handleUiVisibility();

        prepareUrlForStream();

        executePlayback(mIsInStreamerMode);

        if (mUserData != null) {
            mTalkWebCamView.setData(mUserData);
            mTalkWebCamView.setCallback(this);
            mTalkWebCamView.setScore(mWins);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mIsInStreamerMode) {
            mBroadcasterCallback.onConfigurationChanged(newConfig);
        }
    }

    private void handleUiVisibility() {
        if (isUiGone) {
            mUiHolder.setVisibility(GONE);
        }
    }

    private void prepareUrlForStream() {
        if (mPlayerData != null) {
            mRtmpUrl = mVideoUrl + "/" + "s_" + mGameId + "_" + mPlayerData.getId();
        }
    }

    private void prepareWatcher() {
        if (mPlayer != null) {
            return;
        }
        mPlayer = new IjkMediaPlayer();
        mPlayer.setOnInfoListener((mp, what, extra) -> {
            Log.v("LISTENER", "onInfo()" + " what - " + what + " extra - " + extra);
            switch (what) {
                case 3:
                    hideLoadingView();
                    break;
            }
            return false;
        });
        mMediaPlayerCallback = new MediaPlayerCallback(mPlayer, mRtmpUrl);
        mSurfaceView.getHolder().addCallback(mMediaPlayerCallback);
        mVideoHolder.addView(mSurfaceView);
    }

    private void prepareStreamer() {
        mBroadcasterCallback = new BroadcasterCallback(getContext(), mRtmpUrl, mSurfaceView);
        mSurfaceView.getHolder().addCallback(mBroadcasterCallback);
        mVideoHolder.addView(mSurfaceView);
        ((FrameLayout.LayoutParams) mSurfaceView.getLayoutParams()).gravity = Gravity.CENTER;
        hideLoadingView();
        Log.d("Log_ prepareStreamer", "PlayVideo;");
    }

    private void hideLoadingView() {
        mLoadingView.setVisibility(GONE);
    }

    private void showLoadingView() {
        mLoadingView.setVisibility(VISIBLE);
    }

    private void prepareLikeLayout() {
        mLikeLayout.setIsClickable(mCanLikes);
        mLikeLayout.setLikeClickListener(this);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLikeLayout.getMHeartView().getLayoutParams();

        params.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.negative_small));
        params.setMarginStart(getResources().getDimensionPixelSize(R.dimen.negative_small));
        params.setMarginEnd(getResources().getDimensionPixelSize(R.dimen.negative_small));
        mLikeLayout.getMHeartView().setLayoutParams(params);
    }

    private void initViews(View view) {
        mRoot = (ViewGroup) view.findViewById(R.id.fragment_video_root);
        mVideoHolder = (ViewGroup) view.findViewById(R.id.fragment_video_holder);
        mUiHolder = (ViewGroup) view.findViewById(R.id.fragment_video_ui_holder);
        mLoadingView = view.findViewById(R.id.fragment_video_progress_bar);

        mUiHolder.addView(mTalkWebCamView);
        mUiHolder.addView(mLikeLayout);

        mTalkWebCamView.setVisibility(mIsVisible ? VISIBLE : GONE);
    }

    @OnClick(R.id.fragment_video_root)
    void rootClick() {
        mPresenter.onVideoClick();
    }

    @Override
    public void hideUi(boolean isHideUi) {
        if (mUiHolder != null) {
//            TransitionManager.beginDelayedTransition(mUiHolder);
            Log.v("GA_VF", "mTalkWebCamView.setVisibility(" + (isHideUi ? "GONE" : "VISIBLE") + ")");
            mTalkWebCamView.setVisibility(isHideUi ? GONE : VISIBLE);
            Log.v("GA_VF", "mIsVisible = " + mIsVisible);
            mIsVisible = !isHideUi;
            Log.v("GA_VF", "mIsVisible = " + mIsVisible);
        }
    }

    @Override
    public void setData(GameStateResponse.PlayerData session, int gameId, String videoUrl) {
        mPlayerData = session;
        mGameId = gameId;
        mVideoUrl = videoUrl;
    }

    @Override
    public void executePlayback(boolean isInStreamerMode) {
        mSurfaceView = new SurfaceView(getContext());
        mVideoHolder.setVisibility(View.GONE);
        mVideoHolder.removeAllViews();
        if (Utils.checkAndroidIsKitKatOrLater()) {
            TransitionManager.beginDelayedTransition(mRoot);
            mVideoHolder.setVisibility(View.VISIBLE);
        }
        if (mIsInStreamerMode) {
            prepareStreamer();
        } else {
            prepareWatcher();
        }
    }

    @Override
    public void setResults(String result) {
        if (mTalkWebCamView != null) {
            mTalkWebCamView.setExp(result);
        }
    }

    @Override
    public void stopVideo() {
    }

    @Override
    public void addWins() {
        mWins++;
        mTalkWebCamView.setScore(mWins);
    }

    @Override
    public void setUserData(UserData data, int wins) {
        mUserData = data;
        mWins = wins;
        if (mTalkWebCamView != null) {
            mTalkWebCamView.setData(data);
            mTalkWebCamView.setCallback(this);
            mTalkWebCamView.setScore(wins);
        }
    }

    @Override
    public void setChecked(boolean isChecked) {
        mIsChecked = isChecked;
    }

    @Override
    public void showSingleWindowMenu() {
        mMenuMode = TalkWebCamView.WATCHER_SINGLE_WINDOW;
    }

    @Override
    public void refreshDataAndMenu() {
        if (mLikeLayout != null) {
            mLikesCount = (int) mUserData.getLikes();
            mLikeLayout.setLikeCount(mLikesCount);
        }
    }

    @Override
    public void addLikes(int likes) {
        mLikesCount += likes;
        mLikeLayout.setLikeCount(mLikesCount);
        mLikeLayout.performLikeAnimation(likesDiffCount);
    }

    @Override
    public void setLikes(int likes) {
        likesDiffCount = likes - mLikesCount;
        mLikesCount = likes;
        if (mLikeLayout != null) {
            mLikeLayout.setLikeCount(mLikesCount);
            if (likes > 0 && likesDiffCount > 0) {
                if (likesDiffCount > 10)
                    likesDiffCount = 10;
                for (int i = 0; i < likesDiffCount; i++) {
                    mLikeLayout.performLikeAnimation(likesDiffCount);
                }
            }
        }
    }

    @Override
    public void clearLikes() {
        mLikeLayout.setLikeCount(0);
    }

    @Override
    public void setIsStreamerMode(boolean isInStreamerMode) {
        mIsInStreamerMode = isInStreamerMode;
    }

    @Override
    public void setCanLikesMode(boolean can) {
        mCanLikes = can;
    }

    @Override
    public void pause() {
        showLoadingView();
        mVideoHolder.removeView(mSurfaceView);
        mMediaPlayerCallback = null;
        mPlayer = null;
    }

    @Override
    public void resume() {
        prepareWatcher();
    }

    @Override
    public int getPlayerId() {
        return mUserData.getId();
    }


    @Override
    public void onSwitchTalk() {
        //nothing
    }

    @Override
    public void onFollowStreamer() {
        mPresenter.onFollowUser(mPlayerData.getId());
    }

    @Override
    public void onReportStreamer() {
        mPresenter.onReportUser(mPlayerData.getId());
    }

    @Override
    public void onDetachTalk() {
        mPresenter.detachTalk();
    }

    @Override
    public void onSwitchCamera() {
        mBroadcasterCallback.switchCamera();
    }

    @Override
    public void setIsFollower(int link) {
//        mIsFollower = link != 0;
    }

    @Override
    public void showCloseSessionDialog(int title, int message) {
        ((GameActivityView) getActivity()).showSignInDialog(title, message);
    }

    private void onVideoDestroy() {
        if (mPlayer != null) {
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }

        if (mBroadcasterCallback != null) {
            mBroadcasterCallback.releaseStreamer();
            mBroadcasterCallback = null;
        }

        if (mSurfaceView != null) {
            mSurfaceView.setVisibility(View.GONE);
            mVideoHolder.removeView(mSurfaceView);
            mSurfaceView = null;
        }
    }

    @Override
    public void onReportParticipant(int userId, String avatarUrl) {
        mPresenter.onReportUser(mUserData.getId());
    }

    @Override
    public void onAddParticipant() {
        mPresenter.onFollowUser(mUserData.getId());
    }


    @Override
    public void onLocalLikeClick(int like) {
        if (mCanLikes) {
            mPresenter.localLikeClick(mUserData.getId(), like);
        }
    }

    @Override
    public void onNetworkLikeClick(int likePack) {
        if (mCanLikes) {
            mPresenter.networkLikeClick(mUserData.getId(), likePack);
        }
    }

    public enum VideoMode {
        LEFT,
        RIGHT
    }
}
