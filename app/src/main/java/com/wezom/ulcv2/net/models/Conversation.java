package com.wezom.ulcv2.net.models;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 28.03.2016.
 */
@Data
@Accessors(prefix = "m")
public class Conversation {

    @SerializedName("id")
    private int mId;
    @SerializedName("created_timestamp")
    private long mCreatedTimestamp;
    @SerializedName("created_date")
    private String mCreatedDate;
    @SerializedName("unread_count")
    private int mUnreadCount;
    @SerializedName("partner")
    private User mPartner;
    @SerializedName("last_message")
    private Message mLastMessage;
}
