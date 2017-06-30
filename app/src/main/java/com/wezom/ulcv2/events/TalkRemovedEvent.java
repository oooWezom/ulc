package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.responses.websocket.TalkRemovedResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 26.07.2016.
 */
@AllArgsConstructor
@Getter
@Accessors(prefix = "m")
@Data
public class TalkRemovedEvent {
    private TalkRemovedResponse mResponse;
}
