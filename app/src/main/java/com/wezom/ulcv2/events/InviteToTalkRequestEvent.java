package com.wezom.ulcv2.events;

import com.wezom.ulcv2.net.models.requests.websocket.InviteToTalkRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created: Zorin A.
 * Date: 19.07.2016.
 */
@Data
@Accessors(prefix = "m")
@AllArgsConstructor
public class InviteToTalkRequestEvent {

    private InviteToTalkRequest mInviteToTalkRequest;
}
