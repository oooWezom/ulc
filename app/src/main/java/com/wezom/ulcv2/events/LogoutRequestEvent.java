package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by kartavtsev.s on 03.02.2016.
 */

@Data
@AllArgsConstructor
public class LogoutRequestEvent {
    private boolean restart;
}
