package com.wezom.ulcv2.ui.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.MediaCodecUtil;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.google.android.exoplayer.util.Util;
import com.wezom.ulcv2.R;
import com.wezom.ulcv2.common.Constants;
import com.wezom.ulcv2.common.LocaleUtils;
import com.wezom.ulcv2.common.ScreenOrientation;
import com.wezom.ulcv2.mvp.model.Category;
import com.wezom.ulcv2.mvp.presenter.SessionInfoPresenter;
import com.wezom.ulcv2.mvp.view.SessionInfoView;
import com.wezom.ulcv2.net.models.Event;
import com.wezom.ulcv2.net.models.User;
import com.wezom.ulcv2.ui.adapter.UserInfoAdapter;
import com.wezom.ulcv2.ui.player.HlsRendererBuilder;
import com.wezom.ulcv2.ui.player.Player;
import com.wezom.ulcv2.ui.view.EventItemView;
import com.wezom.ulcv2.ui.views.VideoPlayerMediaController;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import lombok.Setter;
import lombok.experimental.Accessors;

import static android.view.View.GONE;
import static android.widget.RelativeLayout.ABOVE;
import static com.wezom.ulcv2.ui.view.EventItemView.GAME_SESSION_RESULT;
import static com.wezom.ulcv2.ui.view.EventItemView.START_FOLLOW;
import static com.wezom.ulcv2.ui.view.EventItemView.TALK_SESSION_RESULT;

/**
 * Created by zorin.a on 28.03.2017.
 */
public class SessionInfoFragment extends BaseFragment
        implements SessionInfoView, VideoPlayerMediaController.OnControlsChangeListener, SurfaceHolder.Callback,
        AudioCapabilitiesReceiver.Listener, Player.Listener {

    //region constants
    private static final String TAG = SessionInfoFragment.class.getSimpleName();
    private static final String EXTRA_NAME = "fragment_session_name";
    private static final String EXTRA_EVENT = "event";
    //endregion

    @InjectPresenter
    SessionInfoPresenter mPresenter;

    @Inject
    LocaleUtils localeUtils;

    //region views
    @BindView(R.id.iew_active_talk_surface_view)
    SurfaceView surfaceView;
    @BindView(R.id.iew_active_talk_video_frame)
    AspectRatioFrameLayout videoFrame;
    @BindView(R.id.fragment_session_info_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.fragment_session_info_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fragment_session_info_header_layout)
    RelativeLayout mHeaderLayout;
    @BindView(R.id.fragment_session_info_event_view)
    EventItemView eventItemView;
    @BindView(R.id.fragment_session_info_video_controller)
    VideoPlayerMediaController mediaController;
    @BindView(R.id.view_active_talk_image_container)
    ViewGroup videoContainer;
    @BindView(R.id.center)
    View centerView;
    //endregion

    //region variables
    @Setter
    @Accessors(prefix = "m")
    private Event mEvent;
    private UserInfoAdapter mAdapter;
    private String videoPath;

    boolean isFullScreen = false;
    private long playerPosition = 0;
    private Configuration config;
    private Player player;

    private boolean playerNeedsPrepare;
    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
    private static final CookieManager defaultCookieManager;

    static {
        defaultCookieManager = new CookieManager();
        defaultCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }
    //endregion

    public static SessionInfoFragment getNewInstance(String name, Event event) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_NAME, name);
        bundle.putSerializable(EXTRA_EVENT, event);
        SessionInfoFragment sessionInfoFragment = new SessionInfoFragment();
        sessionInfoFragment.setArguments(bundle);
        Log.d("Log_ event", "getNewInstance " + String.valueOf(event != null));
        return sessionInfoFragment;
    }

    //region overrides
    @Override
    public int getLayoutRes() {
        return R.layout.fragment_session_info;
    }

    @Override
    public void injectDependencies() {
        getFragmentComponent().inject(this);
    }

    @Override
    public int getScreenOrientation() {
        return ScreenOrientation.UNSPECIFIED;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        CookieHandler currentHandler = CookieHandler.getDefault();
        if (currentHandler != defaultCookieManager) {
            CookieHandler.setDefault(defaultCookieManager);
        }

        audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(getActivity(), this);
        audioCapabilitiesReceiver.register();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_session_info, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.session_share:
                shareSession();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareSession() {
        String eventUrl = Constants.API_URL + "/event/" + mEvent.getId();
        ShareCompat.IntentBuilder.from(getActivity()).setType("text/plain").setText(eventUrl).startChooser();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        config = newConfig;

        if (!isFullScreen) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setWidgetsVisible(false);
                mPresenter.isShowDrawer(false);
            } else {
                setWidgetsVisible(true);
                mPresenter.isShowDrawer(true);
            }
        } else {
            mPresenter.isShowDrawer(false);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.v(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        surfaceView.getHolder().addCallback(this);
        mEvent = (Event) getArguments().getSerializable(EXTRA_EVENT);
        mPresenter.getDetailedEventById(mEvent.getId());
        Log.d(TAG, "onViewCreated " + String.valueOf(mEvent != null));
        if (mEvent == null) {
            return;
        }

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_action_back);

        switch (mEvent.getTypeId()) {
            case EventItemView.TALK_SESSION_RESULT:
                Category category = Category.getCategoryById(mEvent.getCategory());

                if (category != null) {
                    // Uncomment this in case they would want to use locale translation
//                    if (localeUtils.getCurrentLocale().startsWith("ru")) {
//                        mToolbar.setTitle(category.getNameRu());
//                    } else
                    mToolbar.setTitle(category.getName());
                } else {
                    mToolbar.setTitle(R.string.session_info);
                }

                if (mEvent.getName() != null)
                    mToolbar.setTitle(mEvent.getName());
                break;
            case EventItemView.START_FOLLOW:
                setHasOptionsMenu(false);
                mToolbar.setTitle(R.string.start_follow);
                break;
            default:
                mToolbar.setTitle(R.string.session_info);
                break;
        }

        mToolbar.setNavigationOnClickListener(v -> mPresenter.onBackPressed());

        mAdapter = new UserInfoAdapter(getContext(), mPresenter.getBus());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        mHeaderLayout.setVisibility(GONE);
        centerView.setVisibility(GONE);

        eventItemView.setData(mEvent);

        switch (mEvent.getTypeId()) {
            case GAME_SESSION_RESULT:
                set2playType();
                break;
            case START_FOLLOW:
                setFollowType();
                break;
            case TALK_SESSION_RESULT:
                set2TalkType();
                break;
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (player != null) {
            player.setSurface(surfaceHolder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (player != null) {
            player.blockingClearSurface();
        }
    }

    @Override
    public void setDetailedEvent(Event event) {
        if (event.getPlaylist() != null) {
            initPlayer(event);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        if (player != null) {
            player.seekTo(playerPosition);
            player.setPlayWhenReady(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
        if (player != null) {
            playerPosition = player.getCurrentPosition();
            player.setPlayWhenReady(false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
        setScreenOrientation(ScreenOrientation.UNSPECIFIED);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");

        setScreenOrientation(ScreenOrientation.PORTRAIT);
        mPresenter.isShowDrawer(true);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
        audioCapabilitiesReceiver.unregister();
        releasePlayer();
    }

    @Override
    public void onFullScreenClick(View view) {
        changeVideoScreen();
    }

    @Override
    public void onVolumeChange(float volume) {
    }

    @Override
    public void onPlayClicked(boolean isPlayWhenReady) {
        preparePlayer(isPlayWhenReady);
    }

    //endregion

    //region Player listeners

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
            String text = "playWhenReady=" + playWhenReady + ", playbackState=";
            switch (playbackState) {
                case ExoPlayer.STATE_BUFFERING:
                    text += "buffering";
                    break;
                case ExoPlayer.STATE_ENDED:
                    text += "ended";
                    if (playWhenReady) {
                        mediaController.onComplete();
                    }
                    preparePlayer(false);
                    player.seekTo(0);
                    mediaController.seekTo(0);
                    break;
                case ExoPlayer.STATE_IDLE:
                    text += "idle";
                    break;
                case ExoPlayer.STATE_PREPARING:
                    text += "preparing";
                    break;
                case ExoPlayer.STATE_READY:
                    text += "ready";
                    break;
                default:
                    text += "unknown";
                    break;
            }
            Log.v(TAG, text);
        }
    }

    @Override
    public void onError(Exception e) {
        String errorString = null;
        if (e instanceof UnsupportedDrmException) {
            // Special case DRM failures.
            UnsupportedDrmException unsupportedDrmException = (UnsupportedDrmException) e;
            errorString = getString(Util.SDK_INT < 18 ? R.string.error_drm_not_supported
                    : unsupportedDrmException.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
                    ? R.string.error_drm_unsupported_scheme : R.string.error_drm_unknown);
        } else if (e instanceof ExoPlaybackException
                && e.getCause() instanceof MediaCodecTrackRenderer.DecoderInitializationException) {
            // Special case for decoder initialization failures.
            MediaCodecTrackRenderer.DecoderInitializationException decoderInitializationException =
                    (MediaCodecTrackRenderer.DecoderInitializationException) e.getCause();
            if (decoderInitializationException.decoderName == null) {
                if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                    errorString = getString(R.string.error_querying_decoders);
                } else if (decoderInitializationException.secureDecoderRequired) {
                    errorString = getString(R.string.error_no_secure_decoder,
                            decoderInitializationException.mimeType);
                } else {
                    errorString = getString(R.string.error_no_decoder,
                            decoderInitializationException.mimeType);
                }
            } else {
                errorString = getString(R.string.error_instantiating_decoder,
                        decoderInitializationException.decoderName);
            }
        }
        if (errorString != null) {
            showMessageToast(errorString);
        }
        playerNeedsPrepare = true;
        mediaController.onComplete();
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthAspectRatio) {
        videoFrame.setAspectRatio(
                height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
    }

    //endregion

    //region methods
    public void set2playType() {
        mAdapter.addData(mEvent.getOwner());
        mAdapter.addData(mEvent.getOpponent());
    }

    public void setFollowType() {
        ArrayList<User> users = new ArrayList<>();
        users.add(mEvent.getOwner());
        users.add(mEvent.getFollowing());
        mAdapter.setData(users);
    }

    public void set2TalkType() {
        if (mEvent.getPartners() != null && !mEvent.getPartners().isEmpty()) {
            mAdapter.addData(mEvent.getOwner());
            mAdapter.addData(mEvent.getPartners());
        } else {
            mAdapter.addData(mEvent.getOwner());
        }
    }

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        if (player == null) {
            return;
        }
        boolean backgrounded = player.getBackgrounded();
        boolean playWhenReady = player.getPlayWhenReady();
        releasePlayer();
        preparePlayer(playWhenReady);
        player.setBackgrounded(backgrounded);
    }

    private void initPlayer(Event event) {

        Log.v(TAG, "initPlayer");
        videoPath = Constants.VOD_BASE_URL + event.getPlaylist().getName() + ".m3u8";
        mHeaderLayout.setVisibility(View.VISIBLE);
        centerView.setVisibility(View.VISIBLE);
        preparePlayer(false);
        mediaController.setOnControlsChangeListener(this);
    }

    private void changeVideoScreen() {
        if (!isFullScreen) {

            setWidgetsVisible(false);
        } else {

            setWidgetsVisible(true);
        }
        if (config != null && config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mPresenter.isShowDrawer(false);
        } else {
            mPresenter.isShowDrawer(isFullScreen);
        }

        isFullScreen = !isFullScreen;
    }

    private void setWidgetsVisible(boolean isVisible) {
        if (!isVisible) {
            mHeaderLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            mToolbar.setVisibility(View.GONE);
            eventItemView.setVisibility(GONE);
            mRecyclerView.setVisibility(GONE);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(ABOVE, R.id.fragment_session_info_event_view);
            mHeaderLayout.setLayoutParams(layoutParams);

            mToolbar.setVisibility(View.VISIBLE);
            eventItemView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playerPosition = player.getCurrentPosition();
            player.release();
            player = null;
        }
    }

    private void preparePlayer(boolean playWhenReady) {
        if (player == null) {
            player = new Player(getRendererBuilder());
            player.addListener(this);
            player.seekTo(playerPosition);
            playerNeedsPrepare = true;
            mediaController.setMediaPlayer(player.getPlayerControl());
            mediaController.setEnabled(true);

        }
        if (playerNeedsPrepare) {
            player.prepare();
            playerNeedsPrepare = false;
        }
        player.setSurface(surfaceView.getHolder().getSurface());
        player.setPlayWhenReady(playWhenReady);
    }

    private Player.RendererBuilder getRendererBuilder() {
        String userAgent = Util.getUserAgent(getActivity(), "Ulc2");
        return new HlsRendererBuilder(getActivity(), userAgent, videoPath);
    }

    //endregion
}
