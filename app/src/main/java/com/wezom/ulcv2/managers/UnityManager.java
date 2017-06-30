package com.wezom.ulcv2.managers;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.unity3d.player.UnityPlayer;
import com.wezom.ulcv2.interfaces.UnityActionsManager;

import org.greenrobot.eventbus.EventBus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public abstract class UnityManager implements UnityActionsManager {

    private static final String TAG = UnityManager.class.getSimpleName();

    //types
    private static final String UNITY_SYSTEM_ROUTER_NAME = "MessageRouter";
    public static final int MESSAGE_TYPE_INIT = 0;
    public static final int MESSAGE_TYPE_ORIENTATION_CHANGE = 1;
    public static final int MESSAGE_TYPE_SWITCH_SCENE = 2;
    public static final int MESSAGE_TYPE_INITIAL_GAME_STATE = 1000;
    public static final int MESSAGE_TYPE_PLAYER_READY = 1001;
    public static final int MESSAGE_TYPE_ROUND_START = 1002;
    public static final int MESSAGE_TYPE_MOVE_MESSAGE = 1003;
    public static final int MESSAGE_TYPE_ROUND_RESULT = 1005;

    //region screen orientations
    public static final int ORIENTATION_LANDSCAPE = 0;
    public static final int ORIENTATION_PORTRAIT = 1;
    public static final int ORIENTATION_AUTOROTATE = 2;
//endregion

    protected Gson mGson;
    protected EventBus mBus;


    public UnityManager(EventBus bus) {
        mGson = new GsonBuilder().create();
        mBus = bus;
    }

    public abstract String getUnityRouterName();

    public void sendMessage(String message) {
        try {
            Log.v(TAG, "sendSystemMessage: " + message);
            UnityPlayer.UnitySendMessage(getUnityRouterName(), "OnMessage", message);
        } catch (UnsatisfiedLinkError error) {
        }
    }

    public void sendMessage(UnityMessage message) {
        try {
            String json = mGson.toJson(message);
            Log.v(TAG, "sendSystemMessage: " + json);
            UnityPlayer.UnitySendMessage(getUnityRouterName(), "OnMessage", json);
        } catch (UnsatisfiedLinkError error) {
        }
    }

    public void sendSystemMessage(InitMessage message) {
        String json = mGson.toJson(message);
        Log.v(TAG, "sendSystemMessage: " + json);
        try {
            UnityPlayer.UnitySendMessage(UNITY_SYSTEM_ROUTER_NAME, "OnMessage", json);
        } catch (UnsatisfiedLinkError error) {
        }
    }

    public void sendSystemMessage(OrientationMessage message) {
        String json = mGson.toJson(message);
        Log.v(TAG, "sendSystemMessage OrientationMessage: " + json);
        try {
            UnityPlayer.UnitySendMessage(UNITY_SYSTEM_ROUTER_NAME, "OnMessage", json);
        } catch (UnsatisfiedLinkError error) {
        }
    }

    public void sendSystemMessage(SwitchSceneMessage message) {
        String json = mGson.toJson(message);
        Log.v(TAG, "sendSystemMessage SwitchScene: " + json);
        try {
            UnityPlayer.UnitySendMessage(UNITY_SYSTEM_ROUTER_NAME, "OnMessage", json);
        } catch (UnsatisfiedLinkError error) {
        }
    }

    public abstract void onMessage(String message);

    public void switchScene(int scene) {
        UnityPlayer.UnitySendMessage("ScenesRouter", "onSceneSwitch", String.valueOf(scene));
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class UnityMessage {
        @SerializedName("type")
        private int messageType; // example: 1001 - ready(deprecated)
        @SerializedName("message")
        private String message;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class OrientationMessage {
        @SerializedName("type")
        private int messageType; //1 - orientation change
        @SerializedName("orientation")
        private int orientation;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class SwitchSceneMessage {
        @SerializedName("type")
        private int messageType; //2 - switch scene
        @SerializedName("scene")
        private String orientation;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class InitMessage {
        @SerializedName("type")
        private int type;
        @SerializedName("user_id")
        private int userId;
        @SerializedName("game_id")
        private int gameId;
        @SerializedName("left_player_id")
        private int leftPlayerId;
        @SerializedName("base_url")
        private String baseUrl;
    }
}
