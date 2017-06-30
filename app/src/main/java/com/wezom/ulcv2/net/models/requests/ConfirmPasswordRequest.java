package com.wezom.ulcv2.net.models.requests;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Android 3 on 24.03.2017.
 */

@Data
@AllArgsConstructor
public class ConfirmPasswordRequest {
    @SerializedName("key")
    public String key;
    @SerializedName("password")
    public String password;
}
