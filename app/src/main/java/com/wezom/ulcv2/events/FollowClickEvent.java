package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 14.06.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class FollowClickEvent {

    private int mId;
    private boolean mIsFollow;
}
