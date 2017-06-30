package com.wezom.ulcv2.managers;

import android.util.Log;

import com.wezom.ulcv2.events.game.DiskMoveEvent;
import com.wezom.ulcv2.events.game.RoundResultEvent;
import com.wezom.ulcv2.net.models.responses.websocket.RoundResultResponse;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zorin.a on 13.04.2017.
 */

public class SpinTheDisksUnityManager extends UnityManager {
    private static final String TAG = SpinTheDisksUnityManager.class.getSimpleName();

    public SpinTheDisksUnityManager(EventBus bus) {
        super(bus);
    }

    @Override
    public String getUnityRouterName() {
        return "SpinTheDisksMessageRouter";
    }

    @Override
    public void onMessage(String message) {
        Log.v(TAG, "onMessage: " + message);
        UnityMessage unityMessage = mGson.fromJson(message, UnityMessage.class);
        switch (unityMessage.getMessageType()) {
            case 1: // moveDisk
                DiskMoveEvent diskMoveEvent = mGson.fromJson(unityMessage.getMessage(), DiskMoveEvent.class);
                mBus.post(diskMoveEvent);
                break;
            case 1005://callback from game when round ends
                RoundResultResponse roundResultResponse = mGson.fromJson(unityMessage.getMessage(), RoundResultResponse.class);
                mBus.post(new RoundResultEvent(roundResultResponse.getWinnerId(), roundResultResponse.isFinalRound()));
                break;

        }
    }


}
