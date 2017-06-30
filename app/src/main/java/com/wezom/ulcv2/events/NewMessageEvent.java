package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.responses.websocket.NewMessageResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 28.01.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class NewMessageEvent {

    private NewMessageResponse mMessage;
}
