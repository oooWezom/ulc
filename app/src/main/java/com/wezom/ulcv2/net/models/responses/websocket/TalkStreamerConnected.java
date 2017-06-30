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
@Accessors(prefix = "m")
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TalkStreamerConnected extends Response {
    @SerializedName("talk")
    private int mTalk;
    @SerializedName("user")
    private int mUser;
}
