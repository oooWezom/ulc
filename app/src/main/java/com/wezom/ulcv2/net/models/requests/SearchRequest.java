package com.wezom.ulcv2.net.models.requests;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 26.01.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class SearchRequest {

    @SerializedName("query")
    private Request mQuery;
    @SerializedName("offset")
    private int mOffset;
    @SerializedName("count")
    private int mCount;

    @Accessors(prefix = "m")
    @Data
    public static class Request {

        @SerializedName("name")
        private String mName;
        @SerializedName("sex")
        private Integer mSex;
        @SerializedName("min_level")
        private Integer mMinLevel;
        @SerializedName("max_level")
        private Integer mMaxLevel;
        @SerializedName("status")
        private Integer mStatus;
        @SerializedName("languages")
        private List<Integer> mLanguages;
    }
}
