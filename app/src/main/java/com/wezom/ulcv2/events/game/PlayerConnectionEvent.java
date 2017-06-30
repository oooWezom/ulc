package com.wezom.ulcv2.events.game;

import com.wezom.ulcv2.common.Constants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 24.03.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class PlayerConnectionEvent {

    @Constants.ConnectionMode
    private int mType;
    private int mPlayerId;
}
