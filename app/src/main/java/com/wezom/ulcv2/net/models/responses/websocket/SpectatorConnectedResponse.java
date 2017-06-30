package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;

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
public class SpectatorConnectedResponse extends Response {

    @SerializedName("spectator")
    private Spectator mSpectator;

    @Data
    @Accessors(prefix = "m")
    public static class Spectator {

        @SerializedName("id")
        private int mId;
        @SerializedName("name")
        private String mName;
    }
}
