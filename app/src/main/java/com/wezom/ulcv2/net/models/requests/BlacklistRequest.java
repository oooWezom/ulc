package com.wezom.ulcv2.net.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 11.02.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class BlacklistRequest {

    private int mOffset;
    private int mCount;
    private String mQuery;
}
