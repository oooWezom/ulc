package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 10.11.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class CreatePlayEvent {
    private int mGameId;
    private int mGameType;
    private String mVideoUrl;
    private User mMainUser;
    private User mLinkedUser;

}
