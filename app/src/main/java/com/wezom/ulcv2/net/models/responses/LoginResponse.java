package com.wezom.ulcv2.net.models.responses;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 15.01.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(prefix = "m")
public class LoginResponse extends Response {

    @SerializedName("id")
    private int mId;
    @SerializedName("token")
    private String mToken;
}
