package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.Talk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 30.09.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class onSwitchTalkClickEvent {
    private Talk mTalk;
}
