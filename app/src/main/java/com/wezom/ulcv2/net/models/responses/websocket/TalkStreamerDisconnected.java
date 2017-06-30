package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 26.07.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
@EqualsAndHashCode(callSuper = true)
public class TalkStreamerDisconnected extends Response {
    @SerializedName("talk")
    private int mTalkId;
    @SerializedName("user")
    private int mUserId;
}
