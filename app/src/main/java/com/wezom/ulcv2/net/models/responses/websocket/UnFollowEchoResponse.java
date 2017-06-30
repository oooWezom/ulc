package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.requests.websocket.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 15.01.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class UnFollowEchoResponse extends Request {

    @SerializedName("unfollow_id")
    private int mUnFollowId;
    @SerializedName("result")
    private boolean mResult;
}
