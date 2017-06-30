package com.wezom.ulcv2.net.models;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 26.07.2016.
 */
@Accessors(prefix = "m")
@Data
@Getter
public class Sender {
    @SerializedName("id")
    private int mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("sex")
    private int mSex;
    @SerializedName("level")
    private int mLevel;
}
