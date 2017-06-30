package com.wezom.ulcv2.net.models.responses.websocket;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.Talk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 26.07.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TalkClosedResponse extends Response {
    @SerializedName("talk")
    private Talk mTalk;

}
