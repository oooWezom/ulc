package com.wezom.ulcv2.net.models.requests.websocket;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.responses.websocket.Response;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by oskalenko.v on 03.08.2016.
 */
@Data
@Accessors(prefix = "m")
public class TalkLikesRequest extends Request {

    @SerializedName("talk")
    private int mTalkId;
    @SerializedName("likes")
    private int mLikes;
}