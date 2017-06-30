package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 24.05.2016.
 */
@Accessors(prefix = "m")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetTitleEvent {
    private String mTitle;
}
