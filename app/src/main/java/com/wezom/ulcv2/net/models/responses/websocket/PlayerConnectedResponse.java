package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 24.03.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class PlayerConnectedResponse extends Response {

    @SerializedName("player")
    private Player mPlayer;

    @Data
    @Accessors(prefix = "m")
    public static class Player {

        @SerializedName("id")
        private int mId;
        @SerializedName("name")
        private String mName;
        @SerializedName("level")
        private int mLevel;
        @SerializedName("exp")
        private int mExp;
        @SerializedName("state")
        private String mState;
        @SerializedName("state_id")
        private int mStateId;
        @SerializedName("games")
        private ArrayList<Integer> mGames;
        @SerializedName("languages")
        private ArrayList<Integer> mLanguages;
    }
}
