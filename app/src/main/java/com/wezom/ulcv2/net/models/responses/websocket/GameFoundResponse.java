package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.Session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 16.02.2016.
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Accessors(prefix = "m")
@Data
public class GameFoundResponse extends Response {

    @SerializedName("session")
    private Session mSession;
}
