package com.wezom.ulcv2.net.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(prefix = "m")
public class Session {

    @SerializedName("players")
    ArrayList<User> mPlayers;
    @SerializedName("id")
    private int mId;
    @SerializedName("game_id")
    private int mGameType;
    @SerializedName("game")
    private int mGameId;
    @SerializedName("state")
    private int mState;
    @SerializedName("spectators")
    private int mViewers;
    @SerializedName("videoUrl")
    private String mVideoUrl;
}