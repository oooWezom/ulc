package com.wezom.ulcv2.managers;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zorin.a on 13.04.2017.
 */

public class NotImplementedUnityManager extends UnityManager {
    private static final String TAG = NotImplementedUnityManager.class.getSimpleName();

    public NotImplementedUnityManager(EventBus bus) {
        super(bus);
    }

    @Override
    public String getUnityRouterName() {
        return "no router";
    }

    @Override
    public void onMessage(String message) {
    }
}
