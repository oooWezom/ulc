package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 28.03.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class ErrorMessageEvent {

    private int mMessageResId;
}
