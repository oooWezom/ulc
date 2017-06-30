package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.Talk;
import com.wezom.ulcv2.net.models.User;
import com.wezom.ulcv2.net.models.requests.ProfileUpdateRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Oskalenko V.
 * Date: 02.08.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class ShowTalkEvent {
    private Talk mTalk;
}
