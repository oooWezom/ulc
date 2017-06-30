package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.Profile;
import com.wezom.ulcv2.net.models.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 20.09.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class AddUserToTalkEvent {
    private User mUser;
}
