package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.Profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by Sivolotskiy.v on 31.05.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class ProfileUpdatedEvent {

    private Profile mProfile;
}
