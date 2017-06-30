package com.wezom.ulcv2.net.models.responses;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 15.03.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(prefix = "m")
public class CountersResponse extends Response {

    @SerializedName("response")
    Response mResponse;

    @Data
    @Accessors(prefix = "m")
    public static class Response {

        @SerializedName("conversations")
        private int mConversations;
        @SerializedName("messages")
        private int mMessages;
        @SerializedName("followers")
        private int mFollowers;
        @SerializedName("warnings")
        private int mWarnings;
    }
}
