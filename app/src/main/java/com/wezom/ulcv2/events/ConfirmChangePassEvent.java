package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Android 3 on 24.03.2017.
 */

@Data
@AllArgsConstructor
public class ConfirmChangePassEvent {
    String token;
}
