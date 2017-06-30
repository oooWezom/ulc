package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 31.05.2016.
 */
@Data
@Accessors(prefix = "m")
@NoArgsConstructor
@AllArgsConstructor
public class MainMenuPageSelectedEvent {

    private int mPage;
}
