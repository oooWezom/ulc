package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.Session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 16.02.2016.
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Accessors(prefix = "m")
@Data
public class SearchGamesResponse extends Response {

    @SerializedName("message")
    private String mMessage;
    @SerializedName("result")
    private Result mResult;
    @SerializedName("session")
    private Session mSession;

    @Getter
    @Accessors(prefix = "m")
    public enum Result {
        @SerializedName("1")
        ADDED_TO_QUEUE(1),
        @SerializedName("2")
        WARNING_BAN(2),
        @SerializedName("3")
        ALREADY_IN_QUEUE(3),
        @SerializedName("4")
        ALREADY_IN_GAME(4),
        @SerializedName("5")
        UNKNOWN_ERROR(5),
        @SerializedName("6")
        PLAYER_DISCONNECTED(6),
        @SerializedName("7")
        GAME_NOT_SUPPORTED(7);

        private int mId;

        Result(int id) {
            mId = id;
        }
    }
}
