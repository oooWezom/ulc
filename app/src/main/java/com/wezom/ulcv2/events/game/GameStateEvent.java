package com.wezom.ulcv2.events.game;

import com.wezom.ulcv2.net.models.responses.websocket.GameStateResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 22.02.2016.
 */
@Data
@AllArgsConstructor
public class GameStateEvent {
    private GameStateResponse.GameState gameState;
    private String self;
}
