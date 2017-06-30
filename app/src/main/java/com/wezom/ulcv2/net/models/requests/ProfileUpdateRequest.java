package com.wezom.ulcv2.net.models.requests;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 14.03.2016.
 */
@Data
@Accessors(prefix = "m")
public class ProfileUpdateRequest {

    @SerializedName("username")
    private String mUsername;
    @SerializedName("about")
    private String mAbout;
    @SerializedName("languages")
    private ArrayList<Integer> mLanguages;
    @SerializedName("block_messages")
    private Boolean mBlockMessages;
    @SerializedName("disabled")
    private Boolean mDisabled;
    @SerializedName("private")
    private Boolean mPrivate;
    @SerializedName("avatar")
    private String mAvatar;
    @SerializedName("remove_avatar")
    private Boolean mRemoveAvatar;
    @SerializedName("background")
    private String mBackground;
    @SerializedName("remove_background")
    private Boolean mRemoveBackground;
}
