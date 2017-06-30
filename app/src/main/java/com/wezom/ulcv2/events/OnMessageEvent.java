package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 25.07.2016.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(prefix = "m")
public class OnMessageEvent {
    private int mTitle;
    private int mMessage;
}
