package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.requests.websocket.DonateMessageResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 25.07.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
@Getter
public class DonateMessageEvent {
    private DonateMessageResponse mResponse;
}
