package com.wezom.ulcv2.managers;

import android.util.Log;

import com.wezom.ulcv2.events.game.DiskMoveEvent;
import com.wezom.ulcv2.events.game.RockSpockMoveEvent;
import com.wezom.ulcv2.events.game.RoundResultEvent;
import com.wezom.ulcv2.net.models.responses.websocket.RoundResultResponse;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zorin.a on 1.06.2017.
 */

public class RockSpockUnityManager extends UnityManager {
    private static final String TAG = RockSpockUnityManager.class.getSimpleName();

    public RockSpockUnityManager(EventBus bus) {
        super(bus);
    }

    @Override
    public String getUnityRouterName() {
        return "RockSpockMessageRouter";
    }

    @Override
    public void onMessage(String message) {
        Log.v(TAG, "onMessage: " + message);
        UnityMessage unityMessage = mGson.fromJson(message, UnityMessage.class);
        switch (unityMessage.getMessageType()) {
            case 1: // player move
                RockSpockMoveEvent playerMove = mGson.fromJson(unityMessage.getMessage(), RockSpockMoveEvent.class);
                mBus.post(playerMove);
                break;
            case 1005://callback from game when round ends
                RoundResultResponse roundResultResponse = mGson.fromJson(unityMessage.getMessage(), RoundResultResponse.class);
                mBus.post(new RoundResultEvent(roundResultResponse.getWinnerId(), roundResultResponse.isFinalRound()));
                break;
        }
    }
}
