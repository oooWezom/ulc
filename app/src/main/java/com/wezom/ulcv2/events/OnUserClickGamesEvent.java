package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 22.07.2016.
 */
@Accessors(prefix = "m")
@Data
@AllArgsConstructor
public class OnUserClickGamesEvent {
    private int mUserId;
}
