package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 22.07.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class OnUserClickStreamsEvent {
    private int mUserId;
}
