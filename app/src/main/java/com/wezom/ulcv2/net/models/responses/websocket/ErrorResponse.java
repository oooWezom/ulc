package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 22.09.2016.
 */
@Data
@Accessors(prefix = "m")
public class ErrorResponse extends Response {
    @SerializedName("error")
    private int mError;
    @SerializedName("message")
    private String mMessage;

}
