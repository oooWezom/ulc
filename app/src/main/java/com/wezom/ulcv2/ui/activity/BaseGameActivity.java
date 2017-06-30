package com.wezom.ulcv2.ui.activity;

import android.content.res.Configuration;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.wezom.ulcv2.common.CustomUnityPlayer;
import com.wezom.ulcv2.mvp.view.SessionActivityView;
import com.wezom.ulcv2.net.SessionChannel;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 13.07.2016
 * Time: 13:40
 */

public abstract class BaseGameActivity extends BaseActivity implements SessionActivityView, SessionChannel {

    protected CustomUnityPlayer mUnityPlayer;

    @Override
    protected void onPause() {
        super.onPause();
        if (mUnityPlayer != null) {
            mUnityPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mUnityPlayer != null) {
            mUnityPlayer.resume();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    if (mUnityPlayer != null) {
            mUnityPlayer.configurationChanged(newConfig);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mUnityPlayer != null) {
            return mUnityPlayer.injectEvent(event);
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mUnityPlayer != null) {
            return mUnityPlayer.injectEvent(event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mUnityPlayer != null) {
            return mUnityPlayer.injectEvent(event);
        }
        return super.onTouchEvent(event);
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mUnityPlayer != null) {
            return mUnityPlayer.injectEvent(event);
        }
        return super.onGenericMotionEvent(event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mUnityPlayer != null) {
            mUnityPlayer.windowFocusChanged(hasFocus);
        }
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
    protected void onDestroy() {
        if (mUnityPlayer != null) {
            mUnityPlayer.quit();
        }
        super.onDestroy();
    }

    public void quitGame() {
        if (mUnityPlayer != null) {
            mUnityPlayer.quit();
            mUnityPlayer = null;
        }
    }
}
