package com.wezom.ulcv2.events;

import com.google.gson.annotations.SerializedName;
import com.wezom.ulcv2.net.models.responses.websocket.TalkAddedResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 26.07.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
@Getter
public class TalkAddedEvent {
    private TalkAddedResponse mResponse;
}
