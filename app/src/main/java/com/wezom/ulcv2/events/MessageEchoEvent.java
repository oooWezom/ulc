package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.responses.websocket.MessageEchoResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 14.03.2016.
 */
@AllArgsConstructor
@Accessors(prefix = "m")
@Data
public class MessageEchoEvent {

    private MessageEchoResponse mEchoResponse;
}
