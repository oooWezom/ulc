package com.wezom.ulcv2.net.models;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(prefix = "m")
public enum Status {
    @SerializedName("1")
    OFFLINE(1),
    @SerializedName("2")
    ONLINE(2),
    @SerializedName("3")
    SEARCHING(3),
    @SerializedName("4")
    WATCHING(4),
    @SerializedName("5")
    PLAYING(5),
    @SerializedName("6")
    TALKING(6);

    private int mId;

    Status(int id) {
        mId = id;
    }
    public  int getValue(){
        return mId;
    }
}