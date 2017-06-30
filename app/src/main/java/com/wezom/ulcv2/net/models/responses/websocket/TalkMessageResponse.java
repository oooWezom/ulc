package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.Sender;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 26.07.2016.
 */

@Data
@Accessors(prefix = "m")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TalkMessageResponse extends Response {
    @SerializedName("text")
    private String mText;
    @SerializedName("sender")
    private Sender mUser;
}
