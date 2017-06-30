package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.responses.websocket.TalkStreamerConnected;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 26.07.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class TalkStreamerConnectedEvent {
    private TalkStreamerConnected mResponse;
}
