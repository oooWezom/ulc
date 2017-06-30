package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 19.07.2016.
 */
@AllArgsConstructor
@Data
@Accessors(prefix = "m")
public class InviteToTalkSentEvent {
    private int mProfileId;
    private int mCategoryId;

    private String mUserName;
    private String mAvatar;
}
