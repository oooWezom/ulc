package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 22.02.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GameStateResponse extends Response {

    @SerializedName("data")
    private GameState gameState;

    @Data
    public static class GameState {
        @SerializedName("game_id")
        private int id;
        @SerializedName("wins_count")
        private int winsCount;
        @SerializedName("state")
        private int state;
        @SerializedName("round")
        private int round;
        @SerializedName("players_data")
        private ArrayList<PlayerData> playersData;
    }

    @Data
    public static class PlayerData {
        @SerializedName("id")
        private int id;
        @SerializedName("ready")
        private boolean isReady;
        @SerializedName("wins")
        private int wins;
    }
}
