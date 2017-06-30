package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.Session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 01.03.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class GameFoundEvent {

    private Session mSession;
}
