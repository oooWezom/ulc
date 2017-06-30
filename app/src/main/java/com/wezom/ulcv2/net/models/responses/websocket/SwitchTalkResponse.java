package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 26.07.2016.
 */

@Data
@Accessors(prefix = "m")
@EqualsAndHashCode(callSuper = true)
public class SwitchTalkResponse extends Response {
    @SerializedName("user")
    private int mUserId;
    @SerializedName("to")
    private int mToSession;
    @SerializedName("from")
    private int mFromSession;

}
