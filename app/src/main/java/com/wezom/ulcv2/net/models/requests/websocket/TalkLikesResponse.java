package com.wezom.ulcv2.net.models.requests.websocket;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.Talk;
import com.wezom.ulcv2.net.models.TalkState;
import com.wezom.ulcv2.net.models.User;
import com.wezom.ulcv2.net.models.responses.websocket.Response;

import java.util.ArrayList;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by sivolotskiy.v on 26.07.2016.
 */
@Data
@Accessors(prefix = "m")
public class TalkLikesResponse extends Response {

    @SerializedName("talk")
    private int mTalkId;
    @SerializedName("likes")
    private int mLikes;
}