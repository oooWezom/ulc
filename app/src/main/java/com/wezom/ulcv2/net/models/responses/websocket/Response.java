package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.ResponseType;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 15.01.2016.
 */
@Data
@Accessors(prefix = "m")
public class Response {
    @SerializedName("type")
    private ResponseType mType;
    @SerializedName("stype")
    private String mStringType;
    private String mSelf;
}
