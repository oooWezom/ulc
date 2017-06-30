package com.wezom.ulcv2.net.models.requests.websocket;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 18.02.2016.
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class DiskMoveRequest extends Request {

    @SerializedName("disk")
    private int disk;
    @SerializedName("angle")
    private int angle;
}
