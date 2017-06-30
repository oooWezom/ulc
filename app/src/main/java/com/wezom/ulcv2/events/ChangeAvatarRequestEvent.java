package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 31.05.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class ChangeAvatarRequestEvent {
    public static final int GALLERY = 0;
    public static final int PHOTO = 1;

    private int mType; //0 - gallery 1 - photo
}
