package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 05.10.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class OnNetworkLikeClickEvent {
    private int mTalkId;
    private int mCount;
}
