package com.wezom.ulcv2.events.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 22.02.2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoundResultEvent {

    private int winner;
    private boolean isFinalRound;
}
