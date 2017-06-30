package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.Event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by sivolotskiy.v on 14.07.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class SessionInfoEvent {
    private Event mEvent;
}
