package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 17.06.2016.
 */
@AllArgsConstructor
@Data
@Accessors(prefix = "m")
public class OnDrawerOpenEvent {
    private boolean mIsOpen;
}
