package com.wezom.ulcv2.events.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 17.03.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class ResentUnityMessageEvent {

    private String mMessage;
}
