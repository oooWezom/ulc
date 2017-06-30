package com.wezom.ulcv2.events;


import com.wezom.ulcv2.net.models.requests.ProfileUpdateRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 14.03.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class ProfileUpdateEvent {

    private ProfileUpdateRequest mProfile;
}
