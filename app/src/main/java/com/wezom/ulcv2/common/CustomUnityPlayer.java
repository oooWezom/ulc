package com.wezom.ulcv2.common;

import android.content.ContextWrapper;
import android.view.WindowManager;

import com.unity3d.player.UnityPlayer;

/**
 * Created by kartavtsev.s on 29.02.2016.
 */
public class CustomUnityPlayer extends UnityPlayer {

    public CustomUnityPlayer(ContextWrapper contextWrapper) {
        super(contextWrapper);

    }


    @Override
    public void kill() {
        currentActivity = null;
    }
}
