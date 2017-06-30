package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 23.09.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class CreateTalkClickEvent {
    private int mCategoryId;
    private String mCategoryName;

}
