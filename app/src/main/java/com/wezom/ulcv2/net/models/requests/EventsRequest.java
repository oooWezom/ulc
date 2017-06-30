package com.wezom.ulcv2.net.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 19.01.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class EventsRequest {

    private int mId;
    private int mOffset = 0;
    private int mCount = 10;
    private int mFilter;
}
