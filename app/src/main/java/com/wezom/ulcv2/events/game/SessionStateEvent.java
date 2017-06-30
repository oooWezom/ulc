package com.wezom.ulcv2.events.game;

import com.wezom.ulcv2.net.models.User;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by kartavtsev.s on 22.02.2016.
 */
@Data
@AllArgsConstructor
public class SessionStateEvent {
    private int id;
    private int game;
    private int state;
    private int spectators;
    private List<User> players;
    private int viewers;
    private int loserId;
}
