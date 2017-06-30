package com.wezom.ulcv2.net.models.responses;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 15.01.2016.
 */
@Data
@Accessors(prefix = "m")
public class Response {

    @SerializedName("result")
    private String mResult;
    @SerializedName("message")
    private String mErrorMessage;
    @SerializedName("error")
    private int mError;
}
