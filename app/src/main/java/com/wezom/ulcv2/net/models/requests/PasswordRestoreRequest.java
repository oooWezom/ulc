package com.wezom.ulcv2.net.models.requests;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 18.03.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class PasswordRestoreRequest {

    @SerializedName("email")
    private String mEmail;
}
