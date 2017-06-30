package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 01.03.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(prefix = "m")
public class ChatMessageResponse extends Response {

    @SerializedName("text")
    private String mText;
    @SerializedName("user")
    private User mUser;

    @Data
    @Accessors(prefix = "m")
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {

        @SerializedName("id")
        private int mId;
        @SerializedName("name")
        private String mName;
    }
}
