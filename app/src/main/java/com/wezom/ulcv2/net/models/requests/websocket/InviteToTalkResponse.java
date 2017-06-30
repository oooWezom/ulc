package com.wezom.ulcv2.net.models.requests.websocket;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 22.07.2016.
 */
@Data
@Accessors(prefix = "m")
@EqualsAndHashCode(callSuper = true)
public class InviteToTalkResponse extends Request {
    @SerializedName("sender")
    private int mSenderId;
    @SerializedName("result")
    private int mResult;
    @SerializedName("time")
    private int mTimeDoNotDisturb; //seconds
    @SerializedName("message")
    private String mMessage;
}
