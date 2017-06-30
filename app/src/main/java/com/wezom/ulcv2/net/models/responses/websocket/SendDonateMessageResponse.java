package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 25.07.2016.
 */

@Data
@Accessors(prefix = "m")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SendDonateMessageResponse extends Response {
    @SerializedName("result")
    private int mResult;
    @SerializedName("message")
    private String mMessage;
}
