package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 14.03.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(prefix = "m")
public class MessageEchoResponse extends Response {

    @SerializedName("recipient_id")
    private int mRecipientId;
    @SerializedName("message")
    private Message mMessage;

    @Accessors(prefix = "m")
    @Data
    public static class Message {

        @SerializedName("id")
        private int mId;
        @SerializedName("conversation_id")
        private int mConversationId;
        @SerializedName("sender")
        private Sender mSender;
        @SerializedName("text")
        private String mText;
        @SerializedName("posted_timestamp")
        private long mPostedTimestamp;
        @SerializedName("posted_date")
        private String mPostedDate;
    }

    @Accessors(prefix = "m")
    @Data
    public static class Sender {

        @SerializedName("id")
        private int mId;
        @SerializedName("name")
        private String mName;
        @SerializedName("status")
        private int mStatus;
    }
}
