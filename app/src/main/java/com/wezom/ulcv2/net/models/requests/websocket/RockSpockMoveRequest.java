package com.wezom.ulcv2.net.models.requests.websocket;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by zorin.a on 1.05.2017.
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class RockSpockMoveRequest extends Request {
    @SerializedName("move")
    private int moveType;
}
