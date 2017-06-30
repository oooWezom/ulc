package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.responses.websocket.UpdateTalkDataResponse;

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
public class UpdateTalkDateEvent {
    private UpdateTalkDataResponse mResponse;
}
