package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.responses.websocket.InviteToTalkResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 21.07.2016.
 */
@Data
@AllArgsConstructor
@Accessors(prefix = "m")
public class InviteToTalkResponseEvent {
    public static final int ACCEPT = 1;
    public static final int DENY = 2;
    public static final int DO_NOT_DISTURB = 3;

    private InviteToTalkResponse mResponse;
}
