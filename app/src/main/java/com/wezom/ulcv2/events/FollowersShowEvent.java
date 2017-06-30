package com.wezom.ulcv2.events;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 06.04.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class FollowersShowEvent {

    private ArrayList<Integer> mFollowers;
}
