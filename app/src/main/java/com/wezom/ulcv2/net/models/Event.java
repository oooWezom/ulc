package com.wezom.ulcv2.net.models;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.mvp.model.Playlist;
import com.wezom.ulcv2.net.models.responses.Response;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 28.03.2016.
 */
@Data
@Accessors(prefix = "m")
public class Event extends Response implements Serializable {

    @SerializedName("id")
    private int mId;
    @SerializedName("type")
    private int mTypeId;
    @SerializedName("type_desc")
    private String mTypeDesc;
    @SerializedName("name")
    private String mName;
    @SerializedName("created_timestamp")
    private long mCreatedTimestamp;
    @SerializedName("created_date")
    private String mCreatedDate;
    @SerializedName("category")
    private int mCategory;
    @SerializedName("partners")
    private ArrayList<User> mPartners;
    @SerializedName("owner")
    private User mOwner;
    @SerializedName("opponent")
    private User mOpponent;
    @SerializedName("winner")
    private int mWinnerId;
    @SerializedName("session")
    private long mSessionId;
    @SerializedName("game")
    private long mGameId;
    @SerializedName("spectatorsCount")
    private long mSpectators;
    @SerializedName("likes")
    private long mLikes;
    @SerializedName("following")
    User mFollowing;
    @SerializedName("viewers")
    private int mViewesr;
    @SerializedName("exp")
    private int mExp;
    @SerializedName("exp1")
    private int mExp1;
    @SerializedName("exp2")
    private int mExp2;
    @SerializedName("level")
    private int mLevel;
    @SerializedName("new_level")
    private int mNewLevel;
    @SerializedName("watchers")
    private int mWatchers;
    @SerializedName("playlist")
    private Playlist mPlaylist;
}
