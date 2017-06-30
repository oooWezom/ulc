package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.Session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 16.02.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class GameCreatedEvent {

    private Session mSession;
}
