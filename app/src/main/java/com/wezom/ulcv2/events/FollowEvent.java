package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 09.02.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class FollowEvent {
    private boolean mResult;
    private int mFollowId;
    private User mUser;
}
