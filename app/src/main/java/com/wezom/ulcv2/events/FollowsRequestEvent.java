package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by Sivolotskiy.v on 25.05.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class FollowsRequestEvent {
    private int mId;
    private boolean mIsCalledFromDrawer;
}
