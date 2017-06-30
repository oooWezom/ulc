package com.wezom.ulcv2.ui.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Utils;
import com.wezom.ulcv2.interfaces.OnLikeClickListener;
import com.wezom.ulcv2.interfaces.VideoConnectionListener;
import com.wezom.ulcv2.interfaces.VideoFragmentConnector;
import com.wezom.ulcv2.media.BroadcasterCallback;
import com.wezom.ulcv2.media.MediaPlayerCallback;
import com.wezom.ulcv2.mvp.presenter.VideoFragmentPresenter;
import com.wezom.ulcv2.mvp.view.TalkActivityView;
import com.wezom.ulcv2.mvp.view.VideoFragmentView;
import com.wezom.ulcv2.net.models.Talk;
import com.wezom.ulcv2.ui.view.TalkWebCamView;
import com.wezom.ulcv2.ui.views.LikeLayout;

import butterknife.OnClick;
import rx.Observable;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created: Zorin A.
 * Date: 26.09.2016.
 */

@FragmentWithArgs
public class VideoFragment extends BaseFragment
        implements VideoFragmentView, VideoFragmentConnector, TalkWebCamView.WebcamClickListener, OnLikeClickListener, VideoConnectionListener {

    public static final String TAG = VideoFragment.class.getSimpleName();

    @Arg
    VideoMode mVideoMode;
    @InjectPresenter
    VideoFragmentPresenter mPresenter;

    //region views
    ViewGroup mUiHolder;
    ViewGroup mVisibleUiHolder;
    ViewGroup mVideoHolder;
    ViewGroup mRoot;
    View mLoadingView;
    TalkWebCamView mTalkWebCamView;
    LikeLayout mLikeLayout;
    SurfaceView mSurfaceView;
    //endregion

    boolean mIsChecked;
    boolean mIsInStreamerMode;
    boolean isUiGone;

    int mMenuMode;
    int mLikesCount;
    int likesDiffCount = 0;
    String mRtmpUrl;

    Talk mTalk;
    IjkMediaPlayer mPlayer;
    BroadcasterCallback mBroadcasterCallback;
    private MediaPlayerCallback mMediaPlayerCallback;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_video;
    }

    @Override
    public void injectDependencies() {
        getFragmentComponent().inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroy() {
        onVideoDestroy();
        super.onDestroy();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int heartMode = mVideoMode == VideoMode.LEFT ? LikeLayout.LEFT_POSITION : LikeLayout.RIGHT_POSITION;
        mTalkWebCamView = new TalkWebCamView(getActivity(), mVideoMode);
        mLikeLayout = new LikeLayout(getContext(), heartMode);

        initViews(view);
        setWebcamData();
        prepareLikeLayout();
        refreshDataAndMenu();
        handleUiVisibility();

        prepareUrlForStream();
        executePlayback();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mIsInStreamerMode && mBroadcasterCallback != null) {
            mBroadcasterCallback.onConfigurationChanged(newConfig);
        }
    }

    @OnClick(R.id.fragment_video_root)
    void rootClick() {
        mPresenter.onVideoClick();
    }

    @Override
    public void hideUi(boolean isHideUi) {
        if (Utils.checkAndroidIsKitKatOrLater()) {
            TransitionManager.beginDelayedTransition(mRoot);
        }
        mUiHolder.setVisibility(isHideUi ? View.GONE : View.VISIBLE);
    }

    @Override
    public void setData(Talk talk) {
        mTalk = talk;
    }

    @Override
    public void showStreamerMenu() {
        mMenuMode = TalkWebCamView.STREAMER_MULTI_WINDOW;
    }

    @Override
    public void showMultiWindowMenu() {
        mMenuMode = TalkWebCamView.WATCHER_MULTI_WINDOW;
    }

    @Override
    public void hideMenu() {
        mMenuMode = TalkWebCamView.NO_CONTEXT_MENU;
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
        if (mTalkWebCamView != null) {
            mTalkWebCamView.setMenuMode(mMenuMode, !mIsChecked);
        }
        if (mLikeLayout != null) {
            mLikesCount = mTalk.getLikes();
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
                mLikeLayout.performLikeAnimation(likesDiffCount);
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
    public void setUiVisibilityState(boolean isVisible) {
        isUiGone = isVisible;
    }

    @Override
    public void stopContentExecution() {
        mVideoHolder.removeAllViews();
    }

    @Override
    public void pause() {
        showLoadingView(true);
        mVideoHolder.removeView(mSurfaceView);
        mMediaPlayerCallback = null;
        mPlayer = null;

    }

    @Override
    public void resume() {
        prepareWatcher();
    }

    @Override
    public Talk getCurrentTalk() {
        return mTalk;
    }

    @Override
    public void onSwitchTalk() {
        mPresenter.switchTalk(mTalk);
    }

    @Override
    public void onFollowStreamer() {
        mPresenter.onFollowUser(mTalk.getStreamer().getId());
    }

    @Override
    public void onReportStreamer() {
        mPresenter.onReportUser(mTalk.getId());
    }

    @Override
    public void onDetachTalk() {
        mPresenter.detachTalk();
    }

    @Override
    public void onSwitchCamera() {
        Log.d("Log_onClick", "SwitchCamera");
        mBroadcasterCallback.switchCamera();
    }

    @Override
    public void setIsFollower(int link) {
//        mIsFollower = link != 0;
    }

    @Override
    public void showCloseSessionDialog(int title, int message) {
        ((TalkActivityView) getActivity()).showSignInDialog(title, message);
    }

    @Override
    public void onLocalLikeClick(int like) {
        mPresenter.localLikeClick(mTalk.getId(), like);
    }

    @Override
    public void onNetworkLikeClick(int likePack) {
        mPresenter.networkLikeClick(mTalk.getId(), likePack);
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
    public void onConnected() {
        Log.v(TAG, "onConnected()");
        showLoadingView(false);
    }

    @Override
    public void onDisconnected() {
        Log.v(TAG, "onDisconnected()");
        showLoadingView(true);
        mPresenter.onStreamDisconnected(mTalk.getId());

    }

    private void executePlayback() {
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

    private void handleUiVisibility() {
        if (isUiGone) {
            mUiHolder.setVisibility(View.GONE);
        }
    }

    private void prepareUrlForStream() {
        mRtmpUrl = mTalk.getVideoUrl() + "/" + "t_" + mTalk.getId();
    }

    private void prepareWatcher() {
        mPlayer = new IjkMediaPlayer();
        mPlayer.setOnInfoListener((mp, what, extra) -> {
            Log.v("LISTENER", "onInfo()" + " what - " + what + " extra - " + extra);
            switch (what) {
                case 3:
                    showLoadingView(false);
                    break;
            }
            return false;
        });
        mMediaPlayerCallback = new MediaPlayerCallback(mPlayer, mRtmpUrl);
        mSurfaceView.getHolder().addCallback(mMediaPlayerCallback);
        if (mSurfaceView.getParent() == null) {
            mVideoHolder.addView(mSurfaceView);
        }
    }

    private void showLoadingView(boolean isShow) {
        if (Utils.checkAndroidIsKitKatOrLater()) {
            TransitionManager.beginDelayedTransition(mRoot);
        }
        mLoadingView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private void prepareStreamer() {
        mBroadcasterCallback = new BroadcasterCallback(getContext(), mRtmpUrl, mSurfaceView, this);
        mSurfaceView.getHolder().addCallback(mBroadcasterCallback);
        mVideoHolder.addView(mSurfaceView);
        showLoadingView(false);
    }

    private void prepareLikeLayout() {
        if (mMenuMode == TalkWebCamView.NO_CONTEXT_MENU || mMenuMode == TalkWebCamView.STREAMER_MULTI_WINDOW) { //if u r single or multi streamer
            mLikeLayout.setIsClickable(false);
        } else {
            mLikeLayout.setIsClickable(true);
        }
        mLikeLayout.setLikeClickListener(this);
    }

    private void initViews(View view) {
        mRoot = (ViewGroup) view.findViewById(R.id.fragment_video_root);
        mVideoHolder = (ViewGroup) view.findViewById(R.id.fragment_video_holder);
        mUiHolder = (ViewGroup) view.findViewById(R.id.fragment_video_ui_holder);
        mVisibleUiHolder = (ViewGroup) view.findViewById(R.id.fragment_video_ui_holder_allways_visible);
        mLoadingView = view.findViewById(R.id.fragment_video_progress_bar);
        mUiHolder.addView(mTalkWebCamView);
        mVisibleUiHolder.addView(mLikeLayout);
    }

    private void setWebcamData() {
        mTalkWebCamView.setData(mTalk.getStreamer());
        mTalkWebCamView.setWebcamClickListener(this);
    }

    public enum VideoMode {
        LEFT,
        RIGHT
    }
}
