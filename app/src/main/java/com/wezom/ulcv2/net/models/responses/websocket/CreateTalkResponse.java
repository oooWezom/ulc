package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.Talk;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 19.07.2016.
 */

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(prefix = "m")
public class CreateTalkResponse extends Response {
    @SerializedName("result")
    private int mResult;
    @SerializedName("message")
    private String mMessage;
    @SerializedName("talk")
    private Talk mTalk;
}
