package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 28.09.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class OnShowToastEvent {
    private int mMessageRes;
}
