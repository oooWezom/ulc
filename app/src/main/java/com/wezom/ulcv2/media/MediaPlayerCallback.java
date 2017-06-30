package com.wezom.ulcv2.media;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


/**
 * Created by kartavtsev.s on 17.03.2016.
 */
public class MediaPlayerCallback implements SurfaceHolder.Callback {
    private static final String TAG = "MPC";

    private static final int RECONNECT_TIMEOUT_MS = 3000;

    private IjkMediaPlayer mMediaPlayer;
    private String mUrl;

    public MediaPlayerCallback(IjkMediaPlayer player, String url) {
        mMediaPlayer = player;
        mUrl = url;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        init(holder.getSurface());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setDisplay(null);
            mMediaPlayer.reset();
            mMediaPlayer.release();
        }
    }

    private void init(Surface surface) {
        try {
            if (mMediaPlayer == null) {
                return;
            }
            mMediaPlayer.setSurface(surface);
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "nobuffer");
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "live-streaming", 1);

            mMediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.v(TAG, "OnError");
                new Handler().postDelayed(() -> {
                    if (mMediaPlayer != null) {
                        mMediaPlayer.reset();
                        init(surface);
                    }
                }, RECONNECT_TIMEOUT_MS);
                return false;
            });
            mMediaPlayer.setOnCompletionListener(mp -> {
                Log.v(TAG, "OnCompletion");
                new Handler().postDelayed(() -> {
                    if (mMediaPlayer != null) {
                        mMediaPlayer.reset();
                        init(surface);
                    }
                }, RECONNECT_TIMEOUT_MS);
            });
            mMediaPlayer.setOnPreparedListener((iMediaPlayer) -> {
                Log.v(TAG, "OnPrepared");
                iMediaPlayer.start();
            });
            if (!mMediaPlayer.isPlaying()) {
                startPlayback();
            }
            mMediaPlayer.setOnControlMessageListener(new IjkMediaPlayer.OnControlMessageListener() {
                @Override
                public String onControlResolveSegmentUrl(int segment) {
                    Log.v(TAG, "onControlResolveSegmentUrl");
                    return null;
                }
            });

            mMediaPlayer.setOnNativeInvokeListener(new IjkMediaPlayer.OnNativeInvokeListener() {
                @Override
                public boolean onNativeInvoke(int what, Bundle args) {
                    Log.v(TAG, "onNativeInvoke");
                    return false;
                }
            });

            mMediaPlayer.setOnVideoSizeChangedListener(new IMediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
                    Log.v(TAG, "onVideoSizeChanged");
                }
            });
        } catch (IllegalStateException e) {
            // do nothing
        }
    }

    private void startPlayback() {
        try {
            mMediaPlayer.setDataSource(mUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
    }
}
