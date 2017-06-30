package com.wezom.ulcv2.net.models;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(prefix = "m")
public class Warning {

    @SerializedName("user_id")
    private int mUserId;
    @SerializedName("created_timestamp")
    private long mCreatedTimestamp;
    @SerializedName("created_date")
    private String mCreatedDate;
    @SerializedName("unread")
    private boolean mUnread;
}