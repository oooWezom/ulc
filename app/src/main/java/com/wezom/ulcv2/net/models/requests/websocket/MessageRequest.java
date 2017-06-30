package com.wezom.ulcv2.net.models.requests.websocket;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 10.02.2016.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class MessageRequest extends Request {

    @SerializedName("text")
    private String mText;
    @SerializedName("recipient_id")
    private int mRecipientId;
}
