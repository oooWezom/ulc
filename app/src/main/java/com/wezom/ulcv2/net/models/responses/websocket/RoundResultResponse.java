package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 03.03.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class RoundResultResponse extends Response {

    @SerializedName("winner_id")
    private int winnerId;
    @SerializedName("final")
    private boolean isFinalRound;
}
