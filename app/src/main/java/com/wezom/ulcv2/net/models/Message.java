package com.wezom.ulcv2.net.models;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 28.03.2016.
 */
@Data
@Accessors(prefix = "m")
public class Message {

    @SerializedName("id")
    private int mId;
    @SerializedName("conversation_id")
    private int mConversationId;
    @SerializedName("out")
    private int mOut;
    @SerializedName("sender")
    private User mSender;
    @SerializedName("text")
    private String mMessage;
    @SerializedName("posted_timestamp")
    private long mPostedTimestamp;
    @SerializedName("posted_date")
    private String mPostedDate;
    @SerializedName("delivered_timestamp")
    private long mDeliveredTimestamp;
    @SerializedName("delivered_date")
    private String mDeliveredDate;
}
