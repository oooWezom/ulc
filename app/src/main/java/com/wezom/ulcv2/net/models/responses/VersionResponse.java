package com.wezom.ulcv2.net.models.responses;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by usik.a on 23.02.2017.
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(prefix = "m")
public class VersionResponse extends Response {
    @SerializedName("version")
    private int mVersion;
}
