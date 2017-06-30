package com.wezom.ulcv2.net.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 20.01.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class ProfileRequest {
    private int mId;
}
