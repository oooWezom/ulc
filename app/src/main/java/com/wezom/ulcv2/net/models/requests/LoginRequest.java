package com.wezom.ulcv2.net.models.requests;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 15.01.2016.
 */
@Accessors(prefix = "m")
@Data
@AllArgsConstructor
public class LoginRequest {

    @SerializedName("login")
    private String mLogin;
    @SerializedName("password")
    private String mPassword;
}
