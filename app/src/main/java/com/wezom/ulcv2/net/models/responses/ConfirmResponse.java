package com.wezom.ulcv2.net.models.responses;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * Created by Android 3 on 24.03.2017.
 */

@Data
public class ConfirmResponse extends Response {

    @SerializedName("id")
    private int id;
    @SerializedName("token")
    private String token;
}