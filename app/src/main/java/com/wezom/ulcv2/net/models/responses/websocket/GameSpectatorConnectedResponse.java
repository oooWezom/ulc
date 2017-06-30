package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by oskalenko.v on 08.08.2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Accessors(prefix = "m")
public class GameSpectatorConnectedResponse extends Response {

    @SerializedName("user")
    private User mUser;
}
