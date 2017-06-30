package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 26.07.2016
 * Time: 15:10
 */
@Data
@AllArgsConstructor
public class ChatSwitchEvent {

    private boolean isOpened;
}
