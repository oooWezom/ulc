package com.wezom.ulcv2.events.game;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 18.02.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class DiskMoveEvent {
    @SerializedName("angle")
    private int mAngle;
    @SerializedName("disk")
    private int mDisk;
}
