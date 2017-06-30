package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 15.02.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class BlacklistRemoveRequestEvent {

    private int mId;
}
