package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 24.03.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class UserDisconnectedResponse extends Response {

    @SerializedName("user")
    private User mUser;

    @Data
    @Accessors(prefix = "m")
    public static class User {

        @SerializedName("id")
        private int mId;
        @SerializedName("name")
        private String mName;
    }
}
