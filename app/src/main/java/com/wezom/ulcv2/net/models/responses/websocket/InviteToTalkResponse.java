package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 19.07.2016.
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(prefix = "m")
@NoArgsConstructor
public class InviteToTalkResponse extends Response {
    @SerializedName("sender")
    private int mSenderId;
    @SerializedName("result")
    private int mResult;
    @SerializedName("time")
    private int mTimeDoNotDisturb; //seconds
    @SerializedName("message")
    private String mMessage;


}
