package com.wezom.ulcv2.ui.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;


import com.wezom.ulcv2.R;

import android.widget.MediaController.*;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;


/**
 * Created by zorin.a on 27.03.2017.
 */

public class VideoPlayerMediaController extends MediaController {

    private static final long SEEK_TO_POST_DELAY_MILLIS = 100;
    private MediaPlayerControl playerControl;
    ;

    //region views
    @BindView(R.id.video_control_root)
    ViewGroup rootLayout;
    @BindView(R.id.video_control_video_seekbar)
    SeekBar videoSeekBarView;
    @BindView(R.id.video_control_play_big)
    ImageView playBigView;
    @BindView(R.id.video_control_panel)
    ViewGroup controlPanelLayout;
    @BindView(R.id.video_control_play_small)
    ImageView playSmallView;
    //    @BindView(R.id.video_control_audio_small)
//    ImageView audioSmallView;
//    @BindView(R.id.video_control_audio_seekbar)
//    SeekBar audioSeekbarView;
    @BindView(R.id.video_control_full_screen_toggle)
    ImageView fullScreenToggleView;
    @BindView(R.id.video_control_time_current)
    TextView currentTimeView;
    @BindView(R.id.video_control_time_full)
    TextView fullTimeView;
    //endregion

    //region res
    @BindDrawable(R.drawable.play)
    Drawable playDrawable;
    @BindDrawable(R.drawable.pause)
    Drawable pauseDrawable;
    @BindDrawable(R.drawable.volume)
    Drawable volOnDrawable;
    @BindDrawable(R.drawable.volume_off)
    Drawable volOffDrawable;
    @BindDrawable(R.drawable.expand)
    Drawable expandDrawable;
    @BindDrawable(R.drawable.contract)
    Drawable contractDrawable;

    //endregion

    //region vars
    private OnControlsChangeListener onControlsChangeListener;
    private Subscription updateSubscription;
    //    private boolean isMutedState; //mute audio state
    private boolean isFullScreenState;
    private long duration;
    private AudioManager audioManager;
    private boolean isDragging;
    //endregion

    public VideoPlayerMediaController(Context context) {
        super(context);
        init(context);
    }

    public VideoPlayerMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    //region overrides

    public void setMediaPlayer(MediaPlayerControl playerControl) {
        super.setMediaPlayer(playerControl);
        this.playerControl = playerControl;
    }
    //endregion

    //region onClick
    @OnClick(R.id.video_control_play_big)
    void playClick(View view) {
        switchPlayerState();
    }

    @OnClick(R.id.video_control_play_small)
    void playSmallClick(View view) {
        switchPlayerState();
    }

    @OnClick(R.id.video_control_root)
    void onRootClick(View view) {
        switchPlayerState();
    }

    @OnClick(R.id.video_control_full_screen_toggle)
    void onFullScreenClick(View view) {
        onControlsChangeListener.onFullScreenClick(view);
        switchFullScreenState();
    }

//    @OnClick(R.id.video_control_audio_small)
//    void onMuteClick(View view) {
//        switchMuteAudio();
//    }
    //endregion

    //region methods
    private void init(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.view_video_controller, this);
        ButterKnife.bind(this);
        videoSeekBarView.setMax(1000);
        initListeners();
    }

    private void initListeners() {
//        audioSeekbarView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (progress == 0 && !isMutedState) {
//                    isMutedState = true;
//                    audioSmallView.setImageDrawable(volOffDrawable);
//                } else if (progress > 0 && isMutedState) {
//                    isMutedState = false;
//                    audioSmallView.setImageDrawable(volOnDrawable);
//                }
//
//                float finalProgress = progress * 0.01f;
//                onControlsChangeListener.onVolumeChange(finalProgress);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });

        videoSeekBarView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                final long newPosition = (long) (duration * progress) / 1000;
                String time = generateTime(newPosition);
                currentTimeView.setText(time);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isDragging = true;
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                final long newPosition = (long) (duration * seekBar.getProgress()) / 1000;
                playerControl.seekTo(Math.round(newPosition));
                String time = generateTime(newPosition);
                currentTimeView.setText(time);
                isDragging = false;
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                updateProgress();
            }
        });
    }

//    private void switchMuteAudio() {
//        if (!isMutedState) {
//            onControlsChangeListener.onVolumeChange(0);
//            audioSmallView.setImageDrawable(volOffDrawable);
//
//        } else {
//            int audioLevel = audioSeekbarView.getProgress();
//            onControlsChangeListener.onVolumeChange(audioLevel);
//            audioSmallView.setImageDrawable(volOnDrawable);
//        }
//        isMutedState = !isMutedState;
//    }

    private void switchPlayerState() {
        if (playerControl.isPlaying()) {
            playerControl.pause();
            showPlayingState(true);
        } else {
            onControlsChangeListener.onPlayClicked(true);
            playerControl.start();
            showPlayingState(true);
            startUpdating();
        }
    }

    private void showPlayingState(boolean isPlaying) {
        Log.d("Log_ showPlayingState", String.valueOf(isPlaying));
        playBigView.setVisibility(isPlaying ? GONE : VISIBLE);
        if (playSmallView.getDrawable() == pauseDrawable) {
            playSmallView.setImageDrawable(playDrawable);
        } else {
            playSmallView.setImageDrawable(pauseDrawable);
        }
    }

    public void onComplete() {
        stopUpdating();
        showPlayingState(false);
        videoSeekBarView.setProgress(1000);
    }

    public void seekTo(int seekTo) {
        videoSeekBarView.setProgress(seekTo);
    }

    public long updateProgress() {
        if (playerControl == null || isDragging) {
            return 0;
        }
        long position = playerControl.getCurrentPosition();
        String ct = generateTime(position);

        currentTimeView.setText(ct);

        long duration = playerControl.getDuration();
        this.duration = duration;
        if (duration > 0) {
            long pos = 1000L * position / duration;
            videoSeekBarView.setProgress(Math.round(pos));
        }
        String ft = generateTime(duration);
        fullTimeView.setText(ft);

        return position;
    }

    private void startUpdating() {
        if (updateSubscription == null || updateSubscription.isUnsubscribed()) {
            updateSubscription = Observable
                    .interval(SEEK_TO_POST_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> updateProgress(), throwable -> {/*do nothing*/});
        }
    }

    private void stopUpdating() {
        if (updateSubscription != null && !updateSubscription.isUnsubscribed()) {
            updateSubscription.unsubscribe();
        }
    }

    private void switchFullScreenState() {
        isFullScreenState = !isFullScreenState;
        if (isFullScreenState) {
            fullScreenToggleView.setImageDrawable(contractDrawable);
        } else {
            fullScreenToggleView.setImageDrawable(expandDrawable);
        }
    }

    public void updateDuration(long duration) {
        this.duration = duration;
        String ft = generateTime(duration);
        fullTimeView.setText(ft);
    }

    private static String generateTime(long position) {
        int totalSeconds = (int) (position / 1000);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.ROOT, "%02d:%02d:%02d", hours, minutes,
                    seconds).toString();
        } else {
            return String.format(Locale.ROOT, "%02d:%02d", minutes, seconds)
                    .toString();
        }
    }
    //endregion

    public void setOnControlsChangeListener(OnControlsChangeListener onControlsChangeListener) {
        this.onControlsChangeListener = onControlsChangeListener;
    }

    public void onResume() {
        startUpdating();
    }

    public void onPause() {
        stopUpdating();
        showPlayingState(false);
    }

    public interface OnControlsChangeListener {
        void onFullScreenClick(View view);

        void onVolumeChange(float volume);

        void onPlayClicked(boolean isPlayWhenReady);
    }
}
