package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 08.06.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class VideoPreviewEvent {
    public static final int STOP = 0;
    public static final int START = 1;

    private int mVideoPreviewState;

}
