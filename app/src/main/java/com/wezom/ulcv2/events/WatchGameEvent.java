package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.Session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 31.05.2016.
 */

@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class WatchGameEvent {

    private Session mSession;
}
