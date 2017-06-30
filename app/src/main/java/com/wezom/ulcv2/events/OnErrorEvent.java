package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.responses.websocket.ErrorResponse;
import com.wezom.ulcv2.net.models.responses.websocket.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 18.08.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class OnErrorEvent {
    private ErrorResponse mResponse;
}
