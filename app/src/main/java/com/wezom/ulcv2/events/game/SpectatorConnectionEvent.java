package com.wezom.ulcv2.events.game;

import com.wezom.ulcv2.common.Constants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by oskalenko.v on 07.08.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class SpectatorConnectionEvent {

    @Constants.ConnectionMode
    private int mType;
    private int mUserId;
}
