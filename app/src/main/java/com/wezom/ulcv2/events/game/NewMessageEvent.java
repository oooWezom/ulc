package com.wezom.ulcv2.events.game;

import com.wezom.ulcv2.mvp.model.NewMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 01.03.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class NewMessageEvent {

    private NewMessage mNewMessage;
}
