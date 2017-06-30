package com.wezom.ulcv2.net.models.responses.websocket;


import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.mvp.model.Game;
import com.wezom.ulcv2.net.models.Talk;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by Sivolotskiy.v on 19.07.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(prefix = "m")
public class NotificationResponse extends Response {

    @SerializedName("user")
    private int mUserId;
    @SerializedName("talk")
    private Talk mTalk;
    @SerializedName("game")
    private Game mGame;

}
