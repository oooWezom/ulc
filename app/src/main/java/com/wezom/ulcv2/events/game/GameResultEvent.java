package com.wezom.ulcv2.events.game;

import com.wezom.ulcv2.net.models.responses.websocket.GameResultResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 29.02.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class GameResultEvent {

    private GameResultResponse mGameResultResponse;
}
