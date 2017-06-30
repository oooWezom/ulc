package com.wezom.ulcv2.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 26.05.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class SocialLoginRequestEvent {
    public static final int FACEBOOK = 0;
    public static final int VK = 1;
    private int mSocialType; //0 - facebook, 1 - vk
}
