package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.Talk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 26.07.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class TalkAddedResponse extends Response {
    @SerializedName("talk")
    private Talk mTalk;

}
