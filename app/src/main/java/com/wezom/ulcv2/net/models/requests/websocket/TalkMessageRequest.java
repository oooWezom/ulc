package com.wezom.ulcv2.net.models.requests.websocket;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 26.07.2016.
 */
@AllArgsConstructor
@Data
@Accessors(prefix = "m")
@EqualsAndHashCode(callSuper = true)
public class TalkMessageRequest extends Request {
    @SerializedName("text")
    private String mText;
}
