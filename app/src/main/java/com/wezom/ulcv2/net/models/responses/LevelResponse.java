package com.wezom.ulcv2.net.models.responses;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 21.06.2016.
 */
@Data
@Accessors(prefix = "m")
public class LevelResponse extends Response {
    @SerializedName("response")
    Response mResponse;

    @Data
    @Accessors(prefix = "m")
    public static class Response {
        @SerializedName("max_level")
        private int mMaxLevel;
    }
}
