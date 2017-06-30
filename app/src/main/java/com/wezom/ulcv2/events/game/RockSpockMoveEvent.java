package com.wezom.ulcv2.events.game;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by zorin.a on 1.06.2017.
 */
@Data
@AllArgsConstructor
public class RockSpockMoveEvent {
    @SerializedName("move")
    private int moveId;
}
