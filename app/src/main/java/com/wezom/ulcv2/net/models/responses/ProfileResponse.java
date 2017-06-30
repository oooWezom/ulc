package com.wezom.ulcv2.net.models.responses;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.Profile;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 20.01.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(prefix = "m")
public class ProfileResponse extends Response {

    @SerializedName("response")
    private Profile mProfile;
}
