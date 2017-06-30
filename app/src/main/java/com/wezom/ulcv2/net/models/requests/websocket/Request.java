package com.wezom.ulcv2.net.models.requests.websocket;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.RequestType;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 15.01.2016.
 */
@Data
@Accessors(prefix = "m")
public class Request {

    @SerializedName("type")
    private int mType;
    @SerializedName("stype")
    private String mStringType;

    public void setData(RequestType data) {
        mType = data.getType();
        mStringType = data.getStringType();
    }
}
