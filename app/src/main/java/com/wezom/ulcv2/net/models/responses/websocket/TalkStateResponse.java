package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.Talk;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 30.07.2016
 * Time: 10:53
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(prefix = "m")
public class TalkStateResponse extends Response {
    @SerializedName("result")
    private int mResult;
    @SerializedName("message")
    private String mMessage;
    @SerializedName("talk")
    private Talk mTalk;
}