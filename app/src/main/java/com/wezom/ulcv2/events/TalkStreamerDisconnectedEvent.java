package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.responses.websocket.TalkStreamerDisconnected;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 26.07.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class TalkStreamerDisconnectedEvent {
private TalkStreamerDisconnected mResponse;
}
