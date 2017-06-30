package com.wezom.ulcv2.net.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 12.07.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class Talk {
    @SerializedName("id")
    private int mId;
    @SerializedName("streamer")
    private User mStreamer;
    @SerializedName("category")
    private int mCategory;
    @SerializedName("name")
    private String mName;
    @SerializedName("state")
    private TalkState mTalkState;
    @SerializedName("state_desc")
    private String mStateDescription;
    @SerializedName("likes")
    private int mLikes;
    @SerializedName("spectators")
    private int mSpectators;
    @SerializedName("linked")
    private ArrayList<Talk> mLinked;
    @SerializedName("videoUrl")
    private String mVideoUrl;

    private boolean mIsChosen;

}
