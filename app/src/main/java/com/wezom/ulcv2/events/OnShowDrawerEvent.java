package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 12.07.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class OnShowDrawerEvent {
    boolean mIsShow;
}
