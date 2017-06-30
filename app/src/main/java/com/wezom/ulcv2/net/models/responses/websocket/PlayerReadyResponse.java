package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 22.02.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(prefix = "m")
@Data
public class PlayerReadyResponse extends Response {

    @SerializedName("id")
    private int mId;
}
