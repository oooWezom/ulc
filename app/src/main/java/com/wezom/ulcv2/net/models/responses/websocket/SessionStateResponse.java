package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.User;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 16.02.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class SessionStateResponse extends Response {

    @SerializedName("session_state")
    private SessionState sessionState;

    @Data
    public static class SessionState {

        @SerializedName("id")
        private int id;
        @SerializedName("game")
        private int game;
        @SerializedName("state")
        private int state;
        @SerializedName("spectators")
        private int spectators;
        @SerializedName("players")
        private List<User> players;

        @SerializedName("viewers")
        private int viewers;
        @SerializedName("loser_id")
        private int loserId;
    }
}
