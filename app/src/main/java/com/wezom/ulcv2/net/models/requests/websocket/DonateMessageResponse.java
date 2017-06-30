package com.wezom.ulcv2.net.models.requests.websocket;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.Sender;
import com.wezom.ulcv2.net.models.responses.websocket.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 25.07.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DonateMessageResponse extends Response {

    @SerializedName("message")
    private DonateMessageResponse.Message mMessage;

    @Accessors(prefix = "m")
    @Data
    public static class Message {
        @SerializedName("text")
        private String mText;
        @SerializedName("sender")
        private Sender mSender;

    }
}
