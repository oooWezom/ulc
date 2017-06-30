package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 30.08.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class OnNetworkStateEvent {
    private boolean mIsConnected;
}
