package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 26.02.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(prefix = "m")
public class BeginExecutionResponse extends Response {

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
    }
}
