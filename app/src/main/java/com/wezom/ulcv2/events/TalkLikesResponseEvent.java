package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.requests.websocket.TalkLikesResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Created by sivolotskiy.v on 25.07.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
@Getter
public class TalkLikesResponseEvent {
    private TalkLikesResponse mResponse;
}
