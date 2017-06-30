package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.Talk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 20.07.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class CreateTalkEvent {
    private Talk mTalk;
    private String mMessage;
}
