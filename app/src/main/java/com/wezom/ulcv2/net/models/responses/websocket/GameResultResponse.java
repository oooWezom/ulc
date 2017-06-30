package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 24.03.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(prefix = "m")
public class GameResultResponse extends Response {

    @SerializedName("session_state")
    private SessionState mSessionState;

    @Data
    @Accessors(prefix = "m")
    public static class SessionState {

        @SerializedName("state")
        private int mState;
        @SerializedName("viewers")
        private int mViewers;
        @SerializedName("loser_id")
        private int mLoserId;
        @SerializedName("players_stats")
        private ArrayList<PlayerStats> mPlayerStats;
    }

    @Data
    @Accessors(prefix = "m")
    public static class PlayerStats {

        @SerializedName("id")
        private int mId;
        @SerializedName("level")
        private int mLevel;
        @SerializedName("exp")
        private int mExp;
    }
}
