package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 25.01.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class DialogSelectedEvent {

    private int mDialogId;
    private String mName;
    private String mAvatar;
}
