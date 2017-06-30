package com.wezom.ulcv2.net.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 28.03.2016.
 */
@Data
public class User implements Serializable {
    @SerializedName("id")
    private int id;
    @Nullable
    @SerializedName("name")
    private String name;
    @SerializedName("level")
    private int level;
    @SerializedName("exp")
    private int exp;
    @Nullable
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("state")
    private int state;
    @Nullable
    @SerializedName("sex")
    private Sex sex;
    @SerializedName("likes")
    private long likes;
    @SerializedName("level_up")
    private int levelUp;
    @SerializedName("leaver")
    private int leaver;
    @Nullable
    @SerializedName("status")
    private Status status;
    @SerializedName("videoUrl")
    String videoUrl;
    @SerializedName("wins_count")
    private int winsCount;
}