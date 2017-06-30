package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 23.09.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class SendTalkMessageEvent {
    private String mMessage;
}
