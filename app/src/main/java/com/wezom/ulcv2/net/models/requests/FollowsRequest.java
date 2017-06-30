package com.wezom.ulcv2.net.models.requests;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 21.01.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class FollowsRequest {

    private int mId;
    private int mOffset = 0;
    private int mCount = 10;
    @SerializedName("query")
    private Request mQuery;

    @Accessors(prefix = "m")
    @Data
    @AllArgsConstructor
    public static class Request {

        @SerializedName("name")
        private String mName;
    }
}
